package cn.wl.study.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

 
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "a_message")
@TableName("a_message")
@ApiModel(value = "留言")
public class Message extends WlBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "留言内容")
    private String content;

    @ApiModelProperty(value = "留言人ID")
    private Integer userId;

    @ApiModelProperty(value = "留言人")
    private String userName;

    @ApiModelProperty(value = "留言时间")
    private String time;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "回复人ID")
    private Integer replyId;

    @ApiModelProperty(value = "回复人")
    private String replyName;

    @ApiModelProperty(value = "回复时间")
    private String replyTime;
}