package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class TradeRes implements Serializable {

    private String symbol;
    private String price;
    private String quantity;
    private String makerSide;
    private LocalDateTime createTime;

}
