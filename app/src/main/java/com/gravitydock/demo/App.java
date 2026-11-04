package com.gravitydock.demo;

import android.app.Application;
import android.util.Log;

import com.gravitydock.sdk.GravityDockSDK;
import com.gravitydock.sdk.GravityDockSDKListener;

public class App extends Application {

    private static final String TAG = "GravityDockDemo";

    @Override
    public void onCreate() {
        super.onCreate();
        // 打开 SDK 日志，便于观察各模块调用与事件分发
        GravityDockSDK.setLogEnabled(true);
        GravityDockSDK.initSDK(this, new GravityDockSDKListener() {
            @Override
            public void onAppExit() {
                Log.w(TAG, "应用退出");
            }

            @Override
            public void onInitSuccess() {
                Log.i(TAG, "SDK 初始化成功");
            }

            @Override
            public void onInitFail(String error) {
                Log.e(TAG, "SDK 初始化失败: " + error);
            }
        });
    }
}
