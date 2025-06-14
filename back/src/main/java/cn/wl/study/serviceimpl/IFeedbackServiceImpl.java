package cn.wl.study.serviceimpl;

import cn.wl.study.entity.Feedback;
import cn.wl.study.mapper.FeedbackMapper;
import cn.wl.study.service.IFeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IFeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {
    @Autowired
    private FeedbackMapper feedbackMapper;
}