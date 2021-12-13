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
 * 交易对配置表
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SymbolConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * Taker手续费率
     */
    private BigDecimal takerFeeRate;

    /**
     * Maker手续费率
     */
    private BigDecimal makerFeeRate;

    /**
     * 价格精度
     */
    private Integer priceScale;

    /**
     * 数量精度
     */
    private Integer quantityScale;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 可用
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
