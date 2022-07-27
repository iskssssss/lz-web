package cn.lz.web.core.model;

import java.util.Collection;

/**
 * @Version 版权 Copyright(c)2021 LZ
 * @ClassName:
 * @Descripton:
 * @Author: 孔胜
 * @Date: 2021/09/17 10:33
 */
public interface BaseResponse {

	/**
	 * 设置请求头信息
	 *
	 * @param name  键
	 * @param value 值
	 * @return 响应流
	 */
	BaseResponse setHeader(String name, String value);

	/**
	 * 添加请求头信息
	 *
	 * @param name      键
	 * @param valueList 值列表
	 * @return 响应流
	 */
	BaseResponse setHeader(String name, Collection<String> valueList);

	/**
	 * 追加请求头信息
	 *
	 * @param name  键
	 * @param value 值
	 * @return 响应流
	 */
	BaseResponse addHeader(String name, String value);

	/**
	 * 向客户端打印信息
	 *
	 * @param message 信息
	 */
	void print(String message);

	/**
	 * 向客户端打印信息
	 *
	 * @param bytes 字节数组
	 */
	void print(byte[] bytes);

	/**
	 * 向客户端打印信息
	 *
	 * @param bytes 字节数组
	 * @param off   偏移量
	 * @param len   长度
	 */
	void print(byte[] bytes, int off, int len);

	/**
	 * 获取响应流中的数据字节数组
	 *
	 * @return 数据字节数组
	 */
	byte[] getResponseDataBytes();

	/**
	 * 获取响应流响应状态
	 *
	 * @return 响应状态
	 */
	int getStatus();

	/**
	 * 设置响应流响应状态
	 *
	 * @param value 响应状态
	 */
	void setStatus(int value);

	/**
	 * 添加Cookie
	 *
	 * @param name   名称
	 * @param value  值
	 * @param path   地址
	 * @param domain 作用域
	 * @param expiry 过期时间
	 */
	void addCookie(String name, String value, String path, String domain, int expiry);

	/**
	 * 删除Cookie
	 *
	 * @param name 名称
	 */
	void removeCookie(String name);
}
