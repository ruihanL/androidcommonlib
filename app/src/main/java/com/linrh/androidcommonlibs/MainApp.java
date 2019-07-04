package com.linrh.androidcommonlibs;

import android.app.Application;


import com.linrh.androidcommonlib.CrashHandler;

import java.io.FileNotFoundException;

/**
 * 作者：created by @author{ John } on 2019/7/4 0004上午 10:32
 * 描述：
 * 修改备注：
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        CrashHandler crashHandler = new CrashHandler();
        try {
            crashHandler.init(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
