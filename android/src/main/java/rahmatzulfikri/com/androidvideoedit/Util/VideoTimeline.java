package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.View;

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
    private int x = 0;
    private int x2 = 0;
    private int x3 = 0;

    public VideoTimeline(@NonNull Context context) {
        this(context, null , 0);
        setWillNotDraw(false);
    }


    public VideoTimeline(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setWillNotDraw(false);
    }

    public VideoTimeline(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init();
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
            layoutWidth = layoutWidth - Math.round((float)(layoutHeight * 70) / 200f);
            this.setX(Math.round((float)(layoutHeight * 70) / 200f)/2);
            this.setMeasuredDimension( layoutWidth, layoutHeight);
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
            int count = (int)videoLength/60000000;
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
                if(count >= 10){
                    return (mDuration * 1000L) / count;
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
            if(count >= 10){
                return count;
            }else{
                return 10;
            }
        }
    }

    private void getBitmap(final int width) {
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
                            // Set thumbnail properties (Thumbs are squares)

                            final int thumbWidth = getThumbWidth(videoLengthInMs, width);
                            final int thumbHeight = layoutHeight;

                            final long interval = getInterval(videoLengthInMs);

                            final int count = getCount(videoLengthInMs);
                            for (int i = 0; i < count; i++) {
                                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(((i * interval) + (mStartVideo * 1000L)), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                try {
                                    if(isHorizontal){
                                        int xOffset = (bitmap.getWidth() - bitmap.getHeight()) / 2;
                                        bitmap = Bitmap.createBitmap(bitmap, xOffset,0,bitmap.getHeight(), bitmap.getHeight());
                                        bitmap = Bitmap.createScaledBitmap(bitmap,thumbWidth , thumbHeight, false);

                                    }else{
                                        int yOffset = (bitmap.getHeight() - bitmap.getWidth()) / 2;
                                        bitmap = Bitmap.createBitmap(bitmap, 0,yOffset,bitmap.getWidth(), bitmap.getWidth());
                                        bitmap = Bitmap.createScaledBitmap(bitmap,thumbWidth , thumbHeight, false);

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
            x = 0 + x2;

            for (int i = 0; i < mBitmapList.size(); i++) {
                Bitmap bitmap = mBitmapList.get(i);

                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x, 0, null);
                    x = x + bitmap.getWidth();
                }
            }
            x3 = x;
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