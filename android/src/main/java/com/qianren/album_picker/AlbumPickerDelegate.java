package com.qianren.album_picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import com.qianren.album_picker.R;

public class AlbumPickerDelegate implements PluginRegistry.ActivityResultListener,PluginRegistry.RequestPermissionsResultListener {
    private final Activity activity;
    private Handler handler = null;
    public static MethodChannel.Result result = null;

    private static final int HANDLER_ALBUM_PICK = 1;

    public AlbumPickerDelegate(Activity activity){
        this.activity = activity;
        this.handler = new MyHandler(activity,this);

        System.out.println("R.id.album_count_tv = " + R.id.album_count_tv);
    }

    public void pickFile(MethodCall call, MethodChannel.Result result){
        System.out.println("Android call pickFile()");
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
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                Looper.loop();
            }
        };
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
