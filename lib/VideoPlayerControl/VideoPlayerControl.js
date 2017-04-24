import React, {Component, PropTypes} from 'react'
import {
    View,
    Dimensions,
    Image,
    TouchableOpacity,
    TouchableWithoutFeedback,
    Slider,
} from 'react-native'

import {VideoPlayer} from '../VideoPlayer'

export class VideoPlayerControl extends Component {
    static propTypes = {
        src : PropTypes.string.isRequired,
        crop : PropTypes.bool,
        autoplay : PropTypes.bool,
        filter : PropTypes.number,
        style : PropTypes.object,
        startPos : PropTypes.number,
        endPos : PropTypes.number,
        onCrop : PropTypes.func,
        ...View.propTypes,
    }

    static defaultProps = {
        autoplay : false,
        crop : false,
        filter : 0,
        style : {width: Dimensions.get('window').width, height: Dimensions.get('window').width},
        startPos : 0,
        endPos : 0,
    }

    constructor(props){
        super(props)
        this.state = {
            isPlaying : props.autoplay,
            isCrop : props.crop,
            videoDuration : 0,
            sliderValue : 0,
            videoCompleted : false,
            isVideoReady : false,
            startPos : props.startPos,
            endPos : props.endPos,
            limitDuration : props.endPos - props.startPos,
            currentPos : 0,
        }
    }

    _getVideoDuration = (e) =>{
        console.log("VIDEO DURATION "+e.videoDuration)
        this.setState({videoDuration : e.videoDuration})
    }

    _getVideoProgress = (e) =>{
        if(this.state.limitDuration > 0){
            console.log("VIDEO PROGRESS "+(e.videoProgress - this.state.startPos)+" "+Math.round(((e.videoProgress - this.state.startPos) * 1) / this.state.limitDuration)+" "+Number((((e.videoProgress - this.state.startPos) * 1) / this.state.limitDuration).toFixed(2)))
            if((e.videoProgress - this.state.startPos) >= this.state.limitDuration && Number((((e.videoProgress - this.state.startPos) * 1) / this.state.limitDuration).toFixed(2)) >= 1){
                this.videoPlayerRef.pause()
                this.setState({sliderValue : 1, videoCompleted: true, currentPos : e.videoProgress})
            }else{
                this.setState({sliderValue : (((e.videoProgress - this.state.startPos) * 1) / this.state.limitDuration), currentPos : e.videoProgress})
            }
        }else{
            console.log("VIDEO PROGRESS "+e.videoProgress+" "+Math.round((e.videoProgress * 1) / this.state.videoDuration)+" "+Number(((e.videoProgress * 1) / this.state.videoDuration).toFixed(2)))
            if(e.videoProgress === this.state.videoDuration && Number(((e.videoProgress * 1) / this.state.videoDuration).toFixed(2)) === 1){
                this.setState({sliderValue : 1, currentPos : e.videoProgress})
            }else{
                this.setState({sliderValue : ((e.videoProgress * 1) / this.state.videoDuration), currentPos : e.videoProgress})
            }
        } 
    }

    _getVideoCompleted = (e) =>{
        console.log("VIDEO COMPLETED")
        if(!this.state.videoCompleted){
            this.setState({videoCompleted : true, isPlaying : true})
        }
    }

    _getVideoReady = (e) =>{
        console.log("VIDEO READY")
        this.setState({isVideoReady : true})
        console.log("START POS "+this.state.startPos+" END POS "+this.state.endPos+" LIMIT DURATION "+this.state.limitDuration)
        this.videoPlayerRef.seek(this.state.startPos);
    }

    pauseVideo(){
        console.log("PAUSE VIDEO")  
        this.videoPlayerRef.pause()
        this.setState({isPlaying : false, videoCompleted : false})    
    }

    playVideo(){
        console.log("PLAY VIDEO")
        this.videoPlayerRef.play()
        this.setState({isPlaying : true, videoCompleted: false})
    }

    cropVideo(){
        console.log("CROP VIDEO")
        this.videoPlayerRef.crop()
        this.setState({isCrop : true})
        if(typeof this.props.onCrop === 'function'){
            this.props.onCrop(true)
        }
    }

    unCropVideo(){
        console.log("UNCROP VIDEO")
        this.videoPlayerRef.uncrop()
        this.setState({isCrop : false})
        if(typeof this.props.onCrop === 'function'){
            this.props.onCrop(false)
        }
    }

    filterVideo(filter){
        console.log("FILTER VIDEO")
        this.videoPlayerRef.filter(parseInt(filter))
    }

    // setStartPos(e){
    //     console.log("START POS "+e)
        
    //     this.setState({startPos : e})
    // }

    // setEndPos(e){
    //     console.log("END POS"+e)
    //     this.setState({endPos : e})
    // }

