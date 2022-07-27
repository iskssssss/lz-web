package cn.lz.web.core.hanlder;

import cn.lz.tool.http.enums.MediaType;
import cn.lz.tool.reflect.model.ControllerMethod;
import cn.lz.web.core.LzWeb;
import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;
import cn.lz.web.core.router.RouterCore;
import cn.lz.web.core.utils.ControllerMethodUtil;

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
        System.out.println("接口　名称：" + method.getName());
        try {
            Object invoke = ControllerMethodUtil.invoke(request, routerCore.getRouterObj(method.getName()), controllerMethod);
            if (invoke != null) {
                content.sendMessage("", MediaType.TEXT_PLAIN_VALUE);
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
}
