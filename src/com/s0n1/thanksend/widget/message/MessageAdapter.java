package com.s0n1.thanksend.widget.message;

import com.s0n1.thanksend.tcp.Message;
import com.s0n1.thanksend.ui.PictureJFrame;
import com.s0n1.thanksend.util.Util;
import com.s0n1.thanksend.widget.editor.AttachmentIconHelper;
import com.s0n1.thanksend.widget.message.bubble.RCMessageBubble;
import com.s0n1.thanksend.widget.message.viewholder.*;
import com.s0n1.thanksend.widget.view.list.BaseAdapter;
import com.s0n1.thanksend.widget.view.list.ViewHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Edsuns@qq.com on 2020/6/17
 */
public class MessageAdapter extends BaseAdapter<ViewHolder> {
    private final List<Message> messages;

    private final MessagePopupMenu popupMenu = new MessagePopupMenu();

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(int viewType) {
        switch (viewType) {
            case Message.LEFT_TEXT:
                return new MessageLeftTextViewHolder();
            case Message.RIGHT_TEXT:
                return new MessageRightTextViewHolder();
            case Message.LEFT_IMAGE:
                return new MessageLeftImageViewHolder();
            case Message.RIGHT_IMAGE:
                return new MessageRightImageViewHolder();
            case Message.LEFT_ATTACHMENT:
                return new MessageLeftAttachmentViewHolder();
            case Message.RIGHT_ATTACHMENT:
                return new MessageRightAttachmentViewHolder();
        }
        return new MessageRightTextViewHolder();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (viewHolder == null) return;

        final Message item = messages.get(position);
        if (item.getMessageType() == Message.LEFT_TEXT)
            processLeftTextMessage(viewHolder, item);
        else if (item.getMessageType() == Message.RIGHT_TEXT)
            processRightTextMessage(viewHolder, item);
        else if (item.getMessageType() == Message.RIGHT_IMAGE)
            processRightImageMessage(viewHolder, item);
        else if (item.getMessageType() == Message.LEFT_IMAGE)
            processLeftImageMessage(viewHolder, item);
        else if (item.getMessageType() == Message.LEFT_ATTACHMENT) {
            processLeftAttachmentMessage(viewHolder, item);
        } else if (item.getMessageType() == Message.RIGHT_ATTACHMENT) {
            processRightAttachmentMessage(viewHolder, item);
        }
    }

    private final AttachmentIconHelper attachmentIconHelper = new AttachmentIconHelper();

    private void processRightAttachmentMessage(ViewHolder viewHolder, Message item) {
        MessageRightAttachmentViewHolder holder = (MessageRightAttachmentViewHolder) viewHolder;
        String fileName = Util.getFileName(item.getPath());
        holder.attachmentTitle.setText(fileName);
        holder.attachmentIcon.setIcon(attachmentIconHelper.getImageIcon(fileName));

        processClickListener(holder.attachmentIcon, item);

        attachPopupMenu(viewHolder, Message.RIGHT_ATTACHMENT);
    }

    private void processLeftAttachmentMessage(ViewHolder viewHolder, Message item) {
        MessageLeftAttachmentViewHolder holder = (MessageLeftAttachmentViewHolder) viewHolder;
        String fileName = Util.getFileName(item.getPath());
        holder.attachmentTitle.setText(fileName);
        holder.attachmentIcon.setIcon(attachmentIconHelper.getImageIcon(fileName));

        processClickListener(holder.attachmentIcon, item);

        attachPopupMenu(viewHolder, Message.LEFT_ATTACHMENT);
    }

