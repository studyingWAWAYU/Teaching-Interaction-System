package cn.wl.data.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "a_department_header")
@TableName("a_department_header")
@ApiModel(value = "部门负责人")
public class DepartmentHeader extends WlBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "部门ID")
    private Integer departmentId;

    @ApiModelProperty(value = "领导类型")
    private Integer type = 0;
}