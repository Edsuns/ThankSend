package com.s0n1.thanksend.widget.view;


import com.android.ninepatch.NinePatch;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class NinePatchImageIcon extends ImageIcon {

    private NinePatch mNinePatch;

    public NinePatchImageIcon(URL urlRes) {
        try {
            mNinePatch = NinePatch.load(urlRes, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {

        int iCompWidth = c.getWidth();
        int iCompHeight = c.getHeight();
        mNinePatch.draw((Graphics2D) g, 0, 0, iCompWidth, iCompHeight);
    }

}
