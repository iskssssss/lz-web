package cn.lz.beans.anno;

import java.lang.annotation.*;

/**
 * 数据注解
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/8 16:51
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value();

    /**
     * 是否必须
     *
     * @return 默认必须
     */
    boolean required() default true;
}
