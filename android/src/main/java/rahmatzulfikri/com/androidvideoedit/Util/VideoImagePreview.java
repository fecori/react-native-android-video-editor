package rahmatzulfikri.com.androidvideoedit.Util;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;

/**
 * Created by Rahmat Zulfikri on 3/30/17.
 */

public class VideoImagePreview extends TextureView implements TextureView.SurfaceTextureListener {

    private ThemedReactContext mContext;
    private int surfaceWidth;
    private int surfaceHeight;
    private boolean isCrop = false;

    private SurfaceTexture surface = null;
    private ImageTextureRenderer mRenderer;

    private String path = null;
    private int position;
    private int filterCode;

    public VideoImagePreview(ThemedReactContext context) {
        super(context);
        mContext = context;
        this.setSurfaceTextureListener(this);
    }

    public VideoImagePreview(ThemedReactContext context, AttributeSet attrs) {
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

    public void setSource(String path){
        this.path = path;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setFilter(int filterCode){
        this.filterCode = filterCode;
    }

    public void setCrop(boolean isCrop){
        this.isCrop = isCrop;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surfaceHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        surfaceWidth = View.MeasureSpec.getSize(widthMeasureSpec);
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
