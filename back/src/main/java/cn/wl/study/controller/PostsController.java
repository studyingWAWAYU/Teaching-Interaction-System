package cn.wl.study.controller;

import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.data.entity.User;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.study.entity.Posts;
import cn.wl.study.service.IPostsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "留言管理接口")
@RequestMapping("/wl/posts")
@Transactional
public class PostsController {
    @Autowired
    private IPostsService iPostsService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条留言")
    public Result<Posts> get(@RequestParam String id){
        return new ResultUtil<Posts>().setData(iPostsService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部留言个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iPostsService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部留言")
    public Result<List<Posts>> getAll(){
        return new ResultUtil<List<Posts>>().setData(iPostsService.list());
    }

    @RequestMapping(value="/getAll/sorted_by_likes")
    @ApiOperation(value="查询全部posts，以likes降序排序")
    public Result<List<Posts>> getAllSorted(){
        List<Posts> posts = iPostsService.getAllOrderByLikesDesc();
        return new ResultUtil<List<Posts>>().setData(posts);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询留言")
    public Result<IPage<Posts>> getByPage(@ModelAttribute Posts posts , @ModelAttribute PageVo page){
        QueryWrapper<Posts> qw = new QueryWrapper<>();
        if(!WlNullUtils.isNull(posts.getContent())) {
            qw.like("content",posts.getContent());
        }
        /*
        if(!WlNullUtils.isNull(posts.getCreateBy())) {
            qw.like("user_id",posts.getCreate_by());
        }
        */

        IPage<Posts> data = iPostsService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Posts>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改留言")
    public Result<Posts> saveOrUpdate(Posts posts){
        if(iPostsService.saveOrUpdate(posts)){
            return new ResultUtil<Posts>().setData(posts);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增留言")
    public Result<Posts> insert(Posts posts){
        User currUser = securityUtil.getCurrUser();
        //posts.setCreateBy(currUser.getId());
        posts.setContent("");
        iPostsService.saveOrUpdate(posts);
        return new ResultUtil<Posts>().setData(posts);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除留言")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iPostsService.removeById(id);
        }
        return ResultUtil.success();
    }
}
