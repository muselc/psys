package org.ye.psys.core.storage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ye.psys.core.storage.StoragesService;
import org.ye.psys.core.storage.TencentStorage;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageAutoConfiguration {

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public StoragesService storagesService() {
        StoragesService storagesService = new StoragesService();
        String active = this.properties.getActive();
        storagesService.setActive(active);
        if (active.equals("tencent")) {
            storagesService.setStorages(tencentStorage());
        }else {
            throw new RuntimeException("当前存储模式 " + active + " 不支持");
        }

        return storagesService;
    }

    @Bean
    public TencentStorage tencentStorage() {
        TencentStorage tencentStorage = new TencentStorage();
        StorageProperties.Tencent tencent = this.properties.getTencent();
        tencentStorage.setSecretId(tencent.getSecretId());
        tencentStorage.setSecretKey(tencent.getSecretKey());
        tencentStorage.setBucketName(tencent.getBucketName());
        tencentStorage.setRegion(tencent.getRegion());
        return tencentStorage;
    }

}
