package cn.wl.data.entity;

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

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dict_data")
@TableName("dict_data")
@ApiModel(value = "数据字典值")
public class DictData{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @ApiModelProperty(value = "数据字典值备注")
    private String description;

    @ApiModelProperty(value = "数据字典ID")
    private Integer dictId;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "数据字典名称")
    private String dictName;

    @ApiModelProperty(value = "是否启用")
    private Integer status = 0;

    @ApiModelProperty(value = "数据字典键")
    private String title;

    @ApiModelProperty(value = "数据字典值")
    private String value;

    private String createTime;
    private Integer sortOrder;

}