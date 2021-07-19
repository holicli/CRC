package com.daniutec.crc;

import com.daniutec.crc.model.bo.UserBO;
import com.daniutec.crc.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 单元测试
 * @author Administrator
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {

    @Autowired
    UserService service;

    @Test
    void testLogin() {
        UserBO bo = service.findByName("admin");
        Assert.assertEquals("管理员", bo.getUsername());
    }
}
