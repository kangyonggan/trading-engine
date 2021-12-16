package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.dto.req.AccountLogReq;
import com.kangyonggan.tradingEngine.entity.UserAccountLog;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户日志表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
public interface IUserAccountLogService extends IService<UserAccountLog> {

    /**
     * 保存账户日志
     *
     * @param uid
     * @param orderNo
     * @param accountType
     * @param currency
     * @param amount
     * @param type
     */
    void saveAccountLog(String uid, String orderNo, String accountType, String currency, BigDecimal amount, String type);

    /**
     * 用户账户流水
     *
     * @param req
     * @return
     */
    Page<UserAccountLog> getAccountLogs(AccountLogReq<UserAccountLog> req);
}
