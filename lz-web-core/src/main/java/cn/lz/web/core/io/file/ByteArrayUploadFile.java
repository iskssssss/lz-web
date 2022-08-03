package cn.lz.web.core.io.file;

import cn.hutool.core.io.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/7 17:11
 */
public class ByteArrayUploadFile implements UploadFile {
    private final String dataKey;
    private final String fileName;
    private final byte[] bytes;

    public ByteArrayUploadFile(String dataKey, String filename, byte[] bytes) {
        this.dataKey = dataKey;
        this.fileName = filename;
        this.bytes = bytes;
    }

    @Override
    public String getDataKey() {
        return this.dataKey;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public long getSize() {
        return this.bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.bytes;
    }

    @Override
    public File saveFile(String savePath, String saveFileName) {
        InputStream inputStream = this.getInputStream();
        int length = savePath.length();
        if (savePath.charAt(length - 1) != '\\' && savePath.charAt(length - 1) != '/' ) {
            savePath = savePath + File.separator;
        }
        File file = FileUtil.touch(savePath + saveFileName);
        FileUtil.writeFromStream(inputStream, file);
        return file;
    }
}
