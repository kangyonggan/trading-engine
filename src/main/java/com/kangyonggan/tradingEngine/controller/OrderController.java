package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.engine.TradingEngine;
import com.kangyonggan.tradingEngine.dto.req.CreateOrderReq;
import com.kangyonggan.tradingEngine.dto.res.CreateOrderRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
