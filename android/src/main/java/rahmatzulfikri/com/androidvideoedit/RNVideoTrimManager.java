package rahmatzulfikri.com.androidvideoedit;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import android.util.Log;
import javax.annotation.Nullable;
import java.util.Map;

import rahmatzulfikri.com.androidvideoedit.Util.VideoTrim;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

public class RNVideoTrimManager extends SimpleViewManager<VideoTrim>{
    private static final String REACT_CLASS = "RNVideoTrim";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_MINDUR = "minDuration";

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @Override
    public VideoTrim createViewInstance(ThemedReactContext themedReactContext){
        return new VideoTrim(themedReactContext);
    }

    @Override
    public void onDropViewInstance(VideoTrim videoTrim){
        super.onDropViewInstance(videoTrim);
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants(){
        MapBuilder.Builder<String, Map> builder = MapBuilder.builder();
        for(EventsEnum evt : EventsEnum.values()){
            builder.put(evt.toString(), MapBuilder.of("registrationName", evt.toString()));
        }
        Log.e("DEBUG", builder.toString());
        return builder.build();
    }

    @ReactProp(name = PROPS_SRC)
    public void setSource(VideoTrim videoTrim, String src){
        videoTrim.setSource(src);
    }

    @ReactProp(name = PROPS_MINDUR, defaultInt = 10000)
    public void setMinDur(VideoTrim videoTrim, int minDuration){
        videoTrim.setMinTrimDuration(minDuration);
    }
}