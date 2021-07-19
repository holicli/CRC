package com.daniutec.crc.misc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 该类下存放了一些常量参数， 具体内容详见各部分常量说明
 *
 * @author 孙修瑞
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebConstants {
    /**
     * 格式化时间样式
     *
     * @author 孙修瑞
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DateStyle {
        /** yyyy-MM-dd */
        public static final String DATE = "yyyy-MM-dd";

        /** HH:mm:ss */
        public static final String TIME = "HH:mm:ss";

        /** yyyy-MM-dd HH:mm:ss */
        public static final  String DATETIME = "yyyy-MM-dd HH:mm:ss";

        /** yyyy年MM月dd日 */
        public static final String CHINA_DATE = "yyyy年MM月dd日";

    }
}
