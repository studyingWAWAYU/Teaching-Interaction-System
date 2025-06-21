package cn.wl.study.serviceimpl;

import cn.wl.study.mapper.AssignmentMapper;
import cn.wl.study.entity.Assignment;
import cn.wl.study.service.IAssignmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IAssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements IAssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;
}