package io.github.edsuns.thanksend.widget.expression;

import io.github.edsuns.thanksend.ui.Colors;
import io.github.edsuns.thanksend.util.IconUtil;
import io.github.edsuns.thanksend.widget.layout.GBC;
import io.github.edsuns.thanksend.widget.view.RCBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 04/07/2017.
 * <p>
 * 表情弹窗
 */
public class ExpressionPopup extends JPopupMenu {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private JPanel listPanel;
    private JPanel tabIconPanel;

    private JPanel emojiTabPanel;

    private JLabel emojiTabLabel;

    private EmojiPanel emojiPanel;

    private CardLayout cardLayout;
    public static final String EMOJI = "EMOJI";

    public ExpressionPopup() {
        initComponents();
        initView();

        setListeners();

        selectTab(emojiTabPanel);
    }

    private void initComponents() {
        listPanel = new JPanel();
        listPanel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        cardLayout = new CardLayout();
        listPanel.setLayout(cardLayout);

        tabIconPanel = new JPanel();
        tabIconPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // emoji
        emojiTabPanel = new JPanel();
        emojiTabLabel = new JLabel();
        emojiTabLabel.setIcon(IconUtil.getIcon(this, "/image/smile.png", 23, 23));
        emojiPanel = new EmojiPanel();

        setBackground(Colors.WINDOW_BACKGROUND);
        this.setPopupSize(WIDTH, HEIGHT);
    }

    private void initView() {
        emojiTabPanel.add(emojiTabLabel);

        tabIconPanel.add(emojiTabPanel);

        listPanel.add(emojiPanel, EMOJI);

        setLayout(new GridBagLayout());
        add(listPanel, new GBC(0, 0).setWeight(1, 1000).setFill(GBC.BOTH));
        add(tabIconPanel, new GBC(0, 1).setWeight(1, 1).setFill(GBC.BOTH).setInsets(3, 0, 0, 0));
    }

    public void setExpressionListener(ExpressionListener listener) {
        emojiPanel.setExpressionListener(listener, this);
    }


    private void selectTab(JPanel tab) {
        for (Component component : tabIconPanel.getComponents()) {
            if (component == tab) {
                component.setBackground(Colors.SCROLL_BAR_TRACK_LIGHT);
            } else {
                component.setBackground(Colors.WINDOW_BACKGROUND);
            }
        }
    }


    public void showPanel(String who) {
        cardLayout.show(listPanel, who);
    }

    private void setListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == emojiTabPanel) {
                    showPanel(EMOJI);
                }

                selectTab((JPanel) e.getSource());

                super.mouseClicked(e);
            }
        };

        emojiTabPanel.addMouseListener(adapter);
    }

}
