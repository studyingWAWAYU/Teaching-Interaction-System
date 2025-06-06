package cn.zwz.study.serviceimpl;

import cn.zwz.study.mapper.BaseDataMapper;
import cn.zwz.study.entity.BaseData;
import cn.zwz.study.service.IBaseDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基本信息 服务层接口实现
 * @author 郑为中
 */
@Slf4j
@Service
@Transactional
public class IBaseDataServiceImpl extends ServiceImpl<BaseDataMapper, BaseData> implements IBaseDataService {

    @Autowired
    private BaseDataMapper baseDataMapper;
}