package cn.wl.study.serviceimpl;

import cn.wl.study.mapper.CourseResourcesMapper;
import cn.wl.study.entity.CourseResources;
import cn.wl.study.service.ICourseResourcesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源 服务层接口实现
 * @author 郑为中
 */
@Slf4j
@Service
@Transactional
public class ICourseResourcesServiceImpl extends ServiceImpl<CourseResourcesMapper, CourseResources> implements ICourseResourcesService {

    @Autowired
    private CourseResourcesMapper courseResourcesMapper;
}