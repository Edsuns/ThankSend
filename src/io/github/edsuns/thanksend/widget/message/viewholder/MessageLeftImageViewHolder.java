package io.github.edsuns.thanksend.widget.message.viewholder;

import io.github.edsuns.thanksend.ui.Colors;
import io.github.edsuns.thanksend.widget.layout.GBC;
import io.github.edsuns.thanksend.widget.layout.VerticalFlowLayout;
import io.github.edsuns.thanksend.widget.message.bubble.RCLeftMessageBubble;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-2.
 */
public class MessageLeftImageViewHolder extends BaseMessageViewHolder {
    public JLabel sender = new JLabel();
    public JLabel image = new JLabel();
    public RCLeftMessageBubble imageBubble = new RCLeftMessageBubble();

    public MessageLeftImageViewHolder() {
        initComponents();
        initView();
    }

    private void initComponents() {
        timePanel.setBackground(Colors.WINDOW_BACKGROUND);
        messageAvatarPanel.setBackground(Colors.WINDOW_BACKGROUND);


        imageBubble.add(image);

        time.setForeground(Colors.FONT_GRAY);

        sender.setForeground(Colors.FONT_GRAY);
    }

    private void initView() {
        setLayout(new BorderLayout());
        timePanel.add(time);

        JPanel senderMessagePanel = new JPanel();
        senderMessagePanel.setBackground(Colors.WINDOW_BACKGROUND);
        senderMessagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        senderMessagePanel.add(sender);
        senderMessagePanel.add(imageBubble);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(avatar, new GBC(1, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(4, 5, 0, 0));
        messageAvatarPanel.add(senderMessagePanel, new GBC(2, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.WEST)
                .setInsets(0, 5, 5, 0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
