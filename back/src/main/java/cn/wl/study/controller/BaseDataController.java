package cn.wl.study.controller;

import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.study.entity.BaseData;
import cn.wl.study.service.IBaseDataService;
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

 
@Slf4j
@RestController
@Api(tags = "基本信息管理接口")
@RequestMapping("/wl/baseData")
@Transactional
public class BaseDataController {

    @Autowired
    private IBaseDataService iBaseDataService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条基本信息")
    public Result<BaseData> get(@RequestParam String id){
        return new ResultUtil<BaseData>().setData(iBaseDataService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部基本信息个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iBaseDataService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部基本信息")
    public Result<List<BaseData>> getAll(){
        return new ResultUtil<List<BaseData>>().setData(iBaseDataService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询基本信息")
    public Result<IPage<BaseData>> getByPage(@ModelAttribute BaseData baseData ,@ModelAttribute PageVo page){
        QueryWrapper<BaseData> qw = new QueryWrapper<>();
        if(!WlNullUtils.isNull(baseData.getTitle())) {
            qw.like("title",baseData.getTitle());
        }
        if(!WlNullUtils.isNull(baseData.getContent())) {
            qw.like("content",baseData.getContent());
        }
        IPage<BaseData> data = iBaseDataService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<BaseData>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改基本信息")
    public Result<BaseData> saveOrUpdate(BaseData baseData){
        if(iBaseDataService.saveOrUpdate(baseData)){
            return new ResultUtil<BaseData>().setData(baseData);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增基本信息")
    public Result<BaseData> insert(BaseData baseData){
        iBaseDataService.saveOrUpdate(baseData);
        return new ResultUtil<BaseData>().setData(baseData);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑基本信息")
    public Result<BaseData> update(BaseData baseData){
        iBaseDataService.saveOrUpdate(baseData);
        return new ResultUtil<BaseData>().setData(baseData);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除基本信息")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iBaseDataService.removeById(id);
        }
        return ResultUtil.success();
    }
}
