package cn.lz.web.core.anno.router.route;

import cn.lz.web.core.enums.RouterMethod;

import java.lang.annotation.*;

/**
 * 通用路由注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/7 9:05
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Route {

    /**
     * 接口路径
     *
     * @return 接口路径
     */
    String[] value() default {};

    /**
     * 接口类型
     *
     * @return 接口类型
     */
    RouterMethod[] method() default {};
}
