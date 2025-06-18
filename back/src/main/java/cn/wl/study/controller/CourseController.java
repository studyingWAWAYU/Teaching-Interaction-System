package cn.wl.study.controller;

import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.study.service.ICourseService;
import cn.wl.study.entity.Course;
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
@Api(tags = "课程管理接口")
@RequestMapping("/wl/course")
@Transactional
public class CourseController {
    @Autowired
    private ICourseService iCourseService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条课程")
    public Result<Course> get(@RequestParam String id){
        return new ResultUtil<Course>().setData(iCourseService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iCourseService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程")
    public Result<List<Course>> getAll(){
        return new ResultUtil<List<Course>>().setData(iCourseService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程")
    public Result<IPage<Course>> getByPage(@ModelAttribute Course course , @ModelAttribute PageVo page){
        QueryWrapper<Course> qw = new QueryWrapper<>();
        if(!WlNullUtils.isNull(course.getTitle())) {
            qw.like("title",course.getTitle());
        }
        if(!WlNullUtils.isNull(course.getContent())) {
            qw.like("content",course.getContent());
        }
        if(!WlNullUtils.isNull(course.getStatus())) {
            qw.eq("status",course.getStatus());
        }
        IPage<Course> data = iCourseService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Course>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改课程")
    public Result<Course> saveOrUpdate(Course course){
        if(iCourseService.saveOrUpdate(course)){
            return new ResultUtil<Course>().setData(course);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增课程")
    public Result<Course> insert(Course course){
        iCourseService.saveOrUpdate(course);
        return new ResultUtil<Course>().setData(course);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑课程")
    public Result<Course> update(Course course){
        iCourseService.saveOrUpdate(course);
        return new ResultUtil<Course>().setData(course);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除课程")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iCourseService.removeById(id);
        }
        return ResultUtil.success();
    }
}
