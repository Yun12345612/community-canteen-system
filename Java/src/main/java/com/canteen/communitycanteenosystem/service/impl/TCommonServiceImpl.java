package com.canteen.communitycanteenosystem.service.impl;

import com.canteen.communitycanteenosystem.common.BusinessException;
import com.canteen.communitycanteenosystem.service.TCommonService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TCommonServiceImpl implements TCommonService {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.access.url-prefix:http://localhost:8080/uploads/}")
    private String accessUrlPrefix;

    // 支持所有主流图片格式
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp", "tiff", "ico");

    // 最大文件大小 5MB（兼容高清图片）
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String uploadFile(MultipartFile file) {
        // 1. 校验（仅放宽格式和大小）
        validateFile(file);

        // 2. 智能判断目标格式（JPG/PNG）
        String targetFormat = getTargetFormat(file);

        // 3. 生成对应格式的文件名
        String fileName = generateFileName(targetFormat);

        // 4. 保存并转换文件
        saveAndConvertFile(file, fileName, targetFormat);

        // 5. 返回访问URL
        return accessUrlPrefix + fileName;
    }

    // ========== 智能判断目标格式 ==========
    private String getTargetFormat(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            // 读取图片信息
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                throw new BusinessException("文件不是有效图片");
            }

            // 判断是否为透明图片（PNG/GIF需要保留透明通道）
            String originalExt = getFileExtension(file.getOriginalFilename()).toLowerCase();
            if ("png".equals(originalExt) || "gif".equals(originalExt)) {
                return "png"; // 透明图片用PNG
            } else {
                return "jpg"; // 其他图片用JPG
            }
        } catch (IOException e) {
            log.error("判断图片格式失败", e);
            return "jpg"; // 异常时默认用JPG
        }
    }

    // ========== 保存并转换为目标格式 ==========
    private void saveAndConvertFile(MultipartFile file, String fileName, String targetFormat) {
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 拼接文件路径
            File dest = new File(uploadDir, fileName);

            // 转换并保存（兼容所有图片格式）
            Thumbnails.of(file.getInputStream())
                    .scale(1.0) // 保持原比例
                    .outputFormat(targetFormat) // 输出目标格式（JPG/PNG）
                    .outputQuality(0.85)        // 压缩质量（平衡大小和清晰度）
                    .toFile(dest);

            log.info("图片转换为{}格式并保存成功：{}", targetFormat, dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("图片转换/保存失败", e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }
    }

    // ========== 文件名生成 ==========
    private void validateFile(MultipartFile file) {
        // 空文件校验
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 文件大小校验（放宽到5MB）
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 文件类型校验（支持所有图片格式）
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("只支持" + ALLOWED_EXTENSIONS + "格式的图片文件");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException("文件格式不正确");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // 生成目标格式的文件名（不再用原扩展名）
    private String generateFileName(String targetFormat) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int) (Math.random() * 1000));
        return uuid + "_" + timestamp + "_" + random + "." + targetFormat;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            File file = new File(uploadPath + File.separator + fileName);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("删除文件{}：{}", deleted ? "成功" : "失败", file.getAbsolutePath());
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("删除文件失败：{}", fileUrl, e);
            return false;
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl.startsWith(accessUrlPrefix)) {
            return fileUrl.substring(accessUrlPrefix.length());
        }
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("创建上传目录{}：{}", uploadDir.getAbsolutePath(), created ? "成功" : "失败");
        } else {
            log.info("上传目录已存在：{}", uploadDir.getAbsolutePath());
        }
    }
}