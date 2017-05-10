import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
    Dimensions,
    Image,
    TouchableOpacity,
    TouchableWithoutFeedback,
    UIManager,
    findNodeHandle,
} from 'react-native'

const ProcessingUIVideoPlayer = UIManager.RNVideoPlayer;


/*
  Class videoPlayer,
  this class for playing video & apply crop and filter
*/
export class VideoPlayer extends Component {
    static propTypes = {
        src : PropTypes.string.isRequired,
        crop : PropTypes.bool,
        autoplay : PropTypes.bool,
        filter : PropTypes.number,
        seekTo : PropTypes.number,
        style : PropTypes.object,
        getVideoDuration : PropTypes.func,
        getVideoProgress : PropTypes.func,
        getVideoCompleted : PropTypes.func,
        getVideoReady : PropTypes.func,
        getVideoPlayerStatus : PropTypes.func,
        ...View.propTypes,
    }

    static defaultProps = {
        autoplay : false,
        crop : false,
        filter : 0,
        seekTo : 1,
        style : {width: Dimensions.get('window').width, height: Dimensions.get('window').width},
    }

    constructor(props){
        super(props)
        this._receiveVideoDuration = this._receiveVideoDuration.bind(this)
        this._receiveVideoProgress = this._receiveVideoProgress.bind(this)
        this._receiveVideoComplete = this._receiveVideoComplete.bind(this)
        this._receiveVideoReady = this._receiveVideoReady.bind(this)
        this._receiveVideoPlayerStatus = this._receiveVideoPlayerStatus.bind(this)
    }

    // Callback method to get videoDuration
    _receiveVideoDuration({nativeEvent}){
//       console.log("VIDEO DURATION : "+nativeEvent.videoDuration)
      if(typeof this.props.getVideoDuration === 'function'){
          this.props.getVideoDuration({videoDuration : nativeEvent.videoDuration})
      }
    }

    // Callback method to get videoProgress when playing
    _receiveVideoProgress({nativeEvent}){
//         console.log("VIDEO PROGRESS : "+nativeEvent.videoProgress)
        if(typeof this.props.getVideoProgress === 'function'){
            this.props.getVideoProgress({videoProgress : nativeEvent.videoProgress})
        }
    }

    // Callback method to know status when video completed
    _receiveVideoComplete({nativeEvent}){
//         console.log("VIDEO COMPLETE")
        if(typeof this.props.getVideoCompleted === 'function'){
            this.props.getVideoCompleted({videoCompleted : nativeEvent.videoCompleted})
        }
    }

    // Callback method to know status when videoPlayer is ready
    _receiveVideoReady({nativeEvent}){
//         console.log("VIDEO READY")
        if(typeof this.props.getVideoReady === 'function'){
            this.props.getVideoReady({videoReady : nativeEvent.videoReady})
        }
    }

    // Callback method to know video player status
    _receiveVideoPlayerStatus({nativeEvent}){
        if(typeof this.props.getVideoPlayerStatus === 'function'){
            this.props.getVideoPlayerStatus({videoPlayerStatus : nativeEvent.videoPlayerStatus})
        }
    }

    // Method to pause videoPlayer
    pause(){
//         console.log("PAUSE VIDEO");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.pauseVideo,
            [],
        )
    }

    // Method to play videoPlayer
    play(){
//         console.log("PLAY VIDEO");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.playVideo,
            [],
        )
    }

    // Method to crop video in the middle square
    crop(){
//         console.log("CROP VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.cropVideo,
            [],
        )
    }

    // Method to uncrop video
    uncrop(){
//         console.log("UNCROP VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.unCropVideo,
            [],
        )
    }

    // Method to add some filter to video
    // @param filter = filter Id (0 - 19)
    filter(filter){
//         console.log("FILTER VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.filterVideo,
            [filter]
        )
    }
    // Method to seek video
    // @param position = video duration in milisecond (1000 = 1sec)
    seek(position){
//         console.log("SEEK VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.seekVideo,
            [position],
        )
    }

    render(){
        const{
            src,
            crop,
            autoplay,
            filter,
            seekTo,
            style,
            ...props
        } = this.props
        return(
            <VideoPlayerEdit
                style={style}
                src={src}
                crop={crop}
                autoplay={autoplay}
                filter={filter}
                seekTo={seekTo}
                videoDurationListener={this._receiveVideoDuration}
                videoProgressListener={this._receiveVideoProgress}
                videoCompletedListener={this._receiveVideoComplete}
                videoReadyListener={this._receiveVideoReady}
                videoPlayerStatusListener={this._receiveVideoPlayerStatus}
                {...props}
            />
        );
    }
}

const VideoPlayerEdit = requireNativeComponent('RNVideoPlayer', VideoPlayer)
