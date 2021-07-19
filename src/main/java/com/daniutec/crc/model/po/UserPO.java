package com.daniutec.crc.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.daniutec.crc.model.enums.GenderEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户
 * @author Administrator
 */
@Data
@TableName("pf_user")
@Accessors(chain = true)
public class UserPO implements Serializable {

    /** 用户编号 */
    @TableId(type = IdType.AUTO)

    private Integer id;

    private String userid;

    private String username;

    private String userpwd;

    private Integer yhlx;

    private Integer sfzy;

    private Integer roleId;

    private Date gxsj;
}
