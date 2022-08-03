package cn.lz.web.core.environment;

import cn.hutool.core.util.NumberUtil;
import cn.lz.tool.core.string.StringUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 10:37
 */
public class Environment {
    public static final String BANNER_PATH = "/banner.txt";
    public static final String APPLICATION_PATH = "/application.yml";

    public final Map<String, Object> props = new HashMap<>();

    public void init(Class<?> primarySource) throws IOException {
        URL resource = primarySource.getResource(APPLICATION_PATH);
        InputStream inputStream = resource.openStream();
        Yaml yaml = new Yaml();
        Object applicationObject = yaml.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        if (applicationObject instanceof Map) {
            Map<String, Object> applicationMap = (Map<String, Object>) applicationObject;
            props.putAll(applicationMap);
        }
    }

    public Object getVal(String valuePath) throws IllegalArgumentException {
        return getVal(props, valuePath);
    }

    public int getValInt(String valuePath, int defaultVal) throws IllegalArgumentException {
        Object value = getVal(props, valuePath);
        if (StringUtil.isEmpty(value)) {
            return defaultVal;
        }
        return NumberUtil.parseInt(value.toString());
    }

    public Object getVal(Map<String, Object> applicationMap, String valuePath) throws IllegalArgumentException {
        int index = valuePath.indexOf(".");
        if (index == -1) {
            if ("null".equals(valuePath)) {
                valuePath = null;
            }
            if (!_containsKey(applicationMap, valuePath)) {
                throw new IllegalArgumentException("找不到配置");
            }
            Object applicationObject = _getVal(applicationMap, valuePath);
            return applicationObject;
        }
        String key = valuePath.substring(0, index);
        if ("null".equals(key)) {
            key = null;
        }
        if (!_containsKey(applicationMap, key)) {
            throw new IllegalArgumentException("找不到配置");
        }
        Object applicationObject = _getVal(applicationMap, key);
        return getVal((Map<String, Object>) applicationObject, valuePath.substring(index + 1));
    }

    private Object _getVal(Map<String, Object> applicationMap, String valuePath) {
        Object o = applicationMap.get(valuePath);
        if (o == null) {
            String snakeCase = snakeCase(valuePath);
            if (snakeCase.equals(valuePath)) {
                return o;
            }
            return applicationMap.get(snakeCase);
        }
        return o;
    }

    private boolean _containsKey(Map<String, Object> applicationMap, String valuePath) {
        boolean o = applicationMap.containsKey(valuePath);
        if (!o) {
            String snakeCase = snakeCase(valuePath);
            if (snakeCase.equals(valuePath)) {
                return false;
            }
            return applicationMap.containsKey(snakeCase);
        }
        return true;
    }

    public String snakeCase(String valuePath) {
        int length = valuePath.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = valuePath.charAt(i);
            if ((c >= 'A' && c <= 'Z') && i > 0) {
                sb.append('-');
                sb.append((char) (((int) c) + 32));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean checkName(String source, String target) {
        if (StringUtil.isEmpty(source) && StringUtil.isEmpty(target)) {
            return true;
        }
        if (StringUtil.isEmpty(source) || StringUtil.isEmpty(target)) {
            return Objects.equals(source, target);
        }
        int min = Math.min(source.length(), target.length());
        boolean isSplit = true;
        for (int i = 0, j = 0; i < min; i++, j++) {
            char c = source.charAt(i);
            char c1 = target.charAt(j);
            if (c == '-' && c1 == '-') {
                continue;
            }
            if (c == '-') {
                i++;
                c = source.charAt(i);
                isSplit = c1 >= 'A' && c1 <= 'Z';
            }
            if (c1 == '-') {
                j++;
                c1 = target.charAt(j);
                isSplit = c >= 'A' && c <= 'Z';
            }
            if (String.valueOf(c1).equalsIgnoreCase(String.valueOf(c)) && isSplit) {
                continue;
            }
            return false;
        }
        return true;
    }
}
