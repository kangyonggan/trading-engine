package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.TradeStatus;
import com.kangyonggan.tradingEngine.entity.Order;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.entity.Trade;
import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.kangyonggan.tradingEngine.mapper.TradeMapper;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import com.kangyonggan.tradingEngine.service.ITradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 交易表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Service
@Slf4j
public class TradeServiceImpl extends ServiceImpl<TradeMapper, Trade> implements ITradeService {

    @Autowired
    private ISymbolConfigService symbolConfigService;

    @Autowired
    private IUserAccountService userAccountService;

    @Override
    public boolean saveTrade(Order takerOrder, Order makerOrder, BigDecimal quantity) {
        SymbolConfig symbolConfig = symbolConfigService.getSymbolConfig(takerOrder.getSymbol());
        BigDecimal makerFee = quantity.multiply(takerOrder.getPrice()).multiply(symbolConfig.getMakerFeeRate());
        BigDecimal takerFee = quantity.multiply(takerOrder.getPrice()).multiply(symbolConfig.getTakerFeeRate());

        Trade trade = new Trade();
        trade.setTakerOrderId(takerOrder.getId());
        trade.setMakerOrderId(makerOrder.getId());
        trade.setSymbol(takerOrder.getSymbol());
        trade.setQuantity(quantity);
        trade.setPrice(takerOrder.getPrice());
        trade.setTakerFee(takerFee);
        trade.setMakerFee(makerFee);
        trade.setStatus(TradeStatus.INIT.name());
        // 保存交易
        baseMapper.insert(trade);

        // maker是否是买单
        boolean isBuy = makerOrder.getSide().equals(OrderSide.BUY.name());
        // maker出账币种
        String makerReduceCurrency = isBuy ? AppConstants.USDT : makerOrder.getCurrency();
        // maker入账币种
        String makerAddCurrency = isBuy ? makerOrder.getCurrency() : AppConstants.USDT;
        // taker出账币种
        String takerReduceCurrency = isBuy ? makerOrder.getCurrency() : AppConstants.USDT;
        // taker入账币种
        String takerAddCurrency = isBuy ? AppConstants.USDT : makerOrder.getCurrency();

        BigDecimal makerReduceAmount;
        BigDecimal makerAddAmount;
        synchronized (userAccountService.getLock(makerOrder.getUid(), AccountType.SPOT.name(), makerReduceCurrency)) {
            // 判断maker出账账户余额是否充足
            UserAccount makerReduceAccount = userAccountService.getUserAccount(makerOrder.getUid(), AccountType.SPOT.name(), makerReduceCurrency);

            if (isBuy) {
                // 主动买，需要U，手续费也是U
                makerReduceAmount = quantity.multiply(takerOrder.getPrice()).add(makerFee);
                // 主动买，获得B
                makerAddAmount = quantity;
            } else {
                // 主动卖，需要B
                makerReduceAmount = quantity;
                // 主动卖，获得U，手续费是U
                makerAddAmount = quantity.multiply(takerOrder.getPrice()).subtract(makerFee);
            }
            if (makerReduceAccount.getTotalAmount().subtract(makerReduceAccount.getFrozenAmount()).compareTo(makerReduceAmount) < 0) {
                log.info("账户余额不足，无法吃单，uid={}，currency={}", makerReduceAccount.getUid(), makerReduceCurrency);
                return false;
            }
            // 给maker减钱
            userAccountService.reduceAmount(String.valueOf(trade.getId()), makerOrder.getUid(), AccountType.SPOT.name(), makerReduceCurrency, makerReduceAmount, isBuy ? makerFee : BigDecimal.ZERO);
        }
        // 给maker加钱
        userAccountService.addAmount(String.valueOf(trade.getId()), makerOrder.getUid(), AccountType.SPOT.name(), makerAddCurrency, makerAddAmount, isBuy ? BigDecimal.ZERO : makerFee, TradeStatus.MAKER_REDUCE);

        BigDecimal takerReduceAmount;
        BigDecimal takerAddAmount;
        if (isBuy) {
            // 被动卖，需要B
            takerReduceAmount = quantity;
            // 被动卖，获得U，手续费也是U
            takerAddAmount = quantity.multiply(takerOrder.getPrice()).subtract(takerFee);
        } else {
            // 被动卖，需要U,手续费是U
            takerReduceAmount = quantity.multiply(takerOrder.getPrice()).add(takerFee);
            // 被动卖，获得B
            takerAddAmount = quantity;
        }

        // 给taker减钱
        userAccountService.reduceAmountFromFrozen(String.valueOf(trade.getId()), takerOrder.getUid(), AccountType.SPOT.name(), takerReduceCurrency, takerReduceAmount, isBuy ? BigDecimal.ZERO : takerFee);

        // 给taker加钱
        userAccountService.addAmount(String.valueOf(trade.getId()), takerOrder.getUid(), AccountType.SPOT.name(), takerAddCurrency, takerAddAmount, isBuy ? takerFee : BigDecimal.ZERO, TradeStatus.TAKER_ADD);
        return true;
    }

    @Override
    public void updateTradeStatus(long id, TradeStatus status) {
        Trade trade = new Trade();
        trade.setId(id);
        trade.setStatus(status.name());
        baseMapper.updateById(trade);
    }
}
