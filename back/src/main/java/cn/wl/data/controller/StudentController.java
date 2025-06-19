package cn.wl.data.controller;


import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.data.entity.User;
import cn.wl.data.service.IUserService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/wl/student")
@Api(tags = "学生管理接口")
public class StudentController {

    private static final int STUDENT_ROLE_ID = 0; // 学生角色role_id为0

    @Autowired
    private IUserService iUserService;

    /**
     * 查询所有学生列表
     */
    @GetMapping("/list")
    @ApiOperation("查询学生列表")
    public Result<List<User>> getStudentList() {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("role_id", STUDENT_ROLE_ID);
        List<User> list = iUserService.list(qw);
        list.forEach(u -> u.setPassword(null)); // 去除密码字段
        return ResultUtil.data(list);
    }


    /**
     * 添加学生
     */
    @PostMapping("/add")
    @ApiOperation("添加学生")
    public Result<Object> addStudent(@Valid @RequestBody User u) {
        if (StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())) {
            return ResultUtil.error("用户名和密码不能为空");
        }

        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", u.getUsername());
        if (iUserService.count(qw) > 0L) {
            return ResultUtil.error("用户名已存在");
        }

        u.setRoleId(STUDENT_ROLE_ID);
        u.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(u.getPassword()));
        iUserService.save(u);
        return ResultUtil.success("添加成功");
    }

    /**
     * 编辑学生
     */
    @PostMapping("/edit")
    @ApiOperation("编辑学生信息")
    public Result<Object> editStudent(@RequestBody User u) {
        User old = iUserService.getById(u.getId());
        if (old == null || !Objects.equals(old.getRoleId(), STUDENT_ROLE_ID)) {
            return ResultUtil.error("该用户不是学生");
        }

        u.setUsername(old.getUsername()); // 禁止改用户名
        u.setPassword(old.getPassword()); // 保留原密码
        u.setRoleId(STUDENT_ROLE_ID);     // 保持学生身份
        iUserService.saveOrUpdate(u);
        return ResultUtil.success("修改成功");
    }

    /**
     * 删除学生
     */
    @PostMapping("/delete")
    @ApiOperation("删除学生")
    public Result<Object> deleteStudent(@RequestParam String id) {
        User student = iUserService.getById(id);
        if (student == null || !Objects.equals(student.getRoleId(), STUDENT_ROLE_ID)) {
            return ResultUtil.error("该用户不是学生");
        }

        iUserService.removeById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 查询学生详情
     */
    @GetMapping("/detail")
    @ApiOperation("获取学生详情")
    public Result<User> getStudentDetail(@RequestParam String id) {
        User student = iUserService.getById(id);
        if (student == null || !Objects.equals(student.getRoleId(), STUDENT_ROLE_ID)) {
            return ResultUtil.error("该用户不是学生");
        }

        student.setPassword(null);
        return ResultUtil.data(student);
    }
}

