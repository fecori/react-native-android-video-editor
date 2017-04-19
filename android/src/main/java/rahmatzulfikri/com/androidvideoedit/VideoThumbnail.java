package rahmatzulfikri.com.androidvideoedit;

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

import rahmatzulfikri.com.androidvideoedit.Util.VideoTimeline;
import rahmatzulfikri.com.androidvideoedit.R;

/**
 * Created by lembah8 on 3/20/17.
 */

public class VideoThumbnail extends RelativeLayout implements OnTouchListener, TextureView.SurfaceTextureListener{

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

    private Context context;

    private VideoPlayer videoPlayerView;

    public VideoThumbnail(Context context) {
        super(context);
        this.context = context;
        this.setOnTouchListener(this);
        init(context);
    }

    public VideoThumbnail(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOnTouchListener(this);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.video_thumbnail, this);
        videoTimeline = (VideoTimeline) findViewById(R.id.videoTimeline);
        videoPlayerSeek = (VideoPlayer) findViewById(R.id.videoPlayer);
        overlay = (View) findViewById(R.id.overlay);

        overlay.setBackgroundColor(Color.argb(125, 1, 1, 1));

        Log.e("DEBUG", "MASUK INIT "+layoutHeight);
        setLayout();
    }

    public void setLayout(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (layoutHeight > 0) {
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
                    videoPlayerView.seekTo(seekDur);
                }
            }
        }else{
            int duration = endDur - startDur;
            if(this.xpos >= layoutHeight/2 && this.xpos <= usedWidth - layoutHeight/2) {
                seekDur = Math.round((duration * (float)(this.xpos - (layoutHeight/2))) / (float)(usedWidth - layoutHeight));
                if(seekDur >= 0 && seekDur <= duration){
                    videoPlayerSeek.setX(xpos - (layoutHeight/2));
                    videoPlayerSeek.seekTo(seekDur + startDur);
                    videoPlayerView.seekTo(seekDur + startDur);
                }
            }
        }

    }

    public void setTimeline(String uri, int startDur, int endDur){
        this.uri = uri;
        this.startDur = startDur;
        this.endDur = endDur;
        setupTimeline();
    }

    private void setupTimeline(){
        videoTimeline.setThumbnail(uri, startDur, endDur);
        videoPlayerSeek.seekTo(startDur);
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


    public void onResume(){
        if(videoPlayerSeek.isAvailable()){
            videoPlayerSeek.resume();
        }
    }

    public void setPlayer(VideoPlayer videoPlayer){
        this.videoPlayerView = videoPlayer;
        videoPlayerView.seekTo(startDur);
    }

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
                setXpos((int)event.getX() - (layoutHeight/2));
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
