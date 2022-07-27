package cn.lz.web.core.content;

import cn.lz.tool.http.enums.MediaType;
import cn.lz.web.core.Application;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 17:00
 */
public class WebContent {
    private final Application application;
    private final BaseResponse response;
    private final BaseRequest request;

    public WebContent(Application application, BaseRequest request, BaseResponse response) {
        this.application = application;
        this.response = response;
        this.request = request;
    }

    /**
     * 路由匹配
     * 支持模糊匹配
     * <p>** : 多级模糊匹配</p>
     * <p>*  : 一级模糊匹配</p>
     * <p>? : 单个匹配</p>
     *
     * @param pattern
     * @param path
     * @return
     */
    public boolean matchUrl(String pattern, String path) {
        //TODO 地址匹配
        if (pattern.equals(path)) {
            return true;
        }
        // /**
        // /a/**
        // /a/**/b
        // /a/*
        // /a/*/b
        pattern = pattern.replaceAll("//", "/");
        String[] patternSplit = pattern.split("/");
        path = path.replaceAll("//", "/");
        String[] pathSplit = path.split("/");

        int patternLength = pattern.length();
        int pathLength = path.length();
        int min = Math.min(patternLength, pathLength);
        for (int i = 0; i < min; i++) {
            if (pattern.charAt(i) != path.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public void sendMessage(Throwable throwable) {
        HttpResponseStatus internalServerError = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        StringBuilder errorMsg = new StringBuilder(internalServerError.code() + " " + internalServerError.reasonPhrase());
        errorMsg.append("\r\n").append(throwable.toString());
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            errorMsg.append("\r\n\t").append(stackTraceElement.toString());
        }
        sendMessage(errorMsg.toString(), MediaType.TEXT_PLAIN_VALUE, internalServerError);
    }

    public void sendMessage(String msg) {
        sendMessage(msg, MediaType.APPLICATION_JSON_VALUE);
    }

    public void send404() {
        HttpResponseStatus notFound = HttpResponseStatus.NOT_FOUND;
        sendMessage("<h1>404 Not Found - " + request.getRequestPath() + "</h1>", MediaType.TEXT_HTML_VALUE, notFound);
    }

    public void sendMessage(String msg, String mediaType) {
        sendMessage(msg, mediaType, null);
    }

    public void sendMessage(String msg, String mediaType, HttpResponseStatus httpResponseStatus) {
        response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), mediaType);
        if (httpResponseStatus != null) {
            response.setStatus(httpResponseStatus.code());
        }
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        response.print(bytes, 0, bytes.length);
    }

    public Application getApplication() {
        return application;
    }
}
