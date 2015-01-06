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

    public Date getStartDate() {
        return new Date(Long.parseLong(startTime));
    }

    /**
     * Get the duration of the event in seconds
     * @return the duration of the event in seconds
     */
    public int getDuration() {
        return Integer.parseInt(endTime);
    }

    /**
     * Get the end date of the event
     * @return the Date of the end of the event
     */
    public Date getEndDate() {
        // duration is in seconds, so times 1000 to add milliseconds
        return new Date(Long.parseLong(startTime) + getDuration() * 1000);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Create a CalendarEvent from a database cursor
     * @param cursor the database cursor to pull events from
     * @return a new calendar event
     */
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

    @Override
    public String toString() {
        return String.format("CalendarEvent: {title: %s, start: %s, end: %s}", title, getStartDate().getTime(), endTime);
    }
}
