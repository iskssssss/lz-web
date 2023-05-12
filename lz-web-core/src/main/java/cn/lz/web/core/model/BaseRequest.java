package cn.lz.web.core.model;

import cn.lz.tool.reflect.BeanUtil;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @Version 版权 Copyright(c)2021 LZ
 * @ClassName:
 * @Descripton:
 * @Author: 孔胜
 * @Date: 2021/09/17 10:33
 */
public interface BaseRequest {

	/**
	 * 获取当前完整请求地址
	 *
	 * @return 完整请求地址
	 */
	String getRequestURL();

	/**
	 * 获取当前请求接口地址
	 *
	 * @return 请求接口地址
	 */
	String getRequestPath();

	/**
	 * 路径匹配
	 * <p>当前请求接口地址是否和{path}相匹配</p>
	 *
	 * @param path 待匹配地址
	 * @return 是否匹配
	 */
	boolean matchUrl(String path);

	/**
	 * 获取请求的HTTP方法名称
	 *
	 * @return HTTP方法名称
	 */
	String getMethod();

	/**
	 * 获取请求头中的信息
	 *
	 * @param name 键
	 * @return 值
	 */
	String getHeader(String name);

	/**
	 * 在请求中存储信息
	 *
	 * @param key   键
	 * @param value 值
	 */
	void setAttribute(String key, Object value);

	/**
	 * 从请求的存储中获取值
	 *
	 * @param key 键
	 * @return 值
	 */
	Object getAttribute(String key);

	/**
	 * 从请求的存储中获取值并转换为相应的类型
	 *
	 * @param key    键
	 * @param tClass 转换后的类型
	 * @param <V>    类型
	 * @return 值
	 */
	default <V> V getAttribute(String key, Class<V> tClass) {
		final Object attribute = this.getAttribute(key);
		if (attribute == null) {
			return null;
		}
		try {
			return (V) attribute;
		} catch (Exception e) {
			if (attribute instanceof CharSequence) {
				return BeanUtil.toBean(attribute.toString(), tClass);
			}
		}
		return null;
	}

	/**
	 * 获取请求的存储中所有键的名称
	 *
	 * @return 键的名称列表
	 */
	List<String> getAttributeNames();

	/**
	 * 从请求的存储中删除{key}值
	 *
	 * @param key 键
	 */
	void removeAttribute(String key);

	/**
	 * 删除所有请求中存储信
	 */
	default void removeAllAttribute() {
		final List<String> attributeNames = this.getAttributeNames();
		for (String attributeName : attributeNames) {
			this.removeAttribute(attributeName);
		}
	}

	/**
	 * 获取body中的数据
	 *
	 * @return 字节数组
	 */
	byte[] getBodyBytes();

	/**
	 * 从cookie中获取值
	 *
	 * @param name 键名
	 * @return 值
	 */
	String getCookieValue(String name);

	/**
	 * 获取发送请求的客户端的IP地址的字符串
	 *
	 * @return IP地址
	 */
	String getRemoteAddr();

	Map<String, List<String>> getParameters();

	Object[] injectData(Parameter[] parameters);

	Object injectData(Parameter parameter);

}
