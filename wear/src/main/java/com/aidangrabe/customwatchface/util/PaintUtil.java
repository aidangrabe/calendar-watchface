package com.aidangrabe.customwatchface.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by aidan on 05/01/15.
 * Class with helper methods for working with android.graphics.Paint
 */
public class PaintUtil {

    public static void drawMultiLineText(Canvas canvas, String text, int x, int y, Paint paint) {
        drawMultiLineText(canvas, text, x, y, paint, 1);
    }

    public static void drawMultiLineText(Canvas canvas, String text, int x, int y, Paint paint, float lineHeight) {

        String[] lines = text.split("\n");
        for (String line : lines) {
            canvas.drawText(line, x, y, paint);
            y += (-paint.ascent() + paint.descent()) * lineHeight;
        }

    }

    public static void getMultiLineTextBounds(Paint paint, char[] text, Rect bounds, float lineHeight) {
        getMultiLineTextBounds(paint, new String(text), bounds, lineHeight);
    }

    public static void getMultiLineTextBounds(Paint paint, String text, Rect bounds, float lineHeight) {

        bounds.set(0, 0, 0, 0);
        Rect tmpRect = new Rect();

        String[] lines = text.split("\n");
        for (String line : lines) {
            paint.getTextBounds(text, 0, line.length(), tmpRect);
            bounds.right = Math.max(tmpRect.width(), bounds.width());
            bounds.bottom += (-paint.ascent() + paint.descent()) * lineHeight;
        }

    }

}
