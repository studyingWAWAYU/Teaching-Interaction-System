package cn.wl.data.entity;

import cn.wl.basics.baseClass.WlBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "permission")
@TableName("permission")
@ApiModel(value = "菜单权限")
public class Permission{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "父节点ID")
    private Integer parentId;

    @ApiModelProperty(value = "菜单按钮类型")
    private Integer type;

    @ApiModelProperty(value = "菜单排序值")
    @Column(precision = 10, scale = 2)
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "前端组件名称")
    private String component;

    @ApiModelProperty(value = "页面路径")
    private String path;

    @ApiModelProperty(value = "菜单标题")
    private String title;

    @ApiModelProperty(value = "PC端图标")
    private String icon;

    @ApiModelProperty(value = "菜单层级")
    private Integer level;

    @ApiModelProperty(value = "按钮类型")
    private String buttonType;

    @ApiModelProperty(value = "启用状态")
    private Integer status = 0;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "节点展开状态")
    private Boolean expand = true;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "结点选中状态")
    private Boolean selected = false;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "结点勾选状态")
    private Boolean checked = false;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "子菜单列表")
    private List<Permission> children;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "菜单子权限列表")
    private List<String> permTypes;
}