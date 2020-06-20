package com.s0n1.thanksend.tcp;

import com.s0n1.thanksend.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Edsuns@qq.com on 2020/6/15
 */
public class TCPSocket {
    private static final int DATA_INFO = 0;
    private static final int DATA_TEXT = 1;
    private static final int DATA_IMAGE = 2;
    private static final int DATA_FILE = 3;

    private final ExecutorService threadPool;
    private final HashMap<String, Socket> socketsAccepted = new HashMap<>();
    private final HashMap<String, Socket> socketsRequested = new HashMap<>();

    private String mHost;
    private String mUrl;

    private static TCPSocket instance;

    public static TCPSocket getInstance() {
        if (instance == null) {
            instance = new TCPSocket();
        }
        return instance;
    }

    private TCPSocket() {
        threadPool = Executors.newFixedThreadPool(20);
    }

    private boolean started;

    public void start(int port) {
        if (started) return;
        started = true;

        InetAddress mAddress = Util.getIpBySocket();
        if (mAddress != null) {
            mHost = mAddress.getHostAddress();
            mUrl = toUrl(mHost, port);
        } else {
            // TODO 无局域网连接
            mUrl = "error";
            System.out.println("Without Network!");
            System.exit(-1);
        }
        threadPool.execute(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (started) {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    new Thread(() -> {
                        try {
                            int dataType = dataInputStream.readInt();
                            if (dataType != DATA_INFO) {
                                socket.close();
                                return;
                            }

                            // 顺序是固定的
                            String id = dataInputStream.readUTF();
                            String url = dataInputStream.readUTF();

                            if (socketsAccepted.containsKey(url)) {
                                return;
                            }
                            if (!socketsRequested.containsKey(url)) {
                                if (mCallback != null) {
                                    mCallback.onReceive(SystemMessage.CONNECTION_REQUEST, url);
                                }
                                // 返回连接请求
                                String[] strings = url.split(":");
                                connect(strings[0], Integer.parseInt(strings[1]));
                            }

                            if (socketsRequested.containsKey(url)) {// 如果返回连接成功，即连接完全成功
                                System.out.println(id + " " + url + " Connected");
                                socketsAccepted.put(url, socket);// 添加socketsAccepted
                                threadPool.execute(new Receiver(socket, url));
                                if (mCallback != null) {
                                    mCallback.onReceive(SystemMessage.CONNECTION_SUCCEEDED, url);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (BindException e) {
                // TODO 端口被占用的情况
                // 端口被占用：Address already in use: ...
                e.printStackTrace();
                System.exit(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

//    public void stop() {
//        started = false;
//    }

    public void connectTo(String host, int port) {
        threadPool.execute(() -> connect(host, port));
    }

    /**
     * 阻塞式，勿加线程
     */
    private synchronized void connect(String host, int port) {
        String url = toUrl(host, port);
        try {
            if (socketsRequested.containsKey(url)) return;
            if (url.equals(mUrl)) {
                System.out.println("Can't connect with self!");
                return;
            }

            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            String id = Util.getID();
            dataOutputStream.writeInt(DATA_INFO);
            dataOutputStream.writeUTF(id);
            dataOutputStream.writeUTF(mUrl);
            dataOutputStream.flush();

            socketsRequested.put(url, socket);
            threadPool.execute(new Receiver(socket, url));
        } catch (ConnectException e) {
            if (mCallback != null) {
                mCallback.onReceive(SystemMessage.CONNECTION_FAILED, url);
            }
            System.out.println("Connection Failed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void disconnect(String url) {
        try {
            if (socketsAccepted.containsKey(url)) {
                socketsAccepted.remove(url).close();
            }
            if (socketsRequested.containsKey(url)) {
                socketsRequested.remove(url).close();
                // 在if花括号内，才能只打印一次
                if (mCallback != null) {
                    mCallback.onReceive(SystemMessage.DISCONNECT, url);
                }
                System.out.println(url + " Disconnected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String text, String url) {
        if (!socketsAccepted.containsKey(url)) {
            System.out.println("Connection Not Found!");
            return;
        }
        try {
            Socket socket = socketsAccepted.get(url);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(DATA_TEXT);
            dataOutputStream.writeUTF(text);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(BufferedImage image, String url) {
        if (!socketsRequested.containsKey(url)) {
            System.out.println("Connection Not Found!");
            return;
        }

        try {
            byte[] file = Util.toBytes(image);
            long size = file.length;
            if (size <= 0) return;

            Socket socket = socketsRequested.get(url);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(DATA_IMAGE);
            dataOutputStream.writeLong(size);
            dataOutputStream.flush();

            dataOutputStream.write(file);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(File file, String url) {
        if (!socketsRequested.containsKey(url)) {
            System.out.println("Connection Not Found!");
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            long size = file.length();

            Socket socket = socketsRequested.get(url);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(DATA_FILE);
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(size);
            dataOutputStream.flush();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
                dataOutputStream.write(buffer, 0, length);
                dataOutputStream.flush();
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toUrl(String host, int port) {
        return host + ":" + port;
    }

    public String getHost() {
        return mHost;
    }

    public String getUrl() {
        return mUrl;
    }

    class Receiver implements Runnable {
        private final Socket socket;
        private final String senderUrl;

        private Receiver(Socket in, String url) {
            socket = in;
            senderUrl = url;
        }

        private void downloadFile(DataInputStream dataInputStream, String fileName, long length) {
            try {
                final String saveDir = "temp/files/";
                Util.makeDir(saveDir);
                File file = new File(saveDir + fileName);
                if (file.exists()) {
                    file = new File(saveDir + UUID.randomUUID().toString() + fileName);
                }
                FileOutputStream fos = new FileOutputStream(file);
                // 开始接收文件
                byte[] buffer = new byte[1024];
                long remain = length;
                int num;
                while ((num = dataInputStream.read(buffer, 0, (int) Math.min(remain, buffer.length))) != -1) {
                    fos.write(buffer, 0, num);
                    fos.flush();
                    remain -= num;
                    if (remain <= 0) break;
                }
                if (mCallback != null) {
                    Message message = new Message();
                    message.setMessageType(Message.LEFT_ATTACHMENT);
                    message.setSender(senderUrl);
                    message.setPath(file.getPath());
                    mCallback.onReceive(message);
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                while (true) {
                    // 获取数据类型
                    int dataType = dataInputStream.readInt();
                    switch (dataType) {
                        case DATA_TEXT:
                            String text = dataInputStream.readUTF();
                            if (mCallback != null) {
                                Message message = new Message();
                                message.setMessageType(Message.LEFT_TEXT);
                                message.setSender(senderUrl);
                                message.setMessageText(text);
                                mCallback.onReceive(message);
                            }
                            break;
                        case DATA_IMAGE:
                            long imgLength = dataInputStream.readLong();
                            if (imgLength <= 0) continue;// 必须是continue

                            if (imgLength > 10240000) {
                                downloadFile(dataInputStream, "download_", imgLength);
                                // 必须是break
                                break;
                            }
                            int size = (int) imgLength;
                            byte[] file = new byte[size];
                            int num = 0;
                            while ((num += dataInputStream.read(file, num, size - num)) != -1) {
                                if (num >= size) break;
                            }
                            if (mCallback != null) {
                                Image image = new ImageIcon(file).getImage();
                                Message message = new Message();
                                message.setMessageType(Message.LEFT_IMAGE);
                                message.setImage(Util.toBufferedImage(image));
                                message.setSender(senderUrl);
                                mCallback.onReceive(message);
                            }
                            break;
                        case DATA_FILE:
                            String fileName = dataInputStream.readUTF();
                            long fileLength = dataInputStream.readLong();
                            downloadFile(dataInputStream, fileName, fileLength);
                            break;
                        default:
                            return;
                    }
                }
            } catch (IOException ignored) {
            } finally {
                disconnect(senderUrl);
            }
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void onReceive(Message message);

        void onReceive(int systemMessage, String url);
    }
}
