package cn.wl.study.controller;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.study.entity.PostLike;
import cn.wl.data.entity.User;
import cn.wl.study.service.IPostLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "点赞帖子接口")
@RequestMapping("/wl/course/{postId}")
@Transactional
public class PostLikeController {
    @Autowired
    private IPostLikeService iPostLikeService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/likes",method = RequestMethod.POST)
    @ApiOperation(value = "点赞帖子")
    public Result<PostLike> likePost(@PathVariable Integer postId){
        User currUser = securityUtil.getCurrUser();
        Integer userId = currUser.getId();
        return iPostLikeService.likePost(postId,userId);
    }

    @RequestMapping(value = "/unlikes",method = RequestMethod.POST)
    @ApiOperation(value = "取消点赞帖子")
    public Result<PostLike> unlikePost(@PathVariable Integer postId){
        User currUser = securityUtil.getCurrUser();
        Integer userId = currUser.getId();
        return iPostLikeService.unlikePost(postId,userId);
    }
}
