package com.hackathon.projecttron;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;

    /**
     * "Hello World!" {@link View} generated by {@link #buildView()}.
     */
    private View mView;
    private ArrayList<Point> myPath = new ArrayList<Point>();
    private ArrayList<Point> hisPath = new ArrayList<Point>();
    private boolean running;
    private float posX;
    private float posY;
    private float yAug;
    private float xAug;

    private Handler handler;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);
        handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                if(running){
                    yAug = posY - 180;
                    xAug = posX - 320;
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    public void onDead() {

    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        //CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        Canvas c = new Canvas();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        paint.setColor(Color.GREEN);
        c.drawCircle(320, 180, 25, paint);

        paint.setColor(Color.BLUE);
        for (int i = 0; i < myPath.size(); i++){
            if (i > 0){
                Point pp = myPath.get(i-1);
                Point tp = myPath.get(i);
                c.drawLine(pp.x + xAug, pp.y + yAug, tp.x, tp.y, paint);
            }
        }
        paint.setColor(Color.RED);
        for (int i = 0; i < hisPath.size(); i++){
            if (i > 0){
                Point pp = hisPath.get(i-1);
                Point tp = hisPath.get(i);
                c.drawLine(pp.x + xAug, pp.y + yAug, tp.x, tp.y, paint);
            }
        }

        View v = new View(this);
        v.draw(c);

        return v;
    }
}
