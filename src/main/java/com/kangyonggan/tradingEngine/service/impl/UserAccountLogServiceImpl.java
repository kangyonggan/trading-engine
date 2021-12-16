package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.kangyonggan.tradingEngine.mapper.UserAccountLogMapper;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public void saveAccountLog(String uid, String orderNo, String accountType, String symbol, BigDecimal amount, String type) {
        UserAccountLog log = new UserAccountLog();
        log.setUid(uid);
        log.setOrderNo(orderNo);
        log.setAccountType(accountType);
        log.setSymbol(symbol);
        log.setAmount(amount);
        log.setType(type);
        baseMapper.insert(log);
    }
}
