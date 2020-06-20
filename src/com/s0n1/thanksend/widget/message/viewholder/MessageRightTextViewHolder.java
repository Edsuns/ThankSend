package com.s0n1.thanksend.widget.message.viewholder;

import com.s0n1.thanksend.ui.Colors;
import com.s0n1.thanksend.widget.layout.GBC;
import com.s0n1.thanksend.widget.message.bubble.RCRightMessageBubble;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-2.
 */
public class MessageRightTextViewHolder extends BaseMessageViewHolder {
    public JTextArea text;
    public RCRightMessageBubble messageBubble = new RCRightMessageBubble();
    public JLabel expressionLabel = new JLabel(); // 表情面板

    public MessageRightTextViewHolder() {
        initComponents();
        initView();
    }

    private void initComponents() {
        timePanel.setBackground(Colors.WINDOW_BACKGROUND);
        messageAvatarPanel.setBackground(Colors.WINDOW_BACKGROUND);

        text = new JTextArea();
        text.setOpaque(false);
        text.setEditable(false);
        text.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        text.setCaretPosition(text.getDocument().getLength());

        time.setForeground(Colors.FONT_GRAY);

        expressionLabel.setPreferredSize(new Dimension(120, 120));
        expressionLabel.setVisible(false);
    }

    private void initView() {
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageBubble.add(text, BorderLayout.CENTER);

        JPanel resendTextPanel = new JPanel();
        resendTextPanel.setBackground(Colors.WINDOW_BACKGROUND);

        resendTextPanel.add(messageBubble, BorderLayout.CENTER);
        resendTextPanel.add(expressionLabel, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendTextPanel, new GBC(1, 0).setWeight(1000, 1).setAnchor(GBC.EAST).setInsets(0, 0, 5, 0));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0, 0, 10));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
