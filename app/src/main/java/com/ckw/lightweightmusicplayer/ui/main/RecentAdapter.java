package com.ckw.lightweightmusicplayer.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.RecentBean;

import java.util.List;

/**
 * Created by ckw
 * on 2018/5/10.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    private List<RecentBean> mData;
    private Context mContext;

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public RecentAdapter(List<RecentBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setData(List<RecentBean> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RecentBean recentBean = mData.get(position);
        holder.tvSongName.setText(recentBean.getTitle());
        holder.tvSongArtist.setText(recentBean.getArtist());
        String album = recentBean.getAlbum();

        if(album != null && !"".equals(album)){
            Glide.with(mContext).load(album)
                    .into(holder.ivAlbum);
        }else {
            holder.ivAlbum.setImageResource(R.mipmap.ic_music_default);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.setOnItemClick(position,v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAlbum;
        private TextView tvSongName;
        private TextView tvSongArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvSongArtist = itemView.findViewById(R.id.tv_song_artist);
        }
    }



}
