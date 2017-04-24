package rahmatzulfikri.com.androidvideoedit.Util;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import rahmatzulfikri.com.androidvideoedit.Effect.NoEffect;
/**
 * Created by Rahmat Zulfikri on 3/21/17.
 */
public class VideoTextureRenderer extends TextureSurfaceRenderer implements SurfaceTexture.OnFrameAvailableListener {
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec4 vTexCoordinate;" +
                    "uniform mat4 textureTransform;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "   vTextureCoord = (textureTransform * vTexCoordinate).xy;" +
                    "   gl_Position = vPosition;" +
                    "}";

    private static final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "uniform samplerExternalOES texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main () {" +
                    "    vec4 color = texture2D(texture, v_TexCoordinate);" +
                    "    gl_FragColor = color;" +
                    "}";

    private static final String fragmentShaderBlackWite =
            "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;"
                    + "varying vec2 v_TexCoordinate;"
                    + "uniform samplerExternalOES texture;"
                    + "void main() {"
                    + "  vec4 color = texture2D(texture, v_TexCoordinate);"
                    + "  float colorR = (color.r + color.g + color.b) / 3.0;"
                    + "  float colorG = (color.r + color.g + color.b) / 3.0;"
                    + "  float colorB = (color.r + color.g + color.b) / 3.0;"
                    + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);"
                    + "}";


    private MediaPlayer mediaPlayer = null;

    private static float squareSize = 1.0f;
    private static float squareCoords[] = {-squareSize, squareSize, 0.0f,   // top left
            -squareSize, -squareSize, 0.0f,   // bottom left
            squareSize, -squareSize, 0.0f,   // bottom right
            squareSize, squareSize, 0.0f}; // top right

    private static short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private Context ctx;

    // Texture to be shown in backgrund
    private FloatBuffer textureBuffer;
    private float textureCoords[] = {0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private int[] textures = new int[1];

    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private int shaderProgram;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private SurfaceTexture videoTexture;
    private float[] videoTextureTransform;
    private boolean frameAvailable = false;

    private int videoWidth;
    private int videoHeight;
    private boolean isCrop = false;
    private boolean adjustViewport = false;

    private String Shader = null;

    private boolean loadShader = false;

    public VideoTextureRenderer(Context context, SurfaceTexture texture, int width, int height) {
        super(texture, width, height);
        this.ctx = context;
        videoTextureTransform = new float[16];
    }

    public void setShader(String shader) {
        Shader = shader;
        loadShader = true;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    private void loadShaders() {

        if(vertexShaderHandle != 0){
            GLES20.glDeleteShader(vertexShaderHandle);
        }

        vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
        GLES20.glCompileShader(vertexShaderHandle);
        checkGlError("Vertex shader compile");
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(vertexShaderHandle));
        }

        if(fragmentShaderHandle != 0){
            GLES20.glDeleteShader(fragmentShaderHandle);
        }

        fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (Shader != null) {
            GLES20.glShaderSource(fragmentShaderHandle, Shader);
        } else {
            GLES20.glShaderSource(fragmentShaderHandle, new NoEffect().getVideoShader());
        }
        GLES20.glCompileShader(fragmentShaderHandle);
        checkGlError("Pixel shader compile");
        int[] compiled2 = new int[1];
        GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compiled2, 0);
        if (compiled2[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(fragmentShaderHandle));
        }

        if(shaderProgram != 0){
            GLES20.glDeleteProgram(shaderProgram);
        }
        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShaderHandle);
        GLES20.glAttachShader(shaderProgram, fragmentShaderHandle);
        GLES20.glLinkProgram(shaderProgram);
        checkGlError("Shader program compile");

        int[] status = new int[1];
        GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, status, 0);

        if (status[0] != GLES20.GL_TRUE) {
            Log.e("SurfaceTest", "Could not link program: ");
            Log.e("SurfaceTest", GLES20.glGetProgramInfoLog(shaderProgram));
            GLES20.glDeleteProgram(shaderProgram);
        }

        loadShader = false;
    }



    private void setupVertexBuffer() {
        // Draw list buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // Initialize the texture holder
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
    }


    private void setupTexture(Context context) {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());

        textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("Texture bind");

        videoTexture = new SurfaceTexture(textures[0]);
        videoTexture.setOnFrameAvailableListener(this);
    }

    @Override
    protected boolean draw() {
        synchronized (this) {
            if (frameAvailable) {
                videoTexture.updateTexImage();
                videoTexture.getTransformMatrix(videoTextureTransform);
                frameAvailable = false;
            } else {
                return false;
            }

        }

        if(loadShader){
            loadShaders();
        }

        if (adjustViewport){
            adjustViewport();
        }

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw texture
        GLES20.glUseProgram(shaderProgram);
        int textureParamHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinate");
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        int textureTranformHandle = GLES20.glGetUniformLocation(shaderProgram, "textureTransform");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 4 * 3, vertexBuffer);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(textureParamHandle, 0);

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glUniformMatrix4fv(textureTranformHandle, 1, false, videoTextureTransform, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);

        GLES20.glFinish();
        GLES20.glFlush();

        return true;
    }

    private void adjustViewport() {
        float surfaceAspect = height / (float) width;
        float videoAspect = videoHeight / (float) videoWidth;
        Log.e("DEBUG", "ADJUST VIEWPORT "+videoWidth+" "+videoHeight+" "+width+" "+height);
        if(isCrop){
            if (videoHeight > videoWidth) {
                Log.e("DEBUG", "1");
                int maxHeight = (videoHeight * width) / videoWidth;
                int yOffset = (maxHeight - width) / 2;
                GLES20.glViewport(0, -yOffset, width, maxHeight);
            } else {
                Log.e("DEBUG", "2");
                int maxWidth = (videoWidth * width) / videoHeight;
                int xOffset = (maxWidth - width) / 2;
                GLES20.glViewport(-xOffset, 0, maxWidth, width);
            }
        }else{
            if (videoHeight > videoWidth) {
                Log.e("DEBUG", "3");
                int maxWidth = (videoWidth * width) / videoHeight;
                int xOffset = (maxWidth - width) / 2;
                GLES20.glViewport(-xOffset, 0, maxWidth, width);
            } else {
                Log.e("DEBUG", "4");
                int maxHeight = (videoHeight * width) / videoWidth;
                int yOffset = (maxHeight - width) / 2;
                GLES20.glViewport(0, -yOffset, width, maxHeight);
            }
        }



        adjustViewport = false;
    }

    @Override
    protected void initGLComponents() {
        setupVertexBuffer();
        setupTexture(ctx);
        loadShaders();
    }

    @Override
    protected void deinitGLComponents() {
        GLES20.glDeleteTextures(1, textures, 0);
        GLES20.glDeleteProgram(shaderProgram);
        videoTexture.release();
        videoTexture.setOnFrameAvailableListener(null);
    }

    public void setDimension(int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
        adjustViewport = true;
    }

    public void setCrop(boolean isCrop){
        this.isCrop = isCrop;
        adjustViewport = true;
    }

    public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
        }
    }

    public SurfaceTexture getVideoTexture() {
        return videoTexture;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            frameAvailable = true;
        }
    }
}
