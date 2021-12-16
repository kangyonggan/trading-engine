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
 * 交易表
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Taker订单ID
     */
    private Long takerOrderId;

    /**
     * Maker订单ID
     */
    private Long makerOrderId;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 成交价格
     */
    private BigDecimal price;

    /**
     * 成交数量
     */
    private BigDecimal quantity;

    /**
     * Taker手续费
     */
    private BigDecimal takerFee;

    /**
     * Maker手续费
     */
    private BigDecimal makerFee;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
