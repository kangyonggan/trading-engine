package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.constants.enums.TradeStatus;
import com.kangyonggan.tradingEngine.dto.res.TradeRes;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.Trade;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 获取 beginTime 之后的所有交易
     *
     * @param symbol
     * @param beginTime
     * @return
     */
    List<Trade> getTradeAfterTime(String symbol, long beginTime);

    /**
     * 获取最近30条成交记录
     *
     * @param symbol
     * @return
     */
    List<TradeRes> getLast30Trade(String symbol);
}
