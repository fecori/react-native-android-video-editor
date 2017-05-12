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

import rahmatzulfikri.com.androidvideoedit.Util.VideoSurfaceView;
import rahmatzulfikri.com.androidvideoedit.Events.Events;
import rahmatzulfikri.com.androidvideoedit.Events.EventsEnum;

public class RNVideoPlayerManager extends SimpleViewManager<VideoSurfaceView>{
    private static final String REACT_CLASS = "RNVideoPlayer";

    private static final String PROPS_SRC = "src";
    private static final String PROPS_CROP = "crop";
    private static final String PROPS_AUTOPLAY = "autoplay";
    private static final String PROPS_FILTER = "filter";
    private static final String PROPS_SEEKTO = "seekTo";

    private final int COMMAND_PLAY_VIDEO = 1;
    private final int COMMAND_PAUSE_VIDEO = 2;
    private final int COMMAND_CROP_VIDEO = 3;
    private final int COMMAND_UNCROP_VIDEO = 4;
    private final int COMMAND_SEEK_VIDEO = 5;
    private final int COMMAND_FILTER_VIDEO = 6;

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
        videoSurfaceView.cleanup();
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
            COMMAND_PAUSE_VIDEO,

            Events.CROP_VIDEO,
            COMMAND_CROP_VIDEO,

            Events.UNCROP_VIDEO,
            COMMAND_UNCROP_VIDEO,

            Events.SEEK_VIDEO,
            COMMAND_SEEK_VIDEO,

            Events.FILTER_VIDEO,
            COMMAND_FILTER_VIDEO
        );
    }

    @Override
    public void receiveCommand(VideoSurfaceView videoSurfaceView, int commandId, @Nullable ReadableArray args){
        assert args != null;
        Log.e("DEBUG", "receiveCommand: " + args.toString());
        Log.e("DEBUG", "receiveCommand: commandId" + String.valueOf(commandId));
        switch(commandId){
            case COMMAND_PLAY_VIDEO:
                Log.e("DEBUG", "receiveCommand: Play video");
                videoSurfaceView.playVideo();
                break;
            case COMMAND_PAUSE_VIDEO:
                Log.e("DEBUG", "receiveCommand: Pause video");
                videoSurfaceView.pauseVideo();
                break;
            case COMMAND_CROP_VIDEO:
                Log.e("DEBUG", "receiveCommand: Crop video");
                videoSurfaceView.setSquareCenter(true);
                break;
            case COMMAND_UNCROP_VIDEO:
                Log.e("DEBUG", "receiveCommand: UnCrop video");
                videoSurfaceView.setSquareCenter(false);
                break;
            case COMMAND_SEEK_VIDEO:
                Log.e("DEBUG", "receiveCommand: Seek video");
                int position = (int)args.getInt(0);
                videoSurfaceView.seekTo(position);
                break;
            case COMMAND_FILTER_VIDEO:
                Log.e("DEBUG", "receiveCommand: Filter video");
                int filter = (int)args.getInt(0);
                videoSurfaceView.setShader(filter);
            default:
                Log.e("DEBUG", "receiveCommand: Wrong command received");
        }
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

    @ReactProp(name = PROPS_SEEKTO, defaultInt = 0)
    public void seekTo(VideoSurfaceView videoSurfaceView, int seekTo){
        Log.e("DEBUG", "MASUK SEEK "+seekTo);
        videoSurfaceView.seekTo(seekTo);
    }
}
