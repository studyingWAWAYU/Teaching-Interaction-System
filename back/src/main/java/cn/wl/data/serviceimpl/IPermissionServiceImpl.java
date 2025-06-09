package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.PermissionMapper;
import cn.wl.data.entity.Permission;
import cn.wl.data.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Service
public class IPermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
