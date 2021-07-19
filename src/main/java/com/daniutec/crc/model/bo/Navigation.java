package com.daniutec.crc.model.bo;


import java.util.Date;
import java.util.List;

public class Navigation {


    private Integer menuId;
    private Integer px;
    private Integer parentId;
    private String code;
    private String name;
    private String link;
    private String icon;
    private String enable;
    private String description;
    private Date addDate;

    List<Navigation> childrens;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getPx() {
        return px;
    }

    public void setPx(Integer px) {
        this.px = px;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public List<Navigation> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<Navigation> childrens) {
        this.childrens = childrens;
    }

    @Override
    public String toString() {
        return "Navigation{" +
                "menuId=" + menuId +
                ", px=" + px +
                ", parentId=" + parentId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", icon='" + icon + '\'' +
                ", enable='" + enable + '\'' +
                ", description='" + description + '\'' +
                ", addDate=" + addDate +
                ", childrens=" + childrens +
                '}';
    }
}
