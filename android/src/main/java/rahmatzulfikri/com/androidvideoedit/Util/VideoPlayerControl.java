package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;

import rahmatzulfikri.com.androidvideoedit.R;

/**
 * Created by lembah8 on 4/3/17.
 */

public class VideoPlayerControl extends RelativeLayout {

    private ImageView buttonPlay;
    private ImageView buttonCrop;
    private ImageView buttonRePlay;
    private ProgressBar progressView;
    private SeekBar seekBar;
    private boolean isPlay = false;
    private boolean isCrop = false;
    private boolean completed = false;

    private VideoPlayer videoPlayer;

    private static final int SHOW_PROGRESS = 1;
    public final VideoPlayerControl.MessageHandler mMessageHandler = new VideoPlayerControl.MessageHandler(this);

    private int maxDuration = 0;
    private int startPos = 0;
    private int endPos = 0;

    private boolean isShowCrop = true;

    public VideoPlayerControl(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerControl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.video_player_control, this, true);
        buttonPlay = (ImageView) findViewById(R.id.buttonPlay);
        buttonCrop = (ImageView) findViewById(R.id.buttonCrop);
        buttonRePlay = (ImageView) findViewById(R.id.buttonRePlay);
        progressView = (ProgressBar) findViewById(R.id.progressView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void setup(final VideoPlayer videoPlayer){
        this.videoPlayer = videoPlayer;
        setupView(videoPlayer);

        buttonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoPlayer.isPlayerReady()){
                    if(videoPlayer.isPlaying()){
                        videoPlayer.setPaused();
                        mMessageHandler.removeMessages(SHOW_PROGRESS);
                        buttonPlay.setVisibility(VISIBLE);
                        if(completed){
                            videoPlayer.seekTo(startPos);
                            buttonRePlay.setVisibility(GONE);
                        }else{
                            buttonRePlay.setVisibility(GONE);
                        }
                        completed = false;
                    }else{
                        videoPlayer.setStart();
                        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                        buttonPlay.setVisibility(GONE);
                        if(completed){
                            videoPlayer.seekTo(startPos);
                            buttonRePlay.setVisibility(GONE);
                        }else{
                            buttonRePlay.setVisibility(GONE);
                        }
                        completed = false;
                    }
                }
            }
        });

        videoPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoPlayer.isPlayerReady()){
                    if(videoPlayer.isPlaying()){
                        mMessageHandler.removeMessages(SHOW_PROGRESS);
                        buttonPlay.setVisibility(VISIBLE);
                        if(completed){
                            videoPlayer.seekTo(startPos);
                            buttonRePlay.setVisibility(GONE);
                        }else{
                            buttonRePlay.setVisibility(GONE);
                        }
                        completed = false;
                    }else{
                        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                        buttonPlay.setVisibility(GONE);
                        if(completed){
                            videoPlayer.seekTo(startPos);
                            buttonRePlay.setVisibility(GONE);
                        }else{
                            buttonRePlay.setVisibility(GONE);
                        }
                        completed = false;
                    }
                    videoPlayer.togglePlayPause();
                }
            }
        });

        buttonCrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoPlayer.isPlayerReady() && !completed){
                    isCrop = !isCrop;
                    videoPlayer.setCrop(isCrop);
                    if(isCrop){
                        buttonCrop.setImageResource(R.drawable.ic_crop_free);
                    }else{
                        buttonCrop.setImageResource(R.drawable.ic_crop_square);
                    }

                    mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
                    buttonPlay.setVisibility(GONE);
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(maxDuration > 0){
                    int position = Math.round((float)(i * maxDuration) / 100f);

                    if(b){
                        videoPlayer.seekTo(position + startPos);

                        if(position >= endPos || i == 100){
                            videoPlayer.setPaused();
                            buttonPlay.setVisibility(GONE);
                            buttonRePlay.setVisibility(VISIBLE);
                            completed = true;
                        }else{
                            videoPlayer.setPaused();
                            buttonPlay.setVisibility(VISIBLE);
                            buttonRePlay.setVisibility(GONE);
                            completed = false;
                        }
                    }else{
                        if(position >= endPos || i == 100){
                            videoPlayer.setPaused();
                            buttonPlay.setVisibility(GONE);
                            buttonRePlay.setVisibility(VISIBLE);
                            completed = true;
                        }else{
                            completed = false;
                        }
                    }
                }else{
                    int position = Math.round((float)(i * videoPlayer.getDuration()) / 100f);

                    if(b){
                        videoPlayer.seekTo(position);
                        if(position >= videoPlayer.getDuration() || i == 100){
                            videoPlayer.setPaused();
                            buttonPlay.setVisibility(GONE);
                            buttonRePlay.setVisibility(VISIBLE);
                            completed = true;
                        }else{
                            videoPlayer.setPaused();
                            buttonPlay.setVisibility(VISIBLE);
                            buttonRePlay.setVisibility(GONE);
                            completed = false;
                        }
                    }else{
                        if(position >= videoPlayer.getDuration() || i == 100){
                            videoPlayer.togglePlayPause();
                            buttonPlay.setVisibility(GONE);
                            buttonRePlay.setVisibility(VISIBLE);
                            completed = true;
                        }else{
                            completed = false;
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMessageHandler.sendEmptyMessage(SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(maxDuration > 0){
                    int position = Math.round(((float)(seekBar.getProgress() * maxDuration) / 100f));
                    videoPlayer.seekTo(position + startPos);
                }else{
                    int position = Math.round(((float)(seekBar.getProgress() * videoPlayer.getDuration()) / 100f));
                    videoPlayer.seekTo(position);
                }
            }
        });
    }

    private void setupView(final VideoPlayer videoPlayer){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoPlayer.isPlayerReady()) {
                    progressView.setVisibility(GONE);
                    progressView.setVisibility(View.GONE);
                    buttonPlay.setVisibility(VISIBLE);
                    if(isShowCrop){
                        buttonCrop.setVisibility(VISIBLE);
                    }else{
                        buttonCrop.setVisibility(GONE);
                    }
                    seekBar.setVisibility(VISIBLE);
                } else {
                    setupView(videoPlayer);
                }
            }
        }, 1000);
    }

    public void notifyProgressUpdate(boolean all, boolean completed) {
        if (videoPlayer.getDuration() == 0 ) return;

        if(completed){
            if(all){
                seekBar.setProgress(100);
            }
        }else {
            if(maxDuration > 0){
                if(all){
                    seekBar.setProgress(Math.round(((float)((videoPlayer.getCurrentPos() - startPos) * 100) / (float)maxDuration)));
                }
            }else{
                if(all){
                    seekBar.setProgress(Math.round(((float)(videoPlayer.getCurrentPos() * 100) / (float)videoPlayer.getDuration())));
                }
            }
        }
    }

    public int getDuration(){
        if (videoPlayer.isPlayerReady()) {
            return videoPlayer.getDuration();
        }
        return 0;
    }

    public void setPaused(){
        if (videoPlayer.isPlayerReady()) {
            if(videoPlayer.isPlaying()){
                videoPlayer.setPaused();
            }
        }
    }

    public void setFilter(int filter){
        if(videoPlayer.isPlayerReady()){
            if(videoPlayer.isPlaying()){
                videoPlayer.setFilter(filter);
            }else{
                buttonPlay.setVisibility(GONE);
                videoPlayer.setFilter(filter);
            }
        }
    }

    public boolean isVideoPlayerControlReady(){
        return videoPlayer.isPlayerReady();
    }

    public void setEndPos(int endPos){
        this.endPos = endPos;
        this.maxDuration = endPos - startPos;
    }

    public void setStartPos(int startPos){
        this.startPos = startPos;
        this.maxDuration = endPos - startPos;
        if (videoPlayer.isPlayerReady()) {
            if(videoPlayer.isPlaying()){
                videoPlayer.setPaused();
                buttonPlay.setVisibility(VISIBLE);
            }
            videoPlayer.seekTo(startPos);
        }
    }

    public int getStartPos(){
        return startPos;
    }

    public int getEndPos(){
        return endPos;
    }

    public int getMaxDuration(){
        return maxDuration;
    }

    public boolean getCrop(){
        return isCrop;
    }

    public void showCropButton(boolean show){
        isShowCrop = show;
    }

    public void setCrop(boolean isCrop){
        this.isCrop = isCrop;
    }
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public static class MessageHandler extends Handler {

        @NonNull
        private final WeakReference<VideoPlayerControl> mView;

        MessageHandler(VideoPlayerControl view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerControl view = mView.get();
            if (view == null || view.videoPlayer == null) {
                return;
            }

            view.notifyProgressUpdate(true, false);
            if (view.videoPlayer.isPlaying()) {
                sendEmptyMessageDelayed(0, 10);
            }else{
                Log.e("DEBUG", "COMPLETE");
            }
        }
    }
}
