package cn.wl.data.entity;

import cn.wl.basics.parameter.CommonConstant;
import cn.wl.data.vo.PermissionDTO;
import cn.wl.data.vo.RoleDTO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
@TableName("user")
@ApiModel(value = "用户")
@EntityListeners(AuditingEntityListener.class)
public class User {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @ApiModelProperty(value = "创建时间")
    private LocalDate create_time;

    @ApiModelProperty(value = "真实姓名")
    @NotNull(message = "Real name cannot be emtpy")
    @Size(max = 30, message = "Name length cannot exceed 30 characters.")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "Password cannot be empty.")
    private String password;

    @ApiModelProperty(value = "网名")
    @Size(max = 20, message = "Nickname length cannot exceed 20 characters.")
    private String nickname;

    @ApiModelProperty(value = "邮箱")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\\\.[a-zA-Z0-9-]+)*\\\\.[a-zA-Z]{2,}$", message = "Email format is incorrect.")
    private String email;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^(?:\\\\+86)?1[3-9]\\\\d{9}$", message = "Phone number format is incorrect.")
    private String mobile;


    @ApiModelProperty(value = "学工号")
//    @NotNull(message = "Teacher/Student ID cannot be empty.")
    private String number;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "头像")
    private String avatar = CommonConstant.USER_DEFAULT_AVATAR;

    @ApiModelProperty(value = "部门ID")
    private String departmentId;

    @ApiModelProperty(value = "个人门户")
    private String myDoor;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "用户拥有的菜单列表")
    private List<PermissionDTO> permissions;
/*
    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "用户拥有的角色")
    private List<RoleDTO> roles;


 */
    @ApiModelProperty(value = "用户角色")
    private Integer roleId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "角色对象")
    private RoleDTO role;


}
