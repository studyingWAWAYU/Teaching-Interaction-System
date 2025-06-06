package cn.zwz.study.controller;

import cn.zwz.basics.utils.PageUtil;
import cn.zwz.basics.utils.ResultUtil;
import cn.zwz.basics.baseVo.PageVo;
import cn.zwz.basics.baseVo.Result;
import cn.zwz.data.utils.ZwzNullUtils;
import cn.zwz.study.entity.TeacherData;
import cn.zwz.study.service.ITeacherDataService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Slf4j
@RestController
@Api(tags = "教师信息管理接口")
@RequestMapping("/zwz/teacherData")
@Transactional
public class TeacherDataController {

    @Autowired
    private ITeacherDataService iTeacherDataService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条教师信息")
    public Result<TeacherData> get(@RequestParam String id){
        return new ResultUtil<TeacherData>().setData(iTeacherDataService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部教师信息个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iTeacherDataService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部教师信息")
    public Result<List<TeacherData>> getAll(){
        return new ResultUtil<List<TeacherData>>().setData(iTeacherDataService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询教师信息")
    public Result<IPage<TeacherData>> getByPage(@ModelAttribute TeacherData teacherData ,@ModelAttribute PageVo page){
        QueryWrapper<TeacherData> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(teacherData.getName())) {
            qw.like("name",teacherData.getName());
        }
        if(!ZwzNullUtils.isNull(teacherData.getLevel())) {
            qw.eq("level",teacherData.getLevel());
        }
        IPage<TeacherData> data = iTeacherDataService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<TeacherData>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改教师信息")
    public Result<TeacherData> saveOrUpdate(TeacherData teacherData){
        if(iTeacherDataService.saveOrUpdate(teacherData)){
            return new ResultUtil<TeacherData>().setData(teacherData);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增教师信息")
    public Result<TeacherData> insert(TeacherData teacherData){
        iTeacherDataService.saveOrUpdate(teacherData);
        return new ResultUtil<TeacherData>().setData(teacherData);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑教师信息")
    public Result<TeacherData> update(TeacherData teacherData){
        iTeacherDataService.saveOrUpdate(teacherData);
        return new ResultUtil<TeacherData>().setData(teacherData);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除教师信息")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iTeacherDataService.removeById(id);
        }
        return ResultUtil.success();
    }
}
