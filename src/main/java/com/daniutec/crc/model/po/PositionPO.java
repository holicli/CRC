package com.daniutec.crc.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 职位
 * @author Administrator
 */
@Data
@TableName("t_position")
@Accessors(chain = true)
public class PositionPO implements Serializable {

    private static final long serialVersionUID = -721651088733930383L;

    /** 职位编号 */
    @TableId(type = IdType.AUTO)
    private Long rowId;
    /** 部门ID */
    private Long deptId;
    /** 职位名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 排序 */
    private Integer sort;
    /** 状态，0：未删除，1：已删除 */
    private Boolean deleted;
    /** 创建时间 */
    private LocalDateTime created;
    /** 创建人，对应t_user.row_id */
    private Long createBy;
    /** 修改时间 */
    private LocalDateTime modified;
}
