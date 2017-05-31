package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.Log;


/**
 * Created by Rahmat Zulfikri on 3/21/17.
 */
public class VideoTimeline extends View {

    private String mVideoUri;
    private int mHeightView;
    private int mStartVideo = 0;
    private int mEndVideo = 0;
    private int mDuration = 0;
    private LongSparseArray<Bitmap> mBitmapList = null;
    private int layoutWidth;
    private int layoutHeight;
    private int paddingLeft;
    private int paddingRight;
    private boolean isThubmnail = true;
    private float x = 0;
    private int x2 = 0;
    private int x3 = 0;
    private long mVideoLength = 0;

    Paint paint = new Paint();
    // Paint text = new Paint();

    public VideoTimeline(@NonNull Context context) {
        this(context, null , 0);
        setWillNotDraw(false);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.0f);
        // text.setColor(Color.WHITE); 
        // text.setTextSize(25);
    }


    public VideoTimeline(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setWillNotDraw(false);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.0f);
        // text.setColor(Color.WHITE); 
        // text.setTextSize(25);
    }

    public VideoTimeline(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3.0f);
        // text.setColor(Color.WHITE); 
        // text.setTextSize(25);
        // init();
    }

    private void init() {
//        mHeightView = 50;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        int usedWidth = layoutWidth - (getPaddingLeft() + getPaddingRight());
        if(!isThubmnail){
            layoutWidth = layoutWidth - Math.round((float)(layoutHeight * 40) / 200f);
            int imageWidth = layoutWidth / 10;
            int nWidth = imageWidth * 10;
            this.setX((Math.round((float)(layoutHeight * 40) / 200f)/2) + ((layoutWidth - nWidth)/2));
            this.setMeasuredDimension( nWidth, layoutHeight);
        }else{
            this.setMeasuredDimension(layoutWidth, layoutHeight);
        }
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w != oldW) {
            getBitmap(w);
        }
    }

    private int getThumbWidth(long videoLength, int width){
        if(isThubmnail){
            return width/7;
        }else{
            // int count = (int)videoLength/60000000;
            return width/10;
        }
    }

    private long getInterval(long videoLength){
        if (mEndVideo == 0) {
            if(isThubmnail){
                return  videoLength / 7;
            }else{
                int count = (int)videoLength/60000000;
                if(count >= 10){
                    return videoLength/count;
                }else{
                    return videoLength/10;
                }
            }
        } else {
            if(isThubmnail){
                return (((mEndVideo*1000) - (mStartVideo*1000))/7);
            }else{
                int count = (int)videoLength/60000000;
                if(count > 10){
                    return (mDuration * 1000L) / (count + 1);
                }else{
                    return (mDuration * 1000L) / 10;
                }
            }
        }
    }

    private int getCount(long videoLength){
        if(isThubmnail){
            return 7;
        }else{
            int count = (int)videoLength/60000000;
            if(count > 10){
                return count + 1;
            }else{
                return 10;
            }
        }
    }

    private void getBitmap(final int width) {
        layoutWidth = width;
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                    @Override
                    public void execute() {
                        try {
                            LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(mVideoUri);
                            int videoWidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                            int videoHeight = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                            int rotation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));

                            Boolean isHorizontal = false;
                            if (rotation == 90 || rotation == 360) {
                                isHorizontal = false;
                            }
                            if (rotation == 0 || rotation == 180) {
                                if(videoHeight > videoWidth){
                                    isHorizontal = false;
                                }else {
                                    isHorizontal = true;
                                }
                            }

                            // Retrieve media data
                            long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;
                            mVideoLength = videoLengthInMs / 1000;
                            // Set thumbnail properties (Thumbs are squares)

                            final int thumbWidth = getThumbWidth(videoLengthInMs, width);
                            final int thumbHeight = layoutHeight - 40;

                            final long interval = getInterval(videoLengthInMs);

                            final int count = getCount(videoLengthInMs);
                            for (int i = 0; i < count; i++) {
                                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(((i * interval) + (mStartVideo * 1000L)), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                try {
                                    if(isHorizontal){
                                        if(count > 10 && 1 == count -1){
                                            int minuteDurationI = (int)mVideoLength/60000;
                                            float minuteDurationF = (float)mVideoLength/60000.0f;
                                            float imageWidthLeft = minuteDurationF - minuteDurationI;
                                            int xOffset = (bitmap.getWidth() - bitmap.getHeight()) / 2;
                                            bitmap = Bitmap.createBitmap(bitmap, xOffset,0,bitmap.getHeight(), bitmap.getHeight());
                                            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(thumbWidth * imageWidthLeft) , thumbHeight, false);
                                        }else{
                                            int xOffset = (bitmap.getWidth() - bitmap.getHeight()) / 2;
                                            bitmap = Bitmap.createBitmap(bitmap, xOffset,0,bitmap.getHeight(), bitmap.getHeight());
                                            bitmap = Bitmap.createScaledBitmap(bitmap,thumbWidth , thumbHeight, false);
                                        }

                                    }else{
                                        if(count > 10 && 1 == count -1){
                                            int minuteDurationI = (int)mVideoLength/60000;
                                            float minuteDurationF = (float)mVideoLength/60000.0f;
                                            float imageWidthLeft = minuteDurationF - minuteDurationI;
                                            int yOffset = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                                            bitmap = Bitmap.createBitmap(bitmap, 0,yOffset,bitmap.getWidth(), bitmap.getWidth());
                                            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(thumbWidth * imageWidthLeft) , thumbHeight, false);  
                                        }else{
                                            int yOffset = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                                            bitmap = Bitmap.createBitmap(bitmap, 0,yOffset,bitmap.getWidth(), bitmap.getWidth());
                                            bitmap = Bitmap.createScaledBitmap(bitmap,thumbWidth , thumbHeight, false);   
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                thumbnailList.put(i, bitmap);
                            }

                            mediaMetadataRetriever.release();
                            returnBitmaps(thumbnailList);
                        } catch (final Throwable e) {
                            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                        }
                    }
                }
        );
    }

    private void returnBitmaps(final LongSparseArray<Bitmap> thumbnailList) {
        UiThreadExecutor.runTask("", new Runnable() {
                    @Override
                    public void run() {
                        mBitmapList = thumbnailList;
                        invalidate();
                    }
                }
                , 0L);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmapList != null) {
            canvas.save();
            x = 0.0f + x2;

            // Handle draw image timeline
            int timelineWidth = 0;
            for (int i = 0; i < mBitmapList.size(); i++) {
                Bitmap bitmap = mBitmapList.get(i);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x, 20, null);
                    x = x + bitmap.getWidth();
                    timelineWidth += bitmap.getWidth();
                }
            }
            x3 = (int)x;

            // Handle draw ruler
            int minuteDurationI = (int)mVideoLength/60000;
            float minuteDurationF = (float)mVideoLength/60000.0f;
            float minuteBarRange = 0;
            if(minuteDurationF > 10){
                minuteBarRange = mBitmapList.get(0).getWidth();
            }else{
                minuteBarRange =  timelineWidth / minuteDurationF;
            }
            
            for(int i = 0; i <= minuteDurationI; i++){
                if(minuteDurationI <= 5){
                    for(float j = 1; j< 10; j++){
                        float sInterval = minuteBarRange / 10.0f;
                        canvas.drawLine((j*sInterval) + (i*minuteBarRange) + x2, 0, (j*sInterval) + (i*minuteBarRange) + x2, 10, paint);
                        canvas.drawLine((j*sInterval) + (i*minuteBarRange) + x2, layoutHeight - 10, (j*sInterval) + (i*minuteBarRange) + x2, layoutHeight, paint);
                    }
                }else{
                    for(float j = 1; j< 5; j++){
                        float sInterval = minuteBarRange / 5.0f;
                        canvas.drawLine((j*sInterval) + (i*minuteBarRange) + x2, 0, (j*sInterval) + (i*minuteBarRange) + x2, 10, paint);
                        canvas.drawLine((j*sInterval) + (i*minuteBarRange) + x2, layoutHeight - 10, (j*sInterval) + (i*minuteBarRange) + x2, layoutHeight, paint);
                    }
                }

                canvas.drawLine((i*minuteBarRange + x2), 0,(i*minuteBarRange + x2),25, paint);
                canvas.drawLine((i*minuteBarRange + x2), layoutHeight - 25, (i*minuteBarRange + x2), layoutHeight, paint);
            }
        }

    }

    public int getCanvasWidth(){
        return x3;
    }

    public int getCanvasWidthStart(){
        return x2;
    }

    public void setXPos(int x){
        x2 = x;
        postInvalidate();
    }

    public void setThumbnail(@NonNull String data, int start, int end){
        mVideoUri = data;
        mStartVideo = start;
        mEndVideo = end;
        mDuration = mEndVideo - mStartVideo;
        isThubmnail = true;
    }

    public void setTrim(@NonNull String data){
        mVideoUri = data;
//        mStartVideo = start;
//        mEndVideo = end;
//        mDuration = mEndVideo - mStartVideo;
        isThubmnail = false;
    }


}
