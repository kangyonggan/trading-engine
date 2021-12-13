package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class GetOrderReq implements Serializable {

    /**
     * 用户UID
     */
    @Valid(required = true, length = 8)
    private String uid;

    /**
     * 客户端订单号
     */
    @Valid(required = true, maxLength = 64)
    private String clientOrderNo;

}
