package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.CreateOrderReq;
import com.kangyonggan.tradingEngine.dto.res.CreateOrderRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.dto.res.SymbolRes;
import com.kangyonggan.tradingEngine.engine.TradingEngine;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kyg
 */
@RestController
@RequestMapping("/")
@ApiVersion(1)
public class ApiController extends BaseController {

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Autowired
    private TradingEngine tradingEngine;

    /**
     * 测试服务器连通性
     *
     * @return
     */
    @GetMapping("ping")
    @AnonymousAccess
    public Result<String> ping() {
        return Result.getSuccess("pong");
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    @GetMapping("time")
    @AnonymousAccess
    public Result<Long> time() {
        return Result.getSuccess(System.currentTimeMillis());
    }

    /**
     * 获取交易对
     *
     * @return
     */
    @GetMapping("symbol")
    @AnonymousAccess
    public Result<List<SymbolRes>> symbol() {
        return Result.getSuccess(symbolConfigService.getSymbolList());
    }

    /**
     * 下单
     *
     * @param req
     * @return
     */
    @PostMapping("order")
    @ApiAccess
    public Result<CreateOrderRes> createOrder(@RequestBody CreateOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.createOrder(req));
    }
}
