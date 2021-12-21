package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.constants.enums.Enable;
import com.kangyonggan.tradingEngine.dto.TickDto;
import com.kangyonggan.tradingEngine.dto.res.SymbolRes;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.mapper.SymbolConfigMapper;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.ITradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交易对配置表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Service
public class SymbolConfigServiceImpl extends ServiceImpl<SymbolConfigMapper, SymbolConfig> implements ISymbolConfigService {

    private final Map<String, SymbolConfig> symbolConfigMap = new HashMap<>(16);

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private RedisManager redisManager;

    @PostConstruct
    public void init() {
        QueryWrapper<SymbolConfig> qw = new QueryWrapper<>();
        qw.eq("enable", Enable.YES.getValue());
        List<SymbolConfig> symbolConfigs = baseMapper.selectList(qw);
        for (SymbolConfig symbolConfig : symbolConfigs) {
            symbolConfigMap.put(symbolConfig.getSymbol(), symbolConfig);
        }
    }

    @Override
    public SymbolConfig getSymbolConfig(String symbol) {
        return symbolConfigMap.get(symbol);
    }

    @Override
    public List<SymbolConfig> getAllSymbolConfigs() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<SymbolRes> getSymbolList() {
        QueryWrapper<SymbolConfig> qw = new QueryWrapper<>();
        qw.eq("enable", Enable.YES.getValue());
        qw.orderByAsc("sort");
        List<SymbolConfig> symbolConfigs = baseMapper.selectList(qw);
        List<SymbolRes> resList = new ArrayList<>(symbolConfigs.size());
        for (SymbolConfig symbolConfig : symbolConfigs) {
            SymbolRes symbolRes = new SymbolRes();
            symbolRes.setSymbol(symbolConfig.getSymbol());
            symbolRes.setMakerFeeRate(symbolConfig.getMakerFeeRate());
            symbolRes.setTakerFeeRate(symbolConfig.getTakerFeeRate());
            symbolRes.setSort(symbolConfig.getSort());
            symbolRes.setPriceScale(symbolConfig.getPriceScale());
            symbolRes.setQuantityScale(symbolConfig.getQuantityScale());
            TickDto lastKline = redisManager.getLastKline(symbolConfig.getSymbol());
            symbolRes.setPrice(lastKline == null ? BigDecimal.ZERO : lastKline.getClose());
            Long min = System.currentTimeMillis() / 1000 - 24 * 60 * 60;
            Long max = min;
            TickDto oneDayAgoTick = redisManager.getKline(symbolConfig.getSymbol(), min, max);
            if (oneDayAgoTick == null) {
                max = System.currentTimeMillis() / 1000;
                oneDayAgoTick = redisManager.getKline(symbolConfig.getSymbol(), min, max);
            }
            BigDecimal rose = BigDecimal.ZERO;
            if (oneDayAgoTick != null && lastKline != null) {
                rose = lastKline.getClose().subtract(oneDayAgoTick.getClose()).divide(oneDayAgoTick.getClose(), 4, RoundingMode.HALF_UP);
            }
            symbolRes.setRose(rose);
            resList.add(symbolRes);
        }
        return resList;
    }
}
