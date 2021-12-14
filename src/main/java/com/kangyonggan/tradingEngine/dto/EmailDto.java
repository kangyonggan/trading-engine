package com.kangyonggan.tradingEngine.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class EmailDto implements Serializable {

    private String type;
    private String to;
    private String verifyCode;

}
