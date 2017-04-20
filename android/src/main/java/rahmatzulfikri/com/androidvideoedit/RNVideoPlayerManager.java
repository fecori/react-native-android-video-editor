package rahmatzulfikri.com.androidvideoedit;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import android.util.Log;

import rahmatzulfikri.com.androidvideoedit.Util.VideoSurfaceView;

public class RNVideoPlayerManager extends SimpleViewManager<VideoSurfaceView>{
    private static final String REACT_CLASS = "RNVideoPLayer";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_CROP = "crop";
    private static final String PROPS_AUTOPLAY = "autoplay";
    private static final String PROPS_FILTER = "filter";


    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @Override
    public VideoSurfaceView createViewInstance(ThemedReactContext themedReactContext){
        return new VideoSurfaceView(themedReactContext);
    }

    @Override
    public void onDropViewInstance(VideoSurfaceView videoSurfaceView){
        super.onDropViewInstance(videoSurfaceView);
    }

    @ReactProp(name = PROPS_SRC)
    public void setSource(VideoSurfaceView videoSurfaceView, String src){
        videoSurfaceView.setSource(src);
    }

    @ReactProp(name = PROPS_CROP, defaultBoolean = false)
    public void setCrop(VideoSurfaceView videoSurfaceView, boolean crop){
        videoSurfaceView.setSquareCenter(crop);
    }

    @ReactProp(name = PROPS_AUTOPLAY, defaultBoolean = false)
    public void setAutoPlay(VideoSurfaceView videoSurfaceView, boolean autoplay){
        videoSurfaceView.setAutoplay(autoplay);
    }

    @ReactProp(name = PROPS_FILTER, defaultInt = 0)
    public void setFilter(VideoSurfaceView videoSurfaceView, int filter){
        videoSurfaceView.setShader(filter);
    }
}