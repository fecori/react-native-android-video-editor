package rahmatzulfikri.com.androidvideoedit;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;

import rahmatzulfikri.com.androidvideoedit.Util.VideoSurfaceView;

/**
 * Created by lembah8 on 4/18/17.
 */

public class VideoPlayerNew extends LinearLayout {
    public VideoSurfaceView videoSurfaceView;
    private String path = null;
    private boolean isAutoPlay = false;
    private boolean isLooping = false;
    private boolean isCrop = false;
    private int filter = 0;

    private boolean pathSet = false;
    private boolean autoPlaySet = false;
    private boolean loopingSet = false;
    private boolean cropSet = false;
    private boolean filterSet = false;

    private ProgressBar progressView;
    private ImageView buttonCrop;
    private ImageView buttonPlay;
    private ImageView buttonRePlay;
    private SeekBar seekBar;

    public boolean isPLaying = false;
    private int videoDuration = 0;
    private boolean isVideoComplete = false;


    public final VideoPlayerNew.MessageHandler mMessageHandler = new VideoPlayerNew.MessageHandler(this);
    private static final int SHOW_PROGRESS = 1;


    public VideoPlayerNew(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayerNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayerNew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(layoutWidth, layoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).layout(l, t, r, b);
        }
    }

    private void init(Context context){
        Log.e("DEBUG", "INIT");

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.video_player_new, null));

        progressView = (ProgressBar) findViewById(R.id.progressView);
        buttonCrop = (ImageView) findViewById(R.id.buttonCrop);
        buttonPlay = (ImageView) findViewById(R.id.buttonPlay);
        buttonRePlay = (ImageView) findViewById(R.id.buttonRePlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        setup();
    }

    private void setup(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pathSet && autoPlaySet && cropSet && filterSet){
                    Log.e("DEBUG","SETUP");
                    progressView.setVisibility(GONE);

                    if(isCrop){
                        buttonCrop.setImageResource(R.drawable.ic_crop_square);
                    }else{
                        buttonCrop.setImageResource(R.drawable.ic_crop_free);
                    }
                    buttonCrop.setVisibility(VISIBLE);

                    if(!isAutoPlay){
                        buttonPlay.setVisibility(VISIBLE);
                        isPLaying = false;
                    }else{
                        buttonPlay.setVisibility(INVISIBLE);
                        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                        isPLaying = true;

                    }

                    seekBar.setVisibility(VISIBLE);

                    videoSurfaceView = (VideoSurfaceView) findViewById(R.id.videoSurfaceView);
                    videoSurfaceView.setSource(path);
                    videoSurfaceView.setAutoplay(isAutoPlay);
                    videoSurfaceView.setSquareCenter(isCrop);
//                    videoSurfaceView.setLooping(isLooping);
                    videoSurfaceView.setShader(filter);

                    videoDuration = videoSurfaceView.getVideoDuration();
                    setupListener();
                }else{
                    setup();
                }
            }
        }, 1);
    }

    private void setupListener(){
        buttonCrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                videoSurfaceView.setSquareCenter(isCrop = !isCrop);
                if(isCrop){
                    buttonCrop.setImageResource(R.drawable.ic_crop_square);
                }else{
                    buttonCrop.setImageResource(R.drawable.ic_crop_free);
                }
            }
        });

        videoSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPLaying){
                    mMessageHandler.removeMessages(SHOW_PROGRESS);
                    videoSurfaceView.pauseVideo();
                    buttonPlay.setVisibility(VISIBLE);
                    isPLaying = !isPLaying;
                }else{
                    videoSurfaceView.playVideo();
                    mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                    buttonPlay.setVisibility(INVISIBLE);
                    buttonRePlay.setVisibility(INVISIBLE);
                    isPLaying = !isPLaying;
                    isVideoComplete = false;
                }
            }
        });

        buttonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPLaying){
                    mMessageHandler.removeMessages(SHOW_PROGRESS);
                    videoSurfaceView.pauseVideo();
                    buttonPlay.setVisibility(VISIBLE);
                    isPLaying = !isPLaying;
                }else{
                    videoSurfaceView.playVideo();
                    mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                    buttonPlay.setVisibility(INVISIBLE);
                    isPLaying = !isPLaying;
                    isVideoComplete = false;
                }
            }
        });

        buttonRePlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                videoSurfaceView.playVideo();
                mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                buttonRePlay.setVisibility(INVISIBLE);
                isPLaying = !isPLaying;
                isVideoComplete = false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    int position = Math.round((float)(i * videoDuration) / 100f);
                    videoSurfaceView.seekTo(position);
                    if(i == 100 && position >= videoDuration){
                        isVideoComplete = true;
                    }else{
                        isVideoComplete = false;
                        if(!isPLaying){
                            buttonPlay.setVisibility(VISIBLE);
                            buttonRePlay.setVisibility(INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                if(isPLaying){
//                    videoSurfaceView.pauseVideo();
//                    buttonPlay.setVisibility(VISIBLE);
//                    isPLaying = !isPLaying;
//                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int position = Math.round((float)(seekBar.getProgress() * videoDuration) / 100f);
//                videoSurfaceView.seekTo(position);
//                if(seekBar.getProgress() == 100){
//
//                }
            }
        });
    }

    public void setSource(String path){
        this.path = path;
        pathSet = true;
    }

    public void setAutoPlay(boolean autoPlay){
        isAutoPlay = autoPlay;
        autoPlaySet = true;
    }

//    public void setLooping(boolean looping){
//        isLooping = looping;
//        loopingSet = true;
//    }

    public void setCrop(boolean crop){
        isCrop = crop;
        cropSet = true;
    }

    public void setFilter(int filter){
        this.filter = filter;
        filterSet = true;
    }

    public void onResume(){
        if(videoSurfaceView != null){
            videoSurfaceView.onResume();
        }
    }

    public void onPause(){
        if(videoSurfaceView != null){
            videoSurfaceView.onPause();
        }
    }

    public void notifyProgressUpdate(boolean all){
        if(videoDuration == 0) return;

        if(all){
            int a = (int)Math.floor((float)(videoSurfaceView.getCurrentTime() * 100)/ (float)(videoDuration));
            if(a == 100 && videoSurfaceView.getCurrentTime() >= videoDuration){
                buttonRePlay.setVisibility(VISIBLE);
                isPLaying = !isPLaying;
                mMessageHandler.removeMessages(SHOW_PROGRESS);
                isVideoComplete = true;
            }
            seekBar.setProgress(a);
        }
    }


    public static class MessageHandler extends Handler {

        @NonNull
        private final WeakReference<VideoPlayerNew> mView;

        MessageHandler(VideoPlayerNew view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerNew view = mView.get();
            if (view == null || view.videoSurfaceView == null) {
                return;
            }

            view.notifyProgressUpdate(true);
            if (view.isPLaying) {
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    }
}
