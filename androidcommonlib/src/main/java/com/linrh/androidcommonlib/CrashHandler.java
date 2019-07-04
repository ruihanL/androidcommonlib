package com.linrh.androidcommonlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private File dir = null;
    private HandleCallback mCallback = null;

    private UncaughtExceptionHandler mDefaultHandler;

    private Context mContext;

    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");


    public void init(Context context) throws FileNotFoundException {
        init(context, null, null);
    }

    public void init(Context context,File dir) throws FileNotFoundException {
        init(context, dir, null);
    }

    public void init(Context context, HandleCallback callback) throws FileNotFoundException {
        init(context, null, callback);
    }

    public File getDir() {
        return dir;
    }

    public void init(Context context, File dir, HandleCallback callback) throws FileNotFoundException {
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (dir != null) {
            this.dir = dir;
        } else {
            this.dir = StorageUtil.getDiskCacheDir(mContext, TAG);
        }

        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }

        if (!this.dir.isDirectory()) {
            throw new FileNotFoundException("dir is not exist.");
        }

        if (callback != null) {
            this.mCallback = callback;
        } else {
            this.mCallback = new DefaultHandleCallback(mContext);
        }
    }

    public interface HandleCallback {
        boolean handleException(Throwable ex);

    }

    public class DefaultHandleCallback implements HandleCallback {
        Context mContext = null;

        public DefaultHandleCallback(Context context) {
            mContext = context;
        }

        @Override
        public boolean handleException(Throwable e) {

            collectDeviceInfo(mContext);

            saveCrashInfo2File(e);

            e.printStackTrace();

            return true;
        }
    }

    ;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }


    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }

        if (this.mCallback == null) {
            return false;
        }

        return mCallback.handleException(e);
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String time = formatter.format(new Date());
            String fileName = time + ".txt";

            FileOutputStream fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.close();

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}