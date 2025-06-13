package cn.wl.study.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "topics")
@TableName("topics")
@ApiModel(value = "主题")
public class Topics extends WlBaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="留言人ID")
    private Integer create_by;

    @ApiModelProperty(value = "最后一次更新时间")
    private Date update_time;

    @ApiModelProperty(value="标题")
    private String title;

    @ApiModelProperty(value="内容")
    private String description;

    @ApiModelProperty(value="隶属的课程")
    private Integer course_id;
}