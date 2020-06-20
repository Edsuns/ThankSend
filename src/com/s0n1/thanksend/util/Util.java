package com.s0n1.thanksend.util;

import com.s0n1.thanksend.widget.view.ImageTransferable;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

/**
 * Created by Edsuns@qq.com on 2020/6/15
 */
public class Util {
    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1);
    }

    public static void makeDir(String dir) {
        File file = new File(dir);
        if (!file.exists() && file.mkdirs()) {
            System.out.println("创建目录：" + file.getAbsolutePath());
        }
    }

//    /**
//     * 获取本地局域网IP地址，即获得有线或者WiFi地址
//     * 过滤虚拟机、蓝牙等地址
//     *
//     * @return IP
//     */
//    private static InetAddress getRealIP() {
//        try {
//            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
//                    .getNetworkInterfaces();
//            while (allNetInterfaces.hasMoreElements()) {
//                NetworkInterface netInterface = allNetInterfaces.nextElement();
//
//                // 去除回环接口，子接口，未运行和接口
//                if (netInterface.isLoopback() || netInterface.isVirtual()
//                        || !netInterface.isUp()) {
//                    continue;
//                }
//
//                // 检查网卡名称，过滤虚拟机、蓝牙等地址
//                String name = netInterface.getDisplayName();
//                if (!name.contains("Intel") && !name.contains("Realtek")) {
//                    continue;
//                }
//
//                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
//                while (addresses.hasMoreElements()) {
//                    InetAddress address = addresses.nextElement();
//                    if (address.isSiteLocalAddress()) {
//                        return address;
//                    }
//                }
//                break;
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static InetAddress getIpBySocket() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress().isSiteLocalAddress()) {
                return socket.getLocalAddress();
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getMAC(InetAddress address) {
//        StringBuilder builder = new StringBuilder();
//        try {
//            byte[] macAddress = NetworkInterface.getByInetAddress(address).getHardwareAddress();
//            for (int i = 0; i < macAddress.length; i++) {
//                if (i != 0) {
//                    builder.append(":");
//                }
//                //字节转换为整数
//                int temp = macAddress[i] & 0xff;
//                String str = Integer.toHexString(temp);
//                if (str.length() == 1) {
//                    builder.append("0").append(str);
//                } else {
//                    builder.append(str);
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//
//        return builder.toString().toUpperCase();
//    }

//    /**
//     * 获取字符串的MD5
//     *
//     * @param s 源
//     * @return MD5
//     */
//    public static String getMD5(String s) {
//        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//        try {
//            byte[] btInput = s.getBytes(StandardCharsets.UTF_8);
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            md5.reset();
//            md5.update(btInput);
//            byte[] md = md5.digest();
//            int j = md.length;
//            char[] str = new char[j * 2];
//            int k = 0;
//            for (byte byte0 : md) {
//                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                str[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(str);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 获取字符串的CRC32
     *
     * @param str 源
     * @return CRC32
     */
    public static long getCRC32(String str) {
        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(str.getBytes());
        return crc.getValue();
    }

    public static String getID() {
        return Long.toHexString(getCRC32(HardwareID.getSerialNumber())).toUpperCase();
    }

    public static void copyImage(Image image) {
        Transferable trans = new ImageTransferable(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
    }

    public static void setCenterLocation(Window window) {
        window.setLocation((DeviceUtil.getScreenWidth() - window.getWidth()) / 2, (DeviceUtil.getScreenHeight() - window.getHeight()) / 2);
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return dateFormat.format(new Date());
    }

    public static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
        if (type.getColorModel().hasAlpha())
            ImageIO.write(image, "png", arrayOutputStream);
        else
            ImageIO.write(image, "jpg", arrayOutputStream);
        return arrayOutputStream.toByteArray();
    }
}
