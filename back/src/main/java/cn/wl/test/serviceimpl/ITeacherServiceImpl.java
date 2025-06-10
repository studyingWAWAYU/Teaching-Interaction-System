package cn.wl.test.serviceimpl;

import cn.wl.test.mapper.TeacherMapper;
import cn.wl.test.entity.Teacher;
import cn.wl.test.service.ITeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Slf4j
@Service
@Transactional
public class ITeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
}