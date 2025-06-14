package cn.wl.study.controller;

import cn.hutool.core.date.DateUtil;
import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.data.entity.User;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.study.entity.Feedback;
import cn.wl.study.entity.Course;
import cn.wl.study.service.IFeedbackService;
import cn.wl.study.service.ICourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@Api(tags = "课程评价管理接口")
@RequestMapping("/wl/feedback")
@Transactional
public class FeedbackController {

    @Autowired
    private IFeedbackService iFeedbackService;
    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/addOne", method = RequestMethod.GET)
    @ApiOperation(value = "添加课程评价")
    public Result<Object> addOne(@RequestParam String id,@RequestParam String content){
        Course course = iCourseService.getById(id);
        if(course == null) {
            return ResultUtil.error("课程不存在");
        }
        User currUser = securityUtil.getCurrUser();
        Feedback a = new Feedback();
        a.setCourseId(course.getId());
        a.setCreateBy(currUser.getId());
        a.setContent(content);
        a.setCreateTime(new Date(DateUtil.now()));
        iFeedbackService.saveOrUpdate(a);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条课程评价")
    public Result<Feedback> get(@RequestParam String id){
        return new ResultUtil<Feedback>().setData(iFeedbackService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程评价个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iFeedbackService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程评价")
    public Result<List<Feedback>> getAll(){
        return new ResultUtil<List<Feedback>>().setData(iFeedbackService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程评价")
    public Result<IPage<Feedback>> getByPage(@ModelAttribute Feedback feedback ,@ModelAttribute PageVo page){
        QueryWrapper<Feedback> qw = new QueryWrapper<>();
        /*
        if(!WlNullUtils.isNull(feedback.getCourseName())) {
            qw.like("course_name",feedback.getCourseName());
        }
        if(!WlNullUtils.isNull(feedback.getUserName())) {
            qw.like("user_name",feedback.getUserName());
        }
        */
        if(!WlNullUtils.isNull(feedback.getContent())) {
            qw.like("content",feedback.getContent());
        }
        IPage<Feedback> data = iFeedbackService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Feedback>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改课程评价")
    public Result<Feedback> saveOrUpdate(Feedback feedback){
        if(iFeedbackService.saveOrUpdate(feedback)){
            return new ResultUtil<Feedback>().setData(feedback);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增课程评价")
    public Result<Feedback> insert(Feedback feedback){
        iFeedbackService.saveOrUpdate(feedback);
        return new ResultUtil<Feedback>().setData(feedback);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑课程评价")
    public Result<Feedback> update(Feedback feedback){
        iFeedbackService.saveOrUpdate(feedback);
        return new ResultUtil<Feedback>().setData(feedback);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除课程评价")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iFeedbackService.removeById(id);
        }
        return ResultUtil.success();
    }
}
