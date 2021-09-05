package io.github.edsuns.thanksend.widget.message.viewholder;

import io.github.edsuns.thanksend.widget.view.list.ViewHolder;

import javax.swing.*;

/**
 * Created by song on 13/06/2017.
 */
public class BaseMessageViewHolder extends ViewHolder {
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public JPanel timePanel = new JPanel(); // 时间面板
    public JPanel messageAvatarPanel = new JPanel(); // 消息 + 头像组合面板
}
