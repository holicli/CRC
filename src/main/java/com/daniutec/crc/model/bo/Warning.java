package com.daniutec.crc.model.bo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author Yu Z.F
 * @date 2018/8/26 10:35
 */
public class Warning implements Serializable {

    private static final long serialVersionUID = -5339233978007541512L;

    private Integer lb;
    private String ggbs;
    private Float minPrice, maxPrice;

    public Integer getLb() {
        return lb;
    }

    public void setLb(Integer lb) {
        this.lb = lb;
    }

    public String getGgbs() {
        return ggbs;
    }

    public void setGgbs(String ggbs) {
        this.ggbs = ggbs;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

