package com.example.scupsychological.pojo;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /**
     * 上传文件到 OSS
     * @param file 用户上传的文件
     * @param userId 用于生成唯一文件名
     * @return 文件的公开访问 URL
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 1. 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 2. 生成唯一的文件名，避免覆盖
        // 格式: avatars/用户ID-时间戳.后缀名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = "avatars/" + userId + "-" + System.currentTimeMillis() + fileExtension;

        try {
            // 3. 上传文件流
            ossClient.putObject(bucketName, objectName, file.getInputStream());

            // 4. 返回文件的公网访问 URL
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
