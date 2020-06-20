package com.s0n1.thanksend.tcp;

import java.awt.image.BufferedImage;

public class Message {
    public static final int LEFT_TEXT = 1;
    public static final int LEFT_IMAGE = 2;
    public static final int LEFT_ATTACHMENT = 3;

    public static final int RIGHT_TEXT = -1;
    public static final int RIGHT_IMAGE = -2;
    public static final int RIGHT_ATTACHMENT = -3;

    private String senderUrl;
    private int messageType;
    private String messageText;
    private boolean deleted;

    private BufferedImage bufferedImage;

    public BufferedImage getImage() {
        return bufferedImage;
    }

    public void setImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String link) {
        this.path = link;
    }

    public String getSender() {
        return senderUrl;
    }

    public void setSender(String id) {
        this.senderUrl = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
