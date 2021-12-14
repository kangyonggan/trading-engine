package com.kangyonggan.tradingEngine.constants.enums;

import lombok.Getter;

/**
 * @author kyg
 */
public enum Enable {

    /**
     * 可用
     */
    YES(1),

    /**
     * 不可用
     */
    NO(0);

    @Getter
    private final int value;

    Enable(int value) {
        this.value = value;
    }
}
