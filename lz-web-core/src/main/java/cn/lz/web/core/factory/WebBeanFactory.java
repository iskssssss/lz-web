package cn.lz.web.core.factory;

import cn.hutool.core.util.NumberUtil;
import cn.lz.beans.factory.AbstractBeanFactory;
import cn.lz.web.core.Application;
import cn.lz.web.core.environment.Environment;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/26 16:29
 */
public class WebBeanFactory extends AbstractBeanFactory {

    private final Application application;
    private final Environment environment;

    public WebBeanFactory(Application application) {
        this.application = application;
        this.environment = application.getEnvironment();
    }

    @Override
    protected Object analysisValue(Class<?> type, String valuePath) {
        if (valuePath.startsWith("${")) {
            int length = valuePath.length();
            return toVal(type, this.environment.getVal(valuePath.substring(2, length - 1)));
        }
        return null;
    }

    private Object toVal(Class<?> fieldType, Object val) {
        if (val == null || "".equals(val) || val.getClass().isAssignableFrom(fieldType)) {
            return val;
        }
        if (fieldType.isAssignableFrom(Number.class)) {
            return NumberUtil.parseNumber(val.toString());
        }
        if (fieldType.isAssignableFrom(String.class)) {
            return val.toString();
        }
        return val;
    }
}
