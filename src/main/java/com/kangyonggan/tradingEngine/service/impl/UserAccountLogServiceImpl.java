package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.dto.req.AccountLogReq;
import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.kangyonggan.tradingEngine.mapper.UserAccountLogMapper;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户日志表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Service
public class UserAccountLogServiceImpl extends ServiceImpl<UserAccountLogMapper, UserAccountLog> implements IUserAccountLogService {

    @Override
    public void saveAccountLog(String uid, String orderNo, String accountType, String currency, BigDecimal amount, String type) {
        UserAccountLog log = new UserAccountLog();
        log.setUid(uid);
        log.setOrderNo(orderNo);
        log.setAccountType(accountType);
        log.setCurrency(currency);
        log.setAmount(amount);
        log.setType(type);
        baseMapper.insert(log);
    }

    @Override
    public Page<UserAccountLog> getAccountLogs(AccountLogReq<UserAccountLog> req) {
        QueryWrapper<UserAccountLog> qw = new QueryWrapper<>();
        qw.eq("uid", req.getUid()).eq("account_type", req.getAccountType().name()).eq("currency", req.getCurrency());
        return baseMapper.selectPage(req, qw);
    }
}
