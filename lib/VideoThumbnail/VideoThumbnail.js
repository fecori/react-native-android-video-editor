import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
} from 'react-native'

export class VideoThumbnail extends Component{
    static propTypes = {
        src : PropTypes.string.isRequired,
        filter : PropTypes.number,
        startDur : PropTypes.number,
        endDur : PropTypes.number,
        onSeekPos : PropTypes.func,
        ...View.propTypes,
    }

    static defaultProps = {
        filter : 0,
        startDur : 0,
        endDur : 0
    }

    constructor(props){
        super(props)
        this._receiveSeekPos = this._receiveSeekPos.bind(this)
    }

    // Callback method to get seek position
    _receiveSeekPos = ({nativeEvent}) =>{
        console.log("POSITION "+nativeEvent.seekPos)
        if(typeof this.props.onSeekPos === 'function'){
            this.props.onSeekPos(nativeEvent.seekPos)
        }
    }

    render(){
        return(
            <VideoThumbnailSelect
                style = {this.props.style}
                src = {this.props.src}
                filter = {this.props.filter}
                startDur = {this.props.startDur}
                endDur = {this.props.endDur}
                getSeekPos = {this._receiveSeekPos}
                {...this.props}
            />
        );
    }
}
const VideoThumbnailSelect = requireNativeComponent('RNVideoThumbnail', VideoThumbnail);
