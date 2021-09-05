package io.github.edsuns.thanksend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * 获取主板序列号
 * Created by Edsuns@qq.com on 2020/6/16
 */
public class HardwareID {
    private static String serialNumber = null;

    public static String getSerialNumber() {
        if (serialNumber != null) {
            return serialNumber;
        }

        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            serialNumber = getSNForWindows();
        } else if (os.contains("Linux")) {
            serialNumber = getSNForNix();
        } else if (os.contains("Mac OS")) {
            serialNumber = getSNForMac();
        }

        if (serialNumber == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return serialNumber;
    }

    private static String getSNForWindows() {
        String sn = null;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "bios", "get", "serialnumber"});
            Scanner sc = new Scanner(process.getInputStream());
            while (sc.hasNext()) {
                String next = sc.next();
                if ("SerialNumber".equals(next)) {
                    sn = sc.next().trim();
                    break;
                }
            }
            sc.close();
            process.getOutputStream().close();
        } catch (IOException ignored) {
        }

        return sn;
    }

    private static String getSNForMac() {
        InputStream is = null;
        try {
            Process process = Runtime.getRuntime()
                    .exec(new String[]{"/usr/sbin/system_profiler", "SPHardwareDataType"});
            is = process.getInputStream();
            process.getOutputStream().close();
        } catch (IOException ignored) {
        }

        if (is == null) return null;

        String sn = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            final String marker = "Serial Number";
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }

        return sn;
    }

    private static String getSNForNix() {
        String sn = readDmideCode();
        if (sn == null) {
            sn = readLshal();
        }

        return sn;
    }

    private static BufferedReader read(String command) {
        InputStream is = null;

        try {
            Process process = Runtime.getRuntime().exec(command.split(" "));
            is = process.getInputStream();
            process.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (is == null) {
            return null;
        }

        return new BufferedReader(new InputStreamReader(is));
    }

    private static String readDmideCode() {
        String sn = null;
        BufferedReader br = null;

        try {
            final String marker = "Serial Number:";
            br = read("dmidecode -t system");
            if (br == null) return null;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(marker)[1].trim();
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }

        return sn;
    }

    private static String readLshal() {
        String sn = null;
        BufferedReader br = null;

        try {
            final String marker = "system.hardware.serial =";
            br = read("lshal");
            if (br == null) return null;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(marker)[1].replaceAll("\\(string\\)|(')", "").trim();
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }

        return sn;
    }
}
