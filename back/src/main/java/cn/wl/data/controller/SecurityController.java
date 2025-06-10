package cn.wl.data.controller;

import cn.wl.basics.utils.IpInfoUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@RestController
@RequestMapping("/wl/common")
@Api(tags = "公共接口")
@Transactional
public class SecurityController {

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @RequestMapping(value = "/ip/info", method = RequestMethod.GET)
    @ApiOperation(value = "IP信息")
    public Result<Object> upload(HttpServletRequest request) {
        return ResultUtil.data(ipInfoUtil.getIpCity(request));
    }

    @RequestMapping(value = "/needLogin", method = RequestMethod.GET)
    @ApiOperation(value = "未登录返回的数据")
    public Result<Object> needLogin(){
        return ResultUtil.error(401, "登录失效");
    }
}
