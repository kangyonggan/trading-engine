package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class UserLogoutReq implements Serializable {

    @Valid(required = true, length = 8)
    private String uid;

    @Valid(required = true, maxLength = 128)
    private String token;

}
