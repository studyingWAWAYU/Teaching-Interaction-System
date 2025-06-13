package cn.wl.study.serviceimpl;

import cn.wl.study.entity.Posts;
import cn.wl.study.mapper.PostsMapper;
import cn.wl.study.service.IPostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IPostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements IPostsService {

    @Autowired
    private PostsMapper postsMapper;
}