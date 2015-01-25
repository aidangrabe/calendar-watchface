package com.aidangrabe.customwatchface;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aidan on 03/01/15.
 * This class is the main WatchFace Service. It handles the callbacks and rendering
 * of the WatchFace.
 */
public class SimpleWatchfaceService extends CanvasWatchFaceService {

    // look for events 12 hours from the current time
    private static final long CALENDAR_LOOKAHEAD_MILLIS = DateUtils.DAY_IN_MILLIS / 2;

    private Engine mEngine;

    @Override
    public Engine onCreateEngine() {
        mEngine = new Engine();
        return mEngine;
    }

    /**
     * This class is in charge of rendering the WatchFace and handling the
     * WatchFace events
     */
    private class Engine extends CanvasWatchFaceService.Engine {

        private Bitmap mBackground;
        private Point mCenter;
        private Paint mClockPaint, mHandPaint, mSecondHandPaint;
        private ClockHand mMinutesHand, mSecondsHand, mHoursHand;
        private ClockNumbers mClockNumbers;
        private EventSegmentManager mSegmentManager;

        // Rects used for scaling the background image
        private Rect bgSrcRect, bgDstRect;
        private NextEventInfo mNextEventInfo;
        private AllDayEventInfo mAllDayEventInfo;

        private ArrayList<Paint> mPaints;

        private Timer mTimer;
        private final TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                onSecondTick();
            }
        };

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            mCenter.set(width / 2, height / 2);

            // position the hands
            mSecondsHand.setPosition(mCenter);
            mMinutesHand.setPosition(mCenter);
            mHoursHand.setPosition(mCenter);

            mBackground = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.back_gradient);
            bgSrcRect = new Rect(0, 0, mBackground.getWidth(), mBackground.getHeight());
            bgDstRect = new Rect(0, 0, holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());

            mNextEventInfo = new NextEventInfo(new Point(mCenter.x, mCenter.y + 40));
            mAllDayEventInfo = new AllDayEventInfo();
            mAllDayEventInfo.position.set(mCenter.x, mCenter.y - 60);

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

            mHandPaint = new Paint();
            mHandPaint.setColor(Color.WHITE);
            mHandPaint.setAntiAlias(true);
            mHandPaint.setStrokeCap(Paint.Cap.BUTT);
            mHandPaint.setStrokeWidth(3);

            mSecondHandPaint = new Paint(mHandPaint);
            mSecondHandPaint.setStrokeWidth(1);

            mSecondsHand = new ClockHand(mSecondHandPaint);
            mMinutesHand = new ClockHand(mHandPaint);
            mHoursHand = new ClockHand(mHandPaint);

            mSecondsHand.setLength(120);
            mMinutesHand.setLength(100);
            mHoursHand.setLength(80);

            mClockNumbers = new ClockNumbers(mClockPaint, new Point(160, 160), 150);
            mSegmentManager = new EventSegmentManager(mCenter, 150);

            // add all paints to the array so we can toggle antialiasing later
            mPaints = new ArrayList<Paint>();
            mPaints.add(mClockPaint);
            mPaints.add(mSegmentManager.getPaint());
            mPaints.add(mHandPaint);
            mPaints.add(mSecondHandPaint);

            getEvents();

        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {

            // toggle antialiasing for all the paints
            boolean antialias = !inAmbientMode;
            for (Paint p : mPaints) {
                p.setAntiAlias(antialias);
            }

            mNextEventInfo.setAmbientMode(inAmbientMode);
            mAllDayEventInfo.getPaint().setAntiAlias(antialias);

            refresh();

            super.onAmbientModeChanged(inAmbientMode);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            // draw the background
            if (!isInAmbientMode()) {
                canvas.drawBitmap(mBackground, bgSrcRect, bgDstRect, null);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            mSegmentManager.draw(canvas);
            mClockNumbers.draw(canvas);

            if (!isInAmbientMode()) {
                mSecondsHand.draw(canvas);
            }
            mMinutesHand.draw(canvas);
            mHoursHand.draw(canvas);

            mNextEventInfo.draw(canvas);
            mAllDayEventInfo.draw(canvas);

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
            mNextEventInfo.setEvents(events);
            mAllDayEventInfo.setEvents(events);
        }

    }

    // AsyncTask to get the calendar events
    private class LoadEventsTask extends AsyncTask<Void, Void, ArrayList<CalendarEvent>> {
        @Override
        protected ArrayList<CalendarEvent> doInBackground(Void... voids) {
            long begin = System.currentTimeMillis();
            Uri.Builder builder =
                    WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, begin);
            ContentUris.appendId(builder, begin + CALENDAR_LOOKAHEAD_MILLIS);
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
