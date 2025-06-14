package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.UserMapper;
import cn.wl.data.entity.User;
import cn.wl.data.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class IUserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
