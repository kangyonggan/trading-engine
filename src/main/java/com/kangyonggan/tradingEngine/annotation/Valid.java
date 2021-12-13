package com.kangyonggan.tradingEngine.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段校验
 *
 * @author kangyonggan
 * @since 8/9/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Valid {

    /**
     * 是否为必填项
     */
    boolean required() default false;

    /**
     * 长度。仅作用于String类型的字段，大于0时才生效
     */
    int length() default -1;

    /**
     * 最小长度。仅作用于String类型的字段
     */
    int minLength() default -1;

    /**
     * 最大长度。仅作用于String类型的字段
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * 正则表达式。仅作用于String类型的字段
     */
    String regex() default "";

    /**
     * 大于等于
     */
    long gte() default Long.MIN_VALUE;

    /**
     * 大于
     */
    long gt() default Long.MIN_VALUE;

    /**
     * 小于等于
     */
    long lte() default Long.MAX_VALUE;

    /**
     * 小于
     */
    long lt() default Long.MAX_VALUE;
}
