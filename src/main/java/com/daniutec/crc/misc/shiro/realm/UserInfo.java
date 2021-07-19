package com.daniutec.crc.misc.shiro.realm;

import com.daniutec.crc.model.enums.GenderEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 会话中的用户基本信息
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -7401088535842961914L;

    private Integer id;

    private String userid;

    private String username;

    private String userpwd;

    private Integer yhlx;

    private Integer sfzy;

    private Integer roleId;

    private Date gxsj;

    public UserInfo(Long userId) {
        this.userid = userid;
    }
}
