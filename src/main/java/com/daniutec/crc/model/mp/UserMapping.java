package com.daniutec.crc.model.mp;

import com.daniutec.crc.misc.shiro.realm.UserInfo;
import com.daniutec.crc.model.bo.UserBO;
import com.daniutec.crc.model.po.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户对象转换类
 * @author Administrator
 */
@Mapper(componentModel = "spring")
public interface UserMapping {

    /**
     * 将 UserPO 对象转换为 UserBO 对象
     * @param po UserPO 对象
     * @return 转换后的 UserBO 对象
     */
    UserBO po2Bo(UserPO po);

    /**
     * 将 UserBO 对象转换为 UserInfo 对象
     * @param bo UserBO 对象
     * @return 转换后的 UserInfo 对象
     */
    @Mapping(target = "userid", source = "id")
    UserInfo bo2UserInfo(UserBO bo);
}
