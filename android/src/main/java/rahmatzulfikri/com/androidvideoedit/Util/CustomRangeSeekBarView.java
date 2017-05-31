/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rahmatzulfikri.com.androidvideoedit.R;

public class CustomRangeSeekBarView extends View {

    private static final String TAG = CustomRangeSeekBarView.class.getSimpleName();
    private final Paint mShadow = new Paint();
    private final Paint mLine = new Paint();
    private int mHeightTimeLine;
    private List<CustomSeek> mCustomThumbs;
    private List<OnRangeSeekBarListener> mListeners;
    private float mMaxWidth = 0;
    private float mThumbWidth = 0;
    private float mThumbHeight;
    private int mViewWidth;
    private float mPixelRangeMin;
    private float mPixelRangeMax = 0;
    private float mScaleRangeMax;
    private boolean mFirstRun;
    private int currentThumb = 0;
    private int mMinWidth= 0;

    private float minDuration = 0;

    public CustomRangeSeekBarView(@NonNull Context context) {
        this(context, null, 0);
    }

    public CustomRangeSeekBarView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRangeSeekBarView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(0,0);
    }

    public void init(int height, int width) {

        mCustomThumbs = CustomSeek.initThumbs(getResources(), height, width);
        mThumbWidth = CustomSeek.getWidthBitmap(mCustomThumbs);
        mThumbHeight = CustomSeek.getHeightBitmap(mCustomThumbs);
//        Log.e("DEBUG", "INI "+mThumbWidth+" "+mThumbHeight);

        mScaleRangeMax = 100;
        mHeightTimeLine = 10;

        setFocusable(true);
        setFocusableInTouchMode(true);

        mFirstRun = true;

        int shadowColor = ContextCompat.getColor(getContext(), R.color.black_translucent);
        mShadow.setAntiAlias(true);
        mShadow.setColor(shadowColor);
        mShadow.setAlpha(177);

        int lineColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mLine.setAntiAlias(true);
        mLine.setColor(lineColor);
        mLine.setAlpha(200);
//        Log.e("ini layout width", String.valueOf(this));
    }

    public void initMaxWidth() {
        mMaxWidth = mCustomThumbs.get(1).getPos() - mCustomThumbs.get(0).getPos();

        onSeekStop(this, 0, mCustomThumbs.get(0).getVal());
        onSeekStop(this, 1, mCustomThumbs.get(1).getVal());
    }
    
    public void setMargin(float x){
        this.setX(x);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // use the provided width and height for whatever you need
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
//        mViewWidth = resolveSizeAndState(minW, widthMeasureSpec, 1);
        mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec);

        int minH = getPaddingBottom() + getPaddingTop() + (int) mThumbHeight + mHeightTimeLine;
//        int viewHeight = resolveSizeAndState(minH, heightMeasureSpec, 1);
        int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);

//        Log.e("DEBUG", "MEASURE "+mViewWidth+" "+viewHeight);
        setMeasuredDimension(mViewWidth, viewHeight);

        mPixelRangeMin = 0;
        mPixelRangeMax = mViewWidth - mThumbWidth;

        // Log.e("DEBUG", "MASUK ON MEASURE" + minDuration +" "+ mPixelRangeMax +" "+ mViewWidth +" "+mThumbWidth);
//        if (mFirstRun) {
            for (int i = 0; i < mCustomThumbs.size(); i++) {
                CustomSeek th = mCustomThumbs.get(i);
                th.setVal(mScaleRangeMax * i);
                th.setPos(mPixelRangeMax * i);
            }
            // Fire listener callback
            onCreate(this, currentThumb, getThumbValue(currentThumb));
//            mFirstRun = false;
//        }

        mMaxWidth = mCustomThumbs.get(1).getPos() - mCustomThumbs.get(0).getPos();
//        Log.e("DEBUG", "INI MAX WIDTH" + mMaxWidth +" "+ mCustomThumbs.get(0).getPos() + " "+ mCustomThumbs.get(1).getPos() );

        // Log.e("DEBUG", "MIN WIDTH "+minDuration+" "+mPixelRangeMax);
        mMinWidth = Math.round((minDuration * mPixelRangeMax)/100f);
        // Log.e("DEBUG", "MIN WIDTH "+(Math.round((minDuration * mPixelRangeMax)/100f)));
        // Log.e("DEBUG", "MIN WIDTH "+mMinWidth);

        onSeekStop(this, 0, mCustomThumbs.get(0).getVal());
        onSeekStop(this, 1, mCustomThumbs.get(1).getVal());
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawShadow(canvas);
        drawThumbs(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final CustomSeek mCustomThumb;
        final CustomSeek mCustomThumb2;
        final float coordinate = ev.getX();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where we started
                currentThumb = getClosestThumb(coordinate);

                if (currentThumb == -1) {
                    return false;
                }

                mCustomThumb = mCustomThumbs.get(currentThumb);
                mCustomThumb.setLastTouchX(coordinate);
                onSeekStart(this, currentThumb, mCustomThumb.getVal());
                return true;
            }
            case MotionEvent.ACTION_UP: {

                if (currentThumb == -1) {
                    return false;
                }

                mCustomThumb = mCustomThumbs.get(currentThumb);
                onSeekStop(this, currentThumb, mCustomThumb.getVal());
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                mCustomThumb = mCustomThumbs.get(currentThumb);
                mCustomThumb2 = mCustomThumbs.get(currentThumb == 0 ? 1 : 0);
                // Calculate the distance moved
                final float dx = coordinate - mCustomThumb.getLastTouchX();
                final float newX = mCustomThumb.getPos() + dx;

                // Log.e("DEBUG", "MIN WIDTH0 "+this.mMinWidth);

                if (currentThumb == 0) {
                    if (newX <= mPixelRangeMin) {
                        mCustomThumb.setPos(mPixelRangeMin);
                    } else {
                        if(mCustomThumb2.getPos() - newX >= mMinWidth)
                        {
                            //Check if thumb is not out of max width
                            checkPositionThumb(mCustomThumb, mCustomThumb2, dx, true);
                            // Move the object
                            mCustomThumb.setPos(mCustomThumb.getPos() + dx);

                            // Remember this touch position for the next move event
                            mCustomThumb.setLastTouchX(coordinate);
                        }
                    }

                } else {

                    if (newX >= mPixelRangeMax) {
                        mCustomThumb.setPos(mPixelRangeMax);
                    } else {
                        if(newX - mCustomThumb2.getPos() >= mMinWidth) {
                            //Check if thumb is not out of max width
                            checkPositionThumb(mCustomThumb2, mCustomThumb, dx, false);
                            // Move the object
                            mCustomThumb.setPos(mCustomThumb.getPos() + dx);
                            // Remember this touch position for the next move event
                            mCustomThumb.setLastTouchX(coordinate);
                        }
                    }
                }
                setThumbPos(currentThumb, mCustomThumb.getPos());

                // Invalidate to request a redraw
                invalidate();
                return true;
            }
        }
        return false;
    }

    private void checkPositionThumb(@NonNull CustomSeek mCustomThumbLeft, @NonNull CustomSeek mCustomThumbRight, float dx, boolean isLeftMove) {
        if (isLeftMove && dx < 0) {
            if ((mCustomThumbRight.getPos() - (mCustomThumbLeft.getPos() + dx)) > mMaxWidth) {
                mCustomThumbRight.setPos(mCustomThumbLeft.getPos() + dx + mMaxWidth);
                setThumbPos(1, mCustomThumbRight.getPos());
            }
        } else if (!isLeftMove && dx > 0) {
            if (((mCustomThumbRight.getPos() + dx) - mCustomThumbLeft.getPos()) > mMaxWidth) {
                mCustomThumbLeft.setPos(mCustomThumbRight.getPos() + dx - mMaxWidth);
                setThumbPos(0, mCustomThumbLeft.getPos());
            }
        }
    }

    private int getUnstuckFrom(int index) {
        int unstuck = 0;
        float lastVal = mCustomThumbs.get(index).getVal();
        for (int i = index - 1; i >= 0; i--) {
            CustomSeek th = mCustomThumbs.get(i);
            if (th.getVal() != lastVal)
                return i + 1;
        }
        return unstuck;
    }

    private float pixelToScale(int index, float pixelValue) {
        float scale = (pixelValue * 100) / mPixelRangeMax;
        if (index == 0) {
            float pxThumb = (scale * (mThumbWidth/700)) / 100;
            return scale + (pxThumb * 100) / mPixelRangeMax;
        } else {
            float pxThumb = ((100 - scale) * (mThumbWidth/700)) / 100;
            return scale - (pxThumb * 100) / mPixelRangeMax;
        }
    }

    private float scaleToPixel(int index, float scaleValue) {
        float px = (scaleValue * mPixelRangeMax) / 100;
        if (index == 0) {
            float pxThumb = (scaleValue * (mThumbWidth/700)) / 100;
            return px - pxThumb;
        } else {
            float pxThumb = ((100 - scaleValue) * (mThumbWidth/700)) / 100;
            return px + pxThumb;
        }
    }

    private void calculateThumbValue(int index) {
        if (index < mCustomThumbs.size() && !mCustomThumbs.isEmpty()) {
            CustomSeek th = mCustomThumbs.get(index);
            th.setVal(pixelToScale(index, th.getPos()));
            onSeek(this, index, th.getVal());
        }
    }

    private void calculateThumbPos(int index) {
        if (index < mCustomThumbs.size() && !mCustomThumbs.isEmpty()) {
            CustomSeek th = mCustomThumbs.get(index);
            th.setPos(scaleToPixel(index, th.getVal()));
        }
    }

    private float getThumbValue(int index) {
        return mCustomThumbs.get(index).getVal();
    }

    public void setThumbValue(int index, float value) {
        mCustomThumbs.get(index).setVal(value);
        calculateThumbPos(index);
        // Tell the view we want a complete redraw
        invalidate();
    }

    private void setThumbPos(int index, float pos) {
        mCustomThumbs.get(index).setPos(pos);
        calculateThumbValue(index);

        // Tell the view we want a complete redraw
        invalidate();
    }

    private int getClosestThumb(float coordinate) {
        int closest = -1;
        if (!mCustomThumbs.isEmpty()) {
            for (int i = 0; i < mCustomThumbs.size(); i++) {
                // Find thumb closest to x coordinate
                final float tcoordinate = mCustomThumbs.get(i).getPos() + mThumbWidth;
                if (coordinate >= mCustomThumbs.get(i).getPos() && coordinate <= tcoordinate) {
                    closest = mCustomThumbs.get(i).getIndex();
                }
            }
        }
        return closest;
    }

    private void drawShadow(@NonNull Canvas canvas) {
        if (!mCustomThumbs.isEmpty()) {

            for (CustomSeek th : mCustomThumbs) {
                if (th.getIndex() == 0) {
                    final float x = th.getPos() - (mThumbWidth/2);
                    if (x > mPixelRangeMin) {

                        Rect mRect = new Rect((int)(0 + mThumbWidth/2), 0, (int) (x +  mThumbWidth), (int) mThumbHeight);
                        canvas.drawRect(mRect, mShadow);
                    }
                } else {
                    final float x = th.getPos() + (mThumbWidth/2);
                    if (x < mPixelRangeMax) {
                        Rect mRect = new Rect((int)x, 0, (int)(mViewWidth - (mThumbWidth/2)), (int) mThumbHeight);
                        canvas.drawRect(mRect, mShadow);
                    }
                }
            }
        }
    }

    public void setRangeMin(float rangeMin){
        minDuration = rangeMin;
        mMinWidth = Math.round((rangeMin * mPixelRangeMax)/100f);
    }

    private void drawThumbs(@NonNull Canvas canvas) {

        if (!mCustomThumbs.isEmpty()) {
            for (CustomSeek th : mCustomThumbs) {
                if (th.getIndex() == 0) {
//                    Bitmap bitmap = Bitmap.createScaledBitmap(th.getBitmap(), 35, 100, false);
                    canvas.drawBitmap(th.getBitmap(), th.getPos() , getPaddingTop(), null);
//                    canvas.drawBitmap(th.getBitmap(), th.getPos() + getPaddingLeft(), getPaddingTop() + mHeightTimeLine, null);
                } else {
//                    Bitmap bitmap = Bitmap.createScaledBitmap(th.getBitmap(), 35, 100, false);
                    canvas.drawBitmap(th.getBitmap(), th.getPos() , getPaddingTop(), null);
//                    canvas.drawBitmap(th.getBitmap(), th.getPos() - getPaddingRight(), getPaddingTop() + mHeightTimeLine, null);
                }
            }
        }
    }

    public void addOnRangeSeekBarListener(OnRangeSeekBarListener listener) {

        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }

        mListeners.add(listener);
    }

    private void onCreate(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (OnRangeSeekBarListener item : mListeners) {
            item.onCreate(customRangeSeekBarView, index, value);
        }
    }

    private void onSeek(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeek(customRangeSeekBarView, index, value);
        }
    }

    private void onSeekStart(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeekStart(customRangeSeekBarView, index, value);
        }
    }

    private void onSeekStop(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
        if (mListeners == null)
            return;

        for (OnRangeSeekBarListener item : mListeners) {
            item.onSeekStop(customRangeSeekBarView, index, value);
        }
    }

    public List<CustomSeek> getThumbs() {
        return mCustomThumbs;
    }
}
