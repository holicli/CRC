package com.daniutec.crc.model.bo;

import com.daniutec.crc.model.enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户对象，业务逻辑层
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class UserBO implements Serializable {

    private static final long serialVersionUID = -4215424402589841194L;

    private Integer id;

    private String userid;

    private String username;

    private String userpwd;

    private Integer yhlx;

    private Integer sfzy;

    private Integer roleId;

    private Date gxsj;
}
