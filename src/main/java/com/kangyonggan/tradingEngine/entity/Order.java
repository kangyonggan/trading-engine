package com.kangyonggan.tradingEngine.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户UID
     */
    private String uid;

    /**
     * 客户端订单号
     */
    private String clientOrderNo;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 买卖方向
     */
    private String side;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 委托价格
     */
    private BigDecimal price;

    /**
     * 委托数量
     */
    private BigDecimal quantity;

    /**
     * 成交数量
     */
    private BigDecimal tradeQuantity;

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

    public String getCurrency() {
        return symbol.replace(AppConstants.USDT, StringUtils.EMPTY);
    }

}
