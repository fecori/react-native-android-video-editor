package rahmatzulfikri.com.androidvideoedit.Events;

public class Events{
    /* VideoPlayer Events */
    // Calling Method
    public static final String PLAY_VIDEO = "playVideo";
    public static final String PAUSE_VIDEO = "pauseVideo";
    public static final String CROP_VIDEO = "cropVideo";
    public static final String UNCROP_VIDEO = "unCropVideo";
    public static final String SEEK_VIDEO = "seekVideo";
    public static final String FILTER_VIDEO = "filterVideo";
    // Callback
    public static final String VIDEO_DURATION = "videoDuration";
    public static final String VIDEO_PROGRESS = "videoProgress";
    public static final String VIDEO_COMPLETED = "videoCompleted";
    public static final String VIDEO_READY = "videoReady";

    /* VideoTrim Events */
    // Callback
    public static final String START_POS = "startPos";
    public static final String END_POS = "endPos";

    /* VideoThumbnail Events */
    // Callback
    public static final String SEEK_POS = "seekPos";
}
