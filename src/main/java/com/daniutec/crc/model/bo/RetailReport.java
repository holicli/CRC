package com.daniutec.crc.model.bo;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 苏浩
 */
public class RetailReport implements Serializable {

    private static final long serialVersionUID = 7646130434455187803L;

    /**
     * 上报截止日期
     * 开始日期
     * 结束日期
     */
    @JSONField(format="yyyy-MM-dd")
    private Date closeDate, startDate, endDate;

    /**
     * 采集年份
     * 采集月份
     * 采集周次
     * 上报类型
     * 状态（动销情况）
     * 货源情况（社会库存）
     */
    private Integer reportYear, reportMonth, reportWeek, reportType, saleStatus, stockStatus;

    /**
     * 采集周期
     * 公司名称
     * 图片地址
     * 规格标识
     * 规格名称
     * 建议
     */
    private String reportPeriod, company, url, cigaretteNo, cigaretteName, suggestion;

    /**
     * 批发价
     * 零售价
     * 市场流通价
     * 整条零售价
     * 单包零售价
     */
    private Double wholesalePrice, retailPrice, marketPrice, purchasePrice,cartonPrice, packPrice,minPrice,maxPrice;

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getReportYear() {
        return reportYear;
    }

    public void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }

    public Integer getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(Integer reportMonth) {
        this.reportMonth = reportMonth;
    }

    public Integer getReportWeek() {
        return reportWeek;
    }

    public void setReportWeek(Integer reportWeek) {
        this.reportWeek = reportWeek;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Integer getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(Integer stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCigaretteNo() {
        return cigaretteNo;
    }

    public void setCigaretteNo(String cigaretteNo) {
        this.cigaretteNo = cigaretteNo;
    }

    public String getCigaretteName() {
        return cigaretteName;
    }

    public void setCigaretteName(String cigaretteName) {
        this.cigaretteName = cigaretteName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Double getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(Double wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getCartonPrice() {
        return cartonPrice;
    }

    public void setCartonPrice(Double cartonPrice) {
        this.cartonPrice = cartonPrice;
    }

    public Double getPackPrice() {
        return packPrice;
    }

    public void setPackPrice(Double packPrice) {
        this.packPrice = packPrice;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
