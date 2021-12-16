package com.kangyonggan.tradingEngine.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kangyonggan.tradingEngine.constants.enums.AccountType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class AccountLogReq<T> extends Page<T> implements Serializable {

    private String uid;
    private AccountType accountType;
    private String currency;

}
