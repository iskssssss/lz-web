package cn.lz.beans.factory;

import cn.lz.beans.anno.Configuration;
import cn.lz.beans.anno.Inject;
import cn.lz.beans.anno.Register;
import cn.lz.beans.anno.Value;
import cn.lz.beans.exception.BeanException;
import cn.lz.beans.scanner.Scanner;
import cn.lz.tool.core.string.StringUtil;
import cn.lz.tool.reflect.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 10:25
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBeanFactory.class);

    /**
     * 完整Bean
     */
    private final Map<String, Object> beans = new ConcurrentHashMap<>(64);
    /**
     * 正在创建中的Bean
     */
    private final Map<String, Object> inCreateMap = new ConcurrentHashMap<>(16);

    @Override
    public <T> T createBean(Class<T> tClass, String beanName) throws BeanException {
        if (beans.containsKey(beanName)) {
            return ((T) beans.get(beanName));
        }
        logger.info("create - " + tClass.getName());
        Object bean = newInstance(tClass);
        beans.put(beanName, bean);
        return ((T) bean);
    }

    @Override
    public <T> T getBean(Class<T> tClass) throws BeanException {
        Object bean = getBean(tClass.getSimpleName());
        if (bean != null) {
            return ((T) bean);
        }
        T bean1 = createBean(tClass, tClass.getSimpleName());
        return bean1;
    }

    @Override
    public Object getBean(String beanName) {
        Object bean = beans.get(beanName);
        if (bean == null) {
            return null;
        }
        return bean;
    }

    @Override
    public Map<String, Object> getBeanByAnnotation(Class<? extends Annotation> annotation) {
        Map<String, Object> result = new LinkedHashMap<>();
        Set<Map.Entry<String, Object>> entrySet = beans.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            if (!Scanner.isAnnotation((Class<? extends Annotation>) value.getClass(), annotation)) {
                continue;
            }
            result.put(entry.getKey(), value);
        }
        return result;
    }

    private <T> T newInstance(Class<T> tClass) throws BeanException {
        String simpleName = tClass.getSimpleName();
        if (inCreateMap.containsKey(simpleName)) {
            throw new RuntimeException("循环引用");
        }
        final Constructor<?>[] constructors = tClass.getConstructors();
        boolean oneConstructor = constructors.length == 1;
        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(Inject.class) && !oneConstructor) {
                continue;
            }
            T t = this.newInstance(constructor);
            inCreateMap.put(simpleName, t);
            initBean(t);
            inCreateMap.remove(simpleName);
            return t;
        }
        throw new BeanException("[" + tClass.getName() + "] 存在多个构造方法，请为其中一个构造方法添加@Inject注解！");
    }

    private <T> T newInstance(Constructor<?> constructor) throws BeanException {
        if (constructor.getParameterCount() == 0) {
            try {
                T t = ((T) constructor.newInstance());
                return t;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new BeanException(e);
            }
        }
        Parameter[] parameters = constructor.getParameters();
        int length = parameters.length;
        Object[] initArgs = new Object[length];
        for (int i = 0; i < length; i++) {
            Parameter parameter = parameters[i];
            Value value = parameter.getAnnotation(Value.class);
            Class<?> type = parameter.getType();
            String simpleName = type.getSimpleName();
            Object obj = getVal(simpleName, type, value);
            if (obj == null) {
                throw new RuntimeException("bean 初始化失败");
            }
            initArgs[i] = obj;
        }
        try {
            T t = ((T) constructor.newInstance(initArgs));
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanException(e);
        }
    }

    private void initBean(Object bean) throws BeanException {
        Class<?> beanClass = bean.getClass();
        logger.info("init - " + beanClass.getName());
        Configuration configurationInject = beanClass.getAnnotation(Configuration.class);
        if (configurationInject != null && StringUtil.isNotEmpty(configurationInject.value())) {
            String value = configurationInject.value();
        }
        this.initBeanField(bean);
        this.initBeanMethod(bean);
    }

    private void initBeanField(Object bean) throws BeanException {
        Class<?> beanClass = bean.getClass();
        Set<Field> fieldSet = ReflectUtil.getDeclaredFields(beanClass);
        for (Field field : fieldSet) {
            Inject inject = field.getAnnotation(Inject.class);
            Value value = field.getAnnotation(Value.class);
            if (this.checkAnnotation(inject, value)) {
                continue;
            }
            Class<?> fieldType = field.getType();
            String simpleName = fieldType.getSimpleName();
            Object val = getVal(simpleName, fieldType, value);
            try {
                field.setAccessible(true);
                field.set(bean, val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initBeanMethod(Object bean) throws BeanException {
        Class<?> beanClass = bean.getClass();
        Set<Method> methodSet = ReflectUtil.getDeclaredMethods(beanClass);
        for (Method method : methodSet) {
            Register register = method.getAnnotation(Register.class);
            Inject inject = method.getAnnotation(Inject.class);
            Value value = method.getAnnotation(Value.class);
            if (this.checkAnnotation(inject, value) && register == null) {
                continue;
            }
            Object[] values = this.getVal(method, value);
            method.setAccessible(true);
            try {
                Object invoke = method.invoke(bean, values);
                if (register != null && invoke != null) {
                    Class<?> aClass = invoke.getClass();
                    String simpleName = aClass.getSimpleName();
                    this.inCreateMap.put(simpleName, invoke);
                    initBean(invoke);
                    this.inCreateMap.remove(simpleName);
                    this.beans.put(simpleName, invoke);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Object[] getVal(Method method, Value value) throws BeanException {
        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        Object[] values = new Object[length];
        if (value != null) {
            Value valuePathTemp = value;
            boolean isOne = length == 1;
            if (!isOne) {
                throw new RuntimeException("@Value注解下的参数过多。");
            }
            for (int i = 0; i < length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                String simpleName = type.getSimpleName();
                Value paramValue = parameter.getAnnotation(Value.class);
                if (paramValue != null) {
                    valuePathTemp = paramValue;
                }
                values[i] = getVal(simpleName, type, valuePathTemp);
            }
        } else {
            for (int i = 0; i < length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                String simpleName = type.getSimpleName();
                Value paramValue = parameter.getAnnotation(Value.class);
                values[i] = getVal(simpleName, type, paramValue);
            }
        }
        return values;
    }

    private Object getVal(String name, Class<?> type, Value value) throws BeanException {
        if (StringUtil.isNotEmpty(value)) {
            return this.analysisValue(type, value);
        }
        Object val = this.inCreateMap.get(name);
        if (val == null) {
            return this.getBean(type);
        }
        return val;
    }

    private boolean checkAnnotation(Inject inject, Value value) {
        if (inject == null && value == null) {
            return true;
        }
        if (inject != null && value != null) {
            throw new RuntimeException("@Inject/@Value注解不可同时存在。");
        }
        return false;
    }

    protected abstract Object analysisValue(Class<?> type, Value value);
}
