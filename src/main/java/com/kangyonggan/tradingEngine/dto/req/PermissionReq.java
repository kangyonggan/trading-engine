package com.kangyonggan.tradingEngine.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class PermissionReq implements Serializable {

    private Long id;

    private String uid;

    private String remark;

    private String whiteList;

}
