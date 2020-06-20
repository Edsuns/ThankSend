package com.s0n1.thanksend.util;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 20/06/2017.
 */
public class ClipboardUtil {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void copyString(String content) {
        if (content != null) {
            Transferable tText = new StringSelection(content);
            clipboard.setContents(tText, null);
        }
    }

    public static Object paste() {
        Transferable transferable = clipboard.getContents(null);
        if (transferable != null) {
            try {
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    Object transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    List<File> files = (List<File>) transferData;
                    List<Object> data = new ArrayList<>();
                    for (File file : files) {
                        // 复制的是图片
                        if (isImage(file)) {
                            data.add(transferImageFileToImageIcon(file));
                        }
                        // 复制的是非图片文件
                        else {
                            data.add(file.getAbsolutePath());
                        }
                    }

                    return data;
                } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Object obj = transferable.getTransferData(DataFlavor.imageFlavor);

                    if (obj instanceof Image) {
                        Image image = (Image) obj;
                        return new ImageIcon(image);
                    }
                } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return transferable.getTransferData(DataFlavor.stringFlavor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static boolean isImage(File file) {
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        return suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("png") || suffix.equals("gif");
    }

    private static ImageIcon transferImageFileToImageIcon(File file) {
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        icon.setDescription(file.getAbsolutePath());
        return icon;
    }
}

