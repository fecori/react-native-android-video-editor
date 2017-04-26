package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.media.MediaMetadataRetriever;

import rahmatzulfikri.com.androidvideoedit.R;

import rahmatzulfikri.com.androidvideoedit.Events.Events;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

/**
 * Created by Rahmat Zulfikri on 3/20/17.
 */
public class VideoTrim extends RelativeLayout {

    private VideoTimeline videoTimeline;
    private CustomRangeSeekBarView seekbar;
    private int duration = 0;
    private int maxDuration = 0;
    private int startPosition = 0;
    private int endPosition = 100;
    private int layoutWidth = 0;
    private int layoutHeight = 0;
    private int usedWidth = 0;


    private int move1 = 0;
    private int move2 = 0;
    private int move3 = 0;
    private int move4 = 0;
    private int move5 = 0;
    private boolean move = false;
    private boolean isScrollRight = false;
    private boolean isScrollLeft = false;
    private int move6 = 0;

    private int rangeSeekPos = 0;
    private int canvasSeekPos = 0;
    private int rangeSeekEndPos =0;

    private int minTrimDuration = 0;
    private int maxTrimDuration = 0;

    private RCTEventEmitter eventEmitter;
    // private VideoPlayerControl videoPlayerControl;

    public VideoTrim(ThemedReactContext context) {
        super(context);
        init(context);
    }

