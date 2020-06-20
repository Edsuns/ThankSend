package com.s0n1.thanksend.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edsuns@qq.com on 2020/6/17
 */
public final class IconUtil {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    public static ImageIcon getIcon(Object context, String path) {
        return getIcon(context, path, -1, -1, Image.SCALE_SMOOTH);
    }

    public static ImageIcon getIcon(Object context, String path, int size) {
        return getIcon(context, path, size, size, Image.SCALE_SMOOTH);
    }

    public static ImageIcon getIcon(Object context, String path, int width, int height) {
        return getIcon(context, path, width, height, Image.SCALE_SMOOTH);
    }

    public static ImageIcon getIcon(Object context, String path, int width, int height, int hints) {
        ImageIcon imageIcon = iconCache.get(path);
        if (imageIcon == null) {
            URL url = context.getClass().getResource(path);
            if (url == null) {
                return null;
            }

            imageIcon = new ImageIcon(url);

            if (width > 0 && height > 0) {
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, hints));
            }

            iconCache.put(path, imageIcon);
        }

        return imageIcon;
    }
}
