package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class OrderRes implements Serializable {
    private Long orderId;
    private String uid;
    private String clientOrderNo;
    private String symbol;
    private String side;
    private String type;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal tradeQuantity;
    private String status;
    private LocalDateTime createTime;
}
