package com.kangyonggan.tradingEngine.engine;

import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.OrderStatus;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.service.IOrderService;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kyg
 */
@Component
public class OrderBook {

    /**
     * 买盘
     */
    private final Map<String, List<Order>> buyOrderMap = new HashMap<>(16);

    /**
     * 卖盘
     */
    private final Map<String, List<Order>> sellOrderMap = new HashMap<>(16);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IUserAccountService userAccountService;

    /**
     * 初始化买盘和卖盘
     */
    @PostConstruct
    public void init() {
        List<Order> buyAllOrders = orderService.getBuyOrders();
        for (Order order : buyAllOrders) {
            saveOrder(order);
        }

        List<Order> sellAllOrders = orderService.getSellOrders();
        for (Order order : sellAllOrders) {
            saveOrder(order);
        }
    }

    /**
     * 获取买盘
     *
     * @param symbol
     * @return
     */
    public List<Order> getBuyOrders(String symbol) {
        List<Order> orders = buyOrderMap.getOrDefault(symbol, new ArrayList<>());
        return orders.size() > 15 ? orders.subList(0, 14) : orders;
    }

    /**
     * 获取卖
     *
     * @param symbol
     * @return
     */
    public List<Order> getSellOrders(String symbol) {
        List<Order> orders = sellOrderMap.getOrDefault(symbol, new ArrayList<>());
        return orders.size() > 15 ? orders.subList(0, 14) : orders;
    }

    /**
     * 保存订单
     *
     * @param order
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Order order) {
        userAccountService.frozenAmount(order);
        orderService.updateOrder(order);

        List<Order> orders;
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        if (isBuy) {
            orders = buyOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        } else {
            orders = sellOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        }
        if (orders.isEmpty()) {
            orders.add(order);
        } else {
            for (int i = 0; i < orders.size(); i++) {
                if ((isBuy && order.getPrice().compareTo(orders.get(i).getPrice()) > 0) || (!isBuy && order.getPrice().compareTo(orders.get(i).getPrice()) < 0)) {
                    int index = i - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    orders.add(index, order);
                    break;
                }
            }
        }
        if (isBuy) {
            buyOrderMap.put(order.getSymbol(), orders);
        } else {
            sellOrderMap.put(order.getSymbol(), orders);
        }
    }

    /**
     * 更新订单
     *
     * @param order
     */
    public void updateOrder(Order order) {
        List<Order> orders;
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        if (isBuy) {
            orders = buyOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        } else {
            orders = sellOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        }
        for (int i = 0; i < orders.size(); i++) {
            if (order.getId().equals(orders.get(i).getId())) {
                if (order.getQuantity().compareTo(order.getTradeQuantity()) == 0) {
                    // 完全成交
                    order.setStatus(OrderStatus.FILLED.name());
                    orderService.updateOrder(order);
                    orders.remove(i);
                    break;
                } else {
                    // 部分成交
                    order.setStatus(OrderStatus.PARTIALLY_FILLED.name());
                    orders.get(i).setTradeQuantity(order.getTradeQuantity());
                    orderService.updateOrder(order);
                }
            }
        }
        if (isBuy) {
            buyOrderMap.put(order.getSymbol(), orders);
        } else {
            sellOrderMap.put(order.getSymbol(), orders);
        }
    }

    /**
     * 获取卖一单
     *
     * @param symbol
     * @return
     */
    public Order getSellOrder(String symbol) {
        List<Order> sellOrders = sellOrderMap.getOrDefault(symbol, new ArrayList<>());
        return sellOrders.isEmpty() ? null : sellOrders.get(0);
    }

    /**
     * 获取买一单
     *
     * @param symbol
     * @return
     */
    public Order getBuyOrder(String symbol) {
        List<Order> buyOrders = buyOrderMap.getOrDefault(symbol, new ArrayList<>());
        return buyOrders.isEmpty() ? null : buyOrders.get(0);
    }

    /**
     * 撤销订单
     *
     * @param order
     */
    public void cancelOrder(Order order) {
        List<Order> orders;
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        if (isBuy) {
            orders = buyOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        } else {
            orders = sellOrderMap.getOrDefault(order.getSymbol(), new ArrayList<>());
        }
        for (int i = 0; i < orders.size(); i++) {
            if (order.getId().equals(orders.get(i).getId())) {
                orders.remove(i);
                break;
            }
        }
        if (isBuy) {
            buyOrderMap.put(order.getSymbol(), orders);
        } else {
            sellOrderMap.put(order.getSymbol(), orders);
        }
        synchronized (userAccountService.getLock(order.getUid(), AccountType.SPOT.name(), isBuy ? AppConstants.USDT : order.getCurrency())) {
            userAccountService.freeAmount(order);
        }
    }
}
