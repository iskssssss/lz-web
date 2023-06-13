package cn.lz.web.core.hanlder;

import cn.lz.beans.scanner.Scanner;
import cn.lz.tool.http.enums.MediaType;
import cn.lz.tool.json.JsonUtil;
import cn.lz.tool.reflect.model.ControllerMethod;
import cn.lz.web.core.anno.returns.ReturnBody;
import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;
import cn.lz.web.core.router.RouterCore;
import cn.lz.web.core.utils.ControllerMethodUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 17:02
 */
public class WebFilter implements LzFilter {

    @Override
    public void init() {

    }

    @Override
    public void filter(WebContent content, BaseRequest request, BaseResponse response) {
        String httpMethod = request.getMethod();
        String path = request.getRequestPath();
        if ("/favicon.ico".equals(path)) {
            return;
        }
        RouterCore routerCore = content.getApplication().getRouterCore();
        ControllerMethod controllerMethod = routerCore.getMethodMap(path, httpMethod);
        if (controllerMethod == null) {
            content.send404();
            return;
        }
        Method method = controllerMethod.getMethod();
        try {
            Class<?> declaringClass = method.getDeclaringClass();
            String simpleName = declaringClass.getSimpleName();
            Object invoke = ControllerMethodUtil.invoke(request, response, routerCore.getRouterObj(simpleName), controllerMethod);
            if (invoke != null) {
                String msg = JsonUtil.toJsonString(invoke);
                if (isReturnBodyAnnotation(method)) {
                    content.sendMessage(msg, MediaType.TEXT_PLAIN_VALUE);
                    return;
                }
                content.sendMessage(msg, MediaType.TEXT_HTML_VALUE);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Throwable throwable = Optional.ofNullable(e.getCause()).orElse(e);
            throwable.printStackTrace();
            content.sendMessage(throwable);
        } catch (Exception e) {
            e.printStackTrace();
            content.sendMessage(e);
        }
    }

    private boolean isReturnBodyAnnotation(AccessibleObject obj) {
        boolean exist = isReturnBodyAnnotationMethod(obj);
        if (exist) {
            return true;
        }
        Class<?> declaringClass = null;
        if (obj instanceof Method) {
            declaringClass = ((Method) obj).getDeclaringClass();
        }
        if (declaringClass == null) {
            return false;
        }
        exist = Scanner.isAnnotation((Class<? extends Annotation>) declaringClass, ReturnBody.class);
        return exist;
    }

    private boolean isReturnBodyAnnotationMethod(AccessibleObject obj) {
        Method method = null;
        if (obj instanceof Method) {
            method = ((Method) obj);
        }
        if (method == null) {
            return false;
        }
        boolean annotation = method.isAnnotationPresent(ReturnBody.class);
        return annotation;
    }
}
