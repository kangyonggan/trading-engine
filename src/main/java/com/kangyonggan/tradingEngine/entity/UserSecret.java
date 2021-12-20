package com.kangyonggan.tradingEngine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户密钥表
 * </p>
 *
 * @author mbg
 * @since 2021-12-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserSecret implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * UID
     */
    private String uid;

    /**
     * 类型
     */
    private String type;

    /**
     * 私钥
     */
    private String priKey;

    /**
     * 公钥
     */
    private String pubKey;

    /**
     * 备注
     */
    private String remark;

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