    setLimit(e){
        console.log("START POS "+e.startPos+" END POS "+e.endPos+" LIMIT DURATION "+(e.endPos - e.startPos))
        console.log("CURRENT POS "+this.state.currentPos+" START POS"+ e.startPos + "STATE START POS "+this.state.startPos)
        if(this.state.isVideoReady){
            if(this.state.endPos === e.endPos && this.state.startPos !== e.startPos){
                if(e.startPos > this.state.startPos){
                    if(this.state.isPlaying){
                        this.videoPlayerRef.seek(parseInt(e.startPos))
                    }else{
                        if(e.startPos > this.state.currentPos){
                            this.videoPlayerRef.seek(parseInt(e.startPos))
                            this.setState({currentPos : e.startPos})
                            this.setState({sliderValue : 0})
                        }else{
                            this.setState({sliderValue : (((this.state.currentPos - e.startPos) * 1) / (e.endPos - e.startPos))})
                        }
                    }   
                }else{
                    if(!this.state.isPlaying){
                        this.setState({sliderValue : (((this.state.currentPos - e.startPos) * 1) / (e.endPos - e.startPos))})
                    }
                }
            }else{
                if(this.state.videoCompleted){
                    if(e.endPos > this.state.endPos){
                        if(this.state.isPlaying){
                            this.playVideo()
                        }
                    }else{
                        if(this.state.isPlaying){
                            this.videoPlayerRef.seek(parseInt(e.endPos))
                        }
                    }
                }else{
                    this.setState({sliderValue : (((this.state.currentPos - e.startPos) * 1) / (e.endPos - e.startPos))})
                }
            }
        }
        this.setState({startPos : e.startPos, endPos : e.endPos, limitDuration : (e.endPos - e.startPos) })
    }

    _onPressPlay = () =>{
        if(this.state.isVideoReady){
            if(this.state.limitDuration > 0){
                if(this.state.videoCompleted){
                    this.videoPlayerRef.seek(parseInt(this.state.startPos))
                    this.playVideo()
                }else{
                    this.state.isPlaying ? this.pauseVideo() : this.playVideo()
                }
            }else{
                this.state.videoCompleted ? this.playVideo() : this.state.isPlaying ? this.pauseVideo() : this.playVideo()
            }
        }
    }

    _onPressCrop = () =>{
        if(this.state.isVideoReady){
            this.state.isCrop ? this.unCropVideo() : this.cropVideo()
        }
    }

    _onSeekChange = (value) =>{
        console.log("SEEK : "+value+" "+this.state.videoDuration+" "+(parseInt(value * this.state.videoDuration)))
        if(value < 1){
            if(this.state.videoCompleted){
                this.setState({videoCompleted : false, isPlaying : false})
            }
        }
        if(this.state.limitDuration > 0)
        {
            this.videoPlayerRef.seek(parseInt((value * this.state.limitDuration)+this.state.startPos))
            this.setState({currentPos : parseInt((value * this.state.limitDuration)+this.state.startPos) })
        }else{
            this.videoPlayerRef.seek(parseInt(value * this.state.videoDuration))
            this.setState({currentPos : parseInt(value * this.state.videoDuration)})
        }
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
                    <TouchableWithoutFeedback onPress={this._onPressPlay}>
                        <VideoPlayer
                            ref={ref => this.videoPlayerRef = ref}
                            style={style}
                            src={src}
                            crop={crop}
                            autoplay={autoplay}
                            filter={filter}
                            getVideoDuration={this._getVideoDuration}
                            getVideoProgress={this._getVideoProgress}
                            getVideoCompleted={this._getVideoCompleted}
                            getVideoReady={this._getVideoReady}
                            {...props}
                        />
                    </TouchableWithoutFeedback>

                    {this.state.isPlaying ? 
                    <View></View>
                    :
                    <View style={{position:'absolute'}}>
                        <TouchableOpacity onPress={this._onPressPlay}>
                        <View style={{width: 100, height: 100, borderRadius:50, alignSelf:'center', backgroundColor:'rgba(1,1,1,0.5)', borderColor:'#ffffff', borderWidth:2}}>
                            <Image
                                style={{width: 100, height: 100, alignSelf:'center'}}
                                source={require('./icons/ic_play.png')}
                            />
                        </View>
                        </TouchableOpacity>
                    </View>
                    }

                    {this.state.videoCompleted ? 
                    <View style={{position:'absolute'}}>
                        <TouchableOpacity onPress={this._onPressPlay}>
                            <View style={{width: 100, height: 100, borderRadius:50,alignSelf:'center', backgroundColor:'rgba(1,1,1,0.5)', borderColor:'#ffffff', borderWidth:2}}>
                                <Image
                                    style={{width: 100, height: 100, alignSelf:'center'}}
                                    source={require('./icons/ic_replay.png')}
                                />
                            </View>
                        </TouchableOpacity>
                    </View>
                    :
                    <View></View>
                    }
                    
                    <View style={{height:this.props.style.height, width: this.props.style.width, position:'absolute', justifyContent:'flex-end', alignItems:'flex-end'}}>
                        <TouchableOpacity onPress={this._onPressCrop}>
                        <View style={{width: 30, height: 30, borderRadius:1150,  margin: 15, alignItems:'center', justifyContent:'center', backgroundColor:'rgba(1,1,1,0.5)', borderColor:'#ffffff', borderWidth:2}}>
                            {this.state.isCrop ? 
                            <Image
                                style={{width: 25, height: 25, alignItems:'center'}}
                                source={require('./icons/ic_crop_free.png')}
                            />
                            :
                            <Image
                                style={{width: 25, height: 25, alignItems:'center'}}
                                source={require('./icons/ic_crop.png')}
                            />
                            }
                        </View>
                        </TouchableOpacity>
                    </View>
                </View>
                <Slider
                    value={this.state.sliderValue}
                    style={{backgroundColor:'rgba(1,1,1,0.5)'}}
                    onValueChange={(value) => this._onSeekChange(value)}
                    />
            </View>
        );
    }
}