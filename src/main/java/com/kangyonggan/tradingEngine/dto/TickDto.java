package com.kangyonggan.tradingEngine.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author kyg
 */
@Data
public class TickDto implements Serializable {
    private Long id;
    private Long ts;
    private String symbol;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal open;
    private BigDecimal vol;
}
