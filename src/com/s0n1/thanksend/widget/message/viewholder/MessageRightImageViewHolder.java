package com.s0n1.thanksend.widget.message.viewholder;

import com.s0n1.thanksend.ui.Colors;
import com.s0n1.thanksend.widget.layout.GBC;
import com.s0n1.thanksend.widget.message.bubble.RCRightMessageBubble;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-3.
 */
public class MessageRightImageViewHolder extends BaseMessageViewHolder {
    public JLabel image = new JLabel();

    public RCRightMessageBubble imageBubble = new RCRightMessageBubble();

    public MessageRightImageViewHolder() {
        initComponents();
        initView();
    }

    private void initComponents() {
        timePanel.setBackground(Colors.WINDOW_BACKGROUND);
        messageAvatarPanel.setBackground(Colors.WINDOW_BACKGROUND);

        imageBubble.add(image);

        time.setForeground(Colors.FONT_GRAY);
    }

    private void initView() {
        setLayout(new BorderLayout());
        timePanel.add(time);

        JPanel resendImagePanel = new JPanel();
        resendImagePanel.setBackground(Colors.WINDOW_BACKGROUND);
        resendImagePanel.add(imageBubble, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendImagePanel, new GBC(1, 0).setWeight(1000, 1).setAnchor(GBC.EAST).setInsets(0, 0, 5, 0));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0, 0, 10));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
