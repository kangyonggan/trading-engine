package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class CreateOrderRes implements Serializable {
    private String clientOrderNo;
    private Long orderId;
    private String status;
}
