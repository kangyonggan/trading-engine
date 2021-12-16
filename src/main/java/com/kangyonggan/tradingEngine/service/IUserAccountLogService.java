package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * @param symbol
     * @param amount
     * @param type
     */
    void saveAccountLog(String uid, String orderNo, String accountType, String symbol, BigDecimal amount, String type);
}
