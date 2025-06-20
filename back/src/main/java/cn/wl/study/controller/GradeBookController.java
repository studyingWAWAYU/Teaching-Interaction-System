package cn.wl.study.controller;

import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.study.entity.GradeBook;
import cn.wl.study.entity.Course;
import cn.wl.study.service.IGradeBookService;
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
@Api(tags = "成绩管理接口")
@RequestMapping("/wl/gradeBook")
@Transactional
public class GradeBookController {
    @Autowired
    private IGradeBookService iGradeBookService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条成绩")
    public Result<GradeBook> get(@RequestParam String id){
        return new ResultUtil<GradeBook>().setData(iGradeBookService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部成绩个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iGradeBookService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部成绩")
    public Result<List<GradeBook>> getAll(){
        return new ResultUtil<List<GradeBook>>().setData(iGradeBookService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程成绩")
    public Result<IPage<GradeBook>> getByPage(@ModelAttribute Course course , @ModelAttribute PageVo page){
        QueryWrapper<GradeBook> qw = new QueryWrapper<>();
        qw.eq("course_id",course.getId());
        IPage<GradeBook> data = iGradeBookService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<GradeBook>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改成绩")
    public Result<GradeBook> saveOrUpdate(GradeBook gradeBook){
        if(iGradeBookService.saveOrUpdate(gradeBook)){
            return new ResultUtil<GradeBook>().setData(gradeBook);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增成绩")
    public Result<GradeBook> insert(GradeBook gradeBook){
        iGradeBookService.saveOrUpdate(gradeBook);
        return new ResultUtil<GradeBook>().setData(gradeBook);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑成绩")
    public Result<GradeBook> update(GradeBook gradeBook){
        iGradeBookService.saveOrUpdate(gradeBook);
        return new ResultUtil<GradeBook>().setData(gradeBook);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除成绩")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iGradeBookService.removeById(id);
        }
        return ResultUtil.success();
    }
}
