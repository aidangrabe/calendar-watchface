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

    /**
     * Draw some text with an outline around it
     * @param canvas the canvas to draw the text to
     * @param text the actual text to draw
     * @param x x position of text as aligned by the paint
     * @param y y position of text as aligned by the paint
     * @param paint the paint to draw the text with and the border
     * @param outlineColor the color of the outline
     * @param outlineWidth the thickness of the outline
     */
    public static void drawTextOutlined(Canvas canvas, String text, int x, int y, Paint paint, int outlineColor, float outlineWidth) {

        // get the old state so we can reset the paint after the border has been drawn
        int oldColor = paint.getColor();
        float oldWidth = paint.getStrokeWidth();

        paint.setColor(outlineColor);
        paint.setStrokeWidth(outlineWidth);

        // border
        canvas.drawText(text, x, y, paint);

        // reset the paint
        paint.setColor(oldColor);
        paint.setStrokeWidth(oldWidth);

        // normal text
        canvas.drawText(text, x, y, paint);

    }

}
