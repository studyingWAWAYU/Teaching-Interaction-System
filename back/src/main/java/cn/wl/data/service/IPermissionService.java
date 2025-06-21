package cn.wl.data.service;

import cn.wl.data.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * 权限服务接口
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 根据用户ID获取该用户所有按钮权限的权限标识集合
     *
     * @param userId 用户ID
     * @return 权限标识字符串集合
     */
    Set<String> getPermTypesByUserId(String userId);
}
