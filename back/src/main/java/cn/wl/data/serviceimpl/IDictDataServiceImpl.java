package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.DictDataMapper;
import cn.wl.data.entity.DictData;
import cn.wl.data.service.IDictDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements IDictDataService {

}