    private void processClickListener(JLabel attachmentIcon, Message item) {
        attachmentIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    openFileWithDefaultApplication(item.getPath());
                }
            }
        });
    }

    /**
     * 使用默认程序打开文件
     */
    private void openFileWithDefaultApplication(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "文件打开失败，没有找到关联的应用程序", "打开失败", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        } catch (IllegalArgumentException e2) {
            JOptionPane.showMessageDialog(null, "文件不存在，可能已被删除", "打开失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processLeftImageMessage(ViewHolder viewHolder, Message item) {
        MessageLeftImageViewHolder holder = (MessageLeftImageViewHolder) viewHolder;
        processImage(item, holder.image);
        // 绑定右键菜单
        attachPopupMenu(viewHolder, Message.LEFT_IMAGE);
    }

    private void processRightImageMessage(ViewHolder viewHolder, Message item) {
        MessageRightImageViewHolder holder = (MessageRightImageViewHolder) viewHolder;
        processImage(item, holder.image);
        // 绑定右键菜单
        attachPopupMenu(viewHolder, Message.RIGHT_IMAGE);
    }

    private void processImage(Message item, JLabel imageLabel) {
        BufferedImage image = item.getImage();
        ImageIcon imageIcon = new ImageIcon(image);
        preferredImageSize(imageIcon);
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageLabel.setIcon(imageIcon);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1)
                    PictureJFrame.getInstance().showPicture(image);
            }
        });
    }

    /**
     * 根据图片尺寸大小调整图片显示的大小
     */
    public void preferredImageSize(ImageIcon imageIcon) {
        int width = imageIcon.getIconWidth();
        int height = imageIcon.getIconHeight();
        float scale = width * 1.0F / height;

        // 限制图片显示大小
        // TODO: 2020/6/17 改成窗口大小的0.2倍
        int maxImageWidth = 450;
        if (width > maxImageWidth) {
            width = maxImageWidth;
            height = (int) (width / scale);
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private void processLeftTextMessage(ViewHolder viewHolder, final Message item) {
        MessageLeftTextViewHolder holder = (MessageLeftTextViewHolder) viewHolder;
        holder.expressionLabel.setVisible(false);
        holder.messageBubble.setVisible(true);
        holder.text.setText(item.getMessageText());

        // 绑定右键菜单
        attachPopupMenu(viewHolder, Message.LEFT_TEXT);
    }

    /**
     * 处理 我发送的文本消息
     */
    private void processRightTextMessage(ViewHolder viewHolder, final Message item) {
        MessageRightTextViewHolder holder = (MessageRightTextViewHolder) viewHolder;
        holder.expressionLabel.setVisible(false);
        holder.messageBubble.setVisible(true);
        holder.text.setText(item.getMessageText());

        // 绑定右键菜单
        attachPopupMenu(viewHolder, Message.RIGHT_TEXT);
    }

    private void attachPopupMenu(ViewHolder viewHolder, int messageType) {
        JComponent contentComponent = null;
        RCMessageBubble messageBubble = null;

        switch (messageType) {
            case Message.RIGHT_TEXT: {
                MessageRightTextViewHolder holder = (MessageRightTextViewHolder) viewHolder;
                contentComponent = holder.text;
                messageBubble = holder.messageBubble;

                break;
            }
            case Message.LEFT_TEXT: {
                MessageLeftTextViewHolder holder = (MessageLeftTextViewHolder) viewHolder;
                contentComponent = holder.text;
                messageBubble = holder.messageBubble;
                break;
            }
            case Message.LEFT_IMAGE: {
                MessageLeftImageViewHolder holder = (MessageLeftImageViewHolder) viewHolder;
                contentComponent = holder.image;
                messageBubble = holder.imageBubble;
                break;
            }
            case Message.RIGHT_IMAGE: {
                MessageRightImageViewHolder holder = (MessageRightImageViewHolder) viewHolder;
                contentComponent = holder.image;
                messageBubble = holder.imageBubble;
                break;
            }
            case Message.LEFT_ATTACHMENT: {
                MessageLeftAttachmentViewHolder holder = (MessageLeftAttachmentViewHolder) viewHolder;
                contentComponent = holder.attachmentPanel;
                messageBubble = holder.messageBubble;
                break;
            }
            case Message.RIGHT_ATTACHMENT: {
                MessageRightAttachmentViewHolder holder = (MessageRightAttachmentViewHolder) viewHolder;
                contentComponent = holder.attachmentPanel;
                messageBubble = holder.messageBubble;
                break;
            }
        }

        JComponent finalContentComponent = contentComponent;
        RCMessageBubble finalMessageBubble = messageBubble;

        contentComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getX() > finalContentComponent.getWidth() || e.getY() > finalContentComponent.getHeight()) {
                    finalMessageBubble.setBackgroundIcon(finalMessageBubble.getBackgroundNormalIcon());
                }
                super.mouseExited(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                finalMessageBubble.setBackgroundIcon(finalMessageBubble.getBackgroundActiveIcon());
                super.mouseEntered(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show((Component) e.getSource(), e.getX(), e.getY(), messageType);
                }

                super.mouseReleased(e);
            }
        });

        messageBubble.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(finalContentComponent, e.getX(), e.getY(), messageType);
                }
            }
        });
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getMessageType();
    }

}
