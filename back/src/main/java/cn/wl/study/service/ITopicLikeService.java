package cn.wl.study.service;

import cn.wl.basics.baseVo.Result;
import cn.wl.study.entity.TopicLike;
import cn.wl.study.entity.TopicLike;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITopicLikeService extends IService<TopicLike> {
    Result<TopicLike> likeTopic(Integer topicId, Integer userId);
    Result<TopicLike> unlikeTopic(Integer topicId, Integer userId);
}
