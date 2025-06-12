package cn.wl.study.controller;

import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.study.entity.Curriculum;
import cn.wl.study.service.ICurriculumService;
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
@Api(tags = "课程管理接口")
@RequestMapping("/wl/curriculum")
@Transactional
public class CurriculumController {

    @Autowired
    private ICurriculumService iCurriculumService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条课程")
    public Result<Curriculum> get(@RequestParam String id){
        return new ResultUtil<Curriculum>().setData(iCurriculumService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iCurriculumService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程")
    public Result<List<Curriculum>> getAll(){
        return new ResultUtil<List<Curriculum>>().setData(iCurriculumService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程")
    public Result<IPage<Curriculum>> getByPage(@ModelAttribute Curriculum curriculum ,@ModelAttribute PageVo page){
        QueryWrapper<Curriculum> qw = new QueryWrapper<>();
        if(!WlNullUtils.isNull(curriculum.getTitle())) {
            qw.like("title",curriculum.getTitle());
        }
        if(!WlNullUtils.isNull(curriculum.getContent())) {
            qw.like("content",curriculum.getContent());
        }
        if(!WlNullUtils.isNull(curriculum.getStatus())) {
            qw.eq("status",curriculum.getStatus());
        }
        IPage<Curriculum> data = iCurriculumService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Curriculum>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改课程")
    public Result<Curriculum> saveOrUpdate(Curriculum curriculum){
        if(iCurriculumService.saveOrUpdate(curriculum)){
            return new ResultUtil<Curriculum>().setData(curriculum);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增课程")
    public Result<Curriculum> insert(Curriculum curriculum){
        iCurriculumService.saveOrUpdate(curriculum);
        return new ResultUtil<Curriculum>().setData(curriculum);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑课程")
    public Result<Curriculum> update(Curriculum curriculum){
        iCurriculumService.saveOrUpdate(curriculum);
        return new ResultUtil<Curriculum>().setData(curriculum);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除课程")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iCurriculumService.removeById(id);
        }
        return ResultUtil.success();
    }
}
