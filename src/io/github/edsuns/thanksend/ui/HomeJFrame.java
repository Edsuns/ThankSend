package io.github.edsuns.thanksend.ui;

import io.github.edsuns.thanksend.tcp.Message;
import io.github.edsuns.thanksend.tcp.SystemMessage;
import io.github.edsuns.thanksend.tcp.TCPSocket;
import io.github.edsuns.thanksend.widget.editor.FileEditorThumbnail;
import io.github.edsuns.thanksend.widget.editor.MessageEditorPanel;
import io.github.edsuns.thanksend.widget.editor.RCTextEditor;
import io.github.edsuns.thanksend.widget.layout.VerticalFlowLayout;
import io.github.edsuns.thanksend.widget.view.RCButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edsuns@qq.com on 2020/6/15
 */
public class HomeJFrame extends JFrame implements TCPSocket.Callback {

    private final ChatTabbedPane chatTabbedPane;
    private final MessageEditorPanel messageEditorPanel;
    private final JLabel statusLabel;
    private final RCButton disconnectBtn;

    public HomeJFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(900, 720));
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(600, 500));

        TCPSocket.getInstance().setCallback(this);

        ConnectDialog connectDialog = new ConnectDialog(this);

        chatTabbedPane = new ChatTabbedPane();
        add(chatTabbedPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 15, 15, true, false));

        RCButton addBtn = new RCButton("Add");
        addBtn.addActionListener(e -> {
            connectDialog.setLocationRelativeTo(HomeJFrame.this);
            connectDialog.setVisible(true);
        });
        controlPanel.add(addBtn);

        disconnectBtn = new RCButton("Disconnect");
        disconnectBtn.addActionListener(e -> {
            String url = chatTabbedPane.getCurrentUrl();
            TCPSocket.getInstance().disconnect(url);
            chatTabbedPane.removeConnection(url);
        });
        controlPanel.add(disconnectBtn);

        statusLabel = new JLabel("");
        controlPanel.add(statusLabel);

        messageEditorPanel = new MessageEditorPanel();
        messageEditorPanel.setPreferredSize(new Dimension(730, 170));
        messageEditorPanel.setBorder(new EmptyBorder(10, 10, 10, 30));
        messageEditorPanel.getSendButton().addActionListener(e -> sendMessage());
        messageEditorPanel.getUploadFileLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fileDialog();
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(170);
        splitPane.setDividerSize(0);
        splitPane.setLeftComponent(controlPanel);
        splitPane.setRightComponent(messageEditorPanel);
        add(splitPane, BorderLayout.SOUTH);

        updateVisibility();

        pack();
    }

    private void fileDialog() {
        FileDialog fileDialog = new FileDialog(this, "Select File", FileDialog.LOAD);
        fileDialog.setVisible(true);
        String dir = fileDialog.getDirectory();
        String fileName = fileDialog.getFile();
        // 校验文件
        if (dir == null || dir.isEmpty() || fileName == null || fileName.isEmpty()) return;
        String path = dir + fileName;
        chatTabbedPane.sendFile(path, chatTabbedPane.getCurrentUrl());
        TCPSocket.getInstance().send(new File(path), chatTabbedPane.getCurrentUrl());
    }

    private void updateVisibility() {
        boolean enable = chatTabbedPane.getTabCount() > 0;
        messageEditorPanel.setEnabled(enable);
        disconnectBtn.setVisible(enable);
    }

    @Override
    public void onReceive(Message message) {
        chatTabbedPane.receive(message);
    }

    @Override
    public void onReceive(int systemMessage, String url) {
        switch (systemMessage) {
            case SystemMessage.CONNECTION_FAILED:
                statusLabel.setText("");
                JOptionPane.showMessageDialog(this,
                        "Can't connect to " + url, "Connection Failed!",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case SystemMessage.CONNECTION_REQUEST:
                statusLabel.setText("Connecting...");
                break;
            case SystemMessage.CONNECTION_SUCCEEDED:
                chatTabbedPane.addConnection(url);
                statusLabel.setText("");
                updateVisibility();
                break;
            case SystemMessage.DISCONNECT:
                chatTabbedPane.removeConnection(url);
                updateVisibility();
                break;
        }
    }

    public void onSendText(String text) {
        String url = chatTabbedPane.getCurrentUrl();
        chatTabbedPane.onSendText(text, url);
        TCPSocket.getInstance().send(text, url);
    }

    /**
     * 解析输入框中的内容并发送消息
     */
    private void sendMessage() {
        List<Object> inputDatas = parseEditorInput();
        for (Object data : inputDatas) {
            if (data instanceof String && !data.equals("\n")) {
                String text = (String) data;
                if (!text.isEmpty()) {
                    new Thread(() -> onSendText((String) data)).start();
                }
            } else if (data instanceof JLabel) {
                JLabel label = (JLabel) data;
                ImageIcon icon = (ImageIcon) label.getIcon();
                String path = icon.getDescription();
                if (path != null && !path.isEmpty()) {
                    try {
                        BufferedImage image = ImageIO.read(new File(path));
                        chatTabbedPane.send(image, chatTabbedPane.getCurrentUrl());
                        TCPSocket.getInstance().send(image, chatTabbedPane.getCurrentUrl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (data instanceof FileEditorThumbnail) {
                FileEditorThumbnail component = (FileEditorThumbnail) data;
                String path = component.getPath();
                chatTabbedPane.sendFile(path, chatTabbedPane.getCurrentUrl());
                TCPSocket.getInstance().send(new File(path), chatTabbedPane.getCurrentUrl());
            }
        }

        RCTextEditor editor = messageEditorPanel.getEditor();
        editor.setText("");
        editor.requestFocus();
    }

    /**
     * 解析输入框中的输入数据
     */
    private List<Object> parseEditorInput() {
        List<Object> inputData = new ArrayList<>();

        Document doc = messageEditorPanel.getEditor().getDocument();
        int count = doc.getRootElements()[0].getElementCount();

        // 是否是纯文本，如果发现有图片或附件，则不是纯文本
        boolean pureText = true;

        for (int i = 0; i < count; i++) {
            Element root = doc.getRootElements()[0].getElement(i);

            int elemCount = root.getElementCount();

            for (int j = 0; j < elemCount; j++) {
                try {
                    Element elem = root.getElement(j);
                    String elemName = elem.getName();
                    switch (elemName) {
                        case "content": {
                            int start = elem.getStartOffset();
                            int end = elem.getEndOffset();
                            String text = doc.getText(elem.getStartOffset(), end - start);
                            inputData.add(text);
                            break;
                        }
                        case "component": {
                            pureText = false;
                            Component component = StyleConstants.getComponent(elem.getAttributes());
                            inputData.add(component);
                            break;
                        }
                        case "icon": {
                            pureText = false;

                            ImageIcon icon = (ImageIcon) StyleConstants.getIcon(elem.getAttributes());
                            inputData.add(icon);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 如果是纯文本，直接返回整个文本，否则如果出消息中有换行符\n出现，那么每一行都会被解析成一句话，会造成一条消息被分散成多个消息发送
        if (pureText) {
            inputData.clear();
            inputData.add(messageEditorPanel.getEditor().getText());
        }

        return inputData;
    }
}
