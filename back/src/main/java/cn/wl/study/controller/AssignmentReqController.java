package cn.wl.study.controller;

import cn.hutool.core.date.DateUtil;
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
import cn.wl.study.entity.AssignmentReq;
import cn.wl.study.entity.Course;
import cn.wl.study.entity.Timetable;
import cn.wl.study.service.IAssignmentReqService;
import cn.wl.study.service.IAssignmentService;
import cn.wl.study.service.ICourseService;
import cn.wl.study.service.ITimetableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
@Api(tags = "课程作业发布接口")
@RequestMapping("/wl/assignment_req")
@Transactional
public class AssignmentReqController {

    @Autowired
    private IAssignmentReqService iAssignmentReqService;

    @Autowired
    private ITimetableService iTimetableService;

    @Autowired
    private ICourseService iCourseService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @ApiOperation(value = "生成课程作业")
    public Result<AssignmentReq> create(@RequestParam String title, @RequestParam String status,
                                        @RequestParam String file,
                                        @RequestParam LocalDate startTime, @RequestParam LocalDate endTime,
                                        @RequestParam Integer courseId, @RequestParam String description){
        Course course = iCourseService.getById(courseId);
        if(course == null) {
            return ResultUtil.error("课程不存在");
        }
        /*
        // 删除原作业
        QueryWrapper<AssignmentReq> assQw = new QueryWrapper<>();
        assQw.eq("course_id",course.getId());
        iAssignmentReqService.remove(assQw);
        */
        // 查询所有授课
        QueryWrapper<Timetable> tQw = new QueryWrapper<>();
        tQw.eq("course_id",course.getId());
        List<Timetable> timetableList = iTimetableService.list(tQw);
        for (Timetable t : timetableList) {
            AssignmentReq a = new AssignmentReq();
            a.setCourseId(course.getId());
            a.setTitle(title);
            a.setFile(file);
            a.setDescription(description);
            a.setStartTime(startTime);
            a.setEnd_time(endTime);
            a.setUploadTime(LocalDate.now());
            a.setStatus(status);
            iAssignmentReqService.saveOrUpdate(a);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条课程作业")
    public Result<AssignmentReq> get(@RequestParam String id){
        return new ResultUtil<AssignmentReq>().setData(iAssignmentReqService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程作业个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iAssignmentReqService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部课程作业")
    public Result<List<AssignmentReq>> getAll(){
        return new ResultUtil<List<AssignmentReq>>().setData(iAssignmentReqService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询课程作业")
    public Result<IPage<AssignmentReq>> getByPage(@ModelAttribute Assignment assignment ,@ModelAttribute PageVo page){
        QueryWrapper<AssignmentReq> qw = new QueryWrapper<>();
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
        IPage<AssignmentReq> data = iAssignmentReqService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<AssignmentReq>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改课程作业")
    public Result<AssignmentReq> saveOrUpdate(AssignmentReq assignmentReq){
        if(iAssignmentReqService.saveOrUpdate(assignmentReq)){
            return new ResultUtil<AssignmentReq>().setData(assignmentReq);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增课程作业")
    public Result<AssignmentReq> insert(AssignmentReq assignmentReq){
        iAssignmentReqService.saveOrUpdate(assignmentReq);
        return new ResultUtil<AssignmentReq>().setData(assignmentReq);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑课程作业")
    public Result<AssignmentReq> update(AssignmentReq assignmentReq){
        iAssignmentReqService.saveOrUpdate(assignmentReq);
        return new ResultUtil<AssignmentReq>().setData(assignmentReq);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除课程作业")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iAssignmentReqService.removeById(id);
        }
        return ResultUtil.success();
    }

    /*
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
     */
}
