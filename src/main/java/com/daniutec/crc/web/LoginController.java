package com.daniutec.crc.web;

import com.daniutec.crc.misc.WebResult;
import com.daniutec.crc.misc.WebResult.IWithoutData;
import com.daniutec.crc.model.dto.UserLoginDTO;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登陆相关
 * @author Administrator
 */
@Controller
@AllArgsConstructor
public class LoginController {

    /**
     * 登录页面
     * return 返回：/templates/login.html
     */
    @GetMapping({"", "/", "/login"})
    public String login() {
        // 自动登录
        Subject subject = SecurityUtils.getSubject();
        if(subject.isRemembered() || subject.isAuthenticated()) {
            return "redirect:/index";
        }
        return "login";
    }

    /**
     * 首页面
     * return 返回：/templates/index.html
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 登录验证
     * @param dto 登录信息
     */
    @ResponseBody
    @PostMapping("/login")
    @JsonView(IWithoutData.class)
    public WebResult<Object> checkLogin(@Validated UserLoginDTO dto) {
        UsernamePasswordToken token = new UsernamePasswordToken(dto.getUsername(), dto.getPassword().toCharArray(), dto.getRememberMe());
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        // 验证是否登录成功
        if(subject.isAuthenticated()) {
            return new WebResult<>(true, "登录成功！");
        }

        token.clear();
        return new WebResult<>(false, "暂无访问权限！");
    }

    /**
     * 无访问权限（系统内部跳转）
     * @return 无访问权限结果
     */
    @ResponseBody
    @GetMapping("/unauth")
    public String unauthorized() {
        return "<script>alert('暂无访问权限！')</script>";
    }

    /**
     * 退出系统
     * @return 退出系结果
     */
    @ResponseBody
    @GetMapping("/logout")
    @JsonView(IWithoutData.class)
    public WebResult<Object> logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return WebResult.builder().success(true).message("退出成功！").build();
    }

    /**
     * 禁止所有搜索爬虫访问网站指定目录robots.txt
     * @return robots.txt
     */
    @ResponseBody
    @GetMapping(value="/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return "User-agent: *\nDisallow: /";
    }
}
