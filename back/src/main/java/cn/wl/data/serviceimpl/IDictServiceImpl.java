package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.DictMapper;
import cn.wl.data.entity.Dict;
import cn.wl.data.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

}
