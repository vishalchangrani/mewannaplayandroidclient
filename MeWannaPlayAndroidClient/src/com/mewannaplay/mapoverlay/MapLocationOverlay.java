
package com.mewannaplay.mapoverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.mewannaplay.R;
import com.mewannaplay.model.TennisCourt;

public class MapLocationOverlay extends Overlay implements OnGestureListener, OnDoubleTapListener {

    // Store these as global instances so we don't keep reloading every time
    private final Bitmap publicSemiOccupiedCourt, publicNotOccupiedCourt, publicFullyOccupiedCourt, privateCourt, arrowIcon;

    private final GestureDetector gestureDetector;

    private Context context = null;
    private Paint innerPaint, borderPaint, textPaint;
    List<TennisCourt> tennisCourts = new ArrayList<TennisCourt>();

    // The currently selected Map Location...if any is selected. This tracks
    // whether an information
    // window should be displayed & where...i.e. whether a user 'clicked' on a
    // known map location
    private TennisCourt selectedMapLocation;

    public MapLocationOverlay(Context context) {
        this.context = context;
        publicSemiOccupiedCourt = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tennisball_yellow_16);
        publicNotOccupiedCourt = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tennisball_green_18);
        publicFullyOccupiedCourt = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tennisball_red_15);
        privateCourt = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tennisball_yellow_locked);
        arrowIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.arrow_right_16);
      
        this.gestureDetector = new GestureDetector(this.context, this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        Log.e("MeWannaPlay", "onTap ");
        // super.onTap(p, mapView);

        // Store whether prior popup was displayed so we can call invalidate() &
        // remove it if necessary.

        boolean isRemovePriorPopup = selectedMapLocation != null;
        // Next test whether a new popup should be displayed
        selectedMapLocation = getHitMapLocation(mapView, p);

        if (isRemovePriorPopup || selectedMapLocation != null) {
            mapView.invalidate();
        } // Lastly return true if we handled this onTap()
        return selectedMapLocation != null;

    }

  

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	if (mapView.getZoomLevel() < 10)
    		return;
        drawMapLocations(canvas, mapView, shadow);
        drawInfoWindow(canvas, mapView, shadow);
    }

    /**
     * Test whether an information balloon should be displayed or a prior
     * balloon hidden.
     */
    private TennisCourt getHitMapLocation(MapView mapView, GeoPoint tapPoint) {

        // Track which MapLocation was hit...if any
        TennisCourt hitMapLocation = null;

        RectF hitTestRecr = new RectF();
        Point screenCoords = new Point();
        Iterator<TennisCourt> iterator = getMapLocations().iterator();
        while (iterator.hasNext()) {
            TennisCourt testLocation = iterator.next();

            // Translate the MapLocation's lat/long coordinates to screen
            // coordinates
            mapView.getProjection().toPixels(testLocation.getGeoPoint(), screenCoords);

            // Create a 'hit' testing Rectangle w/size and coordinates of our
            // icon
            // Set the 'hit' testing Rectangle with the size and coordinates of
            // our on screen icon
            hitTestRecr.set(-publicSemiOccupiedCourt.getWidth() / 2, -publicSemiOccupiedCourt.getHeight(),
                    publicSemiOccupiedCourt.getWidth() / 2, 0);
            hitTestRecr.offset(screenCoords.x, screenCoords.y);

            // Finally test for a match between our 'hit' Rectangle and the
            // location clicked by the user
            mapView.getProjection().toPixels(tapPoint, screenCoords);
            if (hitTestRecr.contains(screenCoords.x, screenCoords.y)) {
                hitMapLocation = testLocation;
                break;
            }
        }

        // Lastly clear the newMouseSelection as it has now been processed
        tapPoint = null;

        return hitMapLocation;
    }

    private void drawMapLocations(Canvas canvas, MapView mapView, boolean shadow) {

        Iterator<TennisCourt> iterator = getMapLocations().iterator();
        Point screenCoords = new Point();
        while (iterator.hasNext()) {
            TennisCourt location = iterator.next();
            GeoPoint geoPoint = location.getGeoPoint();
            Log.e("MeWannaPlay", "--getLatitudeE6------------------"
                    + geoPoint.getLatitudeE6());
            Log.e("MeWannaPlay", "---getLongitudeE6-----------------"
                    + geoPoint.getLongitudeE6());

            mapView.getProjection().toPixels(geoPoint, screenCoords);

            if (shadow) {
                // Only offset the shadow in the y-axis as the shadow is angled
                // so the base is at x=0;
                // canvas.drawBitmap(shadowIcon, screenCoords.x, screenCoords.y
                // - shadowIcon.getHeight(),null);
            } else {
                Bitmap targetBitMap = null;
            	if (location.isPrivate())
            		targetBitMap = privateCourt;
            	else if (location.isOccupied())
            		targetBitMap = publicFullyOccupiedCourt;
            	else if (location.isNotOccupied())
            		targetBitMap = publicNotOccupiedCourt;
            	else
            		targetBitMap = publicSemiOccupiedCourt;
            	canvas.drawBitmap(targetBitMap, screenCoords.x - targetBitMap.getWidth() / 2,
                        screenCoords.y - targetBitMap.getHeight(), null);
            }
        }
    }

    private void drawInfoWindow(Canvas canvas, MapView mapView, boolean shadow) {

        if (selectedMapLocation != null) {
            if (shadow) {
                // Skip painting a shadow in this tutorial
            } else {
                // First determine the screen coordinates of the selected
                // MapLocation

                Point selDestinationOffset = new Point();
                mapView.getProjection().toPixels(selectedMapLocation.getGeoPoint(),
                        selDestinationOffset);

                // Setup the info window with the right size & location
                int INFO_WINDOW_WIDTH = 125;
                int INFO_WINDOW_HEIGHT = 35; // 25;
                RectF infoWindowRect = new RectF(0, 0, INFO_WINDOW_WIDTH + 16, INFO_WINDOW_HEIGHT);
                int infoWindowOffsetX = selDestinationOffset.x - INFO_WINDOW_WIDTH / 2;
                int infoWindowOffsetY = selDestinationOffset.y - INFO_WINDOW_HEIGHT
                        - publicSemiOccupiedCourt.getHeight();
                infoWindowRect.offset(infoWindowOffsetX, infoWindowOffsetY);

                int infoWindowOffsetY1 = selDestinationOffset.y;
                int infoWindowOffsetX1 = selDestinationOffset.x;
                int[] _data = {
                        infoWindowOffsetX1, infoWindowOffsetY1
                };
                Path _path = new Path();
                _path.moveTo(_data[0], _data[1]);

                infoWindowOffsetX1 = infoWindowOffsetX1 + 7;
                infoWindowOffsetY1 = infoWindowOffsetY1 + 7;
                _path.lineTo(infoWindowOffsetX1, infoWindowOffsetY1);

                infoWindowOffsetX1 = infoWindowOffsetX1 - 14;
                infoWindowOffsetY1 = infoWindowOffsetY1;

                _path.lineTo(infoWindowOffsetX1, infoWindowOffsetY1);

                infoWindowOffsetX1 = infoWindowOffsetX1 + 7;
                infoWindowOffsetY1 = infoWindowOffsetY1 - 7;

                _path.lineTo(infoWindowOffsetX1, infoWindowOffsetY1);

                int infoWindowOffsetY2 = selDestinationOffset.y + 7;
                int infoWindowOffsetX2 = selDestinationOffset.x - 70;

                RectF infoWindowRect1 = new RectF(0, 0, INFO_WINDOW_WIDTH + 16, INFO_WINDOW_HEIGHT);
                infoWindowRect1.offset(infoWindowOffsetX2, infoWindowOffsetY2);
                Paint paint1 = getInnerPaint();
                int color = paint1.getColor();
                paint1.setARGB(180, 255, 255, 0);

                // Draw inner info window
                canvas.drawPath(_path, paint1);

                canvas.drawRoundRect(infoWindowRect1, 5, 5, getInnerPaint());

                paint1.setColor(color);

                // Draw inner info window
                // canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());

                // Draw border for info window
                // canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());

                // Draw the MapLocation's name
                int TEXT_OFFSET_X = 10;
                int TEXT_OFFSET_Y = 15;
                Paint paint = getTextPaint();
                // paint.setAlpha(150);
                float textSize = paint.getTextSize();
                int color1 = paint.getColor();
                Log.e("MeWannaPlay", "The color is " + color1);
                paint.setColor(0xFF000000);
                // paint.setTextAlign(Paint.Align.CENTER) ;
                // paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                canvas.drawText(selectedMapLocation.getName(), infoWindowOffsetX2
                        + TEXT_OFFSET_X,
                        infoWindowOffsetY2 + TEXT_OFFSET_Y, paint);
                paint.setTextSize(8);
                paint.setColor(0xFF060010);
                canvas.drawText("Long-press for details", infoWindowOffsetX2 + TEXT_OFFSET_X,
                        infoWindowOffsetY2 + TEXT_OFFSET_Y + TEXT_OFFSET_Y, paint);

                paint.setTextSize(textSize);
                paint.setColor(color1);
                canvas.drawBitmap(arrowIcon, infoWindowOffsetX2 + INFO_WINDOW_WIDTH,
                        infoWindowOffsetY2 + 8, null);

            }
        }
    }

    public Paint getInnerPaint() {
        if (innerPaint == null) {
            innerPaint = new Paint();
            innerPaint.setARGB(225, 75, 75, 75); // gray
            innerPaint.setAntiAlias(true);
        }
        return innerPaint;
    }

    public Paint getBorderPaint() {
        if (borderPaint == null) {
            borderPaint = new Paint();
            borderPaint.setARGB(255, 255, 255, 255);
            borderPaint.setAntiAlias(true);
            borderPaint.setStyle(Style.STROKE);
            borderPaint.setStrokeWidth(2);
        }
        return borderPaint;
    }

    public Paint getTextPaint() {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setARGB(255, 255, 255, 255);
            textPaint.setAntiAlias(true);
        }
        return textPaint;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // TODO Auto-generated method stub
        Log.e("MeWannaPlay", "onDoubleTap occure 1");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {

        Log.e("MeWannaPlay", "onDoubleTapEvent occure 1");
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e("MeWannaPlay", "onLongPress ");
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void addMapLocation(TennisCourt tennisCourt)
    {
    	tennisCourts.add(tennisCourt);
    }
    
    public void clearAllMapLocations()
    {
    	tennisCourts.clear();
    }
    
    public List<TennisCourt> getMapLocations() {
        return tennisCourts;
    }
}
