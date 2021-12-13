package com.kangyonggan.tradingEngine.util;


import com.kangyonggan.tradingEngine.components.BizException;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author kyg
 */
public final class NumberUtil {

    private NumberUtil() {
    }

    /**
     * 生成n位随机数字字符串，首位不为0，允许一定的重复概率
     *
     * @param n
     * @return
     */
    public static String getNumberStr(int n) {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        int num;
        for (int i = 0; i < n; i++) {
            num = random.nextInt(10);
            // 首位去零
            if (str.length() == 0) {
                num = num % 9 + 1;
            }
            str.append(num);
        }

        return str.toString();
    }

    public static BigInteger hexToBigInteger(String data) {
        if (data.length() > 2) {
            if (data.charAt(0) == '0' && (data.charAt(1) == 'X' || data.charAt(1) == 'x')) {
                data = data.substring(2);
            }
            return new BigInteger(data, 16);
        }
        throw new BizException("hexToBigInteger,data:" + data);
    }
}
