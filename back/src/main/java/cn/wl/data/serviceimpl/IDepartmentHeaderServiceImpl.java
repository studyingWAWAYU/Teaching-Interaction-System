package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.DepartmentHeaderMapper;
import cn.wl.data.entity.DepartmentHeader;
import cn.wl.data.service.IDepartmentHeaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDepartmentHeaderServiceImpl extends ServiceImpl<DepartmentHeaderMapper, DepartmentHeader> implements IDepartmentHeaderService {

}
