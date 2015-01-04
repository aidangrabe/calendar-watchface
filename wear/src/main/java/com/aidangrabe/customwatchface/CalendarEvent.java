package com.aidangrabe.customwatchface;

import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.Date;

/**
 * Created by aidan on 19/11/14.
 *
 */
public class CalendarEvent {

    private String title, startTime, endTime, location;
    private int color;

    public CalendarEvent(String title, String location, int color, String startTime, String endTime) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartTime() {
        return new Date(Long.parseLong(startTime));
    }

    public int getEndTime() {
        return Integer.parseInt(endTime);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static CalendarEvent instanceFromCursor(Cursor cursor) {
        String startTime = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DTSTART));
        String duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DURATION));
        if (duration == null) {     // non-recurring event
            duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DTEND));
            // get the DTEND column, get the duration of the event and convert it to seconds
            duration = "" + (int) ((Long.parseLong(duration) - Long.parseLong(startTime)) / 1000f);
        } else {
            // convert from the format P<time>S to an integer time
            duration = duration.replaceAll("P(\\d+)S", "$1");
        }
        return new CalendarEvent(
                cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)),
                cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)),
                cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR)),
                startTime,
                duration
        );
    }

}
