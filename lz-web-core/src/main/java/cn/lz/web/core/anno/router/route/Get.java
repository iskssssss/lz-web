package cn.lz.web.core.anno.router.route;

import cn.lz.web.core.enums.RouterMethod;

import java.lang.annotation.*;

/**
 * GET 路由注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 15:24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Route(method = RouterMethod.GET)
public @interface Get {
    String[] value() default {};
}
