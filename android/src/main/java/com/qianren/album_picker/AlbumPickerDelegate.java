package com.qianren.album_picker;

import android.app.Activity;
import android.content.Intent;

import java.util.logging.Handler;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class AlbumPickerDelegate implements PluginRegistry.ActivityResultListener,PluginRegistry.RequestPermissionsResultListener {
    private final Activity activity;
    private Handler handler = null;
    public static MethodChannel.Result result = null;

    public AlbumPickerDelegate(Activity activity){
        this.activity = activity;
    }

    public void pickFile(){
        System.out.println("Android call pickFile()");
    }

    void saveStateBeforeResult() {

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
