package com.daniutec.crc.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daniutec.crc.mapper.UserMapper;
import com.daniutec.crc.model.bo.UserBO;
import com.daniutec.crc.model.mp.UserMapping;
import com.daniutec.crc.model.po.UserPO;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * 用户相关
 * @author Administrator
 */
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserPO> {

    private final UserMapping mapping;

    /**
     * 根据用户名、邮箱、手机号查找用户信息
     * @param name 查询条件
     * @return 查询结果对象
     */
    public UserBO findByName(String name) {
        Preconditions.checkArgument(StringUtils.isNotBlank(StringUtils.trim(name)), "用户名/手机号/邮箱不能为空！");

        final String tempName = StringUtils.lowerCase(StringUtils.trim(name));
//        LambdaQueryWrapper<UserPO> queryWrapper = Wrappers.<UserPO>lambdaQuery().eq(UserPO::getUserid, tempName);

//        UserPO po = getOne(queryWrapper);
        UserPO po = getBaseMapper().getUserbyname(tempName);
        return mapping.po2Bo(Optional.ofNullable(po).orElse(new UserPO()));
    }

    /**
     * 通过用户ID查找用户角色信息
     * @param userId 用户ID
     * @return 用户角色数据
     */
    public Set<String> listRole(String userId) {
        Preconditions.checkNotNull(userId, "用户编号不能为空！");
        return getBaseMapper().listRole(userId);
    }

    /**
     * 通过用户ID查找用户权限信息
     * @param userId 用户ID
     * @return 用户权限数据
     */
    public Set<String> listPermission(String userId) {
        Preconditions.checkNotNull(userId, "用户编号不能为空！");
        return getBaseMapper().listPermission(userId);
    }
}
