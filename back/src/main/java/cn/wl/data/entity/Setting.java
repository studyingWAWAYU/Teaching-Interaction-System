package cn.wl.data.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "file_setting")
@TableName("file_setting")
@ApiModel(value = "配置")
@NoArgsConstructor
public class Setting{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private String id;

    @ApiModelProperty(value = "设置内容")
    private String value;
}