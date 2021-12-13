package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.OrderSide;
import com.kangyonggan.tradingEngine.constants.enums.OrderType;
import com.kangyonggan.tradingEngine.constants.enums.Symbol;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author kyg
 */
@Data
public class CreateOrderReq implements Serializable {

    /**
     * 用户UID
     */
    @Valid(required = true, length = 8)
    private String uid;

    /**
     * 交易对
     */
    @Valid(required = true, maxLength = 64)
    private Symbol symbol;

    /**
     * 客户端订单号
     */
    @Valid(required = true, maxLength = 64)
    private String clientOrderNo;

    /**
     * 买卖方向
     */
    @Valid(required = true)
    private OrderSide side;

    /**
     * 订单类型
     */
    @Valid(required = true)
    private OrderType type;

    /**
     * 委托价格
     */
    @Valid(gt = 0)
    private BigDecimal price;

    /**
     * 数量
     */
    @Valid(required = true, gt = 0)
    private BigDecimal quantity;

}
