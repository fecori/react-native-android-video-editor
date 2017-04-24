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

import rahmatzulfikri.com.androidvideoedit.Util.VideoPlayer;
import rahmatzulfikri.com.androidvideoedit.Events.Events;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

public class RNVideoPlayerNewManager extends SimpleViewManager<VideoPlayer>{
    private static final String REACT_CLASS = "RNVideoPlayerNew";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_CROP = "crop";
    private static final String PROPS_AUTOPLAY = "autoplay";
    private static final String PROPS_FILTER = "filter";

    private final int COMMAND_PLAY_VIDEO = 1;
    private final int COMMAND_PAUSE_VIDEO = 2;

    @Override
    public String getName(){
        return REACT_CLASS;
    }

    @Override
    public VideoPlayer createViewInstance(ThemedReactContext themedReactContext){
        return new VideoPlayer(themedReactContext);
    }

    @Override
    public void onDropViewInstance(VideoPlayer videoPlayer){
        super.onDropViewInstance(videoPlayer);
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

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap(){
        Log.e("DEBUG", "getCommandsMap");
        return MapBuilder.of(
            Events.PLAY_VIDEO,
            COMMAND_PLAY_VIDEO,

            Events.PAUSE_VIDEO,
            COMMAND_PAUSE_VIDEO
        );
    }

    @Override
    public void receiveCommand(VideoPlayer videoPlayer, int commandId, @Nullable ReadableArray args){
        assert args != null;
        Log.e("DEBUG", "receiveCommand: " + args.toString());
        Log.e("DEBUG", "receiveCommand: commandId" + String.valueOf(commandId));
        switch(commandId){
            case COMMAND_PLAY_VIDEO:
                Log.e("DEBUG", "receiveCommand: Play video");
                break;
            case COMMAND_PAUSE_VIDEO:
                Log.e("DEBUG", "receiveCommand: Pause video");
                break;
            default:
                Log.e("DEBUG", "receiveCommand: Wrong command received");
        }
    }

    @ReactProp(name = PROPS_SRC)
    public void setSource(VideoPlayer videoPlayer, String src){
        videoPlayer.setSource(src);
    }

    @ReactProp(name = PROPS_CROP, defaultBoolean = false)
    public void setCrop(VideoPlayer videoPlayer, boolean crop){
        videoPlayer.setCrop(crop);
    }

    @ReactProp(name = PROPS_AUTOPLAY, defaultBoolean = false)
    public void setAutoPlay(VideoPlayer videoPlayer, boolean autoplay){
        videoPlayer.setAutoPlay(autoplay);
    }

    @ReactProp(name = PROPS_FILTER, defaultInt = 0)
    public void setFilter(VideoPlayer videoPlayer, int filter){
        videoPlayer.setFilter(filter);
    }
}