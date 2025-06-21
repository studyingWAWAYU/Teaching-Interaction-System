package cn.wl.study.controller;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.data.entity.User;
import cn.wl.data.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

 
@Slf4j
@RestController
@Api(tags = "超级用户")
@RequestMapping("/wl/superUser")
@Transactional
public class SuperUserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部资源")
    public Result<List<User>> getUserList(@RequestParam Integer type){
        QueryWrapper<User> userQw = new QueryWrapper<>();
        userQw.eq("type",type);
        return new ResultUtil<List<User>>().setData(iUserService.list(userQw));
    }
}
