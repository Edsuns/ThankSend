package com.s0n1.thanksend.widget.message.bubble;

import com.s0n1.thanksend.widget.view.NinePatchImageIcon;

import java.awt.*;

/**
 * 右侧附件聊天气泡
 */
public class RCRightWhiteMessageBubble extends RCMessageBubble {
    public RCRightWhiteMessageBubble() {
        NinePatchImageIcon backgroundNormal = new NinePatchImageIcon(this.getClass().getResource("/image/right_white.9.png"));
        NinePatchImageIcon backgroundActive = new NinePatchImageIcon(this.getClass().getResource("/image/right_white_active.9.png"));
        setBackgroundNormalIcon(backgroundNormal);
        setBackgroundActiveIcon(backgroundActive);
        setBackgroundIcon(backgroundNormal);
    }

    @Override
    public Insets getInsets() {
        return new Insets(2, 2, 2, 8);
    }
}
