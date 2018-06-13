package com.zj.expressway.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Create dell By 2017/11/15 11:35
 */

public class JsonUtils {
    /**
     * 判断是否为JSON数据
     * @param json
     * @return
     */
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }
}
