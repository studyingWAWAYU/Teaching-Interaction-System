package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.DepartmentMapper;
import cn.wl.data.entity.Department;
import cn.wl.data.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

}
