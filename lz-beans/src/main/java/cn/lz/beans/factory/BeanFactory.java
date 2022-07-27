package cn.lz.beans.factory;

import cn.lz.beans.exception.BeanException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 10:21
 */
public interface BeanFactory {

    /**
     * 创建bean
     *
     * @param tClass 类
     * @param <T>    类类型
     * @return bean
     */
    default <T> T createBean(Class<T> tClass) throws BeanException {
        return createBean(tClass, tClass.getSimpleName());
    }

    /**
     * 创建bean
     *
     * @param tClass   类
     * @param beanName bean名称
     * @param <T>      类类型
     * @return bean
     */
    <T> T createBean(Class<T> tClass, String beanName) throws BeanException;

    /**
     * 获取bean
     *
     * @param tClass 类
     * @param <T>    类类型
     * @return bean
     */
    <T> T getBean(Class<T> tClass) throws BeanException;

    /**
     * 获取bean
     *
     * @param beanName bean名称
     * @return bean
     */
    Object getBean(String beanName);

    /**
     * 根据注解获取bean
     *
     * @param annotation 注解
     * @return bean列表
     */
    Map<String, Object> getBeanByAnnotation(Class<? extends Annotation> annotation);
}
