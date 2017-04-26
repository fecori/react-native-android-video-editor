package rahmatzulfikri.com.androidvideoedit.Util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import rahmatzulfikri.com.androidvideoedit.Effect.BlackAndWhiteEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.BrightnessEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.ContrastEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.CrossProcessEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.DocumentaryEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.DuotoneEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.FillLightEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.GammaEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.GreyScaleEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.HueEffect;
import rahmatzulfikri.com.androidvideoedit.Effect.Interfaces.ShaderInterface;
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

public class ImageTextureRenderer extends TextureSurfaceRenderer implements SurfaceTexture.OnFrameAvailableListener {
    private float vertices[] = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
    };

    private float textureVertices[] = {
            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f
    };

    private FloatBuffer verticesBuffer;
    private FloatBuffer textureBuffer;

    private Bitmap photo;
    private int photoWidth;
    private int photoHeight;

    private int vertexShader;
    private int fragmentShader;
    private int program;

    private int textures[] = new int[2];

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 aTexPosition;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "  vTextureCoord = aTexPosition;" +
                    "}";

    private ShaderInterface fragmentShaderCode = null;

    private Context ctx;
    private int mWidth;
    private int mHeight;
    private boolean isCrop;

    private SurfaceTexture imageTexture;

    private boolean frameAvailable = false;

    private String path;
    private int position;
    private int filterCode;

    public ImageTextureRenderer(Context context, SurfaceTexture texture, int width, int height, String path, int position, int filterCode, boolean isCrop) {
        super(texture, width, height);
        mWidth = width;
        mHeight = height;
        this.ctx = context;
        this.path = path;
        this.position = position;
        this.filterCode = filterCode;
        this.isCrop = isCrop;
    }

//    public void setImage(){
    public void setImage(String path, int position, int filterCode){
//         Log.e("DEBUG", "MASUK IMAGE");
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        photo = mediaMetadataRetriever.getFrameAtTime(position * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        photoWidth = photo.getWidth();
        photoHeight = photo.getHeight();

        switch (filterCode){
            case 0:
                fragmentShaderCode = new NoEffect();
                break;
            case 1:
                fragmentShaderCode = new BlackAndWhiteEffect();
                break;
            case 2:
                fragmentShaderCode = new BrightnessEffect(0.5f);
                break;
            case 3:
                fragmentShaderCode = new ContrastEffect(0.5f);
                break;
            case 4:
                fragmentShaderCode = new CrossProcessEffect();
                break;
            case 5:
                fragmentShaderCode = new DocumentaryEffect(photoWidth, photoHeight);
                break;
            case 6:
                fragmentShaderCode = new DuotoneEffect(Color.CYAN, Color.BLUE);
                break;
            case 7:
                fragmentShaderCode = new FillLightEffect(0.5f);
                break;
            case 8:
                fragmentShaderCode = new GammaEffect(1.0f);
                break;
            case 9:
                fragmentShaderCode = new GreyScaleEffect();
                break;
            case 10:
                fragmentShaderCode = new HueEffect(90f);
                break;
            case 11:
                fragmentShaderCode = new InvertColorsEffect();
                break;
            case 12:
                fragmentShaderCode = new LamoishEffect(photoWidth, photoHeight);
                break;
            case 13:
                fragmentShaderCode = new PosterizeEffect();
                break;
            case 14:
                fragmentShaderCode = new SaturationEffect(0.1f);
                break;
            case 15:
                fragmentShaderCode = new SepiaEffect();
                break;
            case 16:
                fragmentShaderCode = new SharpnessEffect(0.5f, photoWidth, photoHeight);
                break;
            case 17:
                fragmentShaderCode = new TemperatureEffect(0.5f);
                break;
            case 18:
                fragmentShaderCode = new TintEffect(Color.BLUE);
                break;
            case 19:
                fragmentShaderCode = new VignetteEffect(0.5f, photoWidth, photoHeight);
                break;
            default:
                fragmentShaderCode = new NoEffect();
                break;
        }

    }

    private void initializeBuffers(){

        ByteBuffer buff = ByteBuffer.allocateDirect(vertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        verticesBuffer = buff.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        buff = ByteBuffer.allocateDirect(textureVertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        textureBuffer = buff.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);

        imageTexture = new SurfaceTexture(textures[0]);
        imageTexture.setOnFrameAvailableListener(this);

    }



    private void initializeProgram(){
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        GLES20.glShaderSource(fragmentShader, fragmentShaderCode.getImageShader());

        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);

    }

    @Override
    protected boolean draw() {

        if(isCrop){
            if (photoHeight > photoWidth) {
                System.out.println("vertical");
                int maxHeight = (photoHeight * mWidth) / photoWidth;
                int yOffset = (maxHeight - mWidth) / 2;
                GLES20.glViewport(0, -yOffset, mWidth, maxHeight);
            } else {
                System.out.println("horizontal");
                int maxWidth = (photoWidth * mWidth) / photoHeight;
                int xOffset = (maxWidth - mWidth) / 2;
                GLES20.glViewport(-xOffset, 0, maxWidth, mWidth);
            }
        }else{
            if (photoHeight > photoWidth) {
                int maxWidth = (photoWidth * mWidth) / photoHeight;
                int xOffset = (maxWidth - mWidth) / 2;
                GLES20.glViewport(-xOffset, 0, maxWidth, mWidth);
            } else {
                System.out.println("horizontal");
                int maxHeight = (photoHeight * mWidth) / photoWidth;
                int yOffset = (maxHeight - mWidth) / 2;
                GLES20.glViewport(0, -yOffset, mWidth, maxHeight);
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(program);
        GLES20.glDisable(GLES20.GL_BLEND);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int textureHandle = GLES20.glGetUniformLocation(program, "sTexture");
        int texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexPosition");

        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        return true;
    }


    private void setup(){
//         Log.e("DEBUG", "MASUK SETUP");

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                setImage(path, position, filterCode);

                GLES20.glViewport(0,0,mWidth, mHeight);
                GLES20.glClearColor(0,0,0,1);

                GLES20.glGenTextures(2, textures, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);

                initializeBuffers();
                initializeProgram();
            }
        });
        thread.run();
    }

    @Override
    protected void initGLComponents() {
        setup();
    }

    @Override
    protected void deinitGLComponents() {
        GLES20.glDeleteTextures(1, textures, 0);
        GLES20.glDeleteProgram(program);
        imageTexture.release();
        imageTexture.setOnFrameAvailableListener(null);
    }


    public SurfaceTexture getVideoTexture() {
        return imageTexture;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        synchronized (this) {
//            frameAvailable = true;
//        }
    }
}
