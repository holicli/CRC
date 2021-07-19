package com.daniutec.crc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daniutec.crc.model.bo.Navigation;
import com.daniutec.crc.model.bo.RetailReport;
import com.daniutec.crc.model.po.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Administrator
 */
@Repository
public interface SpecMapper extends BaseMapper<UserPO> {


    /**
     * 根据日期查询采集上报周期
     *
     * @param date
     * @return
     */
    RetailReport findPeriod(String date);

    /**
     * 根据日期、零售户编号和规格编号查询上报信息是否已经存在
     *
     * @param map
     * @return
     */
    int reportExist(Map<String, Object> map);

    /**
     * 根据日期、零售户编号和规格编号查询区域上报信息是否已经存在
     *
     * @param map
     * @return
     */
    int reportExistBSC(Map<String, Object> map);

    /**
     * 根据日期和零售户编号查询香烟信息
     *
     * @param map
     * @return
     */
    List<RetailReport> findCigarette(Map<String, Object> map);

    /**
     * 根据日期和零售户编号查询区域香烟信息
     *
     * @param map
     * @return
     */
    List<RetailReport> findCigaretteBSC(Map<String, Object> map);

    /**
     * 根据日期和零售户编号查询香烟数量
     *
     * @param date
     * @return
     */
    int findCigaretteCount(String date);
    /**
     * 根据日期和零售户编号查询区域香烟数量
     *
     * @param date
     * @return
     */
    int findCigaretteCountBSC(String date);

    /**
     * 查询输入的市场流通价是否合理
     *
     * @param map
     * @return
     */
    int priceCheck(Map<String, Object> map);

    /**
     * 添加上报信息
     *
     * @param map
     */
    void addRetailReport(Map<String, Object> map);

    /**
     * 添加区域上报信息
     *
     * @param map
     */
    void addRetailReportBSC(Map<String, Object> map);

    /**
     * 删除上报信息
     *
     * @param map
     */
    void deleteRetailReport(Map<String, Object> map);

    /**
     * 删除区域上报信息
     *
     * @param map
     */
    void deleteRetailReportBSC(Map<String, Object> map);

    /**
     * 更新上报信息
     *
     * @param map
     */
    void updateRetailReport(Map<String, Object> map);
    /**
     * 更新区域上报信息
     *
     * @param map
     */
    void updateRetailReportBSC(Map<String, Object> map);

    /**
     * 查询零售户编号
     *
     * @param userId
     * @return
     */
    String findRetailNo(String userId);

    /**
     * 批量更新上报信息
     *
     * @param list
     */
    void updateRetailReports(List<Map<String, Object>> list);

    /**
     * 批量更新区域上报信息
     *
     * @param list
     */
    void updateRetailReportsBSC(List<Map<String, Object>> list);

    /**
     * 查询审核表是否有数据
     *
     * @param map
     * @return
     */
    int findCheckReport(Map<String, Object> map);

    /**
     * 查询区域审核表是否有数据
     *
     * @param map
     * @return
     */
    int findCheckReportBSC(Map<String, Object> map);

    /**
     * 新增审核数据
     *
     * @param map
     */
    void addCheckReport(Map<String, Object> map);

    /**
     * 新增区域审核数据
     *
     * @param map
     */
    void addCheckReportBSC(Map<String, Object> map);

    /**
     * 删除审核数据
     *
     * @param map
     * @return
     */
    void deleteCheckReport(Map<String, Object> map);

    /**
     * 删除区域审核数据
     *
     * @param map
     * @return
     */
    void deleteCheckReportBSC(Map<String, Object> map);

    /**
     * 更新审核数据
     *
     * @param map
     */
    void updateCheckReport(Map<String, Object> map);

    /**
     * 更新区域审核数据
     *
     * @param map
     */
    void updateCheckReportBSC(Map<String, Object> map);

    /**
     * 插入审核表
     *
     * @param map
     */
    void addData(Map<String, Object> map);

    /**
     * 插入区域审核表
     *
     * @param map
     */
    void addDataBSC(Map<String, Object> map);

    /**
     * 插入市场建议
     *
     * @param map
     */
    void addScjy(Map<String, Object> map);

    /**
     * 插入区域市场建议
     *
     * @param map
     */
    void addScjyBSC(Map<String, Object> map);


    /**
     * xxx
     * @param cigaretteNo
     * @return
     */
    List<Map<String, Object>> priceWarnings(String cigaretteNo);
}
