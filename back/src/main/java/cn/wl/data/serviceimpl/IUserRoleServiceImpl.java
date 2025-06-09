package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.UserRoleMapper;
import cn.wl.data.entity.UserRole;
import cn.wl.data.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class IUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
