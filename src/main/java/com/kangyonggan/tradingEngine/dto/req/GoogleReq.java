package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class GoogleReq implements Serializable {

    @Valid(required = true)
    private String uid;

    @Valid(required = true, length = 6)
    private String emailCode;

    @Valid(required = true)
    private Long googleCode;

}
