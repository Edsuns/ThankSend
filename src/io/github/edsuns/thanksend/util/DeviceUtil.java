package io.github.edsuns.thanksend.util;

import java.awt.*;

/**
 * Created by Edsuns@qq.com on 2020-05-25
 */
public final class DeviceUtil {
    private static final String JAVA_VERSION = System.getProperty("java.version");
    public static boolean isOldVersionJava = false;

    static {
        int index = JAVA_VERSION.indexOf(".");
        if (index != -1 && JAVA_VERSION.length() > ++index) {
            isOldVersionJava = Float.parseFloat(JAVA_VERSION.substring(0, ++index)) < 1.9f;
        }
    }

    public static final float DPI_SCALE_RATE = Toolkit.getDefaultToolkit().getScreenResolution() / 96f;

    public static int getScreenWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
    }

    public static int getScreenHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
    }
}
