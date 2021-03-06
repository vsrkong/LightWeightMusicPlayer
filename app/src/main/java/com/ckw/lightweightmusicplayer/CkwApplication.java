package com.ckw.lightweightmusicplayer;

import com.blankj.utilcode.util.Utils;
import com.ckw.lightweightmusicplayer.di.AppComponent;
import com.ckw.lightweightmusicplayer.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by ckw
 * on 2018/3/7.
 */

public class CkwApplication extends DaggerApplication{

    private AppComponent mAppComponent;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        mAppComponent = DaggerAppComponent.builder().application(this).build();
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
