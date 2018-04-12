package com.ckw.lightweightmusicplayer.ui.playmusic.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.playmusic.MutableMediaMetadata;
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ckw
 * on 2018/3/19.
 * 提供本地数据
 */

public class MusicProvider {

    private SongSource mSongSource;

    private List<MediaMetadataCompat> mLocalMusicList;//本地所有的音乐

    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByAlbum;//本地专辑集合

    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;//key是musicId

    
    public MusicProvider(Context context) {
        //提供本地数据
        mSongSource = new LocalSongSource(context);
        mLocalMusicList = new ArrayList<>();
        mMusicListById = new ConcurrentHashMap<>();
        mMusicListByAlbum = new ConcurrentHashMap<>();
    }

    public List<MediaMetadataCompat> getLocalMusic() {
        return mLocalMusicList;
    }

    /**
     * Return the MediaMetadataCompat for the given musicID.
     *
     * @param musicId The unique, non-hierarchical music ID.
     */
    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    /**
     * Get music tracks of the given albumTitle
     *
     */
    public List<MediaMetadataCompat> getMusicsByAlbum(String album) {
        return mMusicListByAlbum.get(album);
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMusic(musicId);
        metadata = new MediaMetadataCompat.Builder(metadata)

                // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                // example, on the lockscreen background when the media session is active.
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

                // set small version of the album art in the DISPLAY_ICON. This is used on
                // the MediaDescription and thus it should be small to be serialized if
                // necessary
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)

                .build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    /*
    * 异步获取本地音乐
    * */
    @SuppressLint("StaticFieldLeak")
    public String retrieveMediaAsync(){
        final String[] retrieveState = new String[1];
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                retrieveLocalMedia();
                retrieveState[0] = "success";
                return retrieveState[0];
            }
        }.execute();

        return retrieveState[0];
    }

    /*
    * 获取本地音乐
    * */
    private synchronized void retrieveLocalMedia(){
        mLocalMusicList = mSongSource.getLocalList();
        buildListByAlbum();
        Iterator<MediaMetadataCompat> tracks = mSongSource.iterator();
        while (tracks.hasNext()) {
            MediaMetadataCompat item = tracks.next();
            String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
        }
    }

    /*
     * 将歌曲通过专辑类别 分类，返回一个（类别：list）的map
     * */
    private void buildListByAlbum() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newMusicListByAlbum = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String albumTitle = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            List<MediaMetadataCompat> list = newMusicListByAlbum.get(albumTitle);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByAlbum.put(albumTitle, list);
            }
            list.add(m.metadata);
        }
        mMusicListByAlbum = newMusicListByAlbum;
    }

    /**
     * Get an iterator over the list of genres
     *
     */
    public Iterable<String> getAlbumList() {
        return mMusicListByAlbum.keySet();
    }


    /*
    * 返回本地音乐数据
    * */
    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId){
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        //所有的音乐
        if(MediaIdHelper.MEDIA_ID_NORMAL.equals(mediaId)){
            for (MediaMetadataCompat mediaMetadataCompat: mLocalMusicList) {
                mediaItems.add(createMediaItem(mediaMetadataCompat));
            }
        }else if(MediaIdHelper.MEDIA_ID_ALBUM.equals(mediaId)){
            for (String albumTitle :
                    getAlbumList()) {
                mediaItems.add(createAlbumMediaItem(albumTitle));
            }
        }
        
        return mediaItems;
    }


    /*
    * 构建专辑数据
    * */
    private MediaBrowserCompat.MediaItem createAlbumMediaItem(String albumTitle){
        List<MediaMetadataCompat> albumList = mMusicListByAlbum.get(albumTitle);
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(albumTitle)
                .setTitle(albumTitle)
                .setSubtitle(String.valueOf(albumList.size()))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /*
    * 构建单个数据
    * */
    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {

        String hierarchyAwareMediaID = metadata.getDescription().getMediaId();
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }



}
