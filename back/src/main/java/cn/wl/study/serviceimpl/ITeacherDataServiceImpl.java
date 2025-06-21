package cn.wl.study.serviceimpl;

import cn.wl.study.mapper.TeacherDataMapper;
import cn.wl.study.entity.TeacherData;
import cn.wl.study.service.ITeacherDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ITeacherDataServiceImpl extends ServiceImpl<TeacherDataMapper, TeacherData> implements ITeacherDataService {

    @Autowired
    private TeacherDataMapper teacherDataMapper;
}