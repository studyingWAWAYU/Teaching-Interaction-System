package cn.wl.study.entity;

import cn.wl.data.entity.User;
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
@Table(name = "student")
@TableName("student")
@ApiModel(value = "教师信息")
public class StudentData extends User {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生年级")
    private String grade;

    @ApiModelProperty(value = "学生专业")
    private String major;
}