package cn.lz.web.demo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.lz.tool.json.JsonUtil;
import cn.lz.beans.anno.Inject;
import cn.lz.beans.anno.Value;
import cn.lz.web.core.anno.params.BodyParam;
import cn.lz.web.core.anno.params.FileParam;
import cn.lz.web.core.anno.params.QueryParam;
import cn.lz.web.core.anno.router.Router;
import cn.lz.web.core.anno.router.route.Get;
import cn.lz.web.core.anno.router.route.Post;
import cn.lz.web.core.content.LzContentManager;
import cn.lz.web.core.content.WebContent;
import cn.lz.web.core.io.file.UploadFile;
import cn.lz.web.demo.config.TestConfiguration;
import cn.lz.web.demo.config.TestConfiguration2;
import cn.lz.web.demo.model.LoginModel;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/6 15:24
 */
@Router("/api")
public class TestRouter {

    @Value("${server.port}")
    private String port;

    @Inject
    private TestConfiguration testConfiguration;

    @Inject
    private TestConfiguration2 testConfiguration2;

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
    public void testByGet() {
        WebContent webContent = LzContentManager.get();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "success");
        resultMap.put("data", "test-get");
        webContent.sendMessage(JsonUtil.toJsonString(resultMap));
    }

    @Get("/error")
    public void error() {
        throw new SecurityException("123");
    }

    @Post("/upload")
    public void upload(
            @FileParam("file") UploadFile uploadFile,
            @QueryParam("fileName") String fileName
    ) {
        InputStream inputStream = uploadFile.getInputStream();
        String name = uploadFile.getFileName();
        String format = DateUtil.format(new Date(), "yyyy\\MM\\dd\\");
        File file = FileUtil.touch("D:\\upload\\" + format + name);
        FileUtil.writeFromStream(inputStream, file);
        WebContent webContent = LzContentManager.get();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "上传成功。");
        resultMap.put("data", fileName);
        webContent.sendMessage(JsonUtil.toJsonString(resultMap));
    }

    public String msg() {
        System.out.println("666666666666666666666666666666666");
        return "success";
    }
}
