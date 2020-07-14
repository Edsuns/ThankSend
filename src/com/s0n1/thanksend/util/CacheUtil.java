package com.s0n1.thanksend.util;

import java.io.File;

/**
 * Created by Edsuns@qq.com on 2020/7/14.
 */
public abstract class CacheUtil {
    private static final String CACHE_PATH = "temp/";

    public static String getPath() {
        File dirs = new File(CACHE_PATH);
        if (!dirs.exists() && dirs.mkdirs()) {
            System.out.println("Created cache directory: " + CACHE_PATH);
        }
        return CACHE_PATH;
    }
}
