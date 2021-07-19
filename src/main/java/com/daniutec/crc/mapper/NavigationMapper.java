package com.daniutec.crc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daniutec.crc.model.bo.Navigation;
import com.daniutec.crc.model.po.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 用户管理
 *
 * @author Administrator
 */
@Repository
public interface NavigationMapper extends BaseMapper<UserPO> {

    List<Navigation> getnavigationListByPId(@Param("pid") int pid);
    List<Navigation> getNavigationByPid(@Param("userid") String userid);
}
