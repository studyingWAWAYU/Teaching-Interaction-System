package cn.wl.study.serviceimpl;

import cn.wl.study.entity.TopicLike;
import cn.wl.study.mapper.TopicLikeMapper;
import cn.wl.study.service.ITopicLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ITopicLikeServiceImpl extends ServiceImpl<TopicLikeMapper, TopicLike> implements ITopicLikeService {
    @Autowired
    private TopicLikeMapper topicLikeMapper;
}
