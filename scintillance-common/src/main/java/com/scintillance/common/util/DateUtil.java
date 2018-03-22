package com.scintillance.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/7 0007.
 */
public class DateUtil {

    public static class  DateFormatType {
        public static final String YYYYMMDD = "YYYYMMDD";
    }

    public static String getDateFormat(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
