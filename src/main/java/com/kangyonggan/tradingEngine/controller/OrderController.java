package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.dto.res.CancelOrderRes;
import com.kangyonggan.tradingEngine.dto.res.CreateOrderRes;
import com.kangyonggan.tradingEngine.dto.res.OrderRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.engine.TradingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kyg
 */
@RestController
@ApiVersion(1)
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    private TradingEngine tradingEngine;

    /**
     * 下单
     *
     * @param req
     * @return
     */
    @PostMapping
    public Result<CreateOrderRes> createOrder(@RequestBody CreateOrderReq req) {
        return Result.getSuccess(tradingEngine.createOrder(req));
    }

    /**
     * 撤销订单
     *
     * @param req
     * @return
     */
    @DeleteMapping
    public Result<CancelOrderRes> cancelOrder(@RequestBody CancelOrderReq req) {
        return Result.getSuccess(tradingEngine.cancelOrder(req));
    }

    /**
     * 查询订单
     *
     * @param req
     * @return
     */
    @GetMapping
    public Result<OrderRes> getOrder(GetOrderReq req) {
        return Result.getSuccess(tradingEngine.getOrder(req));
    }

    /**
     * 当前挂单
     *
     * @param req
     * @return
     */
    @GetMapping("openOrders")
    public Result<List<OrderRes>> openOrders(OpenOrderReq req) {
        return Result.getSuccess(tradingEngine.openOrders(req));
    }

    /**
     * 查询所有订单
     *
     * @param req
     * @return
     */
    @GetMapping("allOrders")
    public Result<List<OrderRes>> allOrders(AllOrderReq req) {
        return Result.getSuccess(tradingEngine.allOrders(req));
    }

}
