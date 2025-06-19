package cn.wl.study.serviceimpl;

import cn.wl.study.entity.AssignmentAns;
import cn.wl.study.mapper.AssignmentAnsMapper;
import cn.wl.study.service.IAssignmentAnsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IAssignmentAnsServiceImpl extends ServiceImpl<AssignmentAnsMapper, AssignmentAns> implements IAssignmentAnsService {

    @Autowired
    private AssignmentAnsMapper assignmentAnsMapper;
}