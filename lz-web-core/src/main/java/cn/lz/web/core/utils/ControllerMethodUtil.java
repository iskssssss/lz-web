package cn.lz.web.core.utils;

import cn.lz.beans.exception.BeanException;
import cn.lz.tool.reflect.model.ControllerMethod;
import cn.lz.web.core.LzWeb;
import cn.lz.web.core.model.BaseRequest;

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
            BaseRequest request,
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
        Object[] params = request.injectData(parameters);
        Object invoke = method.invoke(classBean, params);
        return invoke;
    }
}
