package com.personal.management.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {


    /**
     * 帮助方法：生成版本号
     * @return 新版本号字符串
     */
    public static String nextVersion() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(LocalDateTime.now());
    }
}
