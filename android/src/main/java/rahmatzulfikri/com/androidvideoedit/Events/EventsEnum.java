package rahmatzulfikri.com.androidvideoedit.Events;

public enum EventsEnum {
    /* EVENT FOR VIDEO PLAYER */
    EVENT_GET_VIDEO_DURATION("videoDurationListener"),
    EVENT_GET_VIDEO_PROGRESS("videoProgressListener"),
    EVENT_GET_VIDEO_COMPLETED("videoCompletedListener"),
    EVENT_GET_VIDEO_READY("videoReadyListener"),

    /* EVENT FOR VIDEO TRIM */
    EVENT_GET_START_POS("getStartPos"),
    EVENT_GET_END_POS("getEndPos"),

    /* EVENT FOR VIDEO THUMBNAIL */
    EVENT_GET_SEEK_POS("getSeekPos");
    
    private final String mName;

    EventsEnum(final String name){
        mName = name;
    }

    @Override
    public String toString(){
        return mName;
    }
}
