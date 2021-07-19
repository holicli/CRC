package com.daniutec.crc.service;

import com.daniutec.crc.mapper.NavigationMapper;
import com.daniutec.crc.model.bo.Navigation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NavigationService {

    @Resource
    private NavigationMapper navigationMapper;

    public Map<String,Object> findMenu(String loginInfo){
        Map<String,Object> data = new HashMap<>();
        //按照pid获取到根目录进行存储对应的子目录
        List<Navigation> navId = navigationMapper.getNavigationByPid(loginInfo);
        for(Navigation nav : navId){
            int a = nav.getMenuId();
            List<Navigation> navigationListByPId = navigationMapper.getnavigationListByPId(nav.getMenuId());
            nav.setChildrens(navigationListByPId);
        }
        data.put("menu",navId);
        return data;
    }
}
