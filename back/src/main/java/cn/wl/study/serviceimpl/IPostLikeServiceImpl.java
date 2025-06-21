package cn.wl.study.serviceimpl;

import cn.wl.study.entity.PostLike;
import cn.wl.study.mapper.PostLikeMapper;
import cn.wl.study.service.IPostLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IPostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements IPostLikeService {
    @Autowired
    private PostLikeMapper postLikeMapper;
}
