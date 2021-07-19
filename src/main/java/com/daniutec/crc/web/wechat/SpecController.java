package com.daniutec.crc.web.wechat;

import com.daniutec.crc.misc.WebResult;
import com.daniutec.crc.misc.shiro.annotation.Principal;
import com.daniutec.crc.misc.shiro.realm.UserInfo;
import com.daniutec.crc.service.wechat.SpecService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/wechat/spec")
public class SpecController {

    @Autowired
    private SpecService service;

    /**priceReport
     * 采集上报页面
     *
     * @param user 用户编号
     * @return 返回采集上报页面：WEB-INF/views/wechat/price.jsp
     */
    @GetMapping("index")
    public String index(@Principal UserInfo user) {
            return "wechat/ggreport";
    }

    @ResponseBody
    @PostMapping("getReportList")
    public WebResult<Map<String, Object>> getReportList(@Principal UserInfo user){
        return service.findReport(user.getUserid());
    }

    /**priceReport
     * 区域采集上报页面
     *
     * @param user 用户编号
     * @return 返回采集上报页面：WEB-INF/views/wechat/price.jsp
     */
    @GetMapping("indexbsc")
    public ModelAndView indexbsc(@Principal User user) {
        return new ModelAndView("price_bsc", "report", service.findReportBSC(user.getUsername()));
    }

    /**
     * 查询输入的市场流通价是否合理
     *
     * @param map
     * @return
     */
    @ResponseBody
    @PostMapping("priceCheck")
    public WebResult<Object> priceCheck(@RequestParam Map<String, Object> map) {
        return service.priceCheck(map);
    }


    @ResponseBody
    @PostMapping("priceWarnings")
    public Map<String, Object> priceWarnings(String cigaretteNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("maxCarton", "20000.00");
        map.put("maxMarket", "20000.00");
        map.put("maxPack", "2000.00");
        map.put("maxPurchase", "15000.00");
        map.put("minCarton", "0.00");
        map.put("minMarket", "0.00");
        map.put("minPack", "0.00");
        map.put("minPurchase", "0.00");
//        return service.priceWarnings(cigaretteNo);
        return map;
    }


    /**
     * 上报信息处理
     *
     * @param map    参数
     * @param user 用户编号
     * @return
     */
    @ResponseBody
    @PostMapping("priceReport")
    public WebResult<Object> priceReport(@RequestParam Map<String, Object> map, @Principal UserInfo user) {
        map.put("userId", user.getUserid());
        return service.priceReport(map);
    }

    /**
     * 上报信息处理
     *
     * @param map    参数
     * @param user 用户编号
     * @return
     */
    @ResponseBody
    @PostMapping("priceReport_BSC")
    public WebResult<Object> priceReportBSC(@RequestParam Map<String, Object> map, @Principal User user) {
        map.put("userId", user.getUsername());
        return service.priceReportBSC(map);
    }

    /**
     * 提交更新上报信息
     *
     * @param map    参数
     * @param user 用户编号
     * @return
     */
    @ResponseBody
    @PostMapping("addData")
    public WebResult<Object> addData(@RequestParam Map<String, Object> map, @Principal User user) {
        map.put("userId", user.getUsername());
        return service.addData(map);
    }


    /**
     * 删除审核数据
     *
     * @param closeDate
     * @param user   用户编号
     * @return
     */
    @ResponseBody
    @PostMapping("deleteData")
    public WebResult<Object> deleteData(String closeDate, @Principal User user) {
        return service.deleteData(closeDate, user.getUsername());
    }


    /**
     * 插入市场建议
     *
     * @param map
     * @param user 用户编号
     * @return
     */
    @ResponseBody
    @PostMapping("addScjy")
    public WebResult<Object> addScjy(@RequestParam Map<String, Object> map, @Principal User user) {
        map.put("userId", user.getUsername());
        return service.addScjy(map);
    }

}
