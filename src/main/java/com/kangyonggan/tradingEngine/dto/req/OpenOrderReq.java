package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.Symbol;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class OpenOrderReq implements Serializable {

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

}
