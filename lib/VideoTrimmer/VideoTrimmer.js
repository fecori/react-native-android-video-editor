import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
    Dimensions,
} from 'react-native'
import { noop } from 'lodash';

export class VideoTrimmer extends Component{
    static propTypes = {
        src : PropTypes.string.isRequired,
        minDuration : PropTypes.number,
        style : PropTypes.object,
        onChange: PropTypes.func,
        ...View.propTypes,
    }

    static defaultProps = {
        minDuration : 10000,
        onChange: noop,
        style : {width : Dimensions.get('window').width, height : 75}
    }

    constructor(props){
        super(props)
        this._receiveStartPos = this._receiveStartPos.bind(this)
        this._receiveEndPos = this._receiveEndPos.bind(this)
        this.startPos = 0
        this.endPos = 0
    }

    _receiveStartPos({ nativeEvent }){
        this.startPos = nativeEvent.startPos
        if (typeof this.props.onChange === 'function') {
            this.props.onChange({startPos : this.startPos, endPos : this.endPos})
        }
    }

     _receiveEndPos({ nativeEvent }){
        this.endPos = nativeEvent.endPos
        if (typeof this.props.onChange === 'function') {
            this.props.onChange({startPos : this.startPos, endPos : this.endPos});
        }
    }

    render(){
        const{
            src,
            minDuration,
            style,
            ...props
        } = this.props
        return(
            <VideoTrim
                style = {style}
                src = {src}
                getStartPos = {this._receiveStartPos}
                getEndPos = {this._receiveEndPos}
                minDuration = {minDuration}
                {...props}
            />
        );
    }
}

const VideoTrim = requireNativeComponent('RNVideoTrim', VideoTrimmer);