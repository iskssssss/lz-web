package cn.lz.web.core.model;

import cn.lz.tool.http.enums.HeaderEnums;
import cn.lz.tool.io.FileUtil;
import cn.lz.tool.json.JsonUtil;
import cn.lz.tool.reflect.BeanUtil;
import cn.lz.web.core.anno.params.BodyParam;
import cn.lz.web.core.anno.params.FileParam;
import cn.lz.web.core.anno.params.QueryParam;
import cn.lz.web.core.io.file.ByteArrayUploadFile;
import cn.lz.web.core.io.file.UploadFile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/7 9:51
 */
public class HttpRequest implements BaseRequest {
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private final FullHttpRequest request;
    private final HttpHeaders headers;
    private final ChannelHandlerContext context;
    private final ByteBuf bodyData;
    private final QueryStringDecoder queryStringDecoder;

    private final Map<String, List<String>> parameters;
    private final Map<String, ByteArrayUploadFile> uploadFileMap = new HashMap<>();
    private final Map<String, Object> attribute = new ConcurrentHashMap<>();

    public HttpRequest(ChannelHandlerContext context, FullHttpRequest request) throws IOException {
        this.request = request;
        this.context = context;
        this.queryStringDecoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
        this.parameters = new HashMap<>(queryStringDecoder.parameters());
        String method = request.method().name();
        headers = request.headers();
        bodyData = request.content().copy();
        if (method.equals(HttpMethod.POST.name()) && headers.get(HeaderEnums.CONTENT_TYPE).contains("form")) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            decoder.getBodyHttpDatas().forEach(data -> {
                if (data == null) {
                    return;
                }
                String dataKey = data.getName();
                InterfaceHttpData.HttpDataType httpDataType = data.getHttpDataType();
                try {
                    switch (httpDataType) {
                        case Attribute: // 处理普通文本参数
                            Attribute attribute = (Attribute) data;
                            String value = attribute.getValue();
                            List<String> valueList = this.parameters.computeIfAbsent(dataKey, item -> new LinkedList<>());
                            valueList.add(value);
                            break;
                        case FileUpload: // 处理文件参数
                            FileUpload fileUpload = (FileUpload) data;
                            String filename = fileUpload.getFilename();
                            if (!fileUpload.isCompleted()) {
                                return;
                            }
                            byte[] bytes;
                            if (fileUpload.isInMemory()) {
                                // 从内存获取
                                ByteBuf byteBuf = fileUpload.getByteBuf();
                                bytes = ByteBufUtil.getBytes(byteBuf);
                            } else {
                                // 从本地获取
                                File file = fileUpload.getFile();
                                bytes = FileUtil.readBytes(file);
                            }
                            ByteArrayUploadFile byteArrayUploadFile = new ByteArrayUploadFile(dataKey, filename, bytes);
                            uploadFileMap.put(dataKey, byteArrayUploadFile);
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    data.release();
                }
            });
        }
    }

    @Override
    public String getRequestURL() {
        return request.uri();
    }

    @Override
    public String getRequestPath() {
        return queryStringDecoder.path();
    }

    @Override
    public boolean matchUrl(String path) {
        return false;
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {
        attribute.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attribute.get(key);
    }

    @Override
    public List<String> getAttributeNames() {
        Set<String> keySet = attribute.keySet();
        return new ArrayList<>(keySet);
    }

    @Override
    public void removeAttribute(String key) {
        attribute.remove(key);
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] array = request.content().copy().array();
        return array;
    }

    @Override
    public String getCookieValue(String name) {
        String cookieStr = headers.get("Cookie");
        ServerCookieDecoder cookieDecoder = ServerCookieDecoder.LAX;
        Set<Cookie> decode = cookieDecoder.decode(cookieStr);
        for (Cookie cookie : decode) {
            if (cookie.name().equals(name)) {
                return cookie.value();
            }
        }
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return context.channel().remoteAddress().toString();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        return parameters;
    }

    @Override
    public Object[] injectData(Parameter[] parameters) {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < objects.length; i++) {
            Parameter parameter = parameters[i];
            objects[i] = injectData(parameter);
        }
        return objects;
    }

    @Override
    public Object injectData(Parameter parameter) {
        Class<?> type = parameter.getType();
        FileParam fileParam = parameter.getAnnotation(FileParam.class);
        if (fileParam != null && type.isAssignableFrom(UploadFile.class)) {
            ByteArrayUploadFile byteArrayUploadFile = uploadFileMap.get(fileParam.value());
            return byteArrayUploadFile;
        }
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            List<String> list = this.parameters.get(queryParam.value());
            if (list == null || list.isEmpty()) {
                return null;
            }
            if (type.isAssignableFrom(Collection.class)) {
                return list;
            }
            return list.get(0);
        }
        BodyParam bodyParam = parameter.getAnnotation(BodyParam.class);
        if (bodyParam != null) {
            String body = bodyData.toString(StandardCharsets.UTF_8);
            final Map<String, Object> map = JsonUtil.parseObject(body.replaceAll("\\r\\n", ""), Map.class);
            Object o = BeanUtil.toBean(map, type);
            return o;
        }
        return null;
    }

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }
}
