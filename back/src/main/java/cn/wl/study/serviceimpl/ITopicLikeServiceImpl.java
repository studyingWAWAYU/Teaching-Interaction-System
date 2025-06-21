package cn.wl.study.serviceimpl;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.study.entity.TopicLike;
import cn.wl.study.entity.Topics;
import cn.wl.study.mapper.TopicLikeMapper;
import cn.wl.study.mapper.TopicsMapper;
import cn.wl.study.service.ITopicLikeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

@Slf4j
@Service
@Transactional
public class ITopicLikeServiceImpl extends ServiceImpl<TopicLikeMapper, TopicLike> implements ITopicLikeService {
    @Autowired
    private TopicLikeMapper topicLikeMapper;
    
    @Autowired
    private TopicsMapper topicsMapper;

    @Override
    public Result<TopicLike> likeTopic(Integer topicId, Integer userId) {
        QueryWrapper<TopicLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("topic_id", topicId);
        TopicLike existingLike = topicLikeMapper.selectOne(queryWrapper);
        if (existingLike == null) {
            TopicLike topicLike = new TopicLike();
            topicLike.setTopicId(topicId);
            topicLike.setUserId(userId);
            topicLike.setCreateTime(LocalDate.now());
            updateTopicLikes(topicId,1);
            return new ResultUtil<TopicLike>().setData(topicLike);
        }else{
            return ResultUtil.error("Fail to like this topic.");
        }
    }

    @Override
    public Result<TopicLike> unlikeTopic(Integer topicId, Integer userId) {
        QueryWrapper<TopicLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("topic_id", topicId);
        TopicLike existingLike = topicLikeMapper.selectOne(queryWrapper);
        if (existingLike != null) {
            topicLikeMapper.deleteById(existingLike.getId());
            updateTopicLikes(topicId,-1);
            return new ResultUtil<TopicLike>().setData(existingLike);
        }else{
            return ResultUtil.error("Fail to unlike this topic.");
        }
    }

    private void updateTopicLikes(Integer topicId, int change) {
        // 更新帖子的点赞数量
        Topics topics = topicsMapper.selectById(topicId);
        if (topics != null) {
            topics.setLikes(topics.getLikes() + change);
            topicsMapper.updateById(topics);
        }
    }
}
