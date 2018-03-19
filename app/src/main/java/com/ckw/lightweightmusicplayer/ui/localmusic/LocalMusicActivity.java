package com.ckw.lightweightmusicplayer.ui.localmusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.LocalMusicAdapter;
import com.ckw.lightweightmusicplayer.ui.localmusic.fragments.LocalAlbumFragment;
import com.ckw.lightweightmusicplayer.ui.localmusic.fragments.LocalMusicListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

/**
 * Created by ckw
 * on 2018/3/12.
 */

public class LocalMusicActivity extends BaseActivity {

    @BindView(R.id.coordinatortablayout)
    CoordinatorTabLayout mCoordinatorTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private List<Fragment> mFragments;

    private final String[] mTitles = {"歌曲", "专辑", "歌手"};
    private int[] mImageArray, mColorArray;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new LocalMusicAdapter(getSupportFragmentManager(), mFragments, mTitles));

        mCoordinatorTabLayout
                .setTitle("本地音乐")
                .setBackEnable(true)
                .setImageArray(mImageArray, mColorArray)
                .setupWithViewPager(mViewPager);
    }


    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
        mFragments = new ArrayList<>();

        LocalMusicListFragment localSongFragment  = LocalMusicListFragment.newInstance(mTitles[0]);
        LocalAlbumFragment localAlbumFragment = LocalAlbumFragment.newInstance();
        LocalMusicListFragment localArtistFragment  = LocalMusicListFragment.newInstance(mTitles[2]);

        mFragments.add(localSongFragment);
        mFragments.add(localAlbumFragment);
        mFragments.add(localArtistFragment);

        mImageArray = new int[]{
                R.drawable.bg_music,
                R.drawable.bg_music,
                R.drawable.bg_music};
        mColorArray = new int[]{
                R.color.colorDark,
                R.color.colorDark,
                R.color.colorDark,};

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local_music;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这边由于CoordinatorTabLayout自带的toolbar与baseActivity系列有冲突，使用CoordinatorTabLayout的
        //但是使用它的之后，返回键不起作用，只能自己重写了
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean needToolbar() {
        return false;
    }

    @Override
    public void setToolbar() {
    }


}