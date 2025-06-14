package cn.wl.study.service;

import cn.wl.study.entity.Posts;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPostsService extends IService<Posts> {
    List<Posts> getAllOrderByLikesDesc();
}
