package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author kyg
 */
@Data
public class SymbolRes implements Serializable {

    private String symbol;
    private BigDecimal takerFeeRate;
    private BigDecimal makerFeeRate;
    private Integer priceScale;
    private Integer quantityScale;
    private Integer sort;
    private BigDecimal price;
    private BigDecimal rose;

}
