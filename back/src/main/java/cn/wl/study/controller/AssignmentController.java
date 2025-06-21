package cn.wl.study.controller;

import cn.wl.basics.utils.PageUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.PageVo;
import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.data.entity.User;
import cn.wl.data.service.IUserService;
import cn.wl.data.utils.WlNullUtils;
import cn.wl.data.vo.AntvVo;
import cn.wl.study.entity.Assignment;
import cn.wl.study.entity.Course;
import cn.wl.study.service.IAssignmentService;
import cn.wl.study.service.ICourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
@Api(tags = "课程作业管理接口")
@RequestMapping("/wl/assignment")
@Transactional
public class AssignmentController {

    @Autowired
    private IAssignmentService iAssignmentService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @ApiOperation(value = "生成课程作业")
    public Result<Assignment> create(@RequestParam String id,@RequestParam String title,@RequestParam String file){
        Course curriculum = iCourseService.getById(id);
        if(curriculum == null) {
            return ResultUtil.error("课程不存在");
        }
        // 删除原作业
        QueryWrapper<Assignment> assQw = new QueryWrapper<>();
        assQw.eq("curriculum_id",curriculum.getId());
        iAssignmentService.remove(assQw);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条课程作业")
    public Result<Assignment> get(@RequestParam String id){
        return new ResultUtil<Assignment>().setData(iAssignmentService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程作业个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iAssignmentService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程作业")
    public Result<List<Assignment>> getAll(){
        return new ResultUtil<List<Assignment>>().setData(iAssignmentService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程作业")
    public Result<IPage<Assignment>> getByPage(@ModelAttribute Assignment assignment ,@ModelAttribute PageVo page){
        QueryWrapper<Assignment> qw = new QueryWrapper<>();
        User currUser = securityUtil.getCurrUser();
        QueryWrapper<User> userQw = new QueryWrapper<>();
        userQw.eq("id",currUser.getId());
        //userQw.inSql("id","SELECT user_id FROM user_role WHERE role_id =2 ");
        userQw.inSql("id","SELECT id FROM user WHERE role_id =2 ");
        if(iUserService.count(userQw) < 1L) {
            qw.eq("user_id",currUser.getId());
        }
        /*
        if(!WlNullUtils.isNull(assignment.getCourseName())) {
            qw.like("course_name",assignment.getCourseName());
        }

         */
        if(!WlNullUtils.isNull(assignment.getTitle())) {
            qw.like("title",assignment.getTitle());
        }
        IPage<Assignment> data = iAssignmentService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<Assignment>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改课程作业")
    public Result<Assignment> saveOrUpdate(Assignment assignment){
        if(iAssignmentService.saveOrUpdate(assignment)){
            return new ResultUtil<Assignment>().setData(assignment);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增课程作业")
    public Result<Assignment> insert(Assignment assignment){
        iAssignmentService.saveOrUpdate(assignment);
        return new ResultUtil<Assignment>().setData(assignment);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑课程作业")
    public Result<Assignment> update(Assignment assignment){
        iAssignmentService.saveOrUpdate(assignment);
        return new ResultUtil<Assignment>().setData(assignment);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除课程作业")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iAssignmentService.removeById(id);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传作业")
    public Result<Object> upload(@RequestParam String id,@RequestParam String file){
        Assignment ass = iAssignmentService.getById(id);
        if(ass == null) {
            return ResultUtil.error("作业不存在");
        }
        ass.setFile2(file);
        iAssignmentService.saveOrUpdate(ass);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getAntvVoList", method = RequestMethod.GET)
    @ApiOperation(value = "查询图表数据")
    public Result<List<AntvVo>> getAntvVoList(){
        List<AntvVo> ansList = new ArrayList<>();
        List<Assignment> teacherList = iAssignmentService.list();
        for (Assignment o : teacherList) {
            boolean flag = false;
            for (AntvVo vo : ansList) {
                if(Objects.equals(vo.getTitle(),o.getCurriculumName())) {
                    flag = true;
                    vo.setValue(vo.getValue().add(BigDecimal.ONE));
                    break;
                }
            }
            if(!flag) {
                AntvVo vo = new AntvVo();
                vo.setTitle(o.getCurriculumName());
                vo.setType("作业数量");
                vo.setValue(BigDecimal.ONE);
                ansList.add(vo);
            }
        }
        return new ResultUtil<List<AntvVo>>().setData(ansList);
    }
}
