package rahmatzulfikri.com.androidvideoedit;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import rahmatzulfikri.com.androidvideoedit.Util.VideoImagePreview;

public class RNVideoImagePreviewManager extends SimpleViewManager<VideoImagePreview>{
    private static final String REACT_CLASS = "RNVideoImagePreview";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_POS = "pos";
    private static final String PROPS_FILTER = "filter";
    private static final String PROPS_CROP = "crop";

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @Override
    public VideoImagePreview createViewInstance(ThemedReactContext themedReactContext){
        return new VideoImagePreview(themedReactContext);
    }

    @Override
    public void onDropViewInstance(VideoImagePreview videoImagePreview){
        super.onDropViewInstance(videoImagePreview);
    }

    @ReactProp(name = PROPS_SRC)
    public void setSource(VideoImagePreview videoImagePreview, String src){
        videoImagePreview.setSource(src);
    }

    @ReactProp(name = PROPS_POS, defaultInt = 0)
    public void setPos(VideoImagePreview videoImagePreview, int pos){
        videoImagePreview.setPosition(pos);
    }

    @ReactProp(name = PROPS_CROP, defaultBoolean = false)
    public void setCrop(VideoImagePreview videoImagePreview, boolean crop){
        videoImagePreview.setCrop(crop);
    }

    @ReactProp(name = PROPS_FILTER, defaultInt = 0)
    public void setFilter(VideoImagePreview videoImagePreview, int filter){
        videoImagePreview.setFilter(filter);
    }
}