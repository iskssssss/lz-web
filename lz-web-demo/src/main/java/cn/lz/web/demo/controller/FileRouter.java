package cn.lz.web.demo.controller;

import cn.hutool.core.date.DateUtil;
import cn.lz.beans.anno.Value;
import cn.lz.web.core.anno.params.FileParam;
import cn.lz.web.core.anno.params.QueryParam;
import cn.lz.web.core.anno.router.BodyRouter;
import cn.lz.web.core.anno.router.route.Post;
import cn.lz.web.core.io.file.UploadFile;
import cn.lz.web.demo.vo.R;

import java.io.File;
import java.util.Date;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 杭州设维信息技术有限公司
 * @date 2022/7/27 17:22
 */
@BodyRouter("/api/file")
public class FileRouter {

    @Value(value = "${lz.file.fileSavePath}", required = true)
    private String fileSavePath;

    public String getFileSavePath() {
        return fileSavePath + DateUtil.format(new Date(), "yyyy\\MM\\dd\\");
    }

    @Post("/upload")
    public R<String> upload(@FileParam("file") UploadFile uploadFile, @QueryParam("fileName") String fileName) {
        File file = uploadFile.saveFile(this.getFileSavePath(), fileName);
        return R.success(file.getPath(), "上传成功");
    }
}
