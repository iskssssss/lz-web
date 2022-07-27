package cn.lz.web.demo.config;

import cn.lz.beans.anno.Configuration;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/26 14:49
 */
@Configuration("server")
public class ServerConfig {

    private Integer port;

    private List<String> list;

    private Map<String, String> item;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getItem() {
        return item;
    }

    public void setItem(Map<String, String> item) {
        this.item = item;
    }
}
