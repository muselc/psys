package org.ye.psys.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ye.psys.db.entity.Permission;
import org.ye.psys.db.entity.PermissionExample;
import org.ye.psys.db.mapper.PermissionMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author liansongye
 * @create 2019-04-16 10:34
 */
@Service
public class PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    public Set<String> queryByRoleIds(Integer[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if(roleIds.length == 0){
            return permissions;
        }

        PermissionExample example = new PermissionExample();
        example.or().andRoleIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
        List<Permission> permissionList = permissionMapper.selectByExample(example);

        for(Permission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }
}
