package cn.lz.web.demo.config;

import cn.lz.beans.anno.Configuration;
import cn.lz.beans.anno.Inject;
import cn.lz.beans.anno.Register;
import cn.lz.beans.anno.Value;

import java.util.Map;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 15:14
 */
@Configuration
public class TestConfiguration {

    @Inject
    private TestConfiguration2 testConfiguration;

    public TestConfiguration() {
        System.out.println("配置文件1 - create");
    }

    @Inject
    public TestConfiguration(@Value("${server.port}") Integer port) {
        System.out.println(port);
        System.out.println("配置文件1 - create");
    }

    @Register
    public TestConfiguration2 registerTestConfiguration2(
            @Value("${server}") Map<String, Object> app
    ) {
        return new TestConfiguration2();
    }

    public void print() {
        testConfiguration.print();
        System.out.println("配置文件1 - print");
    }
}
