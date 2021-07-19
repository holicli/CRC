package com.daniutec.crc.model.enums;


import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 * @author 孙修瑞
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GenderEnum implements IEnum<Integer> {

    /** 0表示未定义，1表示男性，2表示女性 */
    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    /** 获取值 */
    @JsonValue
    private final Integer value;
}