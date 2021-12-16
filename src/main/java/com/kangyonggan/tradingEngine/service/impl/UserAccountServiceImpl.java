package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.AccountLogType;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.kangyonggan.tradingEngine.mapper.UserAccountMapper;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Autowired
    private IUserAccountLogService userAccountLogService;

    @Override
    public void frozenAmount(Order order) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(order.getSymbol());
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        BigDecimal amount = isBuy ? order.getQuantity().multiply(order.getPrice()) : order.getQuantity();
        BigDecimal fee = isBuy ? amount.multiply(symbolConfig.getTakerFeeRate()) : BigDecimal.ZERO;
        String currency = isBuy ? AppConstants.USDT : order.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY);
        UserAccount userAccount = getUserAccount(order.getUid(), AccountType.SPOT.name(), currency);

        userAccount.setFrozenAmount(userAccount.getFrozenAmount().add(amount).add(fee));
        baseMapper.updateById(userAccount);
    }

    @Override
    public void freeAmount(Order order) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(order.getSymbol());
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        BigDecimal amount = isBuy ? order.getQuantity().multiply(order.getPrice()) : order.getQuantity();
        BigDecimal fee = isBuy ? amount.multiply(symbolConfig.getTakerFeeRate()) : BigDecimal.ZERO;
        String currency = isBuy ? AppConstants.USDT : order.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY);
        UserAccount userAccount = getUserAccount(order.getUid(), AccountType.SPOT.name(), currency);

        userAccount.setFrozenAmount(userAccount.getFrozenAmount().subtract(amount).subtract(fee));
        baseMapper.updateById(userAccount);
    }

    @Override
    public UserAccount getUserAccount(String uid, String accountType, String currency) {
        QueryWrapper<UserAccount> qw = new QueryWrapper<>();
        qw.eq("uid", uid).eq("account_type", accountType).eq("currency", currency);
        return baseMapper.selectOne(qw);
    }

    @Override
    public void reduceAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(amount));
        baseMapper.updateById(userAccount);

        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(amount), AccountLogType.SPOT.name());

        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }
    }

    @Override
    public void addAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().add(amount));
        baseMapper.updateById(userAccount);

        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, amount, AccountLogType.SPOT.name());

        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }
    }

    @Override
    public void reduceAmountFromFrozen(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(amount));
        userAccount.setFrozenAmount(userAccount.getFrozenAmount().subtract(amount));
        baseMapper.updateById(userAccount);

        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(amount), AccountLogType.SPOT.name());
        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }
    }
}
