package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.Symbol;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class CancelOrderReq implements Serializable {

    /**
     * 用户UID
     */
    @Valid(required = true, length = 8)
    private String uid;

    /**
     * 客户端订单号
     */
    @Valid(maxLength = 64)
    private String clientOrderNo;

    /**
     * 交易对
     */
    private Symbol symbol;

}
