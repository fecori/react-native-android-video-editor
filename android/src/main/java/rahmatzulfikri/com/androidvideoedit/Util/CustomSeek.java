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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Vector;

import rahmatzulfikri.com.androidvideoedit.R;

public class CustomSeek {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private int mIndex;
    private float mVal;
    private float mPos;
    private Bitmap mBitmap;
    private int mWidthBitmap;
    private int mHeightBitmap;
    private int imageWidth = 0;
    private int imageHeight = 0;

    private float mLastTouchX;

    private CustomSeek() {
        mVal = 0;
        mPos = 0;
    }

    @NonNull
    public static List<CustomSeek> initThumbs(Resources resources, int imageHeight, int imageWidth) {
        List<CustomSeek> customThumbs = new Vector<>();

        for (int i = 0; i < 2; i++) {
            CustomSeek th = new CustomSeek();
            th.setIndex(i);
            if (i == 0) {
                int resImageLeft = R.drawable.seek;
                if(imageHeight > 0 && imageWidth > 0){
                    Bitmap bmp = BitmapFactory.decodeResource(resources, resImageLeft);
                    th.setBitmap(Bitmap.createScaledBitmap(bmp, imageWidth, imageHeight, false));
                }else{
                    th.setBitmap(BitmapFactory.decodeResource(resources, resImageLeft));
                }
            } else {
                int resImageRight = R.drawable.seek;
                if(imageHeight > 0 && imageWidth > 0){
                    Bitmap bmp = BitmapFactory.decodeResource(resources, resImageRight);
                    th.setBitmap(Bitmap.createScaledBitmap(bmp, imageWidth, imageHeight, false));
                }else{
                    th.setBitmap(BitmapFactory.decodeResource(resources, resImageRight));
                }
            }

            customThumbs.add(th);
        }

        return customThumbs;
    }

    public static int getWidthBitmap(@NonNull List<CustomSeek> customThumbs) {
        return customThumbs.get(0).getWidthBitmap();
    }

    public static int getHeightBitmap(@NonNull List<CustomSeek> customThumbs) {
        return customThumbs.get(0).getHeightBitmap();
    }

    public int getIndex() {
        return mIndex;
    }

    private void setIndex(int index) {
        mIndex = index;
    }

    public float getVal() {
        return mVal;
    }

    public void setVal(float val) {
        mVal = val;
    }

    public float getPos() {
        return mPos;
    }

    public void setPos(float pos) {
        mPos = pos;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private void setBitmap(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;
        mWidthBitmap = bitmap.getWidth();
        mHeightBitmap = bitmap.getHeight();
    }

    public float getLastTouchX() {
        return mLastTouchX;
    }

    public void setLastTouchX(float lastTouchX) {
        mLastTouchX = lastTouchX;
    }

    public int getWidthBitmap() {
        return mWidthBitmap;
    }

    private int getHeightBitmap() {
        return mHeightBitmap;
    }

    public void setDimension(int width, int height){
        this.imageWidth = width;
        this.imageHeight = height;
    }
}
