package cn.lz.web.core.anno.router;

import cn.lz.web.core.anno.returns.ReturnBody;
import cn.lz.web.core.enums.RouterMethod;

import java.lang.annotation.*;

/**
 * 路由容器注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/8 16:30
 */
@Router
@ReturnBody
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BodyRouter {

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
