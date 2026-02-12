package com.people.hub.core.common.utilities;

import com.alibaba.fastjson2.JSON;

public class JsonUtils {

    public static String toJson(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }
}
