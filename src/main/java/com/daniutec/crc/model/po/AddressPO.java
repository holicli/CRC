package com.daniutec.crc.model.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 行政区域，数据来源：国家统计局统计用区划和城乡划分代码
 * @author Administrator
 */
@Data
@TableName("t_address")
@Accessors(chain = true)
public class AddressPO implements Serializable {

    private static final long serialVersionUID = -6517978041305503054L;

    /** 行政编号 */
    @TableId(type = IdType.AUTO)
    private Long rowId;
    /** 上级行政编号 */
    private Long parentId;
    /** 地名 */
    private String name;
    /** 精简地名，如江苏省简称江苏 */
    private String simpleName;
    /** 行政区域等级，0：省(直辖市,自治区,特别行区)；1：市(地区,盟)；2：县(区,市)；3：乡(镇,街道办事处) */
    private Integer level;
    /** 状态，0：未删除，1：已删除 */
    private Boolean deleted;
    /** 创建时间 */
    private LocalDateTime created;
    /** 修改时间 */
    private LocalDateTime modified;
}
