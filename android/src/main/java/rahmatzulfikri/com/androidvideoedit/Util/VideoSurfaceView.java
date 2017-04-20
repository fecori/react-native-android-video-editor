package rahmatzulfikri.com.androidvideoedit.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rahmatzulfikri.com.androidvideoedit.VideoEffect.BlackAndWhiteEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.BrightnessEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.ContrastEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.CrossProcessEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.DocumentaryEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.DuotoneEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.FillLightEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.GammaEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.GreyScaleEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.HueEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.InvertColorsEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.LamoishEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.NoEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.PosterizeEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.SaturationEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.SepiaEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.SharpnessEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.TemperatureEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.TintEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.VignetteEffect;
import rahmatzulfikri.com.androidvideoedit.VideoEffect.interfaces.ShaderInterface;

import com.facebook.react.uimanager.ThemedReactContext;

/**
 * This GLSurfaceView can be used to display video that is being played by media
 * player and at the same time different effect can be applied on the video.
 * This view uses shader for applying different effects.§
 *
 * @author sheraz.khilji
 */
@SuppressLint("ViewConstructor")
public class VideoSurfaceView extends GLSurfaceView {
    private static final String TAG = "VideoSurfaceView";
    private VideoSurfaceView.VideoRender mRenderer;
    private MediaPlayer mMediaPlayer = null;
    private static VideoSurfaceView mSurfaceView;
    private ThemedReactContext mContext;
    private static ShaderInterface effect;
    private static boolean HORIZONTAL_VIDEO = true;
    private static boolean VERTICAL_VIDEO = false;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private int layoutWidth = 0;
    private int layoutHeight = 0;
    private int videoDuration = 0;
    private boolean isSquareCenter = false;
    private int rotation = 0;
    private boolean isPaused = false;
    private boolean onPaused = false;
    private boolean onMeasured = false;
    private int selectedFilter = 0;

    public VideoSurfaceView(ThemedReactContext context) {
        super(context);
        mContext = context;
        setEGLContextClientVersion(2);
        mRenderer = new VideoSurfaceView.VideoRender(mContext);
        setRenderer(mRenderer);
        mSurfaceView = this;
        mMediaPlayer = new MediaPlayer();
        effect = new NoEffect();
        mRenderer.setMediaPlayer(mMediaPlayer);

//        queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mRenderer.setMediaPlayer(mMediaPlayer);
//
//            }
//        });
    }

