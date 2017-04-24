package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

import rahmatzulfikri.com.androidvideoedit.Effect.AutoFixEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.BlackAndWhiteEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.BrightnessEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.ContrastEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.CrossProcessEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.DocumentaryEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.DuotoneEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.FillLightEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.GammaEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.GrainEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.GreyScaleEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.HueEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.InvertColorsEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.LamoishEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.NoEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.PosterizeEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.SaturationEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.SepiaEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.SharpnessEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.TemperatureEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.TintEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.VignetteEffect;
import rahmatzulfikri.com.androidvideoedit.Util.VideoTextureRenderer;

/**
 * Created by Rahmat Zulfikri on 3/21/17.
 */

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener{

    private static final String TAG = VideoThumbnail.class.getSimpleName();
    public MediaPlayer mMediaPlayer;
    public VideoTextureRenderer mRenderer;
    private int surfaceWidth;
    private int surfaceHeight;
    private Context mContext;
    private String videoPath;
    private String fragmentShaderCode = null;

    private boolean isCrop = false;
    private boolean isAutoPlay = false;
    private boolean isLooping = false;
    private boolean mediaPlayerReady = false;
    private boolean isTextureAvailable = false;
    private boolean isSeekPlayer = false;

    private SurfaceTexture surfaceTexture;
    private SurfaceTexture surface = null;
    private int xpos;

    private int seekto = 0;

    private boolean isPaused = false;


    public VideoPlayer(Context context) {
        super(context);
        mContext = context;
        this.setSurfaceTextureListener(this);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surfaceHeight = MeasureSpec.getSize(heightMeasureSpec);
        surfaceWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(isSeekPlayer){
            this.setMeasuredDimension(surfaceHeight, surfaceHeight);
        }else{
            this.setMeasuredDimension(surfaceWidth, surfaceWidth);
        }
    }

    private void getSurface(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (surface != null) {
                    Log.e("DEBUG", "MASUK SINI");
                    try {
                        mMediaPlayer.setDataSource(videoPath);
                        mRenderer.setMediaPlayer(mMediaPlayer);
                        mMediaPlayer.setScreenOnWhilePlaying(true);
                        mMediaPlayer.setSurface(new Surface(surface));
                        mMediaPlayer.prepare();
                        Log.e("DEBUG", surfaceHeight + " " + surfaceWidth +" "+mMediaPlayer.getVideoWidth()+" "+ mMediaPlayer.getVideoHeight());
                        mRenderer.setDimension(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                        mRenderer.setShader(fragmentShaderCode);
                        mRenderer.setCrop(isCrop);

                        if(!mMediaPlayer.isLooping()){
                            mMediaPlayer.setLooping(isLooping);
                        }

                        if(isAutoPlay){
                            mMediaPlayer.start();
                        }else{
                            mMediaPlayer.seekTo(seekto);
                        }

                        mediaPlayerReady = true;

                    } catch (IOException e) {
                        Log.e("DEBUG", e.toString());
                        throw new RuntimeException("Could not open input video!");
                    }
                } else {
                    Log.e("DEBUG", "MASUK SANA");
                    surface =  mRenderer.getVideoTexture();
                    getSurface();
                }
            }
        }, 1000);
    }

    private void setup(){
        getSurface();
    }

    public void setSource(String src){
        videoPath = src;
    }

    public void setSeekPlayer(boolean isSeekPlayer){
        this.isSeekPlayer = isSeekPlayer;
    }

    public void setFilter(int filterCode){
        switch (filterCode){
            case 0:
                fragmentShaderCode = new NoEffect().getVideoShader();
                break;
            case 1:
                fragmentShaderCode = new BlackAndWhiteEffect().getVideoShader();
                break;
            case 2:
                fragmentShaderCode = new BrightnessEffect(0.5f).getVideoShader();
                break;
            case 3:
                fragmentShaderCode = new ContrastEffect(0.5f).getVideoShader();
                break;
            case 4:
                fragmentShaderCode = new CrossProcessEffect().getVideoShader();
                break;
            case 5:
                if(isCrop){
                    fragmentShaderCode = new DocumentaryEffect(surfaceWidth, surfaceHeight).getVideoShader();
                }else{
                    fragmentShaderCode = new DocumentaryEffect(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight()).getVideoShader();
                }
                break;
            case 6:
                fragmentShaderCode = new DuotoneEffect(Color.CYAN, Color.BLUE).getVideoShader();
                break;
            case 7:
                fragmentShaderCode = new FillLightEffect(0.5f).getVideoShader();
                break;
            case 8:
                fragmentShaderCode = new GammaEffect(1.0f).getVideoShader();
                break;
            case 9:
                fragmentShaderCode = new GreyScaleEffect().getVideoShader();
                break;
            case 10:
                fragmentShaderCode = new HueEffect(90f).getVideoShader();
                break;
            case 11:
                fragmentShaderCode = new InvertColorsEffect().getVideoShader();
                break;
            case 12:
                if(isCrop){
                    fragmentShaderCode = new LamoishEffect(surfaceWidth, surfaceHeight).getVideoShader();
                }else{
                    fragmentShaderCode = new LamoishEffect(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight()).getVideoShader();
                }
                break;
            case 13:
                fragmentShaderCode = new PosterizeEffect().getVideoShader();
                break;
            case 14:
                fragmentShaderCode = new SaturationEffect(0.1f).getVideoShader();
                break;
            case 15:
                fragmentShaderCode = new SepiaEffect().getVideoShader();
                break;
            case 16:
                if(isCrop){
                    fragmentShaderCode = new SharpnessEffect(0.5f, surfaceWidth, surfaceHeight).getVideoShader();
                }else{
                    fragmentShaderCode = new SharpnessEffect(0.5f,mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight()).getVideoShader();
                }
                break;
            case 17:
                fragmentShaderCode = new TemperatureEffect(0.5f).getVideoShader();
                break;
            case 18:
                fragmentShaderCode = new TintEffect(Color.BLUE).getVideoShader();
                break;
            case 19:
                if(isCrop){
                    fragmentShaderCode = new VignetteEffect(0.5f, surfaceWidth, surfaceWidth).getVideoShader();
                }else{
                    fragmentShaderCode = new VignetteEffect(0.5f, mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight()).getVideoShader();
                }
                break;
            default:
                fragmentShaderCode = new NoEffect().getVideoShader();
                break;
        }

        if(mediaPlayerReady){
            mMediaPlayer.start();
            mRenderer.setShader(fragmentShaderCode);
        }
    }

    public void setCrop(boolean isCrop){
        this.isCrop = isCrop;
        if(mediaPlayerReady){
            if(!mMediaPlayer.isPlaying()){
                mMediaPlayer.start();
            }
            mRenderer.setCrop(this.isCrop);
        }
    }

    public void togglePlayPause(){
        if(mediaPlayerReady){
            if(!mMediaPlayer.isPlaying()){
                mMediaPlayer.start();
            }else{
                mMediaPlayer.pause();
            }
        }
    }

    public boolean isPlaying(){
        if(mediaPlayerReady){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void setPaused(){
        if(mediaPlayerReady){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
        }
    }

    public void setStop(){
        if(mediaPlayerReady){
            mMediaPlayer.stop();
        }
    }

    public void setStart(){
        if(mediaPlayerReady){
            if(!mMediaPlayer.isPlaying()){
                mMediaPlayer.start();
            }
        }
    }

    public void seekTo(int time){
        if(mediaPlayerReady){
            mMediaPlayer.seekTo(time);
        }else{
            seekto = time;
        }
    }

    public boolean isPlayerReady(){
        return mediaPlayerReady;
    }

    public int getCurrentPos(){
        if(mediaPlayerReady){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    public void setAutoPlay(boolean isAutoPlay){
        this.isAutoPlay = isAutoPlay;
    }

    public void setLooping(boolean isLooping){
        this.isLooping = isLooping;
    }

    public void resume(){
        setup();
    }

    public void pause(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if (mRenderer != null)
            mRenderer.onPause();
        mediaPlayerReady = false;
        isPaused = true;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("DEBUG", "surface avalilable");

        isTextureAvailable = true;
        surfaceTexture = surface;
        surfaceWidth = width;
        surfaceHeight = height;

        mMediaPlayer = new MediaPlayer();
        mRenderer = new VideoTextureRenderer(mContext, surface, surfaceWidth, surfaceWidth );

        setup();
//        setupListener();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.e("DEBUG", "surface change");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        Log.e("DEBUG", "surface destroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.e("DEBUG", "surface updated");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}
