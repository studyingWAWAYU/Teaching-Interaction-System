package cn.wl.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "role")
@TableName("role")
@ApiModel(value = "角色")
public class Role{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    private LocalDate createTime;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色备注")
    private String description;

    @ApiModelProperty(value = "数据权限")
    private int dataType;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "角色拥有菜单列表")
    private List<RolePermission> permissions;
}
