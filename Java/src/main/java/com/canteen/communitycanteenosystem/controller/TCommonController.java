package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.common.BusinessException;
import com.canteen.communitycanteenosystem.common.Result;
import com.canteen.communitycanteenosystem.service.TCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.controller
 * @Author: 夏日花店
 * @CreateTime: 2026/1/30 18:33
 * @Description: 文件上传控制器
 * @Version: 1.0
 */
@RestController
@RequestMapping("/common")
public class TCommonController {

    // 注入Service层
    @Autowired
    private TCommonService commonService;

    /**
     * 通用文件上传接口
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验文件是否为空
            if (file.isEmpty()) {
                return Result.fail("上传文件不能为空");
            }

            // 调用Service层上传
            String fileUrl = commonService.uploadFile(file);
            return Result.success("上传成功", fileUrl);

        } catch (BusinessException e) {
            // 业务异常
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("上传失败");
        }
    }
}