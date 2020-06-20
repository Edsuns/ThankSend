package com.s0n1.thanksend.widget.message.bubble;

import com.s0n1.thanksend.widget.view.NinePatchImageIcon;

import java.awt.*;

/**
 * 右侧图片聊天气泡
 */
public class RCRightMessageBubble extends RCMessageBubble {
    public RCRightMessageBubble() {
        NinePatchImageIcon backgroundNormal = new NinePatchImageIcon(this.getClass().getResource("/image/right.9.png"));
        NinePatchImageIcon backgroundActive = new NinePatchImageIcon(this.getClass().getResource("/image/right_active.9.png"));
        setBackgroundNormalIcon(backgroundNormal);
        setBackgroundActiveIcon(backgroundActive);
        setBackgroundIcon(backgroundNormal);
    }

    @Override
    public Insets getInsets() {
        return new Insets(2, 2, 5, 8);
    }
}
