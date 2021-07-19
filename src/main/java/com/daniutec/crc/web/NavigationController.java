package com.daniutec.crc.web;

import com.daniutec.crc.service.NavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/navigation")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @GetMapping("/findMenu")
    @ResponseBody
    public Map<String, Object> findMenu(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        String loginInfo="";
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("JSUSERNAME")){
                loginInfo = cookie.getValue();
            }
        }
        Map<String, Object> data = navigationService.findMenu(loginInfo);
        return data;
    }
}