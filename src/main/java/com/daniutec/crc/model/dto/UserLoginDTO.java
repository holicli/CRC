package com.daniutec.crc.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = -6890270832608389164L;

    /** 账号 */
    @NotBlank(message = "账号不能为空！")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空！")
    private String password;

    /** 记住登录，下次自动登录 */
    private Boolean rememberMe = Boolean.FALSE;
}
