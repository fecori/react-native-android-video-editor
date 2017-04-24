package rahmatzulfikri.com.androidvideoedit;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.common.MapBuilder;

import android.util.Log;
import javax.annotation.Nullable;
import java.util.Map;

import rahmatzulfikri.com.androidvideoedit.Util.VideoThumbnail;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

public class RNVideoThumbnailManager extends SimpleViewManager<VideoThumbnail>{
    private static final String REACT_CLASS = "RNVideoThumbnail";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_FILTER = "filter";
    private static final String PROPS_START = "startDur";
    private static final String PROPS_END = "endDur";

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @Override
    public VideoThumbnail createViewInstance(ThemedReactContext themedReactContext){
        return new VideoThumbnail(themedReactContext);
    }

    @Override
    public void onDropViewInstance(VideoThumbnail videoThumbnail){
        super.onDropViewInstance(videoThumbnail);
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Map> builder = MapBuilder.builder();
        for (EventsEnum evt : EventsEnum.values()) {
        builder.put(evt.toString(), MapBuilder.of("registrationName", evt.toString()));
        }
        Log.e("DEBUG", builder.toString());
        return builder.build();
    }

    @ReactProp(name = PROPS_SRC)
    public void setSource(VideoThumbnail videoThumbnail, String src){
        videoThumbnail.setSource(src);
    }

     @ReactProp(name = PROPS_FILTER, defaultInt = 0)
    public void setFilter(VideoThumbnail videoThumbnail, int filter){
        videoThumbnail.setFilter(filter);
    }

    @ReactProp(name = PROPS_START, defaultInt = 0)
    public void setStartDur(VideoThumbnail videoThumbnail, int startDur){
        videoThumbnail.setStartDur(startDur);
    }

    @ReactProp(name = PROPS_END, defaultInt = 0)
    public void setEndDur(VideoThumbnail videoThumbnail, int endDur){
        videoThumbnail.setEndDur(endDur);
    }
}