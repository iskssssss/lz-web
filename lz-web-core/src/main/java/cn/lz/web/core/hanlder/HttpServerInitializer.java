package cn.lz.web.core.hanlder;

import cn.lz.web.core.Application;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 14:17
 */
public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler httpServerHandler;

    public HttpServerInitializer(Application application) {
        this.httpServerHandler = new HttpServerHandler(application);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //先获取管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //httpServerCodec netty提供的对http的编解码器
        pipeline.addLast(new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(httpServerHandler);
    }
}
