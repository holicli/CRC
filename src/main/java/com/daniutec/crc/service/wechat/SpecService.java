package com.daniutec.crc.service.wechat;

import com.daniutec.crc.mapper.NavigationMapper;
import com.daniutec.crc.mapper.SpecMapper;
import com.daniutec.crc.misc.WebResult;
import com.daniutec.crc.model.bo.Navigation;
import com.daniutec.crc.model.bo.RetailReport;
import com.xhinliang.lunarcalendar.LunarCalendar;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SpecService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecService.class);

    @Autowired
    private SpecMapper mapper;

    /**
     * 查询采集上报周期
     *
     * @return
     */
    private RetailReport findPeriod() {
        RetailReport report = mapper.findPeriod(calDate());
        if (report.getReportType() != null && 1 == report.getReportType()) {
            report.setReportPeriod(new StringBuilder().append(report.getReportYear()).append("年")
                    .append(report.getReportMonth()).append("月第")
                    .append(report.getReportWeek()).append("周")
                    .append(DateFormatUtils.format(report.getStartDate(), "MM-dd"))
                    .append("~").append(DateFormatUtils.format(report.getEndDate(), "MM-dd")).toString());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(report.getCloseDate());
            cal.setTimeZone(TimeZone.getDefault());
            LunarCalendar lunar = LunarCalendar.obtainCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            report.setReportPeriod(
                    new StringBuilder().append(lunar.getFullLunarStr()).append(" ").append(DateFormatUtils.format(report.getStartDate(), "MM-dd")).append("~").append(DateFormatUtils.format(report.getEndDate(), "MM-dd")).toString());
        }
        return report;
    }

    /**
     * 查询采集报告
     *
     * @param userId
     * @return
     */
    public WebResult<Map<String, Object>> findReport(String userId) {
        RetailReport report = findPeriod();
        Map<String, Object> map = new HashMap<>(2),
                resultMap = new HashMap<>(4);
        String date = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getCloseDate());
        map.put("date",  formatDate(report.getCloseDate(), "yyyy-MM-dd"));
        map.put("userId", userId);
        resultMap.put("reportDate", report);
        resultMap.put("cigarette", mapper.findCigarette(map));
        resultMap.put("counts", mapper.findCigaretteCount(date));
        return new WebResult<Map<String, Object>>(true).setData(resultMap);
    }
    public static String formatDate(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    /**
     * 查询区域采集报告
     *
     * @param userId
     * @return
     */
    public WebResult<Map<String, Object>> findReportBSC(String userId) {
        RetailReport report = findPeriod();
        Map<String, Object> map = new HashMap<>(2),
                resultMap = new HashMap<>(4);
        String date = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getCloseDate());
        map.put("date", date);
        map.put("userId", userId);
        resultMap.put("reportDate", report);
        resultMap.put("cigarette", mapper.findCigaretteBSC(map));
        resultMap.put("counts", mapper.findCigaretteCountBSC(date));
        return new WebResult<Map<String, Object>>(true).setData(resultMap);
    }

    /**
     * 上报信息处理
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebResult<Object> priceReport(Map<String, Object> map) {
        try {
            map.put("suggestion", URLDecoder.decode(MapUtils.getString(map, "suggestion"), StandardCharsets.UTF_8.name()));
            String marketPrice = MapUtils.getString(map, "marketPrice");
            String purchasePrice = MapUtils.getString(map, "purchasePrice");
            String cartonPrice = MapUtils.getString(map, "cartonPrice");
            String packPrice = MapUtils.getString(map, "packPrice");

            RetailReport report = mapper.findPeriod(MapUtils.getString(map, "closeDate"));
            map.put("marketPrice", "".equals(marketPrice) ? null : Double.parseDouble(marketPrice));
            map.put("purchasePrice", "".equals(purchasePrice) ? null : Double.parseDouble(purchasePrice));
            map.put("cartonPrice", "".equals(cartonPrice) ? null : Double.parseDouble(cartonPrice));
            map.put("packPrice", "".equals(packPrice) ? null : Double.parseDouble(packPrice));
            map.put("reportType", report.getReportType());
            map.put("startDate", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getStartDate()));
            map.put("endDate", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getEndDate()));
            map.put("retailNo", mapper.findRetailNo(map.get("userId").toString()));

            if ("".equals(marketPrice) && "".equals(cartonPrice) && "".equals(packPrice)) {
                mapper.deleteRetailReport(map);
            } else {
                if (mapper.reportExist(map) > 0) {
                    mapper.updateRetailReport(map);
                } else {
                    mapper.addRetailReport(map);
                }
            }
        } catch (UnsupportedEncodingException e) {
            return new WebResult<>(false);
        }
        return new WebResult<>(true);
    }
    /**
     * 区域上报信息处理
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebResult<Object> priceReportBSC(Map<String, Object> map) {
        try {
            map.put("suggestion", URLDecoder.decode(MapUtils.getString(map, "suggestion"), StandardCharsets.UTF_8.name()));
            String marketPrice = MapUtils.getString(map, "marketPrice");
            String purchasePrice = MapUtils.getString(map, "purchasePrice");
            String cartonPrice = MapUtils.getString(map, "cartonPrice");
            String packPrice = MapUtils.getString(map, "packPrice");

            RetailReport report = mapper.findPeriod(MapUtils.getString(map, "closeDate"));
            map.put("marketPrice", "".equals(marketPrice) ? null : Double.parseDouble(marketPrice));
            map.put("purchasePrice", "".equals(purchasePrice) ? null : Double.parseDouble(purchasePrice));
            map.put("cartonPrice", "".equals(cartonPrice) ? null : Double.parseDouble(cartonPrice));
            map.put("packPrice", "".equals(packPrice) ? null : Double.parseDouble(packPrice));
            map.put("reportType", report.getReportType());
            map.put("startDate", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getStartDate()));
            map.put("endDate", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(report.getEndDate()));
            map.put("retailNo", mapper.findRetailNo(map.get("userId").toString()));

            if ("".equals(marketPrice) && "".equals(cartonPrice) && "".equals(packPrice)) {
                mapper.deleteRetailReportBSC(map);
            } else {
                if (mapper.reportExistBSC(map) > 0) {
                    mapper.updateRetailReportBSC(map);
                } else {
                    mapper.addRetailReportBSC(map);
                }
            }
        } catch (UnsupportedEncodingException e) {
            return new WebResult<>(false);
        }
        return new WebResult<>(true);
    }

    /**
     * 查询输入的市场流通价是否合理
     *
     * @param report
     * @return
     */
    public WebResult<Object> priceCheck(Map<String, Object> report) {
        return new WebResult<>(mapper.priceCheck(report) > 0);
    }

    /**
     * 提交更新上报信息
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebResult<Object> addData(Map<String, Object> map) {

        Date date = new Date();
        List<Map<String, Object>> list = new ArrayList<>();
        String closeDateStr = map.get("closeDate").toString();
        String[] cigNo = map.get("cigNo").toString().split(";");
        Integer userId = Integer.valueOf(map.get("userId").toString());
        RetailReport report = mapper.findPeriod(closeDateStr);
        map.put("reportType", report.getReportType());
        String closeDateStr2 = getSpecifiedDay(closeDateStr, 1) + " 06:00:00";
        if (cigNo.length > 0) {
            Map<String, Object> commitMap;
            for (String string : cigNo) {
                commitMap = new HashMap<>(4);
                commitMap.put("cigaretteNo", string);
                commitMap.put("userId", userId);
                commitMap.put("closeDate", closeDateStr);
                list.add(commitMap);
            }
        }
        try {
            Date closeDate = DateUtils.parseDate(closeDateStr2, "yyyy-MM-dd HH:mm:ss");
            if (date.getTime() > closeDate.getTime()) {
                return new WebResult<>(false, "上报已截止，请下次按时上报。");
            } else {
                mapper.updateRetailReports(list);
                int total = mapper.findCheckReport(map);
                if (total > 0) {
                    mapper.updateCheckReport(map);
                } else {
                    mapper.addCheckReport(map);
                }
                mapper.addData(map);
            }
        } catch (ParseException ex) {
            LOG.error("提交更新上报信息异常- {}", ex.getMessage(), ex);
            return new WebResult<>(false);
        }
        return new WebResult<>(true);
    }


    /**
     * 删除审核数据
     *
     * @param closeDate
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebResult<Object> deleteData(String closeDate, String userId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("closeDate", closeDate);
        map.put("userId", userId);
        mapper.deleteCheckReport(map);
        return new WebResult<>(true);
    }

    /**
     * 插入市场建议
     *
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebResult<Object> addScjy(Map<String, Object> map) {
        try {
            map.put("data", URLDecoder.decode(map.get("data").toString(), StandardCharsets.UTF_8.name()));
            mapper.addScjy(map);
        } catch (UnsupportedEncodingException e) {
            return new WebResult<>(false);
        }
        return new WebResult<>(true);
    }

    /**
     * 计算是否超过采集周期
     *
     * @return
     */
    private String calDate() {
        String date = null;
        try {
            Date now = new Date();
            Date sysDate;
            sysDate = DateUtils.parseDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd 06:00:00"), "yyyy-MM-dd HH:mm:ss");
            String nowStr = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(now);
            if (System.currentTimeMillis() > sysDate.getTime()) {
                date = nowStr;
            } else {
                date = getSpecifiedDay(nowStr, 0);
            }
        } catch (ParseException e) {
            LOG.error("日期转换错误- {}", e.getMessage(), e);
        }
        return date;
    }

    /**
     * 获取指定日期前一天（0）或后一天（1）
     *
     * @param specifiedDay
     * @param cal
     * @return
     */
    private String getSpecifiedDay(String specifiedDay, int cal) {
        Calendar caldendar = Calendar.getInstance();
        Date date;
        try {
            date = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse(specifiedDay);
            caldendar.setTime(date);
        } catch (ParseException e) {
            LOG.error("日期格式化异常- {}", e.getMessage(), e);
        }

        int day = caldendar.get(Calendar.DATE);
        switch (cal) {
            case 0:
                caldendar.set(Calendar.DATE, day - 1);
                break;
            case 1:
                caldendar.set(Calendar.DATE, day + 1);
                break;
            default:
                throw new AssertionError("获取日期错误");
        }
        return DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(caldendar);
    }


    public Map<String, Object> priceWarnings(String cigaretteNo) {

        List<Map<String, Object>> warnings = mapper.priceWarnings(cigaretteNo);
        Map<String, Object> map = new HashMap<>(8);

        for (int i = 0; i < warnings.size(); i++) {
            if (i == 0) {
                Map<String, Object> market = warnings.get(0);
                String minMarket = (market.get("jg_min")).toString();
                String maxMarket = (market.get("jg_max")).toString();
                map.put("minMarket", minMarket);
                map.put("maxMarket", maxMarket);
            }
            if (i == 1) {
                Map<String, Object> carton = warnings.get(1);
                String minCarton = (carton.get("jg_min")).toString();
                String maxCarton = (carton.get("jg_max")).toString();
                map.put("minCarton", minCarton);
                map.put("maxCarton", maxCarton);
            }
            if (i == 2) {
                Map<String, Object> pack = warnings.get(2);
                String minPack = (pack.get("jg_min")).toString();
                String maxPack = (pack.get("jg_max")).toString();
                map.put("minPack", minPack);
                map.put("maxPack", maxPack);
            }
            if (i == 3) {
                Map<String, Object> purchase = warnings.get(3);
                String minPurchase = (purchase.get("jg_min")).toString();
                String maxPurchase = (purchase.get("jg_max")).toString();
                map.put("minPurchase", minPurchase);
                map.put("maxPurchase", maxPurchase);
            }
        }
        return map;
    }
}
