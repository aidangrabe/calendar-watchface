package com.aidangrabe.customwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by aidan on 04/01/15.
 * Class to draw events on a clock face in the form of coloured arcs
 */
public class EventSegmentManager {

    private ArrayList<CalendarEvent> mEvents;
    private Point mCenter;
    private Paint mPaint;
    private int mRadius;

    public EventSegmentManager(Point center, int radius) {
        this(center, radius, new ArrayList<CalendarEvent>());
    }

    public EventSegmentManager(Point center, int radius, ArrayList<CalendarEvent> events) {
        mCenter = center;
        mPaint = new Paint();
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);
        mRadius = radius;
        setEvents(events);
    }

    /**
     * Convert a float of range 0..1 to radians in the range 0..2pi
     * This method is like getting a percentage of 2pi or a full circle
     * @param f the float angle
     * @return the radians
     */
    private float floatToRad(float f) {
        return (float) Math.PI * 2 * f;
    }

    public void setEvents(ArrayList<CalendarEvent> events) {
        mEvents = events;
    }

    public Paint getPaint() {
        return mPaint;
    }

    /**
     * Draw the segments to the given canvas
     * @param canvas the Canvas to draw on
     */
    public void draw(Canvas canvas) {

        Calendar calendar = Calendar.getInstance();
        float startAngle, sweepAngle;

        for (CalendarEvent event : mEvents) {

            // we don't want to draw all day event segments all around the face.. it looks awful!
            if (event.isAllDay()) {
                continue;
            }
            calendar.setTime(event.getStartDate());
            startAngle = (calendar.get(Calendar.HOUR) * 60 + calendar.get(Calendar.MINUTE)) / 720f;
            sweepAngle = event.getDuration() / 60f / 720f;

            mPaint.setColor(event.getColor());

            drawArc(canvas, mRadius, floatToRad(startAngle), floatToRad(sweepAngle));
        }

    }

    /**
     * Draw an arc segment. This is like an arc border but only at the circumference of the circle.
     * The arc segment will be drawn with the thickness and colour of mPaint
     * @param canvas the Canvas to draw the arc on
     * @param radius the radius of the circle to which this arc would form drawn at 360 degrees
     * @param startAngle the start angle of the arc in radians
     * @param sweepAngle the angle of the arc itself in radians
     */
    private void drawArc(Canvas canvas, int radius, float startAngle, float sweepAngle) {

        float x, y, delta;
        boolean isStarting = true;

        Path path = new Path();
        delta = sweepAngle / 20;

        for (int i = 0; i < 20; i++) {
            x = mCenter.x + (float) Math.sin(startAngle + delta * i) * radius;
            y = mCenter.y - (float) Math.cos(startAngle + delta * i) * radius;
            if (isStarting) {
                path.moveTo(x, y);
                isStarting = false;
            } else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, mPaint);

    }

}
