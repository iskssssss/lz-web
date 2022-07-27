package cn.lz.web.core.model;

import cn.lz.tool.core.bytes.ByteUtil;
import cn.lz.tool.core.string.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/7 9:51
 */
public class HttpResponse implements BaseResponse {
    private final HttpHeaders headers;
    private final DefaultFullHttpResponse response;
    private final ChannelHandlerContext channelHandlerContext;

    public HttpResponse(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        headers = response.headers();
    }

    @Override
    public BaseResponse setHeader(String name, String value) {
        headers.set(name, value);
        return this;
    }

    @Override
    public BaseResponse setHeader(String name, Collection<String> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return this;
        }
        String header = headers.get(name);
        if (StringUtil.isEmpty(header)) {
            this.setHeader(name, String.join(",", new HashSet<>(valueList)));
            return this;
        }
        String[] split = header.split(",");
        Set<String> valueSet = Arrays.stream(split).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
        valueSet.addAll(valueList);
        this.setHeader(name, String.join(",", valueSet));
        return this;
    }

    @Override
    public BaseResponse addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public void print(String message) {
        final byte[] bytes = ByteUtil.toBytes(message);
        if (StringUtil.isEmpty(bytes)) {
            return;
        }
        this.print(bytes);
    }

    @Override
    public void print(byte[] bytes) {
        this.print(bytes, 0, bytes.length);
    }

    @Override
    public void print(byte[] bytes, int off, int len) {
        if (StringUtil.isEmpty(bytes)) {
            return;
        }
        final int length = bytes.length;
        if (off < 0 || off > length || len < 0 || len > length) {
            return;
        }
        ByteBuf content = response.content();
        content.writeBytes(bytes, off, len);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public byte[] getResponseDataBytes() {
        return response.content().copy().array();
    }

    @Override
    public int getStatus() {
        return response.status().code();
    }

    @Override
    public void setStatus(int value) {
        response.setStatus(HttpResponseStatus.valueOf(value));
    }

    @Override
    public void addCookie(String name, String value, String path, String domain, int expiry) {
    }

    @Override
    public void removeCookie(String name) {

    }
}
