package com.xwq.util;

import org.apache.commons.lang3.StringUtils;

public class FieldFormatUtil {

    public static String formatIntro(String input) {
        if(StringUtils.isBlank(input)) return null;

        input = input.replace("\\r", "");
        input = input.replace("\\n", "");
        input = input.replace("&nbsp;", "");
        input = input.replaceAll("\\s", "");
        input = input.replace("\\u003Cbr\\u003E", "");
        return input;
    }

    public static Long formatLastUpdateTime(String lastUpdateTime) {
        if(lastUpdateTime.contains("年")) {
            String[] split = lastUpdateTime.split(" ");
            return Long.valueOf(split[0]);
        } else {
            return 0L;
        }
    }
}