    public VideoTrim(ThemedReactContext context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(ThemedReactContext context) {
        LayoutInflater.from(context).inflate(R.layout.video_trim, this, true);
        videoTimeline = (VideoTimeline) findViewById(R.id.videoTimeline);
        seekbar = (CustomRangeSeekBarView) findViewById(R.id.seekBar);

        eventEmitter = context.getJSModule(RCTEventEmitter.class);

        setupListener();
    }


    /**
     * Measuring Layout
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        layoutHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        usedWidth = layoutWidth - getPaddingLeft() - getPaddingRight();

        int seekWidth = Math.round((float)(layoutHeight * 70) / 200f);

        this.setMeasuredDimension(layoutWidth, layoutHeight);

        seekbar.init(layoutHeight, seekWidth);
        seekbar.measure(usedWidth, layoutHeight);
        usedWidth = usedWidth - seekWidth;

    }

    /**
     *  Function to setup Gesture listener
     */
    private void setupListener(){
        /*
            Listener to scrool video preview timeline when video timeline
            more than layout width
         */
        videoTimeline.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                switch (event.getAction()){
                    // Detect Touch Down
                    case MotionEvent.ACTION_DOWN:
                        move1 = (int)event.getX();
                        break;

                    // Detect when gesture move
                    case MotionEvent.ACTION_MOVE:

                        // Detect if video preview timeline posision equal than layout start
                        if(videoTimeline.getCanvasWidthStart() == 0){
                            // Detect when scroll to left
                            if((int)event.getX() <= move1){
                                final Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isScrollRight && !isScrollLeft){
                                            move4 = 0;
                                        }
                                        move2 = (int)event.getX();
                                        if(Math.abs((move1 - move2) - move4) < (videoTimeline.getCanvasWidth() - usedWidth)){
                                            move3 = move1 - move2;
                                            move5 = move5 + (move3 - move4);
                                            videoTimeline.setXPos(-Math.abs(move5));
                                            canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                                            // videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                            // videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
                                            WritableMap event = Arguments.createMap();
                                            event.putInt(Events.START_POS, rangeSeekPos + canvasSeekPos);

                                            WritableMap event2 = Arguments.createMap();
                                            event2.putInt(Events.END_POS, rangeSeekEndPos+canvasSeekPos);

                                            eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_START_POS.toString(), event);
                                            eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event2);
                                            move4 = move3;
                                            isScrollRight = false;
                                            isScrollLeft = true;
                                        }
                                    }
                                });
                                thread.run();
                            }

                        }else
                            // Detect if video preview timeline posision less than layout start
                            if(videoTimeline.getCanvasWidthStart() < 0){
                                // Detect when scroll to left
                                if((int)event.getX() <= move1){
                                    final Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(isScrollRight && !isScrollLeft){
                                                move4 = 0;
                                            }
                                            move2 = (int)event.getX();
                                            if(Math.abs((move1 - move2) - move4) < (videoTimeline.getCanvasWidth() - usedWidth)){
                                                move3 = move1 - move2;
                                                move5 = move5 + (move3 - move4);
                                                videoTimeline.setXPos(-Math.abs(move5));
                                                canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                                                // videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                                // videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
                                                WritableMap event = Arguments.createMap();
                                                event.putInt(Events.START_POS, rangeSeekPos + canvasSeekPos);

                                                WritableMap event2 = Arguments.createMap();
                                                event2.putInt(Events.END_POS, rangeSeekEndPos+canvasSeekPos);

                                                eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_START_POS.toString(), event);
                                                eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event2);
                                                move4 = move3;
                                                isScrollRight = false;
                                                isScrollLeft = true;
                                            }
                                        }
                                    });
                                    thread.run();
                                }else
                                    // Detect when scroll to right
                                    if((int)event.getX() > move1){
                                    final Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!isScrollRight && isScrollLeft){
                                                move4 = 0;
                                            }
                                            move2 = (int) event.getX();
                                            if (Math.abs((move5 - ((move2 - move1) - move4))) < Math.abs(videoTimeline.getCanvasWidthStart())) {
                                                move3 = move2 - move1;
                                                move5 = move5 - (move3 - move4);
                                                videoTimeline.setXPos(-Math.abs(move5));
                                                canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                                                // videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                                // videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
                                                WritableMap event = Arguments.createMap();
                                                event.putInt(Events.START_POS, rangeSeekPos + canvasSeekPos);

                                                WritableMap event2 = Arguments.createMap();
                                                event2.putInt(Events.END_POS, rangeSeekEndPos+canvasSeekPos);

                                                eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_START_POS.toString(), event);
                                                eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event2);
                                                move4 = move3;
                                                isScrollRight = true;
                                                isScrollLeft = false;
                                            }
                                        }
                                    });
                                    thread.start();
                                    }

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        move4 = 0;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        /*
             Listener for seekbar control (Seekbar button left and right)
             index (0) = button seek left
             index (1) = button seek right
             value = value of seekButton (0 - 100)
         */
        seekbar.addOnRangeSeekBarListener(new OnRangeSeekBarListener() {
            @Override
            public void onCreate(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
            }

            @Override
            public void onSeek(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
                if(index == 0){
                    rangeSeekPos = (int)((value/100) * maxDuration);
                    canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                    // videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                    WritableMap event = Arguments.createMap();
                    event.putInt(Events.START_POS, rangeSeekPos + canvasSeekPos);

                    eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_START_POS.toString(), event);
                }else{
                    rangeSeekEndPos = (int)(maxDuration * (value/100));
                    // videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
                    WritableMap event = Arguments.createMap();
                    event.putInt(Events.END_POS, rangeSeekEndPos+canvasSeekPos);

                    eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event);
                }
            }

            @Override
            public void onSeekStart(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
            }

            @Override
            public void onSeekStop(CustomRangeSeekBarView customRangeSeekBarView, int index, float value) {
            }
        });
    }

    /**
     * Function to set Video Path to create Video Preview Timeline
     * @param path
     * Path = File path
     */
    public void setSource(String path){
        videoTimeline.setTrim(path);
        getVideoInfo(path);
    }

    /**
     *
     * @param minTrimDuration
     * minTrimDuration in miliSecond (1000 = 1second), minTrimDuration should less than maxTrimDuration
     */
    public void setMinTrimDuration(int minTrimDuration){
        this.minTrimDuration = minTrimDuration;
    }


//    public void setMaxTrimDuration(int maxTrimDuration){
//        this.maxTrimDuration = maxTrimDuration;
//    }

    /**
     * Function to get Video Detail, used to get video Duration and Set Video Player Max Duration
     */
     private void getVideoInfo(String source){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(source);
        duration = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        if (duration / 60000 >= 10) {
            maxDuration = 600000;
            // videoPlayerControl.setEndPos(maxDuration);
            WritableMap event = Arguments.createMap();
            event.putInt(Events.END_POS, maxDuration);

            eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event);
        } else {
            maxDuration = duration;
            rangeSeekEndPos = maxDuration;
            // videoPlayerControl.setEndPos(maxDuration);
            WritableMap event = Arguments.createMap();
            event.putInt(Events.END_POS, maxDuration);

            eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_END_POS.toString(), event);
        }

//         Log.e("DEBUG", " "+duration+" "+maxDuration+" "+((minTrimDuration * 100f) / (float)maxDuration));
        seekbar.setRangeMin((minTrimDuration * 100f) / (float)maxDuration);
        mediaMetadataRetriever.release();
     }
}
