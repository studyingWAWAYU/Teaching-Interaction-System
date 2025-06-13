package cn.wl.study.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "posts")
@TableName("posts")
@ApiModel(value = "回复")
public class Posts extends WlBaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="留言人ID")
    private Integer create_by;

    @ApiModelProperty(value = "回复时间")
    private Date create_time;

    @ApiModelProperty(value="内容")
    private String content;

    @ApiModelProperty(value="隶属的主题")
    private Integer topic_id;
}