package de.mposchmann.planningcards;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import de.firebirdberlin.pageindicator.PageIndicator;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    private CustomViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // hide toolbar
        getSupportActionBar().hide();

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        final PageIndicator pageIndicator = (PageIndicator) findViewById(R.id.page_indicator);

        List<View> views = new ArrayList<View>();
        List<PlanningCardView> planningCardViews = new ArrayList<PlanningCardView>();

        String[] texts = new String[] {"0", "1/2", "1", "2", "3", "5", "8", "13", "20", "40",
                                       "100", "?"};

        // init welcome card
        WelcomeCardView welcomeCardView = new WelcomeCardView(this);
        // layer a button on top of the card view
        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutparams);
        layout.addView(welcomeCardView);
        View welcomeView = (View) getLayoutInflater().inflate(R.layout.welcome, null);
        layout.addView(welcomeView);
        views.add(layout);



        // init cards
        for (String text : texts) {
            PlanningCardView planningCardView = new PlanningCardView(this, text);

            //disable the screen lock for card views
            planningCardView.setKeepScreenOn(true);

            //every view must have its own GestureDetector to handle the events' view later
            MyGestureListener touchListener = new MyGestureListener(planningCardView, viewPager);
            GestureDetector gestureDetector = new GestureDetector(this, touchListener);
            gestureDetector.setOnDoubleTapListener(touchListener);

            //scale detection
            ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this,
                                                                                 new ScaleListener(planningCardViews));
            MyTouchListener onTouchListener = new MyTouchListener(gestureDetector,
                                                                  scaleGestureDetector);
            planningCardView.setOnTouchListener(onTouchListener);

            views.add(planningCardView);
            planningCardViews.add(planningCardView);

        }

        MyPageAdapter p = new MyPageAdapter(views);

        pageIndicator.setPageCount(texts.length + 1);

        viewPager.setAdapter(p);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                pageIndicator.setCurrentPage(position);
            }
        });

    }

    public void onClickWelcomeButton(View view) {
        PreferencesActivity.start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            PreferencesActivity.start(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                PlanningCardView view = (PlanningCardView) adapter.getItem(current);
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
            return gestureScanner.onTouchEvent(motionEvent) || scaleGestureDetector.onTouchEvent(motionEvent);
        }

    };

    private static class MyGestureListener implements OnGestureListener, OnDoubleTapListener {

        private final PlanningCardView planningCardView;
        private final CustomViewPager viewPager;

        public MyGestureListener(PlanningCardView planningCardView, CustomViewPager viewPager) {
            super();
            this.planningCardView = planningCardView;
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
            planningCardView.setHide(!planningCardView.isHide());
            viewPager.setPagingEnabled(! planningCardView.isHide() );
        }
    }

    private final class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private final List<PlanningCardView> views;

        public ScaleListener(List<PlanningCardView> views) {
            super();
            this.views = views;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.d(DEBUG_TAG, "onScale: " + detector.getScaleFactor());

            for(PlanningCardView view : views) {
                view.zoom(detector.getScaleFactor());
                view.invalidate();  //repaint
            }

            return true;
        }
    }
}
