package com.daniutec.crc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daniutec.crc.model.bo.UserBO;
import com.daniutec.crc.model.po.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 用户管理
 *
 * @author Administrator
 */
@Repository
public interface UserMapper extends BaseMapper<UserPO> {

    /**
     * 通过用户ID查找用户角色信息
     * @param userId 用户ID
     * @return 用户角色数据
     */
    Set<String> listRole(@Param("userId") String userId);

    /**
     * 通过用户ID查找用户权限信息
     * @param userId 用户ID
     * @return 用户权限数据
     */
    Set<String> listPermission(@Param("userId") String userId);

    UserPO getUserbyname(@Param("userId") String userId);
}
