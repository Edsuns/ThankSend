package com.s0n1.thanksend.widget.editor;

import com.s0n1.thanksend.ui.PictureJFrame;
import com.s0n1.thanksend.util.CacheUtil;
import com.s0n1.thanksend.util.ClipboardUtil;
import com.s0n1.thanksend.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by song on 03/07/2017.
 */
public class RCTextEditor extends JTextPane implements DropTargetListener {

    public RCTextEditor() {
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    @Override
    public void paste() {
        Object data = ClipboardUtil.getPaste();
        if (data instanceof String) {
            replaceSelection((String) data);
        } else if (data instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) data;
            adjustAndInsertIcon(icon);
        } else if (data instanceof List) {
            List<Object> list = (List<Object>) data;
            for (Object obj : list) {
                // 图像
                if (obj instanceof ImageIcon) {
                    adjustAndInsertIcon((ImageIcon) obj);
                }
                // 文件
                else if (obj instanceof String) {
                    FileEditorThumbnail thumbnail = new FileEditorThumbnail((String) obj);
                    insertComponent(thumbnail);
                }
            }
        }
    }

    /**
     * 插入图片到编辑框，并自动调整图片大小
     */
    private void adjustAndInsertIcon(ImageIcon icon) {
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        float scale = iconWidth * 1.0F / iconHeight;
        boolean needToScale = false;
        int max = 100;
        if (iconWidth >= iconHeight && iconWidth > max) {
            iconWidth = max;
            iconHeight = (int) (iconWidth / scale);
            needToScale = true;
        } else if (iconHeight >= iconWidth && iconHeight > max) {
            iconHeight = max;
            iconWidth = (int) (iconHeight * scale);
            needToScale = true;
        }

        String path = icon.getDescription();
        BufferedImage image = Util.toBufferedImage(icon.getImage());
        JLabel label = new JLabel();
        if (needToScale) {
            if (path == null) {
                path = CacheUtil.getPath() + Util.getCurrentTime() + " paste.jpg";
                try {// 缓存粘贴的图片
                    ImageIO.write(image, "jpg", new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
            scaledIcon.setDescription(path);
            label.setIcon(scaledIcon);
        } else {
            label.setIcon(icon);
        }

        String finalPath = path;
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 双击预览选中的图片
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    new Thread(() -> {
                        try {
                            PictureJFrame.getInstance().showPicture(ImageIO.read(new File(finalPath)));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }).start();
                }
            }
        });

        insertComponent(label);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                List<File> list = (List<File>) (dtde.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor));
                for (Object o : list) {
                    File f = (File) o;
                    if (isImage(f)) {
                        adjustAndInsertIcon(new ImageIcon(f.getAbsolutePath()));
                    } else {
                        FileEditorThumbnail thumbnail = new FileEditorThumbnail(f.getAbsolutePath());
                        this.insertComponent(thumbnail);
                    }

                }
                dtde.dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }
    }

    private static boolean isImage(File file) {
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        return suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("png") || suffix.equals("gif");
    }
}