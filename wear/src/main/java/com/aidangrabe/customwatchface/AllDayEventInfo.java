package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.aidangrabe.customwatchface.util.PaintUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aidan on 19/01/15.
 * This class displays information about the all day calendar events
 */
public class AllDayEventInfo {

    private static final int EVENT_BUBBLE_SIZE = 4;

    public Point position;

    private List<CalendarEvent> mEvents;
    private Paint mPaint;
    private Rect mTextBounds;

    public AllDayEventInfo() {

        mEvents = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        position = new Point(0, 0);
        mTextBounds = new Rect(0, 0, 0, 0);

    }

    public void draw(Canvas canvas) {

        int yy = 0;

        for (CalendarEvent event : mEvents) {

            if (!event.isAllDay()) {
                continue;
            }

            mPaint.getTextBounds(event.getTitle(), 0, event.getTitle().length(), mTextBounds);

            // draw the event bubble and text
            drawEventBubble(canvas, position.x - mTextBounds.width() / 2 - 10, position.y - mTextBounds.height() / 2 + yy, event.getColor());
            PaintUtil.drawTextOutlined(canvas, event.getTitle(), position.x, position.y + yy, mPaint,
                    Color.BLACK, 1);

            // next event will be offset by this
            yy += 20;

        }

    }

    /**
     * Draw a small little ball
     * @param canvas the canvas to draw to
     * @param x x position of center
     * @param y y position of center
     */
    private void drawEventBubble(Canvas canvas, int x, int y, int color) {

        // temporarily change the paint color
        int oldColor = mPaint.getColor();

        mPaint.setColor(color);
        canvas.drawCircle(x, y, EVENT_BUBBLE_SIZE, mPaint);

        // reset the paint color
        mPaint.setColor(oldColor);

    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setEvents(List<CalendarEvent> events) {
        mEvents = events;
    }

}
