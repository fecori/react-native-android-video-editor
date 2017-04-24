# react-native-android-video-editor
# description
This is react-native component for android, to handle video editor like trim video, filter video and thumbnail selection. But this component only use for preview only not prosess video editor (mybe next version). This Component have 5 child component :

1. VideoPlayer = This Component to play, filter and crop video without controler 
2. VideoPlayerControl = This Component to play, filter and crop video and it have player control like play button, replay button, crop button and seek bar.
3. VideoTrimmer = This Component to handle trim (start pos and end pos) by default max trim duration is 10min.
4. VideoImagePreview = This Component to preview video filter in image (you can use it for filter button).
5. VideoThumbnail = This Component to select thumbnail from video.

# preview

# how to install
For now you can add this component manually to your project, using :

`npm install --save https://github.com/RZulfikri/react-native-android-video-editor.git`

then linking component:

`react-native link react-native-android-video-editor`

# how to use
1. Video Trimmer

```
  import {VideoPlayerControl, VideoTrimmer}
  
  ...
  export default class trimViewo extends Components{
    state = {
      //example
      // src : "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4"
      src : "yout video source",    
    }
    
    _onCropListener = (e) => {
      console.log("IS CROP?"+e)
    }
    
    _onChangeTrim = (e) =>{
      console.log("START POS : "+e.startPos+" "+"END POS : "+e.endPos)
      this.videoPlayerControlRef.setLimit(e)
    }
    
    render(){
      return(
        <VideoPlayerControl
          ref={ref => this.videoPlayerControlRef = ref}
          src={this.state.src}
          startPos = {10000}
          endPos = {60000}
          onCrop = {(e) => this._onCropListener(e)}
         />
         <VideoTrimmer
          src={this.state.src}
          minDuration={60000}
          onChange = {(e) => this._onChangeTrim(e)}
        />
      );
    }
  }
```

2. Video Filter

```
  import {VideoPlayerControl, VideoImagePreview}
  
  ...
  export default class trimViewo extends Components{
    state = {
      //example
      // src : "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4"
      src : "yout video source",    
    }
    
    _onCropListener = (e) => {
      console.log("IS CROP?"+e)
    }
    
    _onFilter = (filter) =>{
      console.log("FILTER "+filter)
      this.videoPlayerControlRef.filterVideo(filter)
    }
    
    render(){
      return(
        <VideoPlayerControl
          ref={ref => this.videoPlayerControlRef = ref}
          src={this.state.src}
          startPos = {10000}
          endPos = {60000}
          onCrop = {(e) => this._onCropListener(e)}
         />
        <ScrollView horizontal = {true}>
            <TouchableWithoutFeedback onPress={() => this._onFilter(0)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={0}
              />
            </TouchableWithoutFeedback>

            <TouchableWithoutFeedback onPress={() => this._onFilter(1)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={1}
              />
            </TouchableWithoutFeedback>

            <TouchableWithoutFeedback onPress={() => this._onFilter(2)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={2}
              />
            </TouchableWithoutFeedback>

            <TouchableWithoutFeedback onPress={() => this._onFilter(3)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={3}
              />
            </TouchableWithoutFeedback>

            <TouchableWithoutFeedback onPress={() => this._onFilter(4)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={4}
              />
            </TouchableWithoutFeedback>

            <TouchableWithoutFeedback onPress={() => this._onFilter(5)}>
              <VideoImagePreview
                src={this.state.src}
                pos={10000}
                crop={true}
                filter={5}
              />
            </TouchableWithoutFeedback>
        </ScrollView
      );
    }
  }
```

3. Video Thumbnail

```
import {VideoPlayer, Video}
  
  ...
  export default class trimViewo extends Components{
    state = {
      //example
      // src : "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4"
      src : "yout video source",
      starPos : 10000,
      endPos : 60000,
    }
    
    _onSeekPos = (seekPos) =>{
      console.log("SEEK POS "+seekPos)
      this.videoPlayerRef.seek(seekPos + this.state.starPos)
    }
    
    render(){
      return(
         <VideoPlayer
            ref={ref => this.videoPlayerRef = ref}
            src={this.state.src}
            seekTo = {tis.state.starPos}
          />
          
          <VideoThumbnail
            src={this.state.src}
            style={{width: Dimensions.get('window').width, height: 62}}
            filter = {0}
            startDur = {this.state.starPos}
            endDur = {this.state.endPos}
            onSeekPos = {(e) => this._onSeekPos(e)}
        />
      );
    }
  }
```

# PROPS
1. Video Player

