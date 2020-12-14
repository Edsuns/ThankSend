package com.s0n1.thanksend.ui;

import com.s0n1.thanksend.tcp.Message;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edsuns@qq.com on 2020/6/16
 */
public class ChatTabbedPane extends JTabbedPane {
    private final Map<String, MessageJPanel> panelHashtable = new HashMap<>();

    public ChatTabbedPane() {
        super(JTabbedPane.LEFT);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public void addConnection(String url) {
        MessageJPanel messagePanel = new MessageJPanel();
        addTab(url, null, messagePanel, null);
        panelHashtable.put(url, messagePanel);
    }

    public String getCurrentUrl() {
        if (getTabCount() > 0) {
            return getTitleAt(getSelectedIndex());
        }
        return "";
    }

    public void removeConnection(String url) {
        remove(panelHashtable.remove(url));
    }

    public void receive(Message message) {
        MessageJPanel messagePanel = panelHashtable.get(message.getSender());
        if (messagePanel != null) {
            messagePanel.receive(message);
        }
    }

    public void onSendText(String text, String url) {
        MessageJPanel messagePanel = panelHashtable.get(url);
        if (messagePanel != null) {
            Message item = new Message();
            item.setMessageType(Message.RIGHT_TEXT);
            item.setMessageText(text);
            messagePanel.send(item);
        }
    }

    public void send(BufferedImage image, String url) {
        MessageJPanel messagePanel = panelHashtable.get(url);
        if (messagePanel != null) {
            Message item = new Message();
            item.setMessageType(Message.RIGHT_IMAGE);
            item.setImage(image);
            messagePanel.send(item);
        }
    }

    public void sendFile(String path, String url) {
        MessageJPanel messagePanel = panelHashtable.get(url);
        if (messagePanel != null) {
            Message item = new Message();
            item.setMessageType(Message.RIGHT_ATTACHMENT);
            item.setPath(path);
            messagePanel.send(item);
        }
    }
}
