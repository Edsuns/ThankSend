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
public class MessageLeftTextViewHolder extends BaseMessageViewHolder {
    public JLabel sender = new JLabel();

    public JTextArea text;
    public RCLeftMessageBubble messageBubble = new RCLeftMessageBubble();

    public JLabel expressionLabel = new JLabel(); // 表情面板


    public MessageLeftTextViewHolder() {
        initComponents();
        initView();
    }

    private void initComponents() {
        text = new JTextArea();
        text.setOpaque(false);
        text.setEditable(false);
        text.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        text.setCaretPosition(text.getDocument().getLength());

        time.setForeground(Colors.FONT_GRAY);

        sender.setForeground(Colors.FONT_GRAY);

        messageAvatarPanel.setBackground(Colors.WINDOW_BACKGROUND);
        timePanel.setBackground(Colors.WINDOW_BACKGROUND);

        expressionLabel.setPreferredSize(new Dimension(120, 120));
        expressionLabel.setVisible(false);
    }

    private void initView() {
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageBubble.add(text);

        JPanel senderMessagePanel = new JPanel();
        senderMessagePanel.setBackground(Colors.WINDOW_BACKGROUND);
        senderMessagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        senderMessagePanel.add(sender);
        senderMessagePanel.add(messageBubble);
        senderMessagePanel.add(expressionLabel);

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
