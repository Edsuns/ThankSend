package io.github.edsuns.thanksend.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
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

    public static InetAddress getLocalHost() {
        InetAddress fallback = null;
        DatagramSocket datagramSocket = null;
        Socket socket = null;
        try {
            fallback = InetAddress.getLocalHost();
            if (fallback.isSiteLocalAddress())
                return fallback;

            // If the device is completely offline, the code after this line of code is redundant,
            // and this line of code will throw a UnknownHostException to break them.
            SocketAddress socketAddress = new InetSocketAddress("connectivitycheck.gstatic.com", 80);

            // looking for SiteLocalAddress in NetworkInterfaces
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();

                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isSiteLocalAddress()) {
                        return address;
                    }
                }
            }

            // get SiteLocalAddress by DatagramSocket
            datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 28888);
            InetAddress address = datagramSocket.getLocalAddress();
            if (address.isSiteLocalAddress())
                return address;

            // get SiteLocalAddress by Socket
            socket = new Socket();
            socket.connect(socketAddress, 600);
            address = socket.getLocalAddress();
            if (address.isSiteLocalAddress())
                return address;
        } catch (IOException ignored) {
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
        return fallback;
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

    public static void setCenterLocation(Window window) {
        window.setLocation((DeviceUtil.getScreenWidth() - window.getWidth()) / 2, (DeviceUtil.getScreenHeight() - window.getHeight()) / 2);
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return dateFormat.format(new Date());
    }

    public static byte[] toJPGBytes(BufferedImage image) throws IOException {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        // Convert to TYPE_3BYTE_BGR for JPG format
        BufferedImage jpg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics graphics = jpg.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(jpg, "JPG", arrayOutputStream);

        return arrayOutputStream.toByteArray();
    }

    public static byte[] toPNGBytes(Image image) throws IOException {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        // Convert to TYPE_4BYTE_ABGR for PNG format
        BufferedImage png = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = png.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(png, "PNG", arrayOutputStream);

        return arrayOutputStream.toByteArray();
    }

    public static void savePNG(Image image, File file) throws IOException {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        // Convert to TYPE_4BYTE_ABGR for PNG format
        BufferedImage png = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = png.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        // Save image
        ImageIO.write(png, "PNG", file);
    }
}
