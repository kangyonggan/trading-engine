package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.Trade;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 交易表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
public interface ITradeService extends IService<Trade> {

    /**
     * 保存交易
     *
     * @param takerOrder
     * @param makerOrder
     * @param quantity
     */
    void saveTrade(Order takerOrder, Order makerOrder, BigDecimal quantity);
}
