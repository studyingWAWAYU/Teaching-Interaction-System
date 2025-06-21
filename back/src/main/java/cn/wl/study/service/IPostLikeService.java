package cn.wl.study.service;

import cn.wl.basics.baseVo.Result;
import cn.wl.study.entity.PostLike;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IPostLikeService  extends IService<PostLike> {
    Result<PostLike> likePost(Integer postId, Integer userId);
    Result<PostLike> unlikePost(Integer postId, Integer userId);
}
