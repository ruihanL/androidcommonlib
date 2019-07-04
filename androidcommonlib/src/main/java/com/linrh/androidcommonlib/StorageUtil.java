package com.linrh.androidcommonlib;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;

/**
 * 作者：created by @author{ John } on 2019/7/4 0004上午 9:59
 * 描述：
 * 修改备注：
 */

public class StorageUtil {

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     * external：如：/storage/emulated/0/Android/data/package_name/cache
     * internal 如：/data/data/package_name/cache
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }


    public static File getDiskCacheDir(Context context) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath();
        return new File(cachePath);
    }


    /**
     * 过滤列举出符合的所有文件
     * @param dir 目录
     * @param pix 后缀名
     * @return 文件列表
     */
    public File[] getFiles(File dir, final String pix) {
        File[] fs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                System.out.println(pathname);
                return pathname.isFile() && pathname.getName().endsWith(pix);
            }
        });

        return fs;
    }







}
