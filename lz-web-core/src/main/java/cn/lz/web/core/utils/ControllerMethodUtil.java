package cn.lz.web.core.utils;

import cn.lz.beans.exception.BeanException;
import cn.lz.tool.reflect.model.ControllerMethod;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;
import cn.lz.web.core.model.HttpRequest;
import cn.lz.web.core.model.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 17:58
 */
public class ControllerMethodUtil {

    public static Object invoke(
            BaseRequest request, BaseResponse response,
            Object classBean,
            ControllerMethod controllerMethod
    ) throws BeanException, InvocationTargetException, IllegalAccessException {
        Method method = controllerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        if (length < 1) {
            Object invoke = method.invoke(classBean);
            return invoke;
        }
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if (type.isAssignableFrom(HttpRequest.class)) {
                params[i] = request;
                continue;
            }
            if (type.isAssignableFrom(HttpResponse.class)) {
                params[i] = response;
                continue;
            }
            params[i] = request.injectData(parameter);
        }
        Object invoke = method.invoke(classBean, params);
        return invoke;
    }
}
