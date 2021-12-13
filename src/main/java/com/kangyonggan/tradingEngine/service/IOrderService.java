package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.dto.req.CreateOrderReq;
import com.kangyonggan.tradingEngine.entity.Order;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
public interface IOrderService extends IService<Order> {

    /**
     * 保存订单
     *
     * @param order
     * @return
     */
    Order saveOrder(CreateOrderReq order);

    /**
     * 更新订单
     *
     * @param order
     */
    void updateOrder(Order order);

    /**
     * 查询买单
     *
     * @return
     */
    List<Order> getBuyOrders();

    /**
     * 查询卖单
     *
     * @return
     */
    List<Order> getSellOrders();

    /**
     * 查询用户订单
     *
     * @param uid
     * @param clientOrderNo
     * @return
     */
    Order getOrder(String uid, String clientOrderNo);

}
