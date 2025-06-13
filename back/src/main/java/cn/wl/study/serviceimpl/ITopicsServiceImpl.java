package cn.wl.study.serviceimpl;

import cn.wl.study.entity.Topics;
import cn.wl.study.mapper.TopicsMapper;
import cn.wl.study.service.ITopicsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ITopicsServiceImpl extends ServiceImpl<TopicsMapper, Topics> implements ITopicsService {
    @Autowired
    private TopicsMapper topicsMapper;
}
