package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author kyg
 */
@Data
public class CancelOrderRes implements Serializable {

    /**
     * 撤销成功的客户端订单号
     */
    private List<String> clientOrderNos;

}
