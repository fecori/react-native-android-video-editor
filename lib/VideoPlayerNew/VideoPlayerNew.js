import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
    Dimensions,
    UIManager,
    findNodeHandle,
} from 'react-native'

const ProcessingUIVideoPlayer = UIManager.RNVideoPlayerNew;

class VideoPlayer extends Component {
    static propTypes = {
        src : PropTypes.string.isRequired,
        crop : PropTypes.bool,
        autoplay : PropTypes.bool,
        filter : PropTypes.number,
        style : PropTypes.object,
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
        this.state = {
            isPlaying : this.props.autoplay
        }
    }

    pauseVideo(){
        console.log("PAUSE VIDEO");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.pauseVideo,
            [],
        )
        this.setState({isPlaying: false})
    }

    playVideo(){
        console.log("PLAY VIDEO EUY");
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            ProcessingUIVideoPlayer.Commands.playVideo,
            [],
        )
        this.setState({isPlaying:true})
    }

    render(){
        const{
            src,
            crop,
            autoplay,
            filter,
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
                {...props}
            />
        );
    }
}

const VideoPlayerEdit = requireNativeComponent('RNVideoPlayerNew', VideoPlayer)

export class VideoPlayerNew extends Component {
    static propTypes = {
        src : PropTypes.string.isRequired,
        crop : PropTypes.bool,
        autoplay : PropTypes.bool,
        filter : PropTypes.number,
        style : PropTypes.object,
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
    }

    pauseVideo(){
        console.log("PAUSE VIDEO");
        this.videoPlayerRef.pauseVideo()
    }

    playVideo(){
        console.log("PLAY VIDEO EUY");
        this.videoPlayerRef.playVideo()
    }

    render(){
        const{
            src,
            crop,
            autoplay,
            filter,
            style,
            ...props
        } = this.props
        return(
            <View>
                <View style={{justifyContent:'center', alignItems:'center'}}>
                    <VideoPlayer
                        ref={ref => this.videoPlayerRef = ref}
                        style={style}
                        src={src}
                        crop={crop}
                        autoplay={autoplay}
                        filter={filter}
                        {...props}
                    />
                </View>
            </View>
        );
    }
}