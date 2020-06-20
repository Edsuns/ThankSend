package com.s0n1.thanksend.widget.message.viewholder;

import com.s0n1.thanksend.ui.Colors;
import com.s0n1.thanksend.widget.layout.GBC;
import com.s0n1.thanksend.widget.message.bubble.RCMessageBubble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 16/06/2017.
 */
public class MessageAttachmentViewHolder extends BaseMessageViewHolder {
    public JTextArea attachmentTitle;
    public JPanel attachmentPanel = new JPanel(); // 附件面板
    public JLabel attachmentIcon = new JLabel(); // 附件类型icon
    public JLabel sizeLabel = new JLabel();
    public RCMessageBubble messageBubble;

    public MessageAttachmentViewHolder(RCMessageBubble bubble) {
        messageBubble = bubble;
        initComponents();
        setListeners();
    }

    private void setListeners() {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                messageBubble.setActiveStatus(true);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                messageBubble.setActiveStatus(false);
                super.mouseExited(e);
            }
        };

        attachmentPanel.addMouseListener(listener);
        attachmentTitle.addMouseListener(listener);
    }

    private void initComponents() {
        attachmentTitle = new JTextArea();
        attachmentTitle.setOpaque(false);
        attachmentTitle.setEditable(false);

        timePanel.add(time);

        messageBubble.setCursor(new Cursor(Cursor.HAND_CURSOR));

        timePanel.setBackground(Colors.WINDOW_BACKGROUND);
        messageAvatarPanel.setBackground(Colors.WINDOW_BACKGROUND);

        time.setForeground(Colors.FONT_GRAY);

        attachmentPanel.setLayout(new GridBagLayout());
        attachmentPanel.add(attachmentIcon, new GBC(0, 0)
                .setWeight(1, 1)
                .setInsets(5, 5, 5, 0));
        attachmentPanel.add(attachmentTitle, new GBC(1, 0)
                .setWeight(100, 1)
                .setAnchor(GBC.NORTH)
                .setInsets(5, 8, 5, 5));

        attachmentPanel.add(sizeLabel, new GBC(1, 1)
                .setWeight(1, 1)
                .setFill(GBC.HORIZONTAL)
                .setAnchor(GBC.SOUTH)
                .setInsets(-20, 8, 3, 0));

        messageBubble.add(attachmentPanel);

        attachmentPanel.setOpaque(false);

        sizeLabel.setForeground(Colors.FONT_GRAY);
        add(timePanel, BorderLayout.NORTH);
    }
}
