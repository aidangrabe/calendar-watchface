package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.aidangrabe.customwatchface.util.PaintUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by aidan on 04/01/15.
 * Class to show information about the next event on the canvas
 */
public class NextEventInfo {

    private Paint mBgPaint, mTextPaint;
    private Point mPosition;
    private Rect mBgRect;
    private CalendarEvent mNextEvent;
    private ArrayList<CalendarEvent> mEvents;

    private final Comparator<CalendarEvent> mSortComparator = new Comparator<CalendarEvent>() {
        @Override
        public int compare(CalendarEvent lhs, CalendarEvent rhs) {
            return lhs.getStartDate().compareTo(rhs.getStartDate());
        }
    };

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
        mNextEvent = null;

        mBgRect = new Rect();

    }

    public void setEvents(ArrayList<CalendarEvent> events) {
        mEvents = events;

        Collections.sort(mEvents, mSortComparator);

        getNextEvent();
    }

    public void draw(Canvas canvas) {

        if (mNextEvent != null) {

            canvas.drawRoundRect(new RectF(mBgRect), 4, 4, mBgPaint);

            PaintUtil.drawMultiLineText(canvas, String.format("%s\n%s", mNextEvent.getTitle(), mNextEvent.getLocation()),
                    mPosition.x, mPosition.y, mTextPaint, 1);
        }

    }

    private CalendarEvent getNextEvent() {

        Date now = Calendar.getInstance().getTime();
        mNextEvent = null;

        for (CalendarEvent event : mEvents) {

            // ignore all day events
            if (event.isAllDay()) {
                continue;
            }

            // events that have not started yet
            if (now.before(event.getStartDate())) {
                mNextEvent = event;
                break;
            }

            // check if the event is on now
            if (now.after(event.getStartDate()) && now.before(event.getEndDate())) {
                mNextEvent = event;
                break;
            }
        }

        if (mNextEvent != null) {
            sizeBackgroundRect(mNextEvent);
        }

        return mNextEvent;

    }

    public void sizeBackgroundRect(CalendarEvent event) {

        PaintUtil.getMultiLineTextBounds(mTextPaint, String.format("%s\n%s", event.getTitle(), event.getLocation()),
                mBgRect, 1);
        mBgRect.inset(-4, -4);
        mBgRect.offsetTo(mPosition.x - mBgRect.width() / 2, mPosition.y - mBgRect.height() / 2 + 4);

    }

    public void setAmbientMode(boolean ambientMode) {

        mTextPaint.setAntiAlias(!ambientMode);
        mBgPaint.setAntiAlias(!ambientMode);

    }

}
