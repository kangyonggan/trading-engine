package com.kangyonggan.tradingEngine.engine;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.OrderStatus;
import com.kangyonggan.tradingEngine.constants.enums.OrderType;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.dto.res.CancelOrderRes;
import com.kangyonggan.tradingEngine.dto.res.CreateOrderRes;
import com.kangyonggan.tradingEngine.dto.res.OrderRes;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.service.IOrderService;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.ITradeService;
import com.kangyonggan.tradingEngine.util.ValidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author kyg
 */
@Component
public class TradingEngine {

    private final Map<String, Object> lockMap = new HashMap<>(16);

    @Autowired
    private OrderBook orderBook;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private ISymbolConfigService symbolConfigService;

    /**
     * 下单
     *
     * @param req
     * @return
     */
    @Valid
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderRes createOrder(CreateOrderReq req) {
        checkOrder(req);

        // 保存委托单
        Order order = orderService.saveOrder(req);

        if (req.getSide() == OrderSide.BUY) {
            // 处理买单
            processBuyOrder(order);
        } else {
            // 处理卖单
            processSellOrder(order);
        }

        CreateOrderRes res = new CreateOrderRes();
        res.setOrderId(order.getId());
        res.setClientOrderNo(req.getClientOrderNo());
        res.setStatus(order.getStatus());
        return res;
    }

    /**
     * 查询订单
     *
     * @param req
     * @return
     */
    @Valid
    public OrderRes getOrder(GetOrderReq req) {
        Order order = orderService.getOrder(req.getUid(), req.getClientOrderNo());
        if (order == null) {
            throw new BizException(ErrorCode.ORDER_NOT_EXISTS);
        }
        OrderRes res = new OrderRes();
        BeanUtils.copyProperties(order, res);
        res.setOrderId(order.getId());
        return res;
    }

    /**
     * 撤销订单
     *
     * @param req
     * @return
     */
    @Valid
    public CancelOrderRes cancelOrder(CancelOrderReq req) {
        CancelOrderRes res = new CancelOrderRes();
        if (StringUtils.isEmpty(req.getClientOrderNo()) && req.getSymbol() == null) {
            throw new BizException(ErrorCode.PARAMS_EMPTY, "clientOrderNo|symbol");
        }
        List<Order> orders = orderService.getOrder(req.getUid(), req.getClientOrderNo(), req.getSymbol().name());
        List<String> clientOrderNos = new ArrayList<>();
        for (Order order : orders) {
            if (Arrays.asList(OrderStatus.CANCELED.name(), OrderStatus.FAILURE.name(), OrderStatus.FILLED.name()).contains(order.getStatus())) {
                continue;
            }
            order.setStatus(OrderStatus.CANCELED.name());

            synchronized (getLock(order.getSymbol())) {
                orderBook.cancelOrder(order);
            }
            clientOrderNos.add(order.getClientOrderNo());
        }
        res.setClientOrderNos(clientOrderNos);
        return res;
    }

    /**
     * 查询当前挂单
     *
     * @param req
     * @return
     */
    @Valid
    public List<OrderRes> openOrders(OpenOrderReq req) {
        List<Order> orders = orderService.getOpenOrders(req);
        return toOrderResList(orders);
    }

    /**
     * 查询全部订单
     *
     * @param req
     * @return
     */
    @Valid
    public List<OrderRes> allOrders(AllOrderReq req) {
        List<Order> orders = orderService.getAllOrders(req);
        return toOrderResList(orders);
    }

    /**
     * 查询买盘
     *
     * @param symbol
     * @return
     */
    public List<OrderRes> getBuyOrders(String symbol) {
        List<Order> orders = orderBook.getBuyOrders(symbol);
        return toOrderResList(orders);
    }

    /**
     * 查询卖盘
     *
     * @param symbol
     * @return
     */
    public List<OrderRes> getSellOrders(String symbol) {
        List<Order> orders = orderBook.getSellOrders(symbol);
        return toOrderResList(orders);
    }

