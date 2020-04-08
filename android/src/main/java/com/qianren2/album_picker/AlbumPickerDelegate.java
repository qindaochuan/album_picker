package com.qianren2.album_picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;

import java.lang.ref.WeakReference;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class AlbumPickerDelegate implements PluginRegistry.ActivityResultListener,PluginRegistry.RequestPermissionsResultListener {
    private final Activity activity;
    private Handler handler = null;
    public static MethodChannel.Result result = null;

    private static final int HANDLER_ALBUM_PICK = 1;

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
                System.out.println("AlbumPicker startActivity: MainActivity");
                Matisse.from(activity)
                        .choose(MimeType.ofImage())
                        .theme(R.style.Matisse_Dracula)
                        .countable(false)
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .maxSelectable(9)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .imageEngine(new GlideEngine())
                        .forResult();
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
}
