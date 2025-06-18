package cn.wl.study.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "topics")
@TableName("topics")
@ApiModel(value = "主题")
public class Topics{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value="留言人ID")
    private Integer createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @LastModifiedDate
    @ApiModelProperty(value = "最后一次更新时间")
    private Date updateTime;

    @ApiModelProperty(value="标题")
    private String title;

    @ApiModelProperty(value="内容")
    private String description;

    @ApiModelProperty(value="隶属的课程")
    private Integer course_id;

    @ApiModelProperty(value="点赞数")
    private Integer likes;

    @ApiModelProperty(value = "相似主题")
    private String similarTopic;
}