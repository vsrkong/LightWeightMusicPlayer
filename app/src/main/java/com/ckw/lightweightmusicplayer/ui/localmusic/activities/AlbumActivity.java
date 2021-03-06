package com.ckw.lightweightmusicplayer.ui.localmusic.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.MusicListAdapter;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalAlbumViewHolder;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalSongViewHolder;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;
import com.ckw.lightweightmusicplayer.utils.RecentUtils;
import com.ckw.lightweightmusicplayer.weight.cover_view.MusicCoverView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ckw
 * on 2018/3/14.
 * 专辑 详情界面
 */

public class AlbumActivity extends BaseActivity {

    @BindView(R.id.iv_album)
    ImageView mPhotoAlbum;
    @BindView(R.id.ctl_album)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.rv_album)
    EasyRecyclerView mEasyRecyclerView;

    private RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> mAdapter;

    private List<MediaBrowserCompat.MediaItem> mSongs;

    private String mediaId;
    private String mAlbumTitle;
    private String mAlbumUri;

    @Override
    protected void initView(Bundle savedInstanceState) {
        initRecyclerView();

        if(mAlbumUri != null ){
            Glide.with(this).load(mAlbumUri).into(mPhotoAlbum);
        }

        if(mAlbumTitle != null){
            mCollapsingToolbarLayout.setTitle(mAlbumTitle);
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        }

    }

    @Override
    protected void onMediaBrowserConnected() {
        super.onMediaBrowserConnected();
        boolean connected = mMediaBrowser.isConnected();
        mediaId = MediaIdHelper.MEDIA_ID_ALBUM_DETAIL +"&&"+mAlbumTitle;
        if(connected){
            mMediaBrowser.unsubscribe(mediaId);
            mMediaBrowser.subscribe(mediaId,mSubscriptionCallback);
        }
    }

    /*
     * 浏览器订阅的接口，数据的回调
     * */
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            mSongs.clear();
            mSongs.addAll(children);
            mAdapter.clear();
            mAdapter.addAll(children);
            mAdapter.notifyDataSetChanged();

        }
    };

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {
        mAlbumTitle = bundle.getString("albumTitle");
        mAlbumUri = bundle.getString("albumUri");
    }

    @Override
    protected void initVariable() {
        mSongs = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    protected void initListener() {
        if(mAdapter != null){
            mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if(mSongs != null){

//                        RecentUtils.addToRecent(mSongs.get(position));

                        Uri iconUri = mSongs.get(position).getDescription().getIconUri();

                        Bundle bundle = new Bundle();
                        bundle.putString("musicId",mSongs.get(position).getMediaId());
                        if(iconUri != null){
                            bundle.putString("iconUri",iconUri.toString());
                        }
                        bundle.putBoolean("play",true);
                        ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
                    }
                }
            });
        }
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    public void setToolbar() {

    }


    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mEasyRecyclerView.setLayoutManager(linearLayoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, SizeUtils.dp2px(1), SizeUtils.dp2px(8),0);
        itemDecoration.setDrawLastItem(false);
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new MusicListAdapter(this);
        mEasyRecyclerView.setAdapter(mAdapter);
    }
}
