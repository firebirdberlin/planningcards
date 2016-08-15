package de.mposchmann.planningcards;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import de.firebirdberlin.pageindicator.PageIndicator;

public class MainActivity extends Activity {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    DrawView drawView;
    private CustomViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        final PageIndicator pageIndicator = (PageIndicator) findViewById(R.id.page_indicator);

        List<View> views = new ArrayList<View>();
        List<DrawView> drawViews = new ArrayList<DrawView>();

        String[] texts = new String[] {"0", "1/2", "1", "2", "3", "5", "8", "13", "20", "40",
                                       "100", "?"};

        for (String text : texts) {
            DrawView drawView = new DrawView(this, text);

            //disable the screen lock for card views
            drawView.setKeepScreenOn(true);

            //every view must have its own GestureDetector to handle the events' view later
            MyGestureListener touchListener = new MyGestureListener(drawView, viewPager);
            GestureDetector gestureDetector = new GestureDetector(this, touchListener);
            gestureDetector.setOnDoubleTapListener(touchListener);

            //test scale detection
            ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this,
                                                                                 new ScaleListener(drawViews));
            MyTouchListener onTouchListener = new MyTouchListener(gestureDetector,
                                                                  scaleGestureDetector);
            drawView.setOnTouchListener(onTouchListener);
            /////

            views.add(drawView);
            drawViews.add(drawView);

        }

        MyPageAdapter p = new MyPageAdapter(views);

        pageIndicator.setPageCount(texts.length);

        viewPager.setAdapter(p);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                pageIndicator.setCurrentPage(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( viewPager.isPagingEnabled() ) {
            if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
                viewPager.swipeNext();
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ) {
                viewPager.swipePrev();
                return true;
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                int current = viewPager.getCurrentItem();
                MyPageAdapter adapter = (MyPageAdapter) viewPager.getAdapter();
                DrawView view = (DrawView) adapter.getItem(current);
                if ( view.isHide() ) {
                    view.setHide(false);
                    viewPager.setPagingEnabled(true);
                    return true;
                }
            }
        }
        return false;
    }

   private static class MyTouchListener implements OnTouchListener {

       private final GestureDetector gestureScanner;
       private final ScaleGestureDetector scaleGestureDetector;

        public MyTouchListener(GestureDetector gestureScanner, ScaleGestureDetector scaleGestureDetector) {
            super();
            this.gestureScanner = gestureScanner;
            this.scaleGestureDetector = scaleGestureDetector;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //Log.d("onTouchListener", "touched");
            //return gestureScanner.onTouchEvent(motionEvent);
            //use both detectors
            return gestureScanner.onTouchEvent(motionEvent) || scaleGestureDetector.onTouchEvent(motionEvent);
        }

    };

    private static class MyGestureListener implements OnGestureListener, OnDoubleTapListener {

        private final DrawView drawView;
        private final CustomViewPager viewPager;

        public MyGestureListener(DrawView drawView, CustomViewPager viewPager) {
            super();
            this.drawView = drawView;
            this.viewPager = viewPager;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
           // Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onLongPress for view \"" + drawView.getText() + "\": " + event.toString());
            //switch mode
            toggleDrawView();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
           // Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            toggleDrawView();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return true;
        }

        private void toggleDrawView() {
            drawView.setHide(!drawView.isHide());
            viewPager.setPagingEnabled(! drawView.isHide() );
        }
    }

    private final class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private final List<DrawView> views;

        public ScaleListener(List<DrawView> views) {
            super();
            this.views = views;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.d(DEBUG_TAG, "onScale: " + detector.getScaleFactor());

            for(DrawView view : views) {
                view.zoom(detector.getScaleFactor());
                view.invalidate();  //repaint
            }

            return true;
        }
    }
}
