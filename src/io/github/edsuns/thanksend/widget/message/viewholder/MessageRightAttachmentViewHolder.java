package io.github.edsuns.thanksend.widget.message.viewholder;

import io.github.edsuns.thanksend.widget.layout.GBC;
import io.github.edsuns.thanksend.widget.message.bubble.RCRightWhiteMessageBubble;

import java.awt.*;

/**
 * Created by song on 17-6-3.
 */
public class MessageRightAttachmentViewHolder extends MessageAttachmentViewHolder {
    public MessageRightAttachmentViewHolder() {
        super(new RCRightWhiteMessageBubble());
        initView();
    }

    private void initView() {
        setLayout(new BorderLayout());

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(messageBubble, new GBC(1, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.EAST)
                .setInsets(0, 0, 5, 5));
        messageAvatarPanel.add(avatar, new GBC(2, 0)
                .setWeight(1, 1)
                .setAnchor(GBC.NORTH)
                .setInsets(5, 0, 0, 10));

        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
