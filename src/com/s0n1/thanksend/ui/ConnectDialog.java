package com.s0n1.thanksend.ui;

import com.s0n1.thanksend.tcp.SystemMessage;
import com.s0n1.thanksend.tcp.TCPSocket;
import com.s0n1.thanksend.widget.view.RCButton;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Edsuns@qq.com on 2020/6/16
 */
public class ConnectDialog extends JDialog {
    public ConnectDialog(Frame parent) {
        super(parent, true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(250, 180));
        setLayout(null);
        setTitle("Add Connection");

        JTextField hostText = new JTextField(TCPSocket.getInstance().getHost());
        hostText.setBounds(20, 30, 140, 30);
        add(hostText);

        JTextField portText = new JTextField("2020");
        portText.setBounds(170, 30, 60, 30);
        add(portText);

        JLabel tipLabel = new JLabel("Type IP and Port.");
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipLabel.setBounds(20, 70, 200, 30);
        add(tipLabel);

        RCButton connectBtn = new RCButton("Connect");
        connectBtn.setBounds(75, 120, 100, 30);
        connectBtn.addActionListener(e -> {
            String host = hostText.getText();
            int port = -1;
            try {
                port = Integer.parseInt(portText.getText());
            } catch (NumberFormatException ignored) {
            }
            String url = TCPSocket.toUrl(host, port);
            String mUrl = TCPSocket.getInstance().getUrl();
            if (url.equals(mUrl) || port > 99999 || port < 0) {
                portText.setText("");
                tipLabel.setText("Invalid Port!");
                return;
            }
            TCPSocket.getInstance().connectTo(hostText.getText(), Integer.parseInt(portText.getText()));
            // TODO 不安全的做法
            ((HomeJFrame) parent).onReceive(SystemMessage.CONNECTION_REQUEST, null);
            dispose();
        });
        add(connectBtn);

        pack();
    }
}
