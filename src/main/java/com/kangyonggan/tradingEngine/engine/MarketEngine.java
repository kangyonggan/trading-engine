package com.kangyonggan.tradingEngine.engine;

import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.dto.TickDto;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.entity.Trade;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.ITradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kyg
 */
@Component
@Slf4j
public class MarketEngine {

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private RedisManager redisManager;

    @PostConstruct
    public void init() {
        List<SymbolConfig> symbolConfigs = symbolConfigService.getAllSymbolConfigs();
        for (SymbolConfig symbolConfig : symbolConfigs) {
            TickDto lastTick = redisManager.getLastKline(symbolConfig.getSymbol());
            long beginTime = 0L;
            if (lastTick != null) {
                beginTime = lastTick.getId() * 60000L;
            } else {
                lastTick = new TickDto();
                lastTick.setHigh(BigDecimal.ZERO);
                lastTick.setOpen(BigDecimal.ZERO);
                lastTick.setLow(BigDecimal.valueOf(999999999));
                lastTick.setClose(BigDecimal.ZERO);
                lastTick.setVol(BigDecimal.ZERO);
            }
            // 查询 beginTime 之后的所有交易，封装为1分钟K先数据存入redis
            List<Trade> trades = tradeService.getTradeAfterTime(symbolConfig.getSymbol(), beginTime);
            if (trades.isEmpty()) {
                continue;
            }

            Map<Long, TickDto> dbTicks = toTick(trades, lastTick);
            if (beginTime == 0) {
                beginTime = trades.get(0).getCreateTime().toEpochSecond(ZoneOffset.of("+8")) / 60;
            } else {
                beginTime = beginTime / 60000;
            }
            long endTime = System.currentTimeMillis() / 60000;
            log.info("同步{}行情：[{}, {}], 最后的行情：{}", symbolConfig.getSymbol(), beginTime, endTime, lastTick);

            for (long id = beginTime; id <= endTime; id++) {
                TickDto tickDto = new TickDto();
                tickDto.setId(id);
                tickDto.setTs(id * 60000);
                tickDto.setSymbol(symbolConfig.getSymbol());
                if (dbTicks.containsKey(id)) {
                    TickDto dbTick = dbTicks.get(id);
                    tickDto.setHigh(dbTick.getHigh());
                    tickDto.setOpen(dbTick.getOpen());
                    tickDto.setLow(dbTick.getLow());
                    tickDto.setClose(dbTick.getClose());
                    tickDto.setVol(dbTick.getVol());
                } else {
                    tickDto.setHigh(lastTick.getClose());
                    tickDto.setOpen(lastTick.getClose());
                    tickDto.setLow(lastTick.getClose());
                    tickDto.setClose(lastTick.getClose());
                    tickDto.setVol(BigDecimal.ZERO);
                }

                log.info("存储行情：{}", tickDto);
                redisManager.addKline(tickDto);
                lastTick = tickDto;
            }
        }
    }

    private Map<Long, TickDto> toTick(List<Trade> trades, TickDto lastTick) {
        Map<Long, TickDto> dbTicks = new HashMap<>();
        for (Trade trade : trades) {
            TickDto dbTick = dbTicks.get(trade.getCreateTime().toEpochSecond(ZoneOffset.of("+8")) / 60);
            if (dbTick == null) {
                dbTick = new TickDto();
                dbTick.setSymbol(trade.getSymbol());
                dbTick.setTs(trade.getCreateTime().toEpochSecond(ZoneOffset.of("+8")) * 1000);
                dbTick.setId(dbTick.getTs() / 60000);
                dbTick.setHigh(lastTick.getClose());
                dbTick.setOpen(lastTick.getClose());
                dbTick.setLow(lastTick.getClose());
                dbTick.setClose(lastTick.getClose());
                dbTick.setVol(BigDecimal.ZERO);
            }
            TickDto tickDto = new TickDto();
            tickDto.setSymbol(trade.getSymbol());
            tickDto.setTs(trade.getCreateTime().toEpochSecond(ZoneOffset.of("+8")) * 1000);
            tickDto.setId(dbTick.getTs() / 60000);
            tickDto.setHigh(trade.getPrice().compareTo(dbTick.getHigh()) > 0 ? trade.getPrice() : dbTick.getHigh());
            tickDto.setOpen(lastTick.getClose());
            tickDto.setLow(trade.getPrice().compareTo(dbTick.getLow()) < 0 ? trade.getPrice() : dbTick.getLow());
            tickDto.setClose(trade.getPrice());
            tickDto.setVol(dbTick.getVol().add(trade.getQuantity()));

            dbTicks.put(tickDto.getId(), tickDto);
            lastTick = tickDto;
        }
        return dbTicks;
    }

}
