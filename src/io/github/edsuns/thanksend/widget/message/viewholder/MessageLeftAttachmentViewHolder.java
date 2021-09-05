package io.github.edsuns.thanksend.widget.message.viewholder;

import io.github.edsuns.thanksend.widget.layout.GBC;
import io.github.edsuns.thanksend.widget.message.bubble.RCLeftMessageBubble;

import java.awt.*;

/**
 * Created by song on 17-6-2.
 */
public class MessageLeftAttachmentViewHolder extends MessageAttachmentViewHolder {
    public MessageLeftAttachmentViewHolder() {
        super(new RCLeftMessageBubble());
        initView();
    }

    private void initView() {
        setLayout(new BorderLayout());

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(messageBubble, new GBC(2, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.WEST)
                .setInsets(0, 5, 5, 0));
        messageAvatarPanel.add(avatar, new GBC(1, 0)
                .setWeight(1, 1)
                .setAnchor(GBC.NORTH)
                .setInsets(4, 5, 0, 0));

        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
