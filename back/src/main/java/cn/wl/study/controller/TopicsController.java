package cn.wl.study.controller;

import cn.wl.basics.utils.SecurityUtil;
import cn.wl.study.service.ITopicsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "留言管理接口")
@RequestMapping("/wl/posts")
@Transactional
public class TopicsController {
    @Autowired
    private ITopicsService iTopicsService;

    @Autowired
    private SecurityUtil securityUtil;

}
