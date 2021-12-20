package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.ApiAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.AllOrderReq;
import com.kangyonggan.tradingEngine.dto.req.CancelOrderReq;
import com.kangyonggan.tradingEngine.dto.req.GetOrderReq;
import com.kangyonggan.tradingEngine.dto.req.OpenOrderReq;
import com.kangyonggan.tradingEngine.dto.res.CancelOrderRes;
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
@RequestMapping("order11")
public class OrderController extends BaseController {

    @Autowired
    private TradingEngine tradingEngine;

    /**
     * 撤销订单
     *
     * @param req
     * @return
     */
    @DeleteMapping
    @ApiAccess
    public Result<CancelOrderRes> cancelOrder(@RequestBody CancelOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.cancelOrder(req));
    }

    /**
     * 查询订单
     *
     * @param req
     * @return
     */
    @GetMapping
    @ApiAccess
    public Result<OrderRes> getOrder(GetOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.getOrder(req));
    }

    /**
     * 当前挂单
     *
     * @param req
     * @return
     */
    @GetMapping("openOrders")
    @ApiAccess
    public Result<List<OrderRes>> openOrders(OpenOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.openOrders(req));
    }

    /**
     * 查询所有订单
     *
     * @param req
     * @return
     */
    @GetMapping("allOrders")
    @ApiAccess
    public Result<List<OrderRes>> allOrders(AllOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.allOrders(req));
    }

}