    /**
     * 处理买单
     *
     * @param order
     */
    private void processBuyOrder(Order order) {
        synchronized (getLock(order.getSymbol())) {
            // 剩余可成交数量
            BigDecimal freeQuantity = order.getQuantity().subtract(order.getTradeQuantity());
            while (freeQuantity.compareTo(BigDecimal.ZERO) > 0) {
                Order sellOrder = orderBook.getSellOrder(order.getSymbol());
                if (sellOrder == null || (order.getType().equals(OrderType.LIMIT.name()) && order.getPrice().compareTo(sellOrder.getPrice()) < 0)) {
                    // 卖盘为空 或者 委托价小于卖一价
                    break;
                }

                BigDecimal freeSellQuantity = sellOrder.getQuantity().subtract(sellOrder.getTradeQuantity());
                // 和卖一成交
                BigDecimal tradeQuantity = freeQuantity.compareTo(freeSellQuantity) > 0 ? freeSellQuantity : freeQuantity;
                sellOrder.setTradeQuantity(sellOrder.getTradeQuantity().add(tradeQuantity));
                // 更新卖盘
                orderBook.updateOrder(sellOrder);
                // 保存交易
                tradeService.saveTrade(sellOrder, order, tradeQuantity);
                freeQuantity = freeQuantity.subtract(tradeQuantity);
            }

            // 更新订单
            order.setTradeQuantity(order.getQuantity().subtract(freeQuantity));
            OrderStatus status = OrderStatus.PARTIALLY_FILLED;
            if (freeQuantity.compareTo(BigDecimal.ZERO) == 0) {
                status = OrderStatus.FILLED;
            } else if (freeQuantity.compareTo(order.getQuantity()) == 0) {
                if (order.getType().equals(OrderType.LIMIT.name())) {
                    status = OrderStatus.NEW;
                } else {
                    status = OrderStatus.FAILURE;
                }
            }
            order.setStatus(status.name());
            orderService.updateOrder(order);

            if (status != OrderStatus.FILLED && order.getType().equals(OrderType.LIMIT.name())) {
                // 还有部分没成交，放入买盘
                orderBook.saveOrder(order);
            }
        }
    }

    /**
     * 处理卖单
     *
     * @param order
     */
    private void processSellOrder(Order order) {
        synchronized (getLock(order.getSymbol())) {
            // 剩余可成交数量
            BigDecimal freeQuantity = order.getQuantity().subtract(order.getTradeQuantity());
            while (freeQuantity.compareTo(BigDecimal.ZERO) > 0) {
                Order buyOrder = orderBook.getBuyOrder(order.getSymbol());
                if (buyOrder == null || (order.getType().equals(OrderType.LIMIT.name()) && order.getPrice().compareTo(buyOrder.getPrice()) > 0)) {
                    // 买盘为空 或者 委托价大于买一价
                    break;
                }

                BigDecimal freeBuyQuantity = buyOrder.getQuantity().subtract(buyOrder.getTradeQuantity());
                // 和买一成交
                BigDecimal tradeQuantity = freeQuantity.compareTo(freeBuyQuantity) > 0 ? freeBuyQuantity : freeQuantity;
                buyOrder.setTradeQuantity(buyOrder.getTradeQuantity().add(tradeQuantity));
                // 更新买盘
                orderBook.updateOrder(buyOrder);
                // 保存交易
                tradeService.saveTrade(buyOrder, order, tradeQuantity);
                freeQuantity = freeQuantity.subtract(tradeQuantity);
            }

            // 更新订单
            order.setTradeQuantity(order.getQuantity().subtract(freeQuantity));
            OrderStatus status = OrderStatus.PARTIALLY_FILLED;
            if (freeQuantity.compareTo(BigDecimal.ZERO) == 0) {
                status = OrderStatus.FILLED;
            } else if (freeQuantity.compareTo(order.getQuantity()) == 0) {
                if (order.getType().equals(OrderType.LIMIT.name())) {
                    status = OrderStatus.NEW;
                } else {
                    status = OrderStatus.FAILURE;
                }
            }
            order.setStatus(status.name());
            orderService.updateOrder(order);

            if (status != OrderStatus.FILLED && order.getType().equals(OrderType.LIMIT.name())) {
                // 还有部分没成交，放入买盘
                orderBook.saveOrder(order);
            }
        }
    }

    /**
     * 获取锁
     *
     * @param symbol
     * @return
     */
    private Object getLock(String symbol) {
        // 同一交易对，不区分买卖，都使用同一把锁
        Object lock = lockMap.get(symbol);
        if (lock == null) {
            synchronized (lockMap) {
                lock = lockMap.computeIfAbsent(symbol, k -> new Object());
            }
        }
        return lock;
    }

    /**
     * 下单校验
     *
     * @param req
     */
    private void checkOrder(CreateOrderReq req) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(req.getSymbol().name());

        // 限价单必须有价格
        if (req.getType() == OrderType.LIMIT) {
            if (req.getPrice() == null) {
                throw new BizException(ErrorCode.PARAMS_ERROR, "price");
            }
            // 价格精度截取
            req.setPrice(req.getPrice().setScale(symbolConfig.getPriceScale(), RoundingMode.DOWN));
            ValidUtil.valid(req, "price");
        }

        // 数量精度截取
        req.setQuantity(req.getQuantity().setScale(symbolConfig.getQuantityScale(), RoundingMode.DOWN));
        ValidUtil.valid(req, "quantity");

        // 订单重复
        if (orderService.getOrder(req.getUid(), req.getClientOrderNo()) != null) {
            throw new BizException(ErrorCode.ORDER_REPETITION);
        }
    }

    /**
     * Order 转 OrderRes
     *
     * @param orders
     * @return
     */
    private List<OrderRes> toOrderResList(List<Order> orders) {
        List<OrderRes> resOrders = new ArrayList<>(orders.size());
        for (Order order : orders) {
            OrderRes res = new OrderRes();
            BeanUtils.copyProperties(order, res);
            res.setOrderId(order.getId());
            resOrders.add(res);
        }
        return resOrders;
    }
}
