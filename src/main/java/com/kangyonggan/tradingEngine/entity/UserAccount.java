package com.kangyonggan.tradingEngine.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户账户表
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
