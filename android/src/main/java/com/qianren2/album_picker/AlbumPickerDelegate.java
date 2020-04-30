package com.qianren2.album_picker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.vincent.videocompressor.VideoCompress;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class AlbumPickerDelegate implements PluginRegistry.ActivityResultListener,PluginRegistry.RequestPermissionsResultListener {
    private final static String TAG = "AlbumPickerDelegate";
    private final Activity activity;
    private Handler handler = null;
    public static MethodChannel.Result result = null;

    private static final int HANDLER_ALBUM_PICK = 1;
    private static final int HANDLER_VIDEO_COMPRESS = 2;

    private PictureParameterStyle mPictureParameterStyle;
    private PictureCropParameterStyle mCropParameterStyle;
    private ProgressDialog progressDialog = null;
    private ProgressDialog progressDialog2 = null;
    private String srcPath = null;

    public AlbumPickerDelegate(Activity activity){
        this.activity = activity;
        this.handler = new MyHandler(activity,this);
    }

    public void pickFile(MethodCall call, MethodChannel.Result result){
        System.out.println("Android call pickFile()");
        this.result = result;
        Message msg = new Message();
        msg.what = HANDLER_ALBUM_PICK;
        handler.sendMessage(msg);
    }

    public void videoCompress(MethodCall call, MethodChannel.Result result){
        System.out.println("Android call videoCompress(...)");
        srcPath = call.argument("srcPath");
        System.out.println("srcPath = " + srcPath);
        this.result = result;
        Message msg = new Message();
        msg.what = HANDLER_VIDEO_COMPRESS;
        handler.sendMessage(msg);
    }

    void saveStateBeforeResult() {

    }

    public static class MyHandler extends Handler{
        WeakReference<Activity> mActivity;
        WeakReference<AlbumPickerDelegate> mInstance;

        MyHandler(Activity activity,AlbumPickerDelegate intance){
            mActivity = new WeakReference<>(activity);
            mInstance = new WeakReference<>(intance);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlbumPickerDelegate theInstance = mInstance.get();
            switch (msg.what){
                case HANDLER_ALBUM_PICK:
                    theInstance.doAlbumPick();
                    break;
                case HANDLER_VIDEO_COMPRESS:
                    theInstance.doVideoCompress();
                    break;
            }
        }
    }

    public void doAlbumPick(){
        System.out.println("doAlbumPick()");
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                getWeChatStyle();
                System.out.println("AlbumPicker startActivity: MainActivity");
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(activity)
                        .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                        .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                        .isWeChatStyle(true)// 是否开启微信图片选择风格
                        .isUseCustomCamera(false)// 是否使用自定义相机
                        .setLanguage(LanguageConfig.TRADITIONAL_CHINESE)// 设置语言，默认中文
                        .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                        .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                        .setPictureWindowAnimationStyle(new PictureWindowAnimationStyle())// 自定义相册启动退出动画
                        .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                        .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                        //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                        //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                        .maxSelectNum(5)// 最大图片选择数量
                        .minSelectNum(0)// 最小选择数量
                        .maxVideoSelectNum(5) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
                        //.minVideoSelectNum(1)// 视频最小选择数量，如果没有单独设置的需求则可以不设置，同用minSelectNum字段
                        .maxMultipleSelectNum(5)
                        .imageSpanCount(4)// 每行显示个数
                        .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                        //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && enableCrop(false);有效,默认处理
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 设置相册Activity方向，不设置默认使用系统
                        .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                        //.bindCustomPlayVideoCallback(callback)// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                        //.bindPictureSelectorInterfaceListener(interfaceListener)// 提供给用户的一些额外的自定义操作回调
                        //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                        //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 注意这个不要重复，只适用于单张图压缩使用
                        //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 注意这个不要重复，只适用于单张图裁剪使用
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                        .enablePreviewAudio(false) // 是否可播放音频
                        .isCamera(false)// 是否显示拍照按钮
                        //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                        //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .enableCrop(false)// 是否裁剪
                        //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                        .compress(false)// 是否压缩
                        //.compressQuality(80)// 图片压缩后输出质量 0~ 100
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                        //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
                        .withAspectRatio(16, 9)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                        .isGif(true)// 是否显示gif图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .circleDimmedLayer(true)// 是否圆形裁剪
                        //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                        //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                        //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(null)// 是否传入已选图片
                        //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                        //.videoMinSecond(10)
                        //.videoMaxSecond(15)
                        //.recordVideoSecond(10)//录制视频秒数 默认60s
                        //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                        .cutOutQuality(90)// 裁剪输出质量 默认100
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                        //.rotateEnabled(false) // 裁剪是否可旋转图片
                        //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                        //.videoQuality()// 视频录制质量 0 or 1
                        //.videoSecond()//显示多少秒以内的视频or音频也可适用
                        //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                String path = null;
                                List<String> imagePaths = new ArrayList<String>();
                                List<String> videoPaths = new ArrayList<String>();
                                for (LocalMedia media : result) {
                                    Log.i(TAG, "是否压缩:" + media.isCompressed());
                                    Log.i(TAG, "压缩:" + media.getCompressPath());
                                    Log.i(TAG, "原图:" + media.getPath());
                                    Log.i(TAG, "是否裁剪:" + media.isCut());
                                    Log.i(TAG, "裁剪:" + media.getCutPath());
                                    Log.i(TAG, "是否开启原图:" + media.isOriginal());
                                    Log.i(TAG, "原图路径:" + media.getOriginalPath());
                                    Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                                    Log.i(TAG, "Size: " + media.getSize());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                        path = media.getAndroidQToPath();
                                    }else{
                                        path = media.getPath();
                                    }
                                    int dot = path.lastIndexOf('.');
                                    String ext = path.substring(dot + 1);
                                    if(ext.equals("mp4") || ext.equals("MP4")){
                                        videoPaths.add(path);
                                    }else{
                                        imagePaths.add(path);
                                    }
                                }
                                if(videoPaths.size() >= 1){
                                    multipleVideoCompress(imagePaths,videoPaths);
                                }else{
                                    AlbumPickerDelegate.result.success(imagePaths);
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.i(TAG, "PictureSelector Cancel");
                            }
                        });
                Looper.loop();
            }
        }.start();
    }

    void multipleVideoCompress(List<String> imagePaths, List<String> videoPaths){
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("");
                progressDialog.setMessage("视频压缩中...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            //progressDialog.dismiss();
                        }
                        return false;
                    }
                });
                Looper.loop();
            }
        }.start();

        List<String> destPaths = new ArrayList<String>();
        SerialExecutor serialExecutor = new SerialExecutor();
        for (int i = 0; i < videoPaths.size(); i ++) {
            final int j = i;
            serialExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String srcPath = videoPaths.get(j);
                    int dot = srcPath.lastIndexOf('.');
                    final String destPath = srcPath.substring(0, dot) + "_compress" + srcPath.substring(dot);
                    destPaths.add(destPath);
                    VideoCompress.compressVideoLow(srcPath, destPath, new VideoCompress.CompressListener() {

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess() {
                            if (j == videoPaths.size() - 1) {
                                Handler thisHandler = new Handler(Looper.getMainLooper());
                                thisHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                        List<String> tempList = new ArrayList<String>();
                                        tempList.addAll(imagePaths);
                                        tempList.addAll(destPaths);
                                        result.success(tempList);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFail() {

                        }

                        @Override
                        public void onProgress(float percent) {

                        }
                    });
                }
            });
        }
    }

    public void doVideoCompress(){
        System.out.println("doVideoCompress()");
        if(!(srcPath != null && !srcPath.equals(""))){
            return;
        }
        int dot = srcPath.lastIndexOf('.');
        final String destPath = srcPath.substring(0,dot) + "_compress" + srcPath.substring(dot);
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                progressDialog2 = new ProgressDialog(activity);
                progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog2.setTitle("");
                progressDialog2.setMessage("视频压缩中...");
                progressDialog2.setIndeterminate(false);
                progressDialog2.setCancelable(false);
                progressDialog2.show();
                progressDialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            //progressDialog2.dismiss();
                        }
                        return false;
                    }
                });
                VideoCompress.compressVideoLow(srcPath, destPath, new VideoCompress.CompressListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        Handler thisHandler = new Handler(Looper.getMainLooper());
                        thisHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (progressDialog2 != null) {
                                    progressDialog2.cancel();
                                }
                                result.success(destPath);
                            }
                        });
                    }

                    @Override
                    public void onFail() {

                    }

                    @Override
                    public void onProgress(float percent) {

                    }
                });
                Looper.loop();
            }
        }.start();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }

    private void getWeChatStyle() {
        // 相册主题
        mPictureParameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        mPictureParameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        mPictureParameterStyle.isOpenCheckNumStyle = true;
        // 状态栏背景色
        mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#393a3e");
        // 相册列表标题栏背景色
        mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e");
        // 相册父容器背景色
        mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(activity, R.color.app_color_black);
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_wechat_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_wechat_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_icon_close;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(activity, R.color.picture_color_white);
        // 相册右侧按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureCancelTextColor = ContextCompat.getColor(activity, R.color.picture_color_53575e);
        // 相册右侧按钮字体默认颜色
        mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(activity, R.color.picture_color_53575e);
        // 相册右侧按可点击字体颜色,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureRightSelectedTextColor = ContextCompat.getColor(activity, R.color.picture_color_white);
        // 相册右侧按钮背景样式,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureUnCompleteBackgroundStyle = R.drawable.picture_send_button_default_bg;
        // 相册右侧按钮可点击背景样式,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureCompleteBackgroundStyle = R.drawable.picture_send_button_bg;
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.picture_wechat_num_selector;
        // 相册标题背景样式 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatTitleBackgroundStyle = R.drawable.picture_album_bg;
        // 微信样式 预览右下角样式 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatChooseStyle = R.drawable.picture_wechat_select_cb;
        // 相册返回箭头 ,只针对isWeChatStyle 为true时有效果
        mPictureParameterStyle.pictureWeChatLeftBackStyle = R.drawable.picture_icon_back;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(activity, R.color.picture_color_grey);
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(activity, R.color.picture_color_white);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(activity, R.color.picture_color_9b);
        // 相册列表已完成色值(已完成 可点击色值)
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(activity, R.color.picture_color_white);
        // 相册列表未完成色值(请选择 不可点击色值)
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(activity, R.color.picture_color_53575e);
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(activity, R.color.picture_color_half_grey);
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(activity, R.color.app_color_white);
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        mPictureParameterStyle.pictureNavBarColor = Color.parseColor("#393a3e");

        // 完成文案是否采用(%1$d/%2$d)的字符串，只允许两个占位符哟
//        mPictureParameterStyle.isCompleteReplaceNum = true;
        // 自定义相册右侧文本内容设置
//        mPictureParameterStyle.pictureUnCompleteText = getString(R.string.app_wechat_send);
        //自定义相册右侧已选中时文案 支持占位符String 但只支持两个 必须isCompleteReplaceNum为true
//        mPictureParameterStyle.pictureCompleteText = getString(R.string.app_wechat_send_num);
//        // 自定义相册列表不可预览文字
//        mPictureParameterStyle.pictureUnPreviewText = "";
//        // 自定义相册列表预览文字
//        mPictureParameterStyle.picturePreviewText = "";
//        // 自定义预览页右下角选择文字文案
//        mPictureParameterStyle.pictureWeChatPreviewSelectedText = "";

//        // 自定义相册标题文字大小
//        mPictureParameterStyle.pictureTitleTextSize = 9;
//        // 自定义相册右侧文字大小
//        mPictureParameterStyle.pictureRightTextSize = 9;
//        // 自定义相册预览文字大小
//        mPictureParameterStyle.picturePreviewTextSize = 9;
//        // 自定义相册完成文字大小
//        mPictureParameterStyle.pictureCompleteTextSize = 9;
//        // 自定义原图文字大小
//        mPictureParameterStyle.pictureOriginalTextSize = 9;
//        // 自定义预览页右下角选择文字大小
//        mPictureParameterStyle.pictureWeChatPreviewSelectedTextSize = 9;

        // 裁剪主题
        mCropParameterStyle = new PictureCropParameterStyle(
                ContextCompat.getColor(activity, R.color.app_color_grey),
                ContextCompat.getColor(activity, R.color.app_color_grey),
                Color.parseColor("#393a3e"),
                ContextCompat.getColor(activity, R.color.app_color_white),
                mPictureParameterStyle.isChangeStatusBarFontColor);
    }
}
