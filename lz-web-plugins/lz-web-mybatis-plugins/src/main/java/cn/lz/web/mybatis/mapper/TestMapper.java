package cn.lz.web.mybatis.mapper;

import cn.lz.web.mybatis.model.Test;
import org.apache.ibatis.annotations.Param;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2023 LZJ
 * @date 2023/8/22 14:47
 */
public interface TestMapper {

    Test selectBlog(@Param("id") String id);
}
