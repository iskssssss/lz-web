package cn.lz.web.core.hanlder;

import cn.lz.web.core.Application;
import cn.lz.web.core.content.LzContentManager;
import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.model.BaseRequest;
import cn.lz.web.core.model.BaseResponse;
import cn.lz.web.core.model.HttpRequest;
import cn.lz.web.core.model.HttpResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 14:15
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Application application;

    private final LzFilter lzFilter = new WebFilter();

    public HttpServerHandler(Application application) {
        this.application = application;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (fullHttpRequest == null) {
            return;
        }
        BaseRequest request = new HttpRequest(channelHandlerContext, fullHttpRequest);
        BaseResponse response = new HttpResponse(channelHandlerContext);
        WebContent content = new WebContent(this.application, request, response);
        try {
            LzContentManager.set(content);
            lzFilter.filter(content, request, response);
        } finally {
            LzContentManager.remove();
        }
    }
}
