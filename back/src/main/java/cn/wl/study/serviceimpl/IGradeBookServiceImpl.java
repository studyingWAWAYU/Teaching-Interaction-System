package cn.wl.study.serviceimpl;

import cn.wl.study.entity.GradeBook;
import cn.wl.study.mapper.GradeBookMapper;
import cn.wl.study.service.IFeedbackService;
import cn.wl.study.service.IGradeBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IGradeBookServiceImpl extends ServiceImpl<GradeBookMapper, GradeBook> implements IGradeBookService {
    @Autowired
    private GradeBookMapper gradeBookMapper;
}