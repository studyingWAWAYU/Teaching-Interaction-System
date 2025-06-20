package cn.wl.study.serviceimpl;

import cn.wl.study.entity.AssignmentReq;
import cn.wl.study.mapper.AssignmentReqMapper;
import cn.wl.study.service.IAssignmentReqService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IAssignmentReqServiceImpl extends ServiceImpl<AssignmentReqMapper, AssignmentReq> implements IAssignmentReqService {

    @Autowired
    private AssignmentReqMapper assignmentReqMapper;
}