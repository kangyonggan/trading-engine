package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.dto.res.SymbolRes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kyg
 */
@RestController
@RequestMapping("/")
@ApiVersion(1)
public class ApiController extends BaseController {

    @GetMapping("ping")
    @AnonymousAccess
    public Result<String> ping() {
        return Result.getSuccess("pong");
    }

    @GetMapping("time")
    @AnonymousAccess
    public Result<Long> time() {
        return Result.getSuccess(System.currentTimeMillis());
    }

    @GetMapping("symbol")
    @AnonymousAccess
    public Result<SymbolRes> symbol() {
        return Result.getSuccess();
    }

}
