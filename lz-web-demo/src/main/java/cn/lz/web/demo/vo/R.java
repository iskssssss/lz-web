package cn.lz.web.demo.vo;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 杭州设维信息技术有限公司
 * @date 2022/6/24 14:30
 */
public class R<T> {
    private Integer code;
    private T data;
    private String message;

    public static <T> R<T> success(T t) {
        R<T> r = new R<>();
        r.code = 200;
        r.data = t;
        r.message = "success";
        return r;
    }

    public static <T> R<T> success(T t, String message) {
        R<T> r = new R<>();
        r.code = 200;
        r.data = t;
        r.message = message;
        return r;
    }

    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.code = 430;
        r.data = null;
        r.message = message;
        return r;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
