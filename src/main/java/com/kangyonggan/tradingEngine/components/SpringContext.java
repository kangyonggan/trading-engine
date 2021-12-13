package com.kangyonggan.tradingEngine.components;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author kyg
 */
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static void autowire(Object bean) {
        context.getAutowireCapableBeanFactory().autowireBean(bean);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clzz) {
        return context.getBean(clzz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationClass) {
        return context.getBeansWithAnnotation(annotationClass);
    }
}
