package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
public interface IUserAccountService extends IService<UserAccount> {

    /**
     * 冻结资产
     *
     * @param order
     */
    void frozenAmount(Order order);

    /**
     * 释放冻结的资产
     *
     * @param order
     */
    void freeAmount(Order order);

    /**
     * 查询用户账户
     *
     * @param uid
     * @param accountType
     * @param currency
     * @return
     */
    UserAccount getUserAccount(String uid, String accountType, String currency);

    /**
     * 扣钱
     *
     * @param orderNo
     * @param uid
     * @param accountType
     * @param currency
     * @param amount
     * @param fee
     */
    void reduceAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee);

    /**
     * 加钱
     *
     * @param orderNo
     * @param uid
     * @param accountType
     * @param currency
     * @param amount
     * @param fee
     */
    void addAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee);

    /**
     * 减钱 -从冻结资金
     *
     * @param orderNo
     * @param uid
     * @param accountType
     * @param currency
     * @param amount
     * @param fee
     */
    void reduceAmountFromFrozen(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee);

}
