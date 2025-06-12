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

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "a_teacher_data")
@TableName("a_teacher_data")
@ApiModel(value = "教师信息")
public class TeacherData extends WlBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师姓名")
    private String name;

    @ApiModelProperty(value = "教师性别")
    private String sex;

    @ApiModelProperty(value = "教师级别")
    private String level;

    @ApiModelProperty(value = "教师简介")
    private String content;
}