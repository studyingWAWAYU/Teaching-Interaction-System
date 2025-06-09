package cn.wl.data.controller;

import cn.wl.basics.utils.CreateVerifyCode;
import cn.wl.basics.utils.IpInfoUtil;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.basics.baseVo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @RequestMapping(value = "/ip/info", method = RequestMethod.GET)
    @ApiOperation(value = "IP信息")
    public Result<Object> upload(HttpServletRequest request) {
        return ResultUtil.data(ipInfoUtil.getIpCity(request));
    }

    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void draw(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {
        // 从redis中请求到验证码的答案
        String codeStr = redisTemplate.opsForValue().get(captchaId);
        // 传入到工具类
        CreateVerifyCode createVerifyCode = new CreateVerifyCode(116,36,4,10, codeStr);
        // 指定返回图片类型
        response.setContentType("image/png");
        // 进行写值返回
        createVerifyCode.write(response.getOutputStream());
    }

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    public Result<Object> init() {
        // 产生一个UUID随机字符串，把‘-’符号替换成空格
        String codeId = UUID.randomUUID().toString().replace("-","");
        // 再把这个随机生成的验证码放到redis里面。key是生成的随机字符串，value是验证码的答案，保存两分钟
        redisTemplate.opsForValue().set(codeId, new CreateVerifyCode().randomStr(4),2L, TimeUnit.MINUTES);
        return ResultUtil.data(codeId);
    }

    @RequestMapping(value = "/needLogin", method = RequestMethod.GET)
    @ApiOperation(value = "未登录返回的数据")
    public Result<Object> needLogin(){
        return ResultUtil.error(401, "登录失效");
    }
}
