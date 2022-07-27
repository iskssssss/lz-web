package cn.lz.web.core.hanlder;

import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;

/**
 * 过滤器
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 17:01
 */
public interface LzFilter {

    void init();

    /**
     * 过滤
     *
     * @param content  上下文
     * @param request  请求流
     * @param response 响应流
     */
    void filter(WebContent content, BaseRequest request, BaseResponse response);
}
