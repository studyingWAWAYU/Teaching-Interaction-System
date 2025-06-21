package cn.wl.study.serviceimpl;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.study.entity.PostLike;
import cn.wl.study.entity.Posts;
import cn.wl.study.mapper.PostLikeMapper;
import cn.wl.study.mapper.PostsMapper;
import cn.wl.study.service.IPostLikeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Autowired
    private PostsMapper postsMapper;

    @Override
    public Result<PostLike> likePost(Integer postId, Integer userId) {
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("post_id", postId);
        PostLike existingLike = postLikeMapper.selectOne(queryWrapper);
        if (existingLike == null) {
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            updatePostLikes(postId,1);
            return new ResultUtil<PostLike>().setData(postLike);
        }else{
            return ResultUtil.error("Fail to like this post.");
        }
    }

    @Override
    public Result<PostLike> unlikePost(Integer postId, Integer userId) {
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("post_id", postId);
        PostLike existingLike = postLikeMapper.selectOne(queryWrapper);
        if (existingLike != null) {
            postLikeMapper.deleteById(existingLike.getId());
            updatePostLikes(postId,-1);
            return new ResultUtil<PostLike>().setData(existingLike);
        }else{
            return ResultUtil.error("Fail to unlike this post.");
        }
    }

    private void updatePostLikes(Integer postId, int change) {
        // 更新帖子的点赞数量
        Posts posts = postsMapper.selectById(postId);
        if (posts != null) {
            posts.setLikes(posts.getLikes() + change);
            postsMapper.updateById(posts);
        }
    }
}
