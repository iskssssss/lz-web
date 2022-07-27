package cn.lz.web.core.content;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 17:05
 */
public class LzContentManager {
    private static final FastThreadLocal<WebContent> WEB_CONTENT_THREAD_LOCAL = new FastThreadLocal<>();

    public static void set(WebContent webContent) {
        WEB_CONTENT_THREAD_LOCAL.set(webContent);
    }

    public static void remove() {
        WEB_CONTENT_THREAD_LOCAL.remove();
    }

    public static WebContent get() {
        return WEB_CONTENT_THREAD_LOCAL.get();
    }


}
