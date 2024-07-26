package com.example.musicplayerapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<AudioModel> songList;
    Context context;
    SongClickListener songClickListener;

    public MusicListAdapter(ArrayList<AudioModel> songList, Context context, SongClickListener songClickListener) {
        this.songList = songList;
        this.context = context;
        this.songClickListener = songClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AudioModel songData = songList.get(position);
        ((ViewHolder) holder).textView.setText(songData.getTitle());
        ((ViewHolder) holder).total_time.setText(convertToMMSS(songData.getDuration()));

//        if (MyMediaPlayer.currentIndex == position) {
//            ((ViewHolder) holder).textView.setTextColor(Color.parseColor("#FF0000"));
//            ((ViewHolder) holder).total_time.setTextColor(Color.parseColor("#FF0000"));
//        } else {
//            ((ViewHolder) holder).textView.setTextColor(Color.parseColor("#191414"));
//        }


        holder.itemView.setOnClickListener(v -> songClickListener.onSongClick(position));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    private static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        );
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, total_time;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_music);
            total_time = itemView.findViewById(R.id.total_time);
            imageView = itemView.findViewById(R.id.music_image);
        }
    }

    public interface SongClickListener {
        void onSongClick(int position);
    }
}
