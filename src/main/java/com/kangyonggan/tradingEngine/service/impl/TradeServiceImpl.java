package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.entity.Trade;
import com.kangyonggan.tradingEngine.mapper.TradeMapper;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.ITradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 交易表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Service
public class TradeServiceImpl extends ServiceImpl<TradeMapper, Trade> implements ITradeService {

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Override
    public void saveTrade(Order takerOrder, Order makerOrder, BigDecimal quantity) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(takerOrder.getSymbol());

        Trade trade = new Trade();
        trade.setTakerOrderId(takerOrder.getId());
        trade.setMakerOrderId(makerOrder.getId());
        trade.setSymbol(takerOrder.getSymbol());
        trade.setQuantity(quantity);
        trade.setPrice(takerOrder.getPrice());
        trade.setTakerFee(symbolConfig.getTakerFeeRate().multiply(takerOrder.getPrice()).multiply(quantity).setScale(symbolConfig.getPriceScale(), RoundingMode.UP));
        trade.setMakerFee(symbolConfig.getMakerFeeRate().multiply(takerOrder.getPrice()).multiply(quantity).setScale(symbolConfig.getPriceScale(), RoundingMode.UP));

        baseMapper.insert(trade);
    }
}