    public VideoSurfaceView(ThemedReactContext context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2);
        mRenderer = new VideoSurfaceView.VideoRender(mContext);
        setRenderer(mRenderer);
        mSurfaceView = this;
        mMediaPlayer = new MediaPlayer();
        effect = new NoEffect();
        mRenderer.setMediaPlayer(mMediaPlayer);

//        queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mRenderer.setMediaPlayer(mMediaPlayer);
//            }
//        });
    }

    public void setDimension() {
//        Log.e("DEBUG", ""+videoHeight+" "+videoWidth+" "+layoutHeight+" "+layoutWidth+" "+rotation);

        if(isSquareCenter) {
            if (rotation == 90 || rotation == 360) {
                mRenderer.setDimension(videoWidth, videoHeight, layoutHeight, layoutWidth, VERTICAL_VIDEO);
            }

            if (rotation == 0 || rotation == 180) {
                mRenderer.setDimension(videoHeight, videoWidth, layoutHeight,  layoutWidth, HORIZONTAL_VIDEO);
            }
        }else{
            if (rotation == 90 || rotation == 360) {
                int lHeight = MeasureSpec.getSize(layoutHeight);
                int lWidth = (lHeight * videoHeight) / videoWidth;
                mRenderer.setDimension(lHeight, lWidth, layoutHeight, layoutWidth, VERTICAL_VIDEO);
            }
            if (rotation == 0 || rotation == 180) {
                int lWidth = MeasureSpec.getSize(layoutWidth);
                int lHeight = (lWidth * videoHeight) / videoWidth;
                mRenderer.setDimension(lHeight, lWidth, layoutHeight,  layoutWidth, HORIZONTAL_VIDEO);
            }
        }

        mRenderer.setSquareCenter(isSquareCenter);
    }

    public void setSquareCenter(boolean isSquareCenter){
        this.isSquareCenter = isSquareCenter;

        if(onMeasured) {
            mRenderer.setSquareCenter(isSquareCenter);
            if(isSquareCenter) {
                if (rotation == 90 || rotation == 360) {
                    mRenderer.setDimension(videoWidth, videoHeight, layoutHeight, layoutWidth, VERTICAL_VIDEO);
                }

                if (rotation == 0 || rotation == 180) {
                    mRenderer.setDimension(videoHeight, videoWidth, layoutHeight,  layoutWidth, HORIZONTAL_VIDEO);
                }
            }else{
                if (rotation == 90 || rotation == 360) {
                    int lHeight = layoutHeight;
                    int lWidth = (lHeight * videoHeight) / videoWidth;
                    mRenderer.setDimension(lHeight, lWidth, layoutHeight, layoutWidth, VERTICAL_VIDEO);
                }
                if (rotation == 0 || rotation == 180) {
                    int lWidth = layoutWidth;
                    int lHeight = (lWidth * videoHeight) / videoWidth;
                    mRenderer.setDimension(lHeight, lWidth, layoutHeight,  layoutWidth, HORIZONTAL_VIDEO);
                }
            }
        }

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(videoWidth > 0 && videoHeight > 0){
            setDimension();
        }
        this.setMeasuredDimension(layoutWidth, layoutWidth);
        onMeasured = true;
    }

    public void setSource(String uri){
        try {
            mMediaPlayer.setDataSource(uri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            if(videoWidth > 0 && videoHeight > 0){
                setDimension();
            }
            videoDuration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setShader(int shaderEffect){
        switch (shaderEffect){
            case 0:
                effect = new NoEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 0;
                break;
            case 1:
                effect = new BlackAndWhiteEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 1;
                break;
            case 2:
                effect = new BrightnessEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 2;
                break;
            case 3:
                effect = new ContrastEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 3;
                break;
            case 4:
                effect = new CrossProcessEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 4;
                break;
            case 5:
                effect = new DocumentaryEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 5;
                break;
            case 6:
                effect = new DuotoneEffect(Color.CYAN, Color.BLUE);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 6;
                break;
            case 7:
                effect = new FillLightEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 7;
                break;
            case 8:
                effect = new GammaEffect(1.0f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 8;
                break;
            case 9:
                effect = new GreyScaleEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 9;
                break;
            case 10:
                effect = new HueEffect(90f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 10;
                break;
            case 11:
                effect = new InvertColorsEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 11;
                break;
            case 12:
                effect = new LamoishEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 12;
                break;
            case 13:
                effect = new PosterizeEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 13;
                break;
            case 14:
                effect = new SaturationEffect(0.1f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 14;
                break;
            case 15:
                effect = new SepiaEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 15;
                break;
            case 16:
                effect = new SharpnessEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 16;
                break;
            case 17:
                effect = new TemperatureEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 17;
                break;
            case 18:
                effect = new TintEffect(Color.BLUE);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 18;
                break;
            case 19:
                effect = new VignetteEffect(0.5f);
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 19;
                break;
            default:
                effect = new NoEffect();
                if(selectedFilter != shaderEffect){
                    mRenderer.updateFilter(true);
                }
                selectedFilter = 0;
                break;
        }
    }

    public void setLooping(boolean looping){
        if(looping && !mMediaPlayer.isLooping())
        {
            mMediaPlayer.setLooping(true);
        }
    }

    public void setAutoplay(boolean isAutoPlay){
        mRenderer.setAutoPlay(isAutoPlay);
    }

    public void pauseVideo(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    public void playVideo(){
        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    public void paused(boolean isPaused){
        if(isPaused && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
        if(!isPaused && !mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    public void seekTo(int seek){
        mMediaPlayer.seekTo(seek);
//        mRenderer.seekTo(seek);
    }

    public int getVideoDuration(){
        return videoDuration;
    }

    public int getVideoWidth(){
        return videoWidth;
    }

    public int getVideoHeight(){
        return videoHeight;
    }

    public int getCurrentTime(){
        if(mMediaPlayer != null){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onPaused){
            Log.e("DEBUG", "MASUK RESUME");
            isPaused = false;
            mRenderer.setMediaPlayer(mMediaPlayer);
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    mRenderer.setMediaPlayer(mMediaPlayer);
//                }
//            });
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("DEBUG","MASUK PAUSE");
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
//        mMediaPlayer.setOnErrorListener(null);
//        mMediaPlayer.setSurface(null);
        mMediaPlayer.stop();
        onPaused = true;
    }



    private static class VideoRender implements Renderer,
            SurfaceTexture.OnFrameAvailableListener {
        private static String TAG = "VideoRender";

        private static final int FLOAT_SIZE_BYTES = 4;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
        private final float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -1.0f, -1.0f, 0, 0.f, 0.f, 1.0f, -1.0f, 0, 1.f, 0.f, -1.0f,
                1.0f, 0, 0.f, 1.f, 1.0f, 1.0f, 0, 1.f, 1.f,};

        private FloatBuffer mTriangleVertices;

        private final String mVertexShader = "uniform mat4 uMVPMatrix;\n"
                + "uniform mat4 uSTMatrix;\n"
                + "attribute vec4 aPosition;\n"
                + "attribute vec4 aTextureCoord;\n"
                + "varying vec2 vTextureCoord;\n"
                + "void main() {\n"
                + "  gl_Position = uMVPMatrix * aPosition;\n"
                + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n"
                + "}\n";
        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];

        private int mProgram;
        private int mTextureID[] = new int[2];
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;

        private SurfaceTexture mSurface;
        private boolean updateSurface = false;
        private boolean updateFilter = false;

        private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

        private MediaPlayer mMediaPlayer;

        private int videoHeight;
        private int videoWidth;
        private int layoutHeight;
        private int layoutWidth;
        private boolean isHorizontal = true;
        private boolean isSquareCenter = false;
        private boolean isAutoPlay = false;
        private Surface surface;
        private int currentTime = 0;
        private boolean playerReady = false;

        public VideoRender(ThemedReactContext context) {
            mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTriangleVertices.put(mTriangleVerticesData).position(0);

            Matrix.setIdentityM(mSTMatrix, 0);
        }

        public void setMediaPlayer(MediaPlayer player) {
            mMediaPlayer = player;
        }

        @Override
        public void onDrawFrame(GL10 glUnused) {
            synchronized (this) {
                if (updateSurface) {
                    mSurface.updateTexImage();
                    mSurface.getTransformMatrix(mSTMatrix);
                    updateSurface = false;
                }
            }

            if(isSquareCenter){
                if (isHorizontal) {
                    int maxWidth = (videoWidth * layoutWidth) / videoHeight;
                    int xOffset = (maxWidth - layoutWidth) / 2;
                    GLES20.glViewport(-xOffset, 0, maxWidth, layoutWidth);
                } else {
                    int maxHeight = (videoHeight * layoutWidth) / videoWidth;
                    int yOffset = (maxHeight - layoutWidth) / 2;
                    GLES20.glViewport(0, -yOffset, layoutWidth, maxHeight);
                }

            }else{
//                GLES20.glViewport(0, 0, videoWidth, videoHeight);
                if (isHorizontal) {
                    int maxHeight = (videoHeight * layoutWidth) / videoWidth;
                    int yOffset = (maxHeight - layoutWidth) / 2;
                    GLES20.glViewport(0, -yOffset, videoWidth, videoHeight);
                } else {
                    int maxWidth = (videoWidth * layoutWidth) / videoHeight;
                    int xOffset = (maxWidth - layoutWidth) / 2;
                    GLES20.glViewport(-xOffset, 0, videoWidth, videoHeight);
                }
            }

            if(updateFilter){
                mProgram = createProgram(mVertexShader, effect.getShader(mSurfaceView));
                updateFilter = false;
            }

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID[0]);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

            Matrix.setIdentityM(mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            checkGlError("glDrawArrays");
            GLES20.glFlush();
            GLES20.glFinish();
        }

        @Override
        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        }

        @Override
        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

            mProgram = createProgram(mVertexShader, effect.getShader(mSurfaceView));
            if (mProgram == 0) {
                return;
            }
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkGlError("glGetAttribLocation aTextureCoord");
            if (maTextureHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aTextureCoord");
            }

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkGlError("glGetUniformLocation uMVPMatrix");
            if (muMVPMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uMVPMatrix");
            }

            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
            checkGlError("glGetUniformLocation uSTMatrix");
            if (muSTMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uSTMatrix");
            }

            // int[] textures = new int[1];
            GLES20.glGenTextures(2, mTextureID, 0);
            // GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[0]);

            // mTextureID = textures[0];
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID[0]);
            checkGlError("glBindTexture mTextureID");

            // GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
            // GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
            // GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			/*
             * Create the SurfaceTexture that will feed this textureID, and pass
			 * it to the MediaPlayer
			 */
            mSurface = new SurfaceTexture(mTextureID[0]);
            mSurface.setOnFrameAvailableListener(this);

            setupVideo();
        }

        @Override
        synchronized public void onFrameAvailable(SurfaceTexture surface) {
            updateSurface = true;
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            if (shader != 0) {
                GLES20.glShaderSource(shader, source);
                GLES20.glCompileShader(shader);
                int[] compiled = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,
                        compiled, 0);
                if (compiled[0] == 0) {
                    Log.e(TAG, "Could not compile shader " + shaderType + ":");
                    Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            int program = GLES20.glCreateProgram();
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,
                        linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e(TAG, "Could not link program: ");
                    Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        private void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

        public void setDimension(int videoHeight, int videoWidth, int layoutHeight, int layoutWidth, boolean isHorizontal) {
            this.videoHeight = videoHeight;
            this.videoWidth = videoWidth;
//            this.layoutDimension = layoutDimension;
            this.layoutHeight = layoutHeight;
            this.layoutWidth = layoutWidth;
            this.isHorizontal = isHorizontal;
        }

        public void setSquareCenter(boolean isSquareCenter){
            this.isSquareCenter = isSquareCenter;
        }

        public void setupVideo(){

            if(mSurface == null){
                Log.i(TAG, "setupVideo() canceled");
                return;
            }

            try {
                surface = new Surface(mSurface);
                mMediaPlayer.setSurface(surface);
                surface.release();
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.prepare();
//                Log.d(TAG, String.valueOf(isPaused));
//                Log.e(TAG, String.valueOf(mMediaPlayer.isPlaying()));
                surface.release();
                if(isAutoPlay){
                    mMediaPlayer.start();
                }
                playerReady = true;
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }

            synchronized (this) {
                updateSurface = false;
            }
        }

        public int getCurrenTime(){
            if(playerReady){
                currentTime = mMediaPlayer.getCurrentPosition();
            }
            return currentTime;
        }

        public void seekTo(final int seek){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (playerReady == true) {
                       mMediaPlayer.seekTo(seek);
                    }
                }
            }, 1000);
        }

        public void setAutoPlay(boolean isAutoPlay){
            this.isAutoPlay = isAutoPlay;
        }

        public void updateFilter(boolean isUpdateFilter){
            this.updateFilter = isUpdateFilter;
        }
    } // End of class VideoRender.

} // End of class VideoSurfaceView.
