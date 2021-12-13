package com.kangyonggan.tradingEngine.util;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.components.BizException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author kyg
 */
public final class ValidUtil {

    private ValidUtil() {}

    /**
     * 字段校验
     *
     * @param object
     */
    public static void valid(Object object) {
        // 获取对象所有字段，包括父类字段
        List<Field> fields = Reflections.getAccessibleFields(object);

        // 遍历所有字段并校验
        for (Field field : fields) {
            Valid valid = field.getAnnotation(Valid.class);
            if (valid != null) {
                valid(object, field, valid);
            }
        }
    }

    /**
     * 字段校验
     *
     * @param object
     * @param fieldName
     */
    public static void valid(Object object, String fieldName) {
        Field field = Reflections.getAccessibleField(object, fieldName);
        Valid valid = field.getAnnotation(Valid.class);
        if (valid != null) {
            valid(object, field, valid);
        }
    }

    /**
     * 校验
     *
     * @param object
     * @param field
     * @param valid
     */
    private static void valid(Object object, Field field, Valid valid) {
        Object value = Reflections.getFieldValue(object, field.getName());
        if (value == null && valid.required()) {
            throw new BizException(ErrorCode.PARAMS_EMPTY, field.getName());
        }

        if (value instanceof String) {
            validString(field.getName(), (String) value, valid);
        } else if (value instanceof Integer) {
            validNumber(field.getName(), value, valid);
        } else if (value instanceof Long) {
            validNumber(field.getName(), value, valid);
        } else if (value instanceof BigDecimal) {
            validNumber(field.getName(), value, valid);
        } else if (value instanceof Float) {
            validNumber(field.getName(), value, valid);
        } else if (value instanceof Double) {
            validNumber(field.getName(), value, valid);
        } else if (value instanceof Short) {
            validNumber(field.getName(), value, valid);
        }
    }

    /**
     * 校验字符串
     *
     * @param filed
     * @param value
     * @param valid
     */
    private static void validString(String filed, String value, Valid valid) {
        if (StringUtils.isEmpty(value) && !valid.required()) {
            return;
        }
        // 校验长度
        if (valid.length() > 0 && value.length() != valid.length()) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 校验最小长度
        if (value.length() < valid.minLength()) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 校验最大长度
        if (value.length() > valid.maxLength()) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 校验正则表达式
        if (StringUtils.isNotEmpty(valid.regex()) && !value.matches(valid.regex())) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
    }

    /**
     * 校验数字类型
     *
     * @param filed
     * @param value
     * @param valid
     */
    private static void validNumber(String filed, Object value, Valid valid) {
        BigDecimal val = new BigDecimal(String.valueOf(value));
        BigDecimal gte = BigDecimal.valueOf(valid.gte());
        BigDecimal gt = BigDecimal.valueOf(valid.gt());
        BigDecimal lte = BigDecimal.valueOf(valid.lte());
        BigDecimal lt = BigDecimal.valueOf(valid.lt());

        // 大于等于
        if (val.compareTo(gte) < 0) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 大于
        if (val.compareTo(gt) <= 0) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 小于等于
        if (val.compareTo(lte) > 0) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
        // 小于
        if (val.compareTo(lt) >= 0) {
            throw new BizException(ErrorCode.PARAMS_ERROR, filed);
        }
    }

}
