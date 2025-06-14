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

    @Override
    public List<Posts> getAllOrderByLikesDesc() {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("likes");
        return this.list(queryWrapper);
    }
}