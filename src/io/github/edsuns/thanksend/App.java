package io.github.edsuns.thanksend;

import io.github.edsuns.thanksend.tcp.TCPSocket;
import io.github.edsuns.thanksend.ui.HomeJFrame;
import io.github.edsuns.thanksend.util.DeviceUtil;
import io.github.edsuns.thanksend.util.Util;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

import static io.github.edsuns.thanksend.util.DeviceUtil.DPI_SCALE_RATE;

/**
 * Created by Edsuns@qq.com on 2020/6/15
 */
public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            App app = new App();
            app.homeJFrame.setVisible(true);
        });
    }

    private final HomeJFrame homeJFrame;

    public App() {
        // 开始检查DPI缩放是否开启
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        boolean hasDPIScale = !DeviceUtil.isOldVersionJava && screenSize.height != DeviceUtil.getScreenHeight();
        if (hasDPIScale) {
            // DPI缩放会导致显示模糊，要求关闭DPI缩放，自己适配高DPI
            throw new Error("Need disable DPI Scale by VM option: -Dsun.java2d.uiScale=1");
        }

        setLookAndFeel();

        int port = (int) (1500 + Math.random() * 8000);
        TCPSocket.getInstance().start(port);

        homeJFrame = new HomeJFrame();
        homeJFrame.setTitle(Util.getLocalHost() + ":" + port);
        Util.setCenterLocation(homeJFrame);
    }

    private void setLookAndFeel() {
        // 设置系统默认样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        // 获取系统字体
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFonts = environment.getAvailableFontFamilyNames();
        String fontName = null;
        for (String font : allFonts) {
            if ("Microsoft YaHei UI".equals(font)
                    || "PingFang SC".equals(font) || "YaHei Consolas Hybrid".equals(font)) {
                fontName = font;
                break;
            }
        }
        // 设置系统字体和缩放字体大小，要在设置样式后
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource resource = (FontUIResource) value;
                float size = DeviceUtil.isOldVersionJava ? resource.getSize() : resource.getSize() * DPI_SCALE_RATE;
                int style = resource.getStyle();
                UIManager.put(key, new FontUIResource(fontName, style, (int) size));
            }
        }
    }
}
