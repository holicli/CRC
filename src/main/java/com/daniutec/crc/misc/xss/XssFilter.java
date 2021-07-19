package com.daniutec.crc.misc.xss;

import org.apache.shiro.web.util.WebUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * XSS过滤器只能过滤form表单形式提交的参数
 * @author Administrator
 */
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig config) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(WebUtils.toHttp(request));
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {}
}
