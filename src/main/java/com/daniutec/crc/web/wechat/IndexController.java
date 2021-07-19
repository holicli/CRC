package com.daniutec.crc.web.wechat;

import com.daniutec.crc.misc.shiro.annotation.Principal;
import com.daniutec.crc.misc.shiro.realm.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wechat")
public class IndexController {
    @GetMapping("index")
    public String index(@Principal UserInfo user) {
        return "wechat/index";
    }
}
