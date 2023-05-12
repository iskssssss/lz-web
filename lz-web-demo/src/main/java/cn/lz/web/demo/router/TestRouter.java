package cn.lz.web.demo.router;

import cn.lz.beans.anno.Inject;
import cn.lz.beans.anno.Value;
import cn.lz.tool.json.JsonUtil;
import cn.lz.web.core.anno.params.BodyParam;
import cn.lz.web.core.anno.params.QueryParam;
import cn.lz.web.core.anno.router.Router;
import cn.lz.web.core.anno.router.route.Get;
import cn.lz.web.core.anno.router.route.Post;
import cn.lz.web.core.content.LzContentManager;
import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.model.HttpRequest;
import cn.lz.web.demo.config.TestConfiguration;
import cn.lz.web.demo.config.TestConfiguration2;
import cn.lz.web.demo.model.LoginModel;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 15:24
 */
@Router("/api/test")
public class TestRouter {

    @Value("${server.port}")
    String port;

    @Inject
    TestConfiguration testConfiguration;

    @Inject
    TestConfiguration2 testConfiguration2;

    @Post("/helloWorld")
    public void helloWorld(@BodyParam LoginModel loginModel) {
        String json = loginModel.toJson();
        System.out.println(json);
        System.out.println(port);
        WebContent webContent = LzContentManager.get();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "success");
        resultMap.put("data", loginModel);
        webContent.sendMessage(JsonUtil.toJsonString(resultMap));
    }

    @Post({"/test", "/test2"})
    public void test() {
        WebContent webContent = LzContentManager.get();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "success");
        resultMap.put("data", "test-post");
        webContent.sendMessage(JsonUtil.toJsonString(resultMap));
    }

    @Get("/test")
    public void testByGet(HttpRequest httpRequest, @QueryParam("cookieKey") String cookieKey) {
        WebContent webContent = LzContentManager.get();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "success");
        resultMap.put("data", httpRequest.getCookieValue(cookieKey));
        webContent.sendMessage(JsonUtil.toJsonString(resultMap));
    }

    @Get("/error")
    public void error() {
        throw new SecurityException("123");
    }

    public String msg() {
        System.out.println("666666666666666666666666666666666");
        return "success";
    }
}
