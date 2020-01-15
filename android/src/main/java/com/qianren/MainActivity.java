package com.qianren;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.qianren.adapter.ViewPagerAdapter;
import com.qianren.entity.AlbumInfo;
import com.qianren.entity.PhotoInfo;
import com.qianren.album_picker.R;
import com.qianren.util.CheckImageLoaderConfiguration;
import com.qianren.util.UniversalImageLoader;

import java.io.IOException;
import java.util.List;

/**
 * 选择图片的界面，继承自ActionBarActivity，为了与主工程的主题保持一致。<br>
 * 实现了两个自定义接口，接收列表中条目被点击的响应事件
 *
 * @author ghc
 */
public class MainActivity extends AppCompatActivity
        implements ViewPagerAdapter.OnStickerClickListener {

    private static final String TAG = "MainActivity";
    //    private EditPhotoFragment mEditPhotoFragment;
    private FragmentManager mFragmentManager;
    private int mEditTag = 0;

    public final static String EDIT_PIC_PATH = "com.select.pic.edit";

    /**
     * actionItem的宽度
     */
    public static int iAcionWidth = 0;
    /**
     * actionItem的高度
     */
    public static int iActionHeight = 0;
    public static String COUNT = "count";

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final static int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MainActivity", "onCreate() >> ");

        Bundle savedInstanceStates = null;
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.select_photo_main);

        if (savedInstanceState == null && mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (checkPermission()) {
            Log.d("MainActivity", "init..");
            initData();
            initView();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        UniversalImageLoader.clearMemoryCache();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        if (getIntent().hasExtra(COUNT)) {
            Constants.MAX_SELECT_COUNT = getIntent().getIntExtra(COUNT, 1);
        }

        if (getIntent().hasExtra("edit_photo")) {
            mEditTag = getIntent().getIntExtra("edit_photo", 0);
        }

        int i = getIntent().getIntExtra("sticker", 0);
        if (i == 1) {

        }
    }

    private void initView() {
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setFullScreen(false);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * GridView的Item点击的事件响应--图片列表的点击事件
     */

    /**
     * ListView的Item点击的事件响应--相册列表的点击事件
     */

    public boolean removeFragment(Fragment fragment) {
        if (null == fragment) return false;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().popBackStack();
        return true;
    }

    public void setFullScreen(boolean noTitle) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (noTitle) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private boolean resetDataStatus(AlbumInfo aInfo) {
        List<PhotoInfo> pInfos = aInfo.getPhotoList();
        for (int i = 0; i < pInfos.size(); i++) {
            pInfos.get(i).isSelected = false;
            pInfos.get(i).isOriginal = false;
        }
        return true;
    }

    @Override
    public void onStickerClickListener(String path) {

    }

    @Override
    public void onDeleteTouchListener(String path) {

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                initData();
                initView();
            } else {
                handlePermissionsFailure();
            }
        }
    }

    private void handlePermissionsFailure() {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.camera_error_title))
                .setMessage(getResources().getString(R.string.error_permissions))
                .setCancelable(false)
                .setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finish();
                        }
                        return true;
                    }
                })
                .setPositiveButton(getResources().getString(R.string.dialog_open_permission),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermission();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.dialog_dismiss),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .show();
    }
}
