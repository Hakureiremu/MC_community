package com.nowcoder.community.config;

import com.nowcoder.community.util.AliOssUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/*
 * 配置类，用于创建AliOssUtil对象
 * */
@Configuration
@Slf4j
public class OssConfig {
    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.file.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file.bucket-name}")
    private String bucketName;

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(){
        log.info("create ali file upload object: {}", endpoint, accessKeyId, accessKeySecret, bucketName);
        return new AliOssUtil(endpoint, accessKeyId, accessKeySecret, bucketName);
    }
}
