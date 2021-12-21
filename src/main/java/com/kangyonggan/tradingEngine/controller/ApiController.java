package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.dto.res.*;
import com.kangyonggan.tradingEngine.engine.TradingEngine;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
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

    @Autowired
    private IUserAccountService userAccountService;

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
     * 账户信息
     *
     * @return
     */
    @GetMapping("account")
    @ApiAccess
    public Result<List<AccountRes>> account() {
        return Result.getSuccess(userAccountService.getAccounts(currentApiUid()));
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

    /**
     * 订单查询
     *
     * @param req
     * @return
     */
    @GetMapping("order")
    @ApiAccess
    public Result<OrderRes> getOrder(GetOrderReq req) {
        req.setUid(currentApiUid());
        return Result.getSuccess(tradingEngine.getOrder(req));
    }

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
