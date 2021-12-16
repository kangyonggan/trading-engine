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
 * 用户账户日志表
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserAccountLog implements Serializable {

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
     * 订单号
     */
    private String orderNo;

    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 类型
     */
    private String type;

    /**
     * 金额
     */
    private BigDecimal amount;

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
