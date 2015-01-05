package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aidan on 04/01/15.
 * Class to show information about the next event on the canvas
 */
public class NextEventInfo {

    private Paint mBgPaint, mTextPaint;
    private Point mPosition;
    private Rect mBgRect;
    private ArrayList<CalendarEvent> mEvents;

    public NextEventInfo() {

        this(new Point(0, 0));

    }

    public NextEventInfo(Point position) {

        mBgPaint = new Paint();
        mBgPaint.setColor(Color.argb(200, 0, 0, 0));
        mBgPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(12);

        mPosition = position;
        mEvents = new ArrayList<CalendarEvent>();

        mBgRect = new Rect();

    }

    public void setEvents(ArrayList<CalendarEvent> events) {
        mEvents = events;
    }

    public void draw(Canvas canvas) {

        CalendarEvent nextEvent = getNextEvent();

        if (nextEvent != null) {

//            canvas.drawRoundRect(textBounds.left, textBounds.top, textBounds.right, textBounds.bottom, 4, 4, mBgPaint);
            canvas.drawRoundRect(new RectF(mBgRect), 4, 4, mBgPaint);

            canvas.drawText(nextEvent.getTitle(), mPosition.x, mPosition.y, mTextPaint);
            canvas.drawText(nextEvent.getLocation(), mPosition.x, mPosition.y + 10, mTextPaint);
        }

    }

    private CalendarEvent getNextEvent() {

        Date now = Calendar.getInstance().getTime();
        CalendarEvent nextEvent = null;

        for (CalendarEvent event : mEvents) {
            if (now.before(event.getStartTime())) {
                nextEvent = event;
                break;
            } else if (now.after(event.getStartTime()) && now.before(new Date(event.getStartTime().getTime() + event.getEndTime()))) {
                nextEvent = event;
                break;
            }
        }

        if (nextEvent != null) {
            sizeBackgroundRect(nextEvent);
        }

        return nextEvent;

    }

    public void sizeBackgroundRect(CalendarEvent event) {
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(event.getTitle(), 0, event.getTitle().length(), textBounds);

        mBgRect = textBounds;

        textBounds = new Rect();
        mTextPaint.getTextBounds(event.getLocation(), 0, event.getLocation().length(), textBounds);

        mBgRect.set(0, 0, Math.max(textBounds.width(), mBgRect.width()), textBounds.height() + mBgRect.height());
        mBgRect.offsetTo(mPosition.x - mBgRect.width() / 2, mPosition.y - mBgRect.height() / 2);
        mBgRect.inset(-4, -4);
    }

    public void setAmbientMode(boolean ambientMode) {

        mTextPaint.setAntiAlias(!ambientMode);
        mBgPaint.setAntiAlias(!ambientMode);

    }

}
