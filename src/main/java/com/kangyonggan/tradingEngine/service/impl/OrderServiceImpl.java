package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.OrderStatus;
import com.kangyonggan.tradingEngine.constants.enums.OrderType;
import com.kangyonggan.tradingEngine.dto.req.CreateOrderReq;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.mapper.OrderMapper;
import com.kangyonggan.tradingEngine.service.IOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Override
    public Order saveOrder(CreateOrderReq req) {
        Order order = new Order();
        order.setUid(req.getUid());
        order.setSymbol(req.getSymbol().name());
        order.setSide(req.getSide().name());
        order.setType(req.getType().name());
        order.setQuantity(req.getQuantity());
        order.setTradeQuantity(BigDecimal.ZERO);
        if (req.getType() == OrderType.LIMIT) {
            order.setPrice(req.getPrice());
        }
        order.setStatus(OrderStatus.INIT.name());
        order.setClientOrderNo(req.getClientOrderNo());
        baseMapper.insert(order);

        return order;
    }

    @Override
    public void updateOrder(Order order) {
        baseMapper.updateById(order);
    }

    @Override
    public List<Order> getBuyOrders() {
        QueryWrapper<Order> qw = new QueryWrapper<>();
        qw.eq("side", OrderSide.BUY.name()).eq("type", OrderType.LIMIT.name()).in("status", Arrays.asList(OrderStatus.NEW.name(), OrderStatus.PARTIALLY_FILLED.name()));
        return baseMapper.selectList(qw);
    }

    @Override
    public List<Order> getSellOrders() {
        QueryWrapper<Order> qw = new QueryWrapper<>();
        qw.eq("side", OrderSide.SELL.name()).eq("type", OrderType.LIMIT.name()).in("status", Arrays.asList(OrderStatus.NEW.name(), OrderStatus.PARTIALLY_FILLED.name()));
        return baseMapper.selectList(qw);
    }

    @Override
    public Order getOrder(String uid, String clientOrderNo) {
        QueryWrapper<Order> qw = new QueryWrapper<>();
        qw.eq("uid", uid).eq("client_order_no", clientOrderNo);
        return baseMapper.selectOne(qw);
    }
}
