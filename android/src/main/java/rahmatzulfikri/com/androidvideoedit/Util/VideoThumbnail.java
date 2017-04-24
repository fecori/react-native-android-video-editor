package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import rahmatzulfikri.com.androidvideoedit.R;
import rahmatzulfikri.com.androidvideoedit.Util.VideoPlayer;
import rahmatzulfikri.com.androidvideoedit.Events.Events;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.bridge.LifecycleEventListener;

/**
 * Created by Rahmat Zulfikri on 3/20/17.
 */

public class VideoThumbnail extends RelativeLayout implements OnTouchListener{

    private static final String TAG = VideoThumbnail.class.getSimpleName();
    private VideoTimeline videoTimeline;
    private VideoPlayer videoPlayerSeek;
    private View overlay;

    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private int layoutPaddingLeft = 0;
    private int layoutPaddingRight = 0;
    private int usedWidth = 0;

    private int xpos = 0;
    private int seekDur = 0;

    private int startDur = 0;
    private int endDur = 0;
    private String path;
    private String uri;
    private int filter;

    private RCTEventEmitter eventEmitter;

    public VideoThumbnail(ThemedReactContext context) {
        super(context);
        init(context);
    }

    public VideoThumbnail(ThemedReactContext context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoThumbnail(ThemedReactContext context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(ThemedReactContext context){
        this.setOnTouchListener(this);
        eventEmitter = context.getJSModule(RCTEventEmitter.class);
        LayoutInflater.from(context).inflate(R.layout.video_thumbnail, this);
        videoTimeline = (VideoTimeline) findViewById(R.id.videoTimeline);
        videoPlayerSeek = (VideoPlayer) findViewById(R.id.videoPlayer);
        overlay = (View) findViewById(R.id.overlay);
        overlay.setBackgroundColor(Color.argb(125, 1, 1, 1));
        videoPlayerSeek.setSeekPlayer(true);
        setLayout();
    }

    public void setLayout(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (layoutHeight > 0) {
                    Log.e("DEBUG", "INI" + layoutHeight + " "+layoutWidth);
                    videoPlayerSeek.setLayoutParams(new LayoutParams(layoutHeight,layoutHeight));
                } else {
                    setLayout();
                }
            }
        }, 10);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        if(w != oldw){
            layoutWidth = w;
            usedWidth = w - getPaddingLeft() - getPaddingLeft();
        }
        if(h != oldh){
            layoutHeight = h;
        }

        layoutPaddingLeft = getPaddingLeft();
        layoutPaddingRight = getPaddingRight();

        super.onSizeChanged(w, h, oldw, oldh);
    }


    public void setXpos(int xpos){
        Log.e("DEBUG", "INI "+xpos);
        if(this.xpos != xpos){
            this.xpos = xpos;
        }
        if(startDur == 0 && endDur <= startDur){
            int duration = videoPlayerSeek.getDuration();
            if(this.xpos >= layoutHeight/2 && this.xpos <= usedWidth - layoutHeight/2) {
                seekDur = ((duration * (this.xpos - (layoutHeight/2))) / (usedWidth - layoutHeight));
                if(seekDur >= 0 && seekDur <= videoPlayerSeek.getDuration()){
                    videoPlayerSeek.setX(xpos - (layoutHeight/2));
                    videoPlayerSeek.seekTo(seekDur);
                    WritableMap event = Arguments.createMap();
                    event.putInt(Events.SEEK_POS, seekDur);
                    eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_SEEK_POS.toString(), event);
                    // videoPlayerView.seekTo(seekDur);
                }
            }
        }else{
            int duration = endDur - startDur;
            if(this.xpos >= layoutHeight/2 && this.xpos <= usedWidth - layoutHeight/2) {
                seekDur = Math.round((duration * (float)(this.xpos - (layoutHeight/2))) / (float)(usedWidth - layoutHeight));
                if(seekDur >= 0 && seekDur <= duration){
                    videoPlayerSeek.setX(xpos - (layoutHeight/2));
                    videoPlayerSeek.seekTo(seekDur + startDur);
                    WritableMap event = Arguments.createMap();
                    event.putInt(Events.SEEK_POS, seekDur);
                    eventEmitter.receiveEvent(getId(), EventsEnum.EVENT_GET_SEEK_POS.toString(), event);
                    // videoPlayerView.seekTo(seekDur + startDur);
                }
            }
        }

    }

    public void setStartDur(int startDur){
        this.startDur = startDur;
    }

    public void setEndDur(int endDur){
        this.endDur = endDur;
    }

    public void setVideoSeek(String path, int filter){
        this.path = path;
        this.filter = filter;
        setupVideoSeek();
    }

    public void setupVideoSeek(){
        videoPlayerSeek.setSource(path);
        videoPlayerSeek.setCrop(true);
        videoPlayerSeek.setFilter(filter);
    }

    public void setSource(String path){
        this.path = path;
        videoPlayerSeek.setSource(path);
        videoPlayerSeek.setCrop(true);

        videoTimeline.setThumbnail(path, startDur, endDur);
        videoPlayerSeek.seekTo(startDur);
    }

    public void setFilter(int filter){
        this.filter = filter;
        videoPlayerSeek.setFilter(filter);
    }

    public void onResume(){
        if(videoPlayerSeek.isAvailable()){
            videoPlayerSeek.resume();
        }
    }

    // public void setPlayer(VideoPlayer videoPlayer){
    //     this.videoPlayerView = videoPlayer;
    //     videoPlayerView.seekTo(startDur);
    // }

    public int getSeek(){
        return (seekDur + startDur);
    }

    public void onPause(){
        videoPlayerSeek.pause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("DEBUG", "INI "+event.getX());
                setXpos((int)event.getX());
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
