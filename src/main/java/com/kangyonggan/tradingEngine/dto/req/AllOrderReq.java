package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.Symbol;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class AllOrderReq implements Serializable {

    /**
     * 用户UID
     */
    @Valid(required = true, length = 8)
    private String uid;

    /**
     * 交易对
     */
    @Valid(required = true)
    private Symbol symbol;

    /**
     * 最小订单ID
     */
    @Valid(required = true)
    private Long orderId;

    /**
     * 分页大小
     */
    @Valid(required = true, lte = 1000, gte = 10)
    private Integer limit = 500;

}
