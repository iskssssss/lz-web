package cn.lz.beans.bean;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/25 14:10
 */
public interface FactoryBean<T> {

    T getBean();
}
