package com.canteen.communitycanteenosystem.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service
 * @Author: 夏日花店
 * @CreateTime: 2026/1/30 18:34
 * @Description: 文件上传服务层
 * @Version: 1.0
 */
public interface TCommonService {

    /**
     * 上传文件
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * 删除文件
     * @param fileUrl
     * @return
     */
    boolean deleteFile(String fileUrl);
}
