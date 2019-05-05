package org.ye.psys.core.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.ye.psys.core.util.CharUtil;
import org.ye.psys.db.entity.Storage;
import org.ye.psys.db.service.StorageService;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 提供存储服务类，所有存储服务均由该类对外提供
 */
public class StoragesService {
    private String active;
    private Storages storages;
    @Autowired
    private StorageService StorageService;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Storages getStorages() {
        return storages;
    }

    public void setStorages(Storages storages) {
        this.storages = storages;
    }

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param fileName      文件索引名
     */
    public String store(InputStream inputStream, long contentLength, String contentType, String fileName) {
        String key = generateKey(fileName);
        storages.store(inputStream, contentLength, contentType, key);

        String url = generateUrl(key);
        Storage storageInfo = new Storage();
        storageInfo.setName(fileName);
        storageInfo.setSize((int) contentLength);
        storageInfo.setType(contentType);
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        StorageService.add(storageInfo);

        return url;
    }

    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        Storage storageInfo = null;

        do {
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = StorageService.findByKey(key);
        }
        while (storageInfo != null);

        return key;
    }

    public Stream<Path> loadAll() {
        return storages.loadAll();
    }

    public Path load(String keyName) {
        return storages.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        return storages.loadAsResource(keyName);
    }

    public void delete(String keyName) {
        storages.delete(keyName);
    }

    private String generateUrl(String keyName) {
        return storages.generateUrl(keyName);
    }
}
