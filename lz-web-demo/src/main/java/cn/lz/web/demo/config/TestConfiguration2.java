package cn.lz.web.demo.config;

import cn.lz.beans.anno.Inject;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 15:14
 */
public class TestConfiguration2 {

    @Inject
    private TestConfiguration testConfiguration;

    public TestConfiguration2() {
        System.out.println("配置文件2 - create");
    }

    public void print() {
        System.out.println("配置文件2 - print");
    }
}
