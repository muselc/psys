package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Storage;
import org.ye.psys.db.entity.StorageExample;
import org.ye.psys.db.mapper.StorageMapper;

import java.time.LocalDateTime;

/**
 * @Author liansongye
 * @create 2019-04-24 11:04
 */
@Service
public class StorageService {
    @Autowired
    private StorageMapper storageMapper;


    public void add(Storage storageInfo) {
        storageInfo.setAddTime(LocalDateTime.now());
        storageInfo.setUpdateTime(LocalDateTime.now());
        storageMapper.insertSelective(storageInfo);
    }

    public Storage findByKey(String key) {
        StorageExample example = new StorageExample();
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }

    public int update(Storage storage) {
        storage.setUpdateTime(LocalDateTime.now());
        return storageMapper.updateByPrimaryKeySelective(storage);
    }

    public void deleteByKey(String key) {
        StorageExample example = new StorageExample();
        example.or().andKeyEqualTo(key);
        storageMapper.logicalDeleteByExample(example);
    }

}
