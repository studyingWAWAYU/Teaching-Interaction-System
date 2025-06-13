package cn.wl.data.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import cn.wl.basics.parameter.CommonConstant;
import cn.wl.data.vo.PermissionDTO;
import cn.wl.data.vo.RoleDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * @author 郑为中
 * CSDN: Designer 小郑
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
@TableName("user")
@ApiModel(value = "用户")
public class User extends WlBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "姓名")
    @NotNull(message = "姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20")
    private String name;

    @ApiModelProperty(value = "昵称")
    @NotNull(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过20")
    private String nickname;

    @ApiModelProperty(value = "学工号")
    @Column(unique = true, nullable = false)
    @Pattern(
            regexp = "^[0-9]{8,10}$",  // 只允许数字，长度8-10
            message = "学工号必须是8-10位纯数字"  // 更明确的错误提示
    )
    private String number;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式错误")
    private String mobile;

    @ApiModelProperty(value = "部门")
    private String departmentTitle;

    @ApiModelProperty(value = "邮箱")
    @Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", message = "邮箱格式错误")
    private String email;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "个人门户")
    private String myDoor;

    @ApiModelProperty(value = "头像")
    private String avatar = CommonConstant.USER_DEFAULT_AVATAR;
}
