package com.daniutec.crc.web;

import com.daniutec.crc.misc.WebResult;
import com.daniutec.crc.misc.WebResult.IWithoutData;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 异常拦截
 * @author 孙修瑞
 */
@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class DefaultExceptionHandler {

    private final HttpServletRequest request;

    /**
     * 无访问权限异常处理
     * @param ex 异常
     * @return 结果页面
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(UnauthorizedException.class)
    WebResult<Object> handlerUnauthorizedException(UnauthorizedException ex) {
        log.error("权限访问异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        return WebResult.builder().success(false).message("暂无访问权限！").build();
    }

    /**
     * 处理登录验证异常
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(AuthenticationException.class)
    WebResult<Object> handleAuthenticationException(AuthenticationException ex) {
        WebResult<Object> responseData = WebResult.builder().success(false).message("帐号/密码错误！").build();

        log.error("登录异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);

        if(ex instanceof UnknownAccountException) {
            responseData.setMessage("用户不存在！");
        }
        else if(ex instanceof LockedAccountException) {
            responseData.setMessage("用户已锁定！");
        }
        else if(ex instanceof DisabledAccountException) {
            responseData.setMessage("用户已停用！");
        }
        else if(ex instanceof ExcessiveAttemptsException) {
            responseData.setMessage("密码错误次数过多，请5分钟后再试！");
        }
        else if(ex instanceof ExpiredCredentialsException) {
            responseData.setMessage("登录凭证过期，请重新登录！");
        }
        else{
            responseData.setMessage("帐号/密码错误！");
        }
        return responseData;
    }

    /**
     * 使用@Valid 验证路径中请求实体校验失败后抛出的异常
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(BindException.class)
    WebResult<Object> handleBindException(BindException ex){
        log.error("验证异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        String message = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse("");
        return WebResult.builder().success(false).message(message).build();
    }

    /**
     * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(ConstraintViolationException.class)
    WebResult<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("请求参数格式错误{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        String message = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).findFirst().orElse("");
        return WebResult.builder().success(false).message(message).build();
    }

    /**
     * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    WebResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("请求参数格式错误{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        String message = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse("");
        return WebResult.builder().success(false).message(message).build();
    }

    /**
     * 处理参数异常相关的异常
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler({
        NullPointerException.class,
        IllegalArgumentException.class
    })
    WebResult<Object> handleArgumentException(Exception ex){
        log.error("参数异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        return WebResult.builder().success(false).message(ex.getMessage()).build();
    }
    /**
     * 处理参数异常相关的异常
     * @param ex 异常
     * @return 提示结果
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        ServletRequestBindingException.class
    })
    WebResult<Object> handleRequestException(Exception ex){
        log.error("参数异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        return WebResult.builder().success(false).message("参数错误！").build();
    }

    /**
     * 处理所有不可知的异常
     * @param ex 异常
     * @return 错误页面
     */
    @JsonView(IWithoutData.class)
    @ExceptionHandler(Exception.class)
    WebResult<Object> handleException(Exception ex){
        log.error("程序异常{} - {}", request.getRemoteAddr(), ex.getMessage(), ex);
        return WebResult.builder().success(false).message("网络错误！").build();
    }
}
