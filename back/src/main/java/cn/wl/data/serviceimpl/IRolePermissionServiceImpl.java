package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.RolePermissionMapper;
import cn.wl.data.entity.RolePermission;
import cn.wl.data.service.IRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IRolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {

}
