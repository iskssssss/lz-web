package cn.lz.web.core.anno.router;

import cn.lz.beans.anno.Bean;
import cn.lz.web.core.enums.RouterMethod;

import java.lang.annotation.*;

/**
 * 路由容器注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/8 16:30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
public @interface Router {

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
