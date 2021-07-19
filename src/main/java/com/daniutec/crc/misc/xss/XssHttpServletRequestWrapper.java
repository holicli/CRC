package com.daniutec.crc.misc.xss;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * XSS过滤处理
 * @author Administrator
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    private final HtmlFilterKit htmlFilterKit = new HtmlFilterKit();

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        //非json类型，直接返回
        if(!StringUtils.equalsIgnoreCase(APPLICATION_JSON_VALUE, super.getHeader("Content-Type"))) {
            return super.getInputStream();
        }

        //为空，直接返回
        String json = StreamUtils.copyToString(super.getInputStream(), StandardCharsets.UTF_8);
        if(StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }

        //xss过滤
        json = xssEncode(json);
        ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            @Override
            public int read() {
                return bis.read();
            }
        };
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(StringUtils.isNotEmpty(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (ArrayUtils.isEmpty(parameters)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Arrays.stream(parameters).map(this::xssEncode).toArray(String[]::new);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        if(MapUtils.isNotEmpty(parameters)) {
            Map<String, String[]> map = Maps.newLinkedHashMap();
            parameters.forEach((key, value) -> {
                if(ArrayUtils.isNotEmpty(value)) {
                    map.put(key, Arrays.stream(value).map(this::xssEncode).toArray(String[]::new));
                }
            });
            return map;
        }
        return super.getParameterMap();
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if(StringUtils.isNotEmpty(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    private String xssEncode(String input) {
        return htmlFilterKit.filter(StringUtils.defaultString(input));
    }
}
