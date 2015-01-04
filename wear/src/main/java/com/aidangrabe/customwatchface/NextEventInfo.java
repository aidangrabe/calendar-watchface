package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

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
    private ArrayList<CalendarEvent> mEvents;

    public NextEventInfo() {

        this(new Point(0, 0));

    }

    public NextEventInfo(Point position) {

        mBgPaint = new Paint();
        mBgPaint.setColor(Color.argb(200, 0, 0, 0));

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(12);

        mPosition = position;
        mEvents = new ArrayList<CalendarEvent>();

    }

    public void setEvents(ArrayList<CalendarEvent> events) {
        mEvents = events;
    }

    public void draw(Canvas canvas) {

        CalendarEvent nextEvent = getNextEvent();

        if (nextEvent != null) {
            canvas.drawText(nextEvent.getTitle(), mPosition.x, mPosition.y, mTextPaint);
            canvas.drawText(nextEvent.getLocation(), mPosition.x, mPosition.y + 10, mTextPaint);
        }

    }

    private CalendarEvent getNextEvent() {

        Date now = Calendar.getInstance().getTime();

        for (CalendarEvent event : mEvents) {
            if (now.before(event.getStartTime())) {
                return event;
            } else if (now.after(event.getStartTime()) && now.before(new Date(event.getStartTime().getTime() + event.getEndTime()))) {
                return event;
            }
        }

        return null;

    }

    public void setAmbientMode(boolean ambientMode) {

        mTextPaint.setAntiAlias(!ambientMode);

    }

}
