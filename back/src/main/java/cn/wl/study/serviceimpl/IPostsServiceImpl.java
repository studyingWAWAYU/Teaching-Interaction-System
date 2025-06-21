package cn.wl.study.serviceimpl;

import cn.wl.basics.baseVo.Result;
import cn.wl.basics.utils.ResultUtil;
import cn.wl.study.entity.PostLike;
import cn.wl.study.entity.Posts;
import cn.wl.study.mapper.PostLikeMapper;
import cn.wl.study.mapper.PostsMapper;
import cn.wl.study.service.IPostsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Service
@Transactional
public class IPostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements IPostsService {

    @Autowired
    private PostsMapper postsMapper;
    @Autowired
    private PostLikeMapper postLikeMapper;

    // 根据post_id和topic_id查询单个post
    @Override
    public Posts getByIdAndTopicId(Integer id, Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id)
                .eq("topic_id", topicId);
        return this.getOne(queryWrapper);
    }

    // 根据topic_id查询回复数量
    @Override
    public Long countByTopicId(Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId);
        return this.count(queryWrapper);
    }

    // 根据topic_id查询该课程下的所有回复
    @Override
    public List<Posts> listByTopicId(Integer topicId){
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId);
        return this.list(queryWrapper);
    }

    // 根据topic_id查询该课程下的所有回复，按likes降序排序
    @Override
    public List<Posts> getAllByTopicIdOrderByLikesDesc(Integer topicId) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId)
                .orderByDesc("likes");
        return this.list(queryWrapper);
    }

    @Override
    public Result<Posts> saveOrUpdatePosts(Posts posts){
        posts.setContent(SensitiveWordHelper.replace(posts.getContent()));
        if(saveOrUpdate(posts)){
            return new ResultUtil<Posts>().setData(posts);
        }
        return ResultUtil.error();
    }

    @Override
    public String getSummary(String postsContent){
        try {
            String[] args = new String[]{"python", "back/src/main/java/cn/wl/study/serviceimpl/TextRank4.py", postsContent};
            Process proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF-8"));
            BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String lines = "";
            String line;
            while ((line = in.readLine()) != null) {
                lines += line;
            }
            while((line = err.readLine())!=null){
                lines += line;
            }
            in.close();
            err.close();
            proc.waitFor();
            return lines;
        } catch (Exception e){
            e.printStackTrace();
            return "Fail to generate keywords and summary";
        }
    }


}