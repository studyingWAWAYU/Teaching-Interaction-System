package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.SettingMapper;
import cn.wl.data.entity.Setting;
import cn.wl.data.service.ISettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ISettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements ISettingService {

}
