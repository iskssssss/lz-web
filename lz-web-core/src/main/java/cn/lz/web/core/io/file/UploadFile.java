package cn.lz.web.core.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/7 17:01
 */
public interface UploadFile {

    /**
     * 获取文件存放名称
     *
     * @return 文件存放名称
     */
    String getDataKey();

    /**
     * 获取文件名称
     *
     * @return 文件名称
     */
    String getFileName();

    /**
     * 获取文件输入流
     *
     * @return 文件输入流
     */
    InputStream getInputStream();

    /**
     * 获取文件类型
     *
     * @return 文件类型
     */
    String getContentType();

    /**
     * 获取文件大小
     *
     * @return 文件大小
     */
    long getSize();

    /**
     * 获取文件字节数组
     *
     * @return 文件字节数组
     * @throws IOException I/O异常
     */
    byte[] getBytes() throws IOException;

    /**
     * 保存文件
     *
     * @param savePath     保存文件路径
     * @param saveFileName 保存文件名称
     * @return 文件
     */
    File saveFile(String savePath, String saveFileName);
}

