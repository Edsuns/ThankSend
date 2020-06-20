package com.s0n1.thanksend.widget.message;

import com.s0n1.thanksend.tcp.Message;
import com.s0n1.thanksend.ui.Colors;
import com.s0n1.thanksend.util.ClipboardUtil;
import com.s0n1.thanksend.widget.layout.RCMenuItemUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by song on 2017/6/5.
 */
public class MessagePopupMenu extends JPopupMenu {
    private int messageType;
    private final JMenuItem item1 = new JMenuItem("复制");


    public MessagePopupMenu() {
        initMenuItem();
    }

    private void initMenuItem() {

        item1.setUI(new RCMenuItemUI());
        item1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (messageType) {
                    case Message.RIGHT_TEXT:
                    case Message.LEFT_TEXT: {
                        JTextArea textArea = (JTextArea) getInvoker();
                        String text = textArea.getSelectedText() == null ? textArea.getText() : textArea.getSelectedText();
                        if (text != null) {
                            ClipboardUtil.copyString(text);
                        }
                        break;
                    }
                }

            }
        });
        this.add(item1);

        setBorder(new LineBorder(Colors.SCROLL_BAR_TRACK_LIGHT));
        setBackground(Colors.FONT_WHITE);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        throw new RuntimeException("Please use: show(Component invoker, int x, int y, int messageType)");
    }

    public void show(Component invoker, int x, int y, int messageType) {
        // TODO 消息右键菜单
        if (messageType == Message.LEFT_IMAGE || messageType == Message.RIGHT_IMAGE) return;

        JTextArea textArea = (JTextArea) invoker;
        textArea.requestFocus();// 必须在getSelectedText()前requestFocus()，可清除上一次selectAll()
        String text = textArea.getSelectedText();
        if (text == null) {
            textArea.selectAll();
        }
        this.messageType = messageType;
        super.show(invoker, x, y);
    }
}
