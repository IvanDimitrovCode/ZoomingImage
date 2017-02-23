package com.example.ivandimitrov.zoomimage;


import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final int STATE_IDLE = 0;
    private static final int STATE_DRAG = 1;
    private static final int STATE_ZOOM = 2;

    private ImageView mImage;
    private Matrix mMatrix       = new Matrix();
    private Matrix mSavedMatrix  = new Matrix();
    private PointF mStartPoint   = new PointF();
    private PointF mMidPoint     = new PointF();
    private float  mOldDist      = 1;
    private int    mCurrentState = STATE_IDLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImage = (ImageView) findViewById(R.id.image);
        mImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        mSavedMatrix.set(mMatrix);
                        mStartPoint.set(event.getX(), event.getY());
                        mCurrentState = STATE_DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        mOldDist = distance(event);
                        if (mOldDist > 10) {
                            mSavedMatrix.set(mMatrix);
                            midPoint(mMidPoint, event);
                            mCurrentState = STATE_ZOOM;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mCurrentState = STATE_IDLE;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mCurrentState == STATE_DRAG) {
                            mMatrix.set(mSavedMatrix);
                            mMatrix.postTranslate(event.getX() - mStartPoint.x, event.getY() - mStartPoint.y);
                        } else if (mCurrentState == STATE_ZOOM) {
                            float newDist = distance(event);
                            if (newDist > 10) {
                                mMatrix.set(mSavedMatrix);
                                float scale = newDist / mOldDist;
                                mMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(mMatrix);
                return true;
            }

            private float distance(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });

    }
}
