package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by aidan on 03/01/15.
 *
 */
public class ClockHand {

    private Paint mPaint;
    private float mAngle;
    private Point mPosition;
    private Point mTip;
    private int mLength;

    public ClockHand(Paint paint) {
        mAngle = 0;
        mPaint = paint;
        mPosition = new Point(0, 0);
        mTip = new Point(0, 0);
        mLength = 100;
        updateTip();
    }

    /**
     * Set the angle of the hand based on the given value (0-59)
     * @param minutes integer in range 0-59
     */
    public void setAngleFromMinutes(int minutes) {
        setAngle(minutes / 60f);
    }

    /**
     * Set the angle of the hand based on the given value (0-59)
     * @param seconds integer in range 0-59
     */
    public void setAngleFromSeconds(int seconds) {
        setAngleFromMinutes(seconds);
    }

    /**
     * Set the angle of the hand based on the given value (0-12)
     * @param hours integer in range 0-12
     */
    public void setAngleFromHours(int hours) {
        setAngle(hours / 12f);
    }

    /**
     *
     * @param angle float in range 0-1. 0 is 0 degrees, 1 is 360
     */
    public void setAngle(float angle) {
        mAngle =  (float) Math.PI * 2 * angle;
        updateTip();
    }

    private void updateTip() {
        Double x, y;
        x = mPosition.x + Math.sin(mAngle) * mLength;
        y = mPosition.y + -Math.cos(mAngle) * mLength;
        mTip.set(x.intValue(), y.intValue());
    }

    public void setPosition(Point position) {
        mPosition = position;
    }

    public void setPosition(int x, int y) {
        mPosition.set(x, y);
    }

    public void setLength(int length) {
        mLength = length;
    }

    public Point getTip() {
        return mTip;
    }

    public void draw(Canvas canvas) {

        canvas.drawLine(mPosition.x, mPosition.y, mTip.x, mTip.y, mPaint);

    }

}
