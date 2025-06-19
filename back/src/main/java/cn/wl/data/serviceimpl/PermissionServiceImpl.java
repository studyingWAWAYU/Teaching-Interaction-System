
package cn.wl.data.serviceimpl;

import cn.wl.data.entity.Permission;
import cn.wl.data.dao.mapper.PermissionMapper;
import cn.wl.data.service.IPermissionService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    @Override
    public Set<String> getPermTypesByUserId(String userId) {
        List<Permission> permissions = baseMapper.getUserPermissions(userId);
        return permissions.stream()
                .filter(p -> p.getPermTypes() != null)
                .flatMap(p -> p.getPermTypes().stream())
                .map(String::trim)
                .collect(Collectors.toSet());

    }
}
