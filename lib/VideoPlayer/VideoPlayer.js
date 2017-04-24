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
        ...View.propTypes,
    }

    static defaultProps = {
        autoplay : false,
        crop : false,
        filter : 0,
        style : {width: Dimensions.get('window').width, height: Dimensions.get('window').width},
    }

    constructor(props){
        super(props)
        this._receiveVideoDuration = this._receiveVideoDuration.bind(this)
        this._receiveVideoProgress = this._receiveVideoProgress.bind(this)
        this._receiveVideoComplete = this._receiveVideoComplete.bind(this)
        this._receiveVideoReady = this._receiveVideoReady.bind(this)
    }

    _receiveVideoDuration({nativeEvent}){
      console.log("VIDEO DURATION : "+nativeEvent.videoDuration)
      if(typeof this.props.getVideoDuration === 'function'){
          this.props.getVideoDuration({videoDuration : nativeEvent.videoDuration})
      }
    }

    _receiveVideoProgress({nativeEvent}){
        console.log("VIDEO PROGRESS : "+nativeEvent.videoProgress)
        if(typeof this.props.getVideoProgress === 'function'){
            this.props.getVideoProgress({videoProgress : nativeEvent.videoProgress})
        }
    }

    _receiveVideoComplete({nativeEvent}){
        console.log("VIDEO COMPLETE")
        if(typeof this.props.getVideoCompleted === 'function'){
            this.props.getVideoCompleted({videoCompleted : nativeEvent.videoCompleted})
        }
    }

    _receiveVideoReady({nativeEvent}){
        console.log("VIDEO READY")
        if(typeof this.props.getVideoReady === 'function'){
            this.props.getVideoReady({videoReady : nativeEvent.videoReady})
        }
    }

    pause(){
        console.log("PAUSE VIDEO");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.pauseVideo,
            [],
        )
    }

    play(){
        console.log("PLAY VIDEO");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.playVideo,
            [],
        )
    }

    crop(){
        console.log("CROP VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.cropVideo,
            [],
        )
    }

    uncrop(){
        console.log("UNCROP VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.unCropVideo,
            [],
        )
    }

    filter(filter){
        console.log("FILTER VIDEO")
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.filterVideo,
            [filter]
        )
    }

    seek(position){
        console.log("SEEK VIDEO")
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
                {...props}
            />
        );
    }
}

const VideoPlayerEdit = requireNativeComponent('RNVideoPlayer', VideoPlayer)