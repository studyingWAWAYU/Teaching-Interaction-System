package cn.wl.data.serviceimpl;

import cn.wl.data.dao.mapper.FileMapper;
import cn.wl.data.entity.File;
import cn.wl.data.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Service
public class IFileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
