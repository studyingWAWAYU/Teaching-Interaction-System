/*
package cn.wl.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user_role")
@TableName("user_role")
@ApiModel(value = "用户角色")
public class UserRole {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "角色ID")
    private Integer roleId;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "角色名")
    private String roleName;
}

 */
