package cn.wl.data.service;

import cn.wl.data.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface IPermissionService extends IService<Permission> {

    /**
     * 获取用户按钮权限标识集合
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getPermTypesByUserId(String userId);
}
