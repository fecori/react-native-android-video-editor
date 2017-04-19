package rahmatzulfikri.com.androidvideoedit;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import rahmatzulfikri.com.androidvideoedit.Util.CustomRangeSeekBarView;
import rahmatzulfikri.com.androidvideoedit.Util.OnRangeSeekBarListener;
import rahmatzulfikri.com.androidvideoedit.Util.VideoTimeline;
import rahmatzulfikri.com.androidvideoedit.R;

/**
 * Created by lembah8 on 3/20/17.
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


    private VideoPlayerControl videoPlayerControl;

    public VideoTrim(Context context) {
        super(context);
        init(context);
    }

    public VideoTrim(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_trim, this, true);
        videoTimeline = (VideoTimeline) findViewById(R.id.videoTimeline);
        seekbar = (CustomRangeSeekBarView) findViewById(R.id.seekBar);

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
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
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
                                        if(Math.abs((move1 - move2) - move4) < Math.abs(videoTimeline.getCanvasWidth() - usedWidth)){
                                            move3 = move1 - move2;
                                            move5 = move5 + (move3 - move4);
                                            videoTimeline.setXPos(-Math.abs(move5));
                                            canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                                            videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                            videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
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
                                            if(Math.abs((move1 - move2) - move4) < Math.abs(videoTimeline.getCanvasWidth() - usedWidth)){
                                                move3 = move1 - move2;
                                                move5 = move5 + (move3 - move4);
                                                videoTimeline.setXPos(-Math.abs(move5));
                                                canvasSeekPos = (move5 * duration) / (videoTimeline.getCanvasWidth()  + Math.abs(videoTimeline.getCanvasWidthStart()));
                                                videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                                videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
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
                                                videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                                                videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
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
                    videoPlayerControl.setStartPos(rangeSeekPos + canvasSeekPos);
                }else{
                    rangeSeekEndPos = (int)(maxDuration * (value/100));
                    videoPlayerControl.setEndPos(rangeSeekEndPos+canvasSeekPos);
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
    public void setTimeline(String path){
        videoTimeline.setTrim(path);
    }

    /**
     * Function to set VideoPlayerControl
     * @param videoPlayerControl
     * videoPlayerControl = VideoPlayerControl class, used to handle video player
     */
    public void setPlayer(VideoPlayerControl videoPlayerControl){
        this.videoPlayerControl = videoPlayerControl;
        getVideoInfo();
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
    public void getVideoInfo(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoPlayerControl.isVideoPlayerControlReady()) {
                    duration = videoPlayerControl.getDuration();

                    if (duration / 60000 >= 10) {
                        maxDuration = 600000;
                        videoPlayerControl.setEndPos(maxDuration);
                    } else {
                        maxDuration = duration;
                        rangeSeekEndPos = maxDuration;
                        videoPlayerControl.setEndPos(maxDuration);
                    }

                    Log.e("DEBUG", +duration+" "+maxDuration +" "+rangeSeekEndPos);
                    seekbar.setRangeMin((minTrimDuration * 100f) / (float)maxDuration);
                } else {
                    getVideoInfo();
                }
            }
        }, 1000);
    }

}
