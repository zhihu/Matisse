![Image](/image/banner.png)

# Matisse
[ ![Download](https://api.bintray.com/packages/felix0503/maven/matisse/images/download.svg) ](https://bintray.com/felix0503/maven/matisse/_latestVersion)  

Base on [Matisse](https://github.com/zhihu/Matisse),add crop image function and fix some bug,Used in tongzhuogame

## Download
Gradle:

```groovy
compile 'com.github.chaichuanfa:matisse:1.0.2'
``` 

## Add Themes
`R.style.Matisse_Tongzhuo` (custom mode)   
 
And Also you can define your own theme as you wish.

## Add single image crop
```Java
Matisse.from(SampleActivity.this)
    .choose(MimeType.ofStaticImage())
    .theme(R.style.Matisse_Tongzhuo)
    .countable(false)
    .maxSelectable(1)
    .spanCount(3)
    .thumbnailScale(0.85f)
    .singleMediaPreview(false)
    .singleImageCrop(true)
    .showSingleMediaType(true)
    .imageEngine(new PicassoEngine())
    .forResult(REQUEST_CODE_CHOOSE);
```
single static image can crop and close preview.

## Add open camera right now
```Java
Matisse.from(EditProfileFragment.this)
    .choose(MimeType.ofStaticImage())
    .openCameraNow(true)
    .capture(true)
    .captureStrategy(new CaptureStrategy(true,
            "com.tongzhuo.tongzhuogame.fileprovider"))
    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    .theme(R.style.Matisse_Tongzhuo)
    .singleImageCrop(true)
    .forResult(OPEN_CAMERA_REQUEST_CODE);
```

## More
Find more details about Matisse in [wiki](https://github.com/zhihu/Matisse/wiki).    
Zhuhu [README.md](https://github.com/zhihu/Matisse/blob/master/README.md)

## Thanks
* [Matisse](https://github.com/zhihu/Matisse)
* [Glide](https://github.com/bumptech/glide)
* [Picasso](https://github.com/square/picasso)
* [SimpleCropView](https://github.com/IsseiAoki/SimpleCropView)
