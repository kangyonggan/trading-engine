package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
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
        boolean isBuy = makerOrder.getSide().equals(OrderSide.BUY.name());

        // 判断maker余额是否充足
        UserAccount makerAccount = userAccountService.getUserAccount(makerOrder.getUid(), AccountType.SPOT.name(),
                isBuy ? AppConstants.USDT : makerOrder.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY));

        BigDecimal makerNeedAmount;
        BigDecimal makerGiveAmount;
        BigDecimal makerFee = quantity.multiply(takerOrder.getPrice()).multiply(symbolConfig.getMakerFeeRate());
        if (isBuy) {
            // 需要U，手续费也是U
            makerNeedAmount = quantity.multiply(takerOrder.getPrice()).add(makerFee);
            // 获得B
            makerGiveAmount = quantity;
        } else {
            // 需要B
            makerNeedAmount = quantity;
            // 获得U，手续费是U
            makerGiveAmount = quantity.multiply(takerOrder.getPrice()).subtract(makerFee);
        }
        if (makerAccount.getTotalAmount().subtract(makerAccount.getFrozenAmount()).compareTo(makerNeedAmount) < 0) {
            log.info("账户余额不足，uid={}", makerAccount.getUid());
            return false;
        }
        makerAccount.setTotalAmount(makerAccount.getTotalAmount().subtract(makerNeedAmount));
        // 给maker减钱
        userAccountService.reduceAmount(String.valueOf(makerOrder.getId()), makerAccount.getUid(), makerAccount.getAccountType(), makerAccount.getCurrency(),
                makerNeedAmount, makerFee);
        // 给maker加钱
        userAccountService.addAmount(String.valueOf(makerOrder.getId()), makerAccount.getUid(), makerAccount.getAccountType(), takerOrder.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY),
                makerGiveAmount, makerFee);


        BigDecimal takerNeedAmount;
        BigDecimal takerGiveAmount;
        BigDecimal takerFee = quantity.multiply(takerOrder.getPrice()).multiply(symbolConfig.getTakerFeeRate());

        if (isBuy) {
            // 需要B
            takerNeedAmount = quantity;
            // 获得U，手续费也是U
            takerGiveAmount = quantity.multiply(takerOrder.getPrice()).subtract(takerFee);
        } else {
            // 需要U,手续费是U
            takerNeedAmount = quantity.multiply(takerOrder.getPrice()).subtract(takerFee);
            // 获得B
            takerGiveAmount = quantity;
        }

        // 给taker减钱
        userAccountService.reduceAmountFromFrozen(String.valueOf(takerOrder.getId()), takerOrder.getUid(), AccountType.SPOT.name(), takerOrder.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY),
                takerNeedAmount, takerFee);

        // 给taker加钱
        userAccountService.addAmount(String.valueOf(takerOrder.getId()), takerOrder.getUid(), AccountType.SPOT.name(), takerOrder.getSymbol().replace(AppConstants.USDT, StringUtils.EMPTY),
                takerGiveAmount, takerFee);

        Trade trade = new Trade();
        trade.setTakerOrderId(takerOrder.getId());
        trade.setMakerOrderId(makerOrder.getId());
        trade.setSymbol(takerOrder.getSymbol());
        trade.setQuantity(quantity);
        trade.setPrice(takerOrder.getPrice());
        trade.setTakerFee(symbolConfig.getTakerFeeRate().multiply(takerOrder.getPrice()).multiply(quantity).setScale(symbolConfig.getPriceScale(), RoundingMode.UP));
        trade.setMakerFee(makerFee);
        // 保存交易
        baseMapper.insert(trade);

        return true;
    }
}
