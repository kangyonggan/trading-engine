package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.AccountLogType;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.TradeStatus;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.kangyonggan.tradingEngine.mapper.UserAccountMapper;
import com.kangyonggan.tradingEngine.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, Object> lockMap = new HashMap<>(16);

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Autowired
    private IUserAccountLogService userAccountLogService;

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private IOrderService orderService;

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
    @Transactional(rollbackFor = Exception.class)
    public void freeAmount(Order order) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(order.getSymbol());
        boolean isBuy = order.getSide().equals(OrderSide.BUY.name());
        BigDecimal amount = isBuy ? order.getQuantity().multiply(order.getPrice()) : order.getQuantity();
        BigDecimal fee = isBuy ? amount.multiply(symbolConfig.getTakerFeeRate()) : BigDecimal.ZERO;
        String currency = isBuy ? AppConstants.USDT : order.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY);
        UserAccount userAccount = getUserAccount(order.getUid(), AccountType.SPOT.name(), currency);

        userAccount.setFrozenAmount(userAccount.getFrozenAmount().subtract(amount).subtract(fee));
        baseMapper.updateById(userAccount);
        orderService.updateOrder(order);
    }

    @Override
    public UserAccount getUserAccount(String uid, String accountType, String currency) {
        QueryWrapper<UserAccount> qw = new QueryWrapper<>();
        qw.eq("uid", uid).eq("account_type", accountType).eq("currency", currency);
        return baseMapper.selectOne(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(amount));
        baseMapper.updateById(userAccount);

        // 交易流水
        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(amount), AccountLogType.SPOT.name());

        // 手续费流水
        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }

        // 更新交易状态
        tradeService.updateTradeStatus(Long.parseLong(orderNo), TradeStatus.MAKER_REDUCE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAmount(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee, TradeStatus tradeStatus) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().add(amount));
        baseMapper.updateById(userAccount);

        // 交易流水
        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, amount, AccountLogType.SPOT.name());

        // 手续费流水
        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }

        // 更新交易状态
        tradeService.updateTradeStatus(Long.parseLong(orderNo), tradeStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceAmountFromFrozen(String orderNo, String uid, String accountType, String currency, BigDecimal amount, BigDecimal fee) {
        UserAccount userAccount = getUserAccount(uid, accountType, currency);
        userAccount.setTotalAmount(userAccount.getTotalAmount().subtract(amount));
        userAccount.setFrozenAmount(userAccount.getFrozenAmount().subtract(amount));
        baseMapper.updateById(userAccount);

        // 交易流水
        userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(amount), AccountLogType.SPOT.name());

        // 手续费流水
        if (BigDecimal.ZERO.compareTo(fee) != 0) {
            userAccountLogService.saveAccountLog(uid, orderNo, accountType, currency + AppConstants.USDT, BigDecimal.ZERO.subtract(fee), AccountLogType.FEE.name());
        }

        // 更新交易状态
        tradeService.updateTradeStatus(Long.parseLong(orderNo), TradeStatus.TAKER_REDUCE);
    }

    @Override
    public Object getLock(String uid, String accountType, String currency) {
        Object lock = lockMap.get(uid + accountType + currency);
        if (lock == null) {
            synchronized (lockMap) {
                lock = lockMap.computeIfAbsent(uid + accountType + currency, k -> new Object());
            }
        }
        return lock;
    }
}
