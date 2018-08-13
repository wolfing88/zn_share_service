package com.kwon.znshare.util;

import java.util.Date;

public class PrintUtil {

    /**
     * 带时间打印输出
     *
     * @param content
     */
    public static void println(Object content) {
        System.out.println(DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS) + "\t\t" + content);
    }

}
