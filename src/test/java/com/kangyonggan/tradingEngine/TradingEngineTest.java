package com.kangyonggan.tradingEngine;

import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.OrderType;
import com.kangyonggan.tradingEngine.constants.enums.Symbol;
import com.kangyonggan.tradingEngine.dto.req.CreateOrderReq;
import com.kangyonggan.tradingEngine.dto.req.GetOrderReq;
import com.kangyonggan.tradingEngine.engine.OrderBook;
import com.kangyonggan.tradingEngine.engine.TradingEngine;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * @author kyg
 */
public class TradingEngineTest extends AbstractTest {

    @Autowired
    private TradingEngine tradingEngine;

    @Autowired
    private OrderBook orderBook;

    @Test
    public void createOrderLimitBuy() {
        CreateOrderReq req = new CreateOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo(String.valueOf(System.currentTimeMillis()));
        req.setSymbol(Symbol.ETHUSDT);
        req.setType(OrderType.LIMIT);
        req.setSide(OrderSide.BUY);
        req.setQuantity(BigDecimal.ONE);
        req.setPrice(BigDecimal.ONE);
        LOGGER.info("下单返回：{}", tradingEngine.createOrder(req));
    }

    @Test
    public void createOrderMarketBuy() {
        CreateOrderReq req = new CreateOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo(String.valueOf(System.currentTimeMillis()));
        req.setSymbol(Symbol.ETHUSDT);
        req.setType(OrderType.MARKET);
        req.setSide(OrderSide.BUY);
        req.setQuantity(BigDecimal.valueOf(0.3));
        LOGGER.info("下单返回：{}", tradingEngine.createOrder(req));
    }

    @Test
    public void createOrderLimitSell() {
        CreateOrderReq req = new CreateOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo(String.valueOf(System.currentTimeMillis()));
        req.setSymbol(Symbol.ETHUSDT);
        req.setType(OrderType.LIMIT);
        req.setSide(OrderSide.SELL);
        req.setQuantity(BigDecimal.ONE);
        req.setPrice(BigDecimal.valueOf(2));
        LOGGER.info("下单返回：{}", tradingEngine.createOrder(req));
    }

    @Test
    public void createOrderMarketSell() {
        CreateOrderReq req = new CreateOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo(String.valueOf(System.currentTimeMillis()));
        req.setSymbol(Symbol.ETHUSDT);
        req.setType(OrderType.MARKET);
        req.setSide(OrderSide.SELL);
        req.setQuantity(BigDecimal.valueOf(0.4));
        LOGGER.info("下单返回：{}", tradingEngine.createOrder(req));
    }

    @Test
    public void getOrder() {
        GetOrderReq req = new GetOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo("1639399633157");
        LOGGER.info("查询订单：{}", tradingEngine.getOrder(req));
    }

    @Test
    public void cancelOrder() {
        GetOrderReq req = new GetOrderReq();
        req.setUid("13245678");
        req.setClientOrderNo("1639399904451");
        tradingEngine.cancelOrder(req);
    }

    @Test
    public void getBuySellOrders() {
        LOGGER.info("买盘：{}", orderBook.getBuyOrders(Symbol.ETHUSDT.name()));
        LOGGER.info("卖盘：{}", orderBook.getSellOrders(Symbol.ETHUSDT.name()));
    }

}
