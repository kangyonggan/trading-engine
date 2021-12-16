package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class AccountRes implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * UID
     */
    private String uid;

    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 总资产
     */
    private BigDecimal totalAmount;

    /**
     * 冻结资产
     */
    private BigDecimal frozenAmount;

    /**
     * 是否可用
     */
    private Integer enable;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
