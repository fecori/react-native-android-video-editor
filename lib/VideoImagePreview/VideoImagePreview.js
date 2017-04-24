import React, {Component, PropTypes} from 'react'
import {
    AppRegistry,
    requireNativeComponent,
    View,
    Dimensions,
} from 'react-native'

/*
  Class VideoImagePreview
  this class to show Video Filter Preview in Image
*/
export class VideoImagePreview extends Component{
    static propTypes = {
        src : PropTypes.string.isRequired,
        pos : PropTypes.number,
        filter : PropTypes.number,
        crop : PropTypes.bool,
        style : PropTypes.object,
        ...View.propTypes,
    }

    static defaultProps = {
        pos : 0,
        filter : 0,
        crop : false,
        style : {
            width : Dimensions.get('window').width/5,
            height : Dimensions.get('window').width/5,
            marginTop : ((Dimensions.get('window').width/5)/5),
            marginLeft : ((Dimensions.get('window').width/5)/5),
            marginBottom : ((Dimensions.get('window').width/5)/5),
        }
    }

    constructor(props){
        super(props)
    }

    render(){
        const{
            src,
            pos,
            filter,
            crop,
            style,
            ...props
        } = this.props
        return(
            <ImagePreview
                style = {style}
                src = {src}
                pos = {pos}
                filter = {filter}
                crop = {crop}
                {...this.props}
            />
        );
    }
}

const ImagePreview = requireNativeComponent('RNVideoImagePreview', VideoImagePreview)
