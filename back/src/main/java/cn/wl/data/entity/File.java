package cn.wl.data.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.annotation.CreatedBy;

import java.util.Date;

@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "file")
@TableName("file")
@ApiModel(value = "文件")
public class File{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Id
    @TableId
    private Integer id;

    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者")
    private Integer createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @LastModifiedDate
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "上传文件名")
    private String name;

    @ApiModelProperty(value = "文件大小")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "文件存储路径")
    private String url;

    @ApiModelProperty(value = "实际文件名")
    private String fKey;

    @ApiModelProperty(value = "存储硬盘")
    private Integer location;

}