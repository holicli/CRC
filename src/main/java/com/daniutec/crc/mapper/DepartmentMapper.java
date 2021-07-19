package com.daniutec.crc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daniutec.crc.misc.ibatis.MybatisRedisCache;
import com.daniutec.crc.model.po.DepartmentPO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Property;
import org.springframework.stereotype.Repository;

/**
 * 部门管理
 * @author Administrator
 */
@Repository
@CacheNamespace(
    implementation = MybatisRedisCache.class,
    properties = @Property(name = "flushInterval", value = "5000")
)
public interface DepartmentMapper extends BaseMapper<DepartmentPO> {}
