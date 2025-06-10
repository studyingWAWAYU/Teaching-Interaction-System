package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.LogMapper;
import cn.wl.data.entity.Log;
import cn.wl.data.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Service
public class ILogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

}
