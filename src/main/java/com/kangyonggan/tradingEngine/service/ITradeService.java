package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.constants.enums.TradeStatus;
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
     * @return
     */
    boolean saveTrade(Order takerOrder, Order makerOrder, BigDecimal quantity);

    /**
     * 更新交易状态
     *
     * @param id
     * @param status
     */
    void updateTradeStatus(long id, TradeStatus status);
}
