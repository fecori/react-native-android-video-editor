package rahmatzulfikri.com.androidvideoedit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import rahmatzulfikri.com.androidvideoedit.Util.ImageTextureRenderer;

/**
 * Created by lembah8 on 3/30/17.
 */

public class VideoImagePreview extends TextureView implements TextureView.SurfaceTextureListener {

    private Context mContext;
    private int surfaceWidth;
    private int surfaceHeight;
    private boolean isCrop = false;

    private SurfaceTexture surface = null;
    private ImageTextureRenderer mRenderer;

    private String path = null;
    private int position;
    private int filterCode;

    public VideoImagePreview(Context context) {
        super(context);
        mContext = context;
        this.setSurfaceTextureListener(this);
    }

    public VideoImagePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setSurfaceTextureListener(this);
    }

    private void handleSetup(final SurfaceTexture surface){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (path != null) {
                    Log.e("DEBUG", "IMAGE MASUK SINI");
                    setup(surface);
                } else {
                    Log.e("DEBUG", "IMAGE MASUK SANA");
                    handleSetup(surface);
                }
            }
        }, 1000);
    }

    private void setup(SurfaceTexture surface){
        mRenderer = new ImageTextureRenderer(mContext, surface, surfaceWidth, surfaceWidth , path, position, filterCode, isCrop);
    }

    public void setImage(String path, int position, int filterCode, boolean isCrop){
        this.path = path;
        this.position = position;
        this.filterCode = filterCode;
        this.isCrop = isCrop;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surfaceHeight = MeasureSpec.getSize(heightMeasureSpec);
        surfaceWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.setMeasuredDimension(surfaceWidth, surfaceWidth);
    }

    public void pause(){
        if (mRenderer != null)
            mRenderer.onPause();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        handleSetup(surface);
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
