package com.kangyonggan.tradingEngine.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class UserSecretReq implements Serializable {

    private Long id;

    private String uid;

    private String remark;

    private Long googleCode;

}
