package com.linrh.androidcommonlibs;

import android.app.Activity;
import android.os.Bundle;


import com.linrh.androidcommonlib.UpFileUtil;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpFileUtil.uploadFile(new File("/sdcard/gnss"),new UpFileUtil.Callback() {
            @Override
            public void onResponse(String downloadLink) {
                System.out.println(downloadLink + "");
            }

            @Override
            public void onProgress(int per) {
                System.out.println(per + "");
            }
        });


        int i = 9/0;
    }
}
