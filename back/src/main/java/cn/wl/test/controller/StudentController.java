package cn.wl.test.controller;

import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.data.utils.ZwzNullUtils;
import cn.wl.test.entity.Student;
import cn.wl.test.service.IStudentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "学生管理接口")
@RequestMapping("/wl/student")
@Transactional
public class StudentController {

    @Autowired
    private IStudentService iStudentService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条学生")
    public Result<Student> get(@RequestParam String id){
        return new ResultUtil<Student>().setData(iStudentService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部学生个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iStudentService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部学生")
    public Result<List<Student>> getAll(){
        return new ResultUtil<List<Student>>().setData(iStudentService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询学生")
    public Result<IPage<Student>> getByPage(@ModelAttribute Student student ,@ModelAttribute PageVo page){
        QueryWrapper<Student> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(student.getName())) {
            qw.like("name",student.getName());
        }
        if(!ZwzNullUtils.isNull(student.getNumber())) {
            qw.like("number",student.getNumber());
        }
        if(!ZwzNullUtils.isNull(student.getSex())) {
            qw.like("sex",student.getSex());
        }
        if(!ZwzNullUtils.isNull(student.getSchool())) {
            qw.like("school",student.getSchool());
        }
        IPage<Student> data = iStudentService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Student>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改学生")
    public Result<Student> saveOrUpdate(Student student){
        if(iStudentService.saveOrUpdate(student)){
            return new ResultUtil<Student>().setData(student);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增学生")
    public Result<Student> insert(Student student){
        iStudentService.saveOrUpdate(student);
        return new ResultUtil<Student>().setData(student);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑学生")
    public Result<Student> update(Student student){
        iStudentService.saveOrUpdate(student);
        return new ResultUtil<Student>().setData(student);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除学生")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iStudentService.removeById(id);
        }
        return ResultUtil.success();
    }
}
