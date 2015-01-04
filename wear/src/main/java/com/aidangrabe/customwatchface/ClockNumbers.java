package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by aidan on 04/01/15.
 * Simple class for drawing analog clock numbers on a canvas
 */
public class ClockNumbers {

    private Paint mPaint, mBorderPaint;
    private Point mCenter;
    private int mLength;

    // the number of numbers to draw, should nearly always be 12
    protected int numNumbers;

    /**
     * @param paint the paint to use when drawing the numbers
     * @param center the center point to draw the numbers around
     * @param length the distance from the center point to draw the numbers at
     */
    public ClockNumbers(Paint paint, Point center, int length) {

        paint.setTextAlign(Paint.Align.CENTER);

        mPaint = paint;
        mCenter = center;
        mLength = length;

        mBorderPaint = new Paint(paint);
        mBorderPaint.setColor(Color.BLACK);
//        mBorderPaint.setStrokeWidth(mBorderPaint.getStrokeWidth() + 1);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);
//        mBorderPaint.set

        numNumbers = 12;

    }

    /**
     * Get the vertical center offset of text for the paint
     * @return the vertical offset of the center
     */
    private float vCenter() {
        return (mPaint.ascent() + mPaint.descent()) / 2;
    }

    /**
     * Draw the numbers on the canvas
     * @param canvas the canvas to draw to
     */
    public void draw(Canvas canvas) {

        // start at 1 to have numNumbers at the top instead of 0
        for (int i = 1; i <= numNumbers; i++) {
            float x = (float) Math.sin(Math.PI * 2 * (i / (float) numNumbers)) * mLength;
            float y = - (float) Math.cos(Math.PI * 2 * (i / (float) numNumbers)) * mLength;
            canvas.drawText(String.format("%d", i), mCenter.x + x, mCenter.y + y, mBorderPaint);
            canvas.drawText(String.format("%d", i), mCenter.x + x, mCenter.y + y, mPaint);
        }

    }

}
