package cn.lz.web.mybatis.model;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2023 LZJ
 * @date 2023/8/22 14:47
 */
public class Test {

    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Test{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
