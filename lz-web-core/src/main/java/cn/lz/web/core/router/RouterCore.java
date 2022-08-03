package cn.lz.web.core.router;

import cn.lz.tool.core.string.StringUtil;
import cn.lz.tool.reflect.ReflectUtil;
import cn.lz.tool.reflect.model.ControllerMethod;
import cn.lz.web.core.anno.router.BodyRouter;
import cn.lz.web.core.anno.router.Router;
import cn.lz.web.core.anno.router.route.*;
import cn.lz.web.core.enums.RouterMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 路由信息
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/26 15:39
 */
public class RouterCore {

    /**
     * 接口映射方法：接口名称 -> 方法集合(请求类型 -> 具体方法)
     */
    private final Map<String, Map<String, ControllerMethod>> methodMap = new ConcurrentHashMap<>();
    private final Map<String, Object> routerPool = new ConcurrentHashMap<>();

    /**
     * 初始化路由
     *
     * @param routerMap 含有路由容器注解的bean
     */
    public void initRouter(Map<String, Object> routerMap) {
        Collection<Object> values = routerMap.values();
        values.forEach(value -> {
            Class<?> aClass = value.getClass();
            Map<String, Set<RouterMethod>> routerPathMap = this.routerPathMap(aClass);
            ReflectUtil.getDeclaredMethods(aClass)
                    .stream().filter(method ->
                            method.isAnnotationPresent(Route.class) || method.isAnnotationPresent(Get.class) ||
                            method.isAnnotationPresent(Post.class) || method.isAnnotationPresent(Put.class) ||
                            method.isAnnotationPresent(Delete.class))
                    .forEach(method -> {
                        if (!this.initMethodMappingPaths(routerPathMap, new ControllerMethod(new HashSet<>(), aClass, method))) {
                            return;
                        }
                        Class<?> declaringClass = method.getDeclaringClass();
                        routerPool.put(declaringClass.getSimpleName(), value);
                    });
        });
    }

    /**
     * 获取请求方法
     *
     * @param path       接口
     * @param httpMethod 请求类型
     * @return 请求方法
     */
    public ControllerMethod getMethodMap(String path, String httpMethod) {
        Map<String, ControllerMethod> methodMap = this.methodMap.get(path);
        if (methodMap == null) {
            return null;
        }
        ControllerMethod controllerMethod = methodMap.get(httpMethod);
        return controllerMethod;
    }

    public Object getRouterObj(String routerName) {
        return routerPool.get(routerName);
    }

    private Map<String, Set<RouterMethod>> routerPathMap(Class<?> aClass) {
        Map<String, Set<RouterMethod>> routerPathMap = new HashMap<>();
        final Router router = aClass.getAnnotation(Router.class);
        RouterMethod[] method;
        String[] pathList;
        if (router == null) {
            final BodyRouter bodyRouter = aClass.getAnnotation(BodyRouter.class);
            method = bodyRouter.method();
            pathList = bodyRouter.value();
        } else {
            method = router.method();
            pathList = router.value();
        }
        Set<RouterMethod> requestMethodSet = Arrays.stream(method).collect(Collectors.toSet());
        for (String path : pathList) {
            routerPathMap.put(path, requestMethodSet);
        }
        return routerPathMap;
    }

    /**
     * 获取Mapping注解中的接口地址
     *
     * @param controllerPathMap 类的接口信息
     * @param controllerMethod  接口方法
     */
    private boolean initMethodMappingPaths(Map<String, Set<RouterMethod>> controllerPathMap, ControllerMethod controllerMethod) {
        boolean result = false;
        Set<String> keySet = new HashSet<>(controllerPathMap.keySet());
        Method method = controllerMethod.getMethod();
        Set<RouterMethod> requestMethodSet = new HashSet<>();
        final Route route = method.getAnnotation(Route.class);
        if (null != route) {
            requestMethodSet.addAll(Arrays.stream(route.method()).collect(Collectors.toSet()));
            for (Set<RouterMethod> methodSet : controllerPathMap.values()) {
                requestMethodSet.addAll(methodSet);
            }
            if (handlerPath(route.value(), keySet, controllerMethod, requestMethodSet)) {
                result = true;
            }
        }
        final Get get = method.getAnnotation(Get.class);
        if (null != get) {
            requestMethodSet.add(RouterMethod.GET);
            if (handlerPath(get.value(), keySet, controllerMethod, requestMethodSet)) {
                result = true;
            }
            requestMethodSet.clear();
        }
        final Post post = method.getAnnotation(Post.class);
        if (null != post) {
            requestMethodSet.add(RouterMethod.POST);
            if (handlerPath(post.value(), keySet, controllerMethod, requestMethodSet)) {
                result = true;
            }
            requestMethodSet.clear();
        }
        final Put put = method.getAnnotation(Put.class);
        if (null != put) {
            requestMethodSet.add(RouterMethod.PUT);
            if (handlerPath(put.value(), keySet, controllerMethod, requestMethodSet)) {
                result = true;
            }
            requestMethodSet.clear();
        }
        final Delete delete = method.getAnnotation(Delete.class);
        if (null != delete) {
            requestMethodSet.add(RouterMethod.DELETE);
            if (handlerPath(delete.value(), keySet, controllerMethod, requestMethodSet)) {
                result = true;
            }
            requestMethodSet.clear();
        }
        return result;
    }

    private boolean handlerPath(String[] pathList, Set<String> keySet, ControllerMethod controllerMethod, Set<RouterMethod> requestMethodSet) {
        boolean result = false;
        Set<String> requestPaths = controllerMethod.getRequestPaths();
        for (String path : pathList) {
            String fullPath = getPath(path);
            if (keySet.isEmpty()) {
                requestPaths.add(fullPath);
                Map<String, ControllerMethod> methodMap = this.methodMap.computeIfAbsent(fullPath, s -> new HashMap<>());
                for (RouterMethod routerMethod : requestMethodSet) {
                    methodMap.put(routerMethod.name(), controllerMethod);
                    result = true;
                }
                continue;
            }
            for (String key : keySet) {
                if (StringUtil.isNotEmpty(key)) {
                    fullPath = getPath(key) + fullPath;
                }
                requestPaths.add(fullPath);
                Map<String, ControllerMethod> methodMap = this.methodMap.computeIfAbsent(fullPath, s -> new HashMap<>());
                for (RouterMethod routerMethod : requestMethodSet) {
                    methodMap.put(routerMethod.name(), controllerMethod);
                    result = true;
                }
            }
        }
        return result;
    }

    private String getPath(String path) {
        return (path.startsWith("/") ? "" : "/") + path;
    }

}
