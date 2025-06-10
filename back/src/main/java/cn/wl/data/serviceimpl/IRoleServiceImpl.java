package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.RoleMapper;
import cn.wl.data.entity.Role;
import cn.wl.data.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class IRoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
