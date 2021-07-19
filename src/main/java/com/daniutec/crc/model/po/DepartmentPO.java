package com.daniutec.crc.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName("t_department")
public class DepartmentPO implements Serializable {

    private static final long serialVersionUID = -4182059512150743307L;

    /** 部门ID */
    @TableId(type = IdType.AUTO)
    private Long rowId;
    /** 上级部门ID */
    private Long parentId;
    /** 部门名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 排序 */
    private Long sort;
    /** 状态，0：未删除，1：已删除 */
    private Boolean deleted;
    /** 创建时间 */
    private LocalDateTime created;
    /** 创建人，对应t_user.row_id */
    private Long createBy;
    /** 更新时间 */
    private LocalDateTime modified;
}