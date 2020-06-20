package com.s0n1.thanksend.widget.editor;

import com.s0n1.thanksend.ui.Colors;
import com.s0n1.thanksend.util.IconUtil;
import com.s0n1.thanksend.widget.expression.ExpressionPopup;
import com.s0n1.thanksend.widget.layout.GBC;
import com.s0n1.thanksend.widget.layout.ScrollUI;
import com.s0n1.thanksend.widget.view.RCButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageEditorPanel extends JPanel {
    private JPanel controlLabel;
    private JLabel fileLabel;
    private JLabel expressionLabel;
    private JLabel cutLabel;
    private JScrollPane textScrollPane;
    private RCTextEditor textEditor;
    private JPanel sendPanel;
    private RCButton sendButton;
    private ChatEditorPopupMenu chatEditorPopupMenu;

    private ImageIcon fileNormalIcon;
    private ImageIcon fileActiveIcon;

    private ImageIcon emotionNormalIcon;
    private ImageIcon emotionActiveIcon;

    private ImageIcon cutNormalIcon;
    private ImageIcon cutActiveIcon;

    private ExpressionPopup expressionPopup;

    public MessageEditorPanel() {
        initComponents();
        initView();
        setListeners();
    }

    private void initComponents() {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        controlLabel = new JPanel();
        controlLabel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 7));

        fileLabel = new JLabel();
        fileNormalIcon = IconUtil.getIcon(this, "/image/file.png");
        fileActiveIcon = IconUtil.getIcon(this, "/image/file_active.png");
        fileLabel.setIcon(fileNormalIcon);
        fileLabel.setCursor(handCursor);
        fileLabel.setToolTipText("发送文件/图片");

        expressionLabel = new JLabel();
        emotionNormalIcon = IconUtil.getIcon(this, "/image/emotion.png");
        emotionActiveIcon = IconUtil.getIcon(this, "/image/emotion_active.png");
        expressionLabel.setIcon(emotionNormalIcon);
        expressionLabel.setCursor(handCursor);
        expressionLabel.setToolTipText("表情");

        cutLabel = new JLabel();
        cutNormalIcon = IconUtil.getIcon(this, "/image/cut.png");
        cutActiveIcon = IconUtil.getIcon(this, "/image/cut_active.png");
        cutLabel.setIcon(cutNormalIcon);
        cutLabel.setCursor(handCursor);
//        cutLabel.setToolTipText("截图(Ctrl + Alt + A)");

        textEditor = new RCTextEditor();
        textEditor.setMargin(new Insets(0, 15, 0, 0));
        textEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 回车发送
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                    e.consume();// 要在keyPressed里才有效
                }
            }
        });
        textScrollPane = new JScrollPane(textEditor);
        textScrollPane.getVerticalScrollBar().setUI(new ScrollUI(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND));
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setBorder(BorderFactory.createEmptyBorder());

        sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        sendButton = new RCButton("Send");
        sendButton.setPreferredSize(new Dimension(75, 29));
        sendPanel.add(sendButton, BorderLayout.EAST);

        chatEditorPopupMenu = new ChatEditorPopupMenu();

        expressionPopup = new ExpressionPopup();
        expressionPopup.setExpressionListener(code -> textEditor.replaceSelection(code));
    }

    private void initView() {
        this.setLayout(new GridBagLayout());

        controlLabel.add(expressionLabel);
        controlLabel.add(fileLabel);
        controlLabel.add(cutLabel);

        controlLabel.setBackground(Color.WHITE);
        setBackground(Color.WHITE);
        sendPanel.setBackground(Color.WHITE);
        textEditor.setBackground(Color.WHITE);

        add(controlLabel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(1, 50));
        add(textScrollPane, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 750));
        add(sendPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 1).setInsets(0, 0, 10, 10));
    }

    private void setListeners() {
        fileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                fileLabel.setIcon(fileActiveIcon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fileLabel.setIcon(fileNormalIcon);
                super.mouseExited(e);
            }
        });

        expressionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                expressionLabel.setIcon(emotionActiveIcon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                expressionLabel.setIcon(emotionNormalIcon);
                super.mouseExited(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                expressionPopup.show((Component) e.getSource(), e.getX() - 200, e.getY() - 320);
                super.mouseClicked(e);
            }
        });

        cutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cutLabel.setIcon(cutActiveIcon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cutLabel.setIcon(cutNormalIcon);
                super.mouseExited(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        textEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    chatEditorPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
                }
                super.mouseClicked(e);
            }
        });
    }

//    public void setExpressionListener(com.rc.listener.ExpressionListener listener) {
//        expressionPopup.setExpressionListener(listener);
//    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Component component : getComponents()) {
            component.setVisible(enabled);
        }
        super.setEnabled(enabled);
    }

    public RCTextEditor getEditor() {
        return textEditor;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JLabel getUploadFileLabel() {
        return fileLabel;
    }
}
