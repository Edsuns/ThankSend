package com.s0n1.thanksend.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * 仅数字输入文本过滤器
 * Created by Edsuns@qq.com on 2020-06-04
 */
public class DigitFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributes)
            throws BadLocationException {
        replace(fb, offset, 0, text, attributes);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributes)
            throws BadLocationException {
        // add check here check the length of the text currently in the document
        // with the length of your text String to make sure the total is not above the maximum
        // you should modify the class to pass a paramenter when you create the class for the
        // maximum length so the class can be reused

        if (isDigit(text))
            super.replace(fb, offset, length, text, attributes);
        else
            Toolkit.getDefaultToolkit().beep();
    }

    private boolean isDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i)))
                return false;
        }

        return true;
    }
}
