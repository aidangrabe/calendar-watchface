package com.aidangrabe.customwatchface;

import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by aidan on 19/11/14.
 *
 */
public class CalendarEvent {

    private String title, location;
    private Date startDate, endDate, beginDate;
    private long duration;
    private int color;
    private boolean allDay;

    /**
     * @param title the title of the event
     * @param location the location of the event
     * @param color the color of the event
     * @param startTime the start time in milliseconds
     * @param duration the duration of the event in milliseconds
     */
    public CalendarEvent(String title, String location, int color, long startTime, long duration) {
        this.title = title;
        this.location = location;
        this.color = color;
        setStartTime(startTime);
        setDuration(duration);
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public Date getStartDate() {
        return startDate;
    }

    /**
     * Get the duration of the event in seconds
     * @return the duration of the event in seconds
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Get the end date of the event
     * @return the Date of the end of the event
     */
    public Date getEndDate() {
        // duration is in seconds, so times 1000 to add milliseconds
        return endDate;
    }

    /**
     * Get the event's color
     * @return the color of the event
     */
    public int getColor() {
        return color;
    }

    /**
     * Set the color of the event
     * @param color the color of the event
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Begin time is used for recurring events, as start time will give the first occurrence
     * of the event
     * @return the date of the next occurrence of this event
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * @param beginDate the Date for the next occurrence of this event
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * Create a CalendarEvent from a database cursor
     * @param cursor the database cursor to pull events from
     * @return a new calendar event
     */
    public static CalendarEvent instanceFromCursor(Cursor cursor) {
        String startTime = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DTSTART));
        String duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DURATION));

        int allDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY));
        if (allDay == 1) {
            // if the event is all day, set the duration to 24 hours and skip parsing the durations
            duration = Long.toString(DateUtils.HOUR_IN_MILLIS * 24);
        }
        // non-recurring event
        else if (duration == null) {
            duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.DTEND));
            // get the DTEND column, get the duration of the event and convert it to seconds
            duration = "" + (int) ((Long.parseLong(duration) - Long.parseLong(startTime)) / 1000f);
        } else {
            // convert from the format P<time>S to an integer time
            duration = duration.replaceAll("P(\\d+)S", "$1");
        }

        // begin time for recurring events, but also works for regular events
        // DTSTART gives us the date of the first occurrence of the recurring event
        Date beginDate = new Date(
                cursor.getLong(cursor.getColumnIndex(CalendarContract.Instances.BEGIN)));

        CalendarEvent event = new CalendarEvent(
                cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.TITLE)),
                cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)),
                cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR)),
                Long.parseLong(startTime),
                Long.parseLong(duration)
        );
        event.setAllDay(allDay == 1);
        event.setBeginDate(beginDate);

        return event;
    }

    /**
     * Set the start date of the event
     * @param date the start date of the event
     */
    public void setStartDate(Date date) {
        startDate = date;
    }

    /**
     * Set the end date of the event
     * @param date the end dat of the event
     */
    public void setEndDate(Date date) {
        endDate = date;
    }

    /**
     * Set the start time of the event using the specified millisecond value
     * @param time in milliseconds of the start of the event
     */
    public void setStartTime(long time) {
        startDate = new Date(time);
    }

    /**
     * Set the duration of the event in milliseconds
     * @param duration of the event in milliseconds
     */
    public void setDuration(long duration) {
        this.duration = duration;
        setEndDate(new Date(startDate.getTime() + duration * 1000));
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public String toString() {
        return String.format("CalendarEvent: {title: %s, start: %s, end: %s}", title, getStartDate().getTime(), getEndDate().getTime());
    }
}
