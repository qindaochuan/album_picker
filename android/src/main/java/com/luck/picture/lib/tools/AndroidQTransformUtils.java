package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.config.PictureSelectionConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;

import okio.BufferedSource;
import okio.Okio;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q相关处理类
 */
public class AndroidQTransformUtils {

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param uri
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String copyPathToAndroidQ(Context ctx, String url, int width, int height, String mineType, String customFileName, String fileName) {
        // 这里就是利用图片加载引擎的特性，因为图片加载器加载过了图片本地就有缓存，当然前提是用户设置了缓存策略
        if (PictureSelectionConfig.cacheResourcesEngine != null) {

            String cachePath = PictureSelectionConfig.cacheResourcesEngine.onCachePath(ctx, url);
            if (!TextUtils.isEmpty(cachePath)) {
                String ext = getExtensionName(fileName);
                String path = getFileNameNoEx(cachePath);
                String newPath = path + "." + ext;

                FileChannel inputChannel = null;
                FileChannel outputChannel = null;
                try {
                    inputChannel = new FileInputStream(new File(cachePath)).getChannel();
                    outputChannel = new FileOutputStream(new File(newPath)).getChannel();
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    inputChannel.close();
                    outputChannel.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return newPath;
            }
        }

        // 走普通的文件复制流程，拷贝至应用沙盒内来
        BufferedSource inBuffer = null;
        try {
            Uri uri = Uri.parse(url);
            String encode = DESUtils.encode(DESUtils.DES_KEY_STRING, url, width, height);
            String newPath = PictureFileUtils.createFilePath(ctx, encode, mineType, customFileName);
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            inBuffer = Okio.buffer(Okio.source(Objects.requireNonNull(ctx.getContentResolver().openInputStream(uri))));
            boolean copyFileSuccess = PictureFileUtils.bufferCopy(inBuffer, outFile);
            if (copyFileSuccess) {
                return newPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inBuffer != null && inBuffer.isOpen()) {
                PictureFileUtils.close(inBuffer);
            }
        }
        return null;
    }

    /**
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inFile
     * @param outUri
     */
    public static boolean copyPathToDCIM(Context context, File inFile, Uri outUri) {
        try {
            OutputStream fileOutputStream = context.getContentResolver().openOutputStream(outUri);
            return PictureFileUtils.bufferCopy(inFile, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
