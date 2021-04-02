package com.sl.ylyy.common.utils;

import org.springframework.stereotype.Component;

@Component
public class UploadConfigUtil {

    private static Integer activeId = 1;

    public static Integer getActiveId() {
        return activeId;
    }

    public static void setActiveId(Integer activeId) {
        UploadConfigUtil.activeId = activeId;
    }
}
