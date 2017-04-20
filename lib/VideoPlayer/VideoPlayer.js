import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
    Dimensions,
} from 'react-native'

export class VideoPlayer extends Component {
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
            <RNVideoPlayer
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

const RNVideoPlayer = requireNativeComponent('RNVideoPLayer', VideoPlayer)