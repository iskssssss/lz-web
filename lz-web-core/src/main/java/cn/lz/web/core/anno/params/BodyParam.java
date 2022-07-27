package cn.lz.web.core.anno.params;

import java.lang.annotation.*;

/**
 * body路径参数注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/8 16:07
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {
}