| Props | Type | Description |
| ----- | ----- | ----- |
| scr | String (required) | it use to set video path to video player example accepted path "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4". |
| crop | boolean | it use to set video croped square in center or no, (true  = crop / false = no crop). |
| autoplay | boolean | it use to set video autolay when it ready, (true = autoplay / false =no autoplay). |
| filter | int | it use to set filter to video (choose number between 0 - 19). |
| seekTo | int | it use to seek video player at first time (using time in milisecond 1000 = 1sec). |
| style | object | it uset to style video player (it have default style width and height). |

2. Video Player Control

| Props | Type | Description |
| ----- | ----- | ----- |
| scr | String (required) | it use to set video path to video player example accepted path "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4". |
| crop | boolean | it use to set video croped square in center or no, (true  = crop / false = no crop). |
| autoplay | boolean | it use to set video autolay when it ready, (true = autoplay / false =no autoplay). |
| filter | int | it use to set filter to video (choose number between 0 - 19). |
| style | object | it use to style video player (it have default style width and height). |
| startPos | int | it use to set start position of video (using time in milisecond 1000 = 1sec). |
| endPos | int | it use to set end position of video (using time in millisecond 1000 = 1sec). |

3. Video Image Preview

| Props | Type | Description |
| ----- | ----- | ----- |
| scr | String (required) | it use to set video path to video player example accepted path "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4". |
| pos | int | it use to set posisiton of video, that you want to view (using time in millisecond 1000 = 1sec). |
| crop | boolean | it use to set video croped square in center or no, (true  = crop / false = no crop). |
| filter | int | it use to set filter to video (choose number between 0 - 19). |
| style | object | it use to style video player (it have default style width and height). |

4. Video Trimmer

| Props | Type | Description |
| ----- | ----- | ----- |
| scr | String (required) | it use to set video path to video player example accepted path "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4". |
| minDuration | int | it use to set minimum tim duration (using time in milisecond 1000 = 1sec). |
| onChange | function | it use to callback left and right seek position (callback as object {startPos, endPos}). |
| style | object | it use to style video player (it have default style width and height). |

5. Video Thumbnail

| Props | Type | Description |
| ----- | ----- | ----- |
| scr | String (required) | it use to set video path to video player example accepted path "/storage/emulated/0/Download/10 MINUTES OF FUNNY CATS.mp4". |
| filter | int | it use to set filter to video (choose number between 0 - 19). |
| startDur | int | it use to set start position of video (using time in milisecond 1000 = 1sec). |
| endDur | int | it use to set end position of video (using time in millisecond 1000 = 1sec). |
| onSeekPos | int | it use to callback seek position of thumbnail selec (callback is video position as intger in millisecond). |
| style | object | it use to style video player (it have default style width and height). |

# Method
this method can be called using component ref like `this.videoPlayerRef.pause()`

1. VideoPlayer

| Method Name | Description |
| ----- | ----- |
| pause() | it use to pause video |
| play() | it use to play video |
| crop() | it use to crop video as square in the middle |
| uncrop() | it use to uncrop video |
| filter(int) | it use to set filter to video @param int (0 - 19) |
| seek(int) | it use to seek video @param int (time video in milisecond, 1000 = 1sec) |

2. VideoPlayerControl

| Method Name | Description |
| ----- | ----- |
| pauseVideo() | it use to pause video |
| playVideo() | it use to play video |
| cropVideo() | it use to crop video as square in the middle |
| unCropVideo() | it use to uncrop video |
| filterVideo(int) | it use to set filter to video @param int (0 - 19) |
| setLimit(object) | it use to set limit of video @param object {startPos, endPos} |

most of this method use as internal method, except setLimit(object).

# Video Filter Code

| Filter code | Filter Name |
| ----- | ----- |
| 0 | No Effect |
| 1 | Black White Effect |
| 2 | Brigtness Effect |
| 3 | Contrass Effect |
| 4 | Cross Process Effect |
| 5 | Documentary Effect |
| 6 | Duo Tone Effect (Cyan and Blue) |
| 7 | Fill Light Effect |
| 8 | Gamma Effect |
| 9 | GreyScale Effect |
| 10 | Hue Effect |
| 11 | Invert Color Effect |
| 12 | Lamoish Effect |
| 13 | Posterize Effect |
| 14 | Saturation Effect |
| 15 | Sephia Effect |
| 16 | Sharpness Effect |
| 17 | Temperature Effect |
| 18 | Tint Effect (Blue) |
| 19 | Vignette Effect |

# thanks to

1. <a href="https://github.com/krazykira/VidEffects/">Sheraz Ahmad Khilji</a>
2. <a href="https://code.tutsplus.com/tutorials/how-to-use-android-media-effects-with-opengl-es--cms-23650">code.tutplus.com</a>
3. <a href="https://www.virag.si/2014/03/rendering-video-with-opengl-on-android/">Jernej Virag </a>
