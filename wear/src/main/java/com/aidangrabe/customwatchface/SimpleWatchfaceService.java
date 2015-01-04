package com.aidangrabe.customwatchface;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.provider.WearableCalendarContract;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aidan on 03/01/15.
 *
 */
public class SimpleWatchfaceService extends CanvasWatchFaceService {

    private Engine mEngine;

    @Override
    public Engine onCreateEngine() {
        mEngine = new Engine();
        return mEngine;
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        private Point mCenter;
        private Paint mClockPaint;
        private ClockHand mMinutesHand;
        private ClockHand mSecondsHand;
        private ClockHand mHoursHand;
        private ClockNumbers mClockNumbers;
        private EventSegmentManager mSegmentManager;

        private ArrayList<Paint> mPaints;

        private Timer mTimer;
        private final TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                onSecondTick();
            }
        };

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            mCenter.set(holder.getSurfaceFrame().width() / 2,
                        holder.getSurfaceFrame().height() / 2);

            // position the hands
            mSecondsHand.setPosition(mCenter);
            mMinutesHand.setPosition(mCenter);
            mHoursHand.setPosition(mCenter);

        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
            mCenter = new Point(0, 0);

            mClockPaint = new Paint();
            mClockPaint.setColor(Color.WHITE);
            mClockPaint.setAntiAlias(true);
            mClockPaint.setTextSize(12);
            mClockPaint.setTextAlign(Paint.Align.CENTER);
            mClockPaint.setTypeface(Typeface.SANS_SERIF);

            mSecondsHand = new ClockHand(mClockPaint);
            mMinutesHand = new ClockHand(mClockPaint);
            mHoursHand = new ClockHand(mClockPaint);

            mClockNumbers = new ClockNumbers(mClockPaint, new Point(160, 160), 150);

            mMinutesHand.setLength(120);

            mSegmentManager = new EventSegmentManager(mCenter, 150);

            // add all paints to the array so we can toggle antialiasing later
            mPaints = new ArrayList<Paint>();
            mPaints.add(mClockPaint);
            mPaints.add(mSegmentManager.getPaint());

            getEvents();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(Constants.TAG_D, "onVisiblityChanged");

            super.onVisibilityChanged(visible);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {

            // toggle antialiasing for all the paints
            boolean antialias = !inAmbientMode;
            for (Paint p : mPaints) {
                p.setAntiAlias(antialias);
            }
            refresh();

            super.onAmbientModeChanged(inAmbientMode);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            // draw the background
            canvas.drawColor(Color.BLACK);

            mSegmentManager.draw(canvas);
            mClockNumbers.draw(canvas);

            if (!isInAmbientMode()) {
                mSecondsHand.draw(canvas);
            }
            mMinutesHand.draw(canvas);
            mHoursHand.draw(canvas);

        }

        public void onSecondTick() {
            Calendar calendar = Calendar.getInstance();

            int seconds = calendar.get(Calendar.SECOND);
            int minutes = calendar.get(Calendar.MINUTE);
            int hours = calendar.get(Calendar.HOUR);

            mSecondsHand.setAngleFromSeconds(seconds);
            mMinutesHand.setAngle((minutes * 60 + seconds) / 3600f);
            mHoursHand.setAngle((hours * 3600 + minutes * 60 + seconds) / 43200f);

            if (!isInAmbientMode()) {
                refresh();
            }
        }

        // get the calendar events
        public void getEvents() {
            LoadEventsTask task = new LoadEventsTask();
            task.execute();
        }

        @Override
        public void onTimeTick() {

            getEvents();

            refresh();

            super.onTimeTick();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
        }

        private void refresh() {
            if (isVisible()) {
                invalidate();
            }
        }

        public void onEventsReceived(ArrayList<CalendarEvent> events) {
            mSegmentManager.setEvents(events);
        }

    }

    ///
    private class LoadEventsTask extends AsyncTask<Void, Void, ArrayList<CalendarEvent>> {
        @Override
        protected ArrayList<CalendarEvent> doInBackground(Void... voids) {
            long begin = System.currentTimeMillis();
            Uri.Builder builder =
                    WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, begin);
            ContentUris.appendId(builder, begin + DateUtils.DAY_IN_MILLIS);
            final Cursor cursor = getContentResolver() .query(builder.build(),
                    null, null, null, null);

            ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>(cursor.getColumnCount());
            while (cursor.moveToNext()) {
                events.add(CalendarEvent.instanceFromCursor(cursor));
            }
            cursor.close();

            return events;

        }

        @Override
        protected void onPostExecute(ArrayList<CalendarEvent> result) {
            mEngine.onEventsReceived(result);
        }
    }

}
