package cn.wl.study.serviceimpl;

import cn.wl.study.entity.Posts;
import cn.wl.study.mapper.PostsMapper;
import cn.wl.study.service.IPostsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class IPostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements IPostsService {

    @Autowired
    private PostsMapper postsMapper;

    // 根据post_id和topic_id查询单个post
    @Override
    public Posts getByIdAndTopicId(Integer id, Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id)
                .eq("topic_id", topicId);
        return this.getOne(queryWrapper);
    }

    // 根据topic_id查询回复数量
    @Override
    public Long countByTopicId(Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId);
        return this.count(queryWrapper);
    }

    // 根据topic_id查询该课程下的所有回复
    @Override
    public List<Posts> listByTopicId(Integer topicId){
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId);
        return this.list(queryWrapper);
    }

    // 根据topic_id查询该课程下的所有回复，按likes降序排序
    @Override
    public List<Posts> getAllByTopicIdOrderByLikesDesc(Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId)
                .orderByDesc("likes");
        return this.list(queryWrapper);
    }


}