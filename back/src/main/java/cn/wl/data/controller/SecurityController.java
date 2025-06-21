package cn.wl.data.controller;

import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wl/common")
@Api(tags = "公共接口")
@Transactional
public class SecurityController {

    @RequestMapping(value = "/needLogin", method = RequestMethod.GET)
    @ApiOperation(value = "未登录返回的数据")
    public Result<Object> needLogin(){
        return ResultUtil.error(401, "登录失效");
    }
}
