package com.example.musicplayerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textView, mediaTitle;
    ImageView mediaImage, mediaPlayPause, mediaNext,mediaPrevious;
    ArrayList<AudioModel> songList = new ArrayList<>();
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    AudioModel currentSong;
    int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        textView = findViewById(R.id.no_song);
        mediaPrevious = findViewById(R.id.media_previous);
        mediaTitle = findViewById(R.id.media_title);
        mediaImage = findViewById(R.id.media_image);
        mediaPlayPause = findViewById(R.id.media_play_pause);
        mediaNext = findViewById(R.id.media_next);

        if (checkPermission() == false) {
            requestPermission();
            return;
        }
        loadSongs();
        setupRecyclerView();

        mediaPlayPause.setOnClickListener(v -> pausePlaySong());
        mediaNext.setOnClickListener(v -> nextSong());
        mediaPrevious.setOnClickListener(v -> previousSong());



        mediaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, MusicPlayerActivity.class);
                intent.putExtra("LIST", songList);
                intent.putExtra("INDEX", currentIndex);
                startActivity(intent);
            }
        });

    }

    private void loadSongs() {
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 ";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

        while (cursor.moveToNext()) {
            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
            if (new File(songData.getPath()).exists()) {
                songList.add(songData);
            }
        }

        if (songList.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        MusicListAdapter musicListAdapter = new MusicListAdapter(songList, this, this::playSong);
        recyclerView.setAdapter(musicListAdapter);
    }

    public void playSong(int index) {
        currentIndex = index;
        currentSong = songList.get(currentIndex);
        mediaTitle.setText(currentSong.getTitle());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayPause.setImageResource(R.drawable.pause);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pausePlaySong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayPause.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            mediaPlayPause.setImageResource(R.drawable.pause);
        }
    }

    private void nextSong() {
        if (currentIndex < songList.size() - 1) {
            playSong(currentIndex + 1);
        }
    }
    private void previousSong() {
        if (currentIndex > 0) {
            playSong(currentIndex - 1);
        }
    }

    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)) {
            Toast.makeText(MainActivity.this, "Permission is required", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null) {
            recyclerView.setAdapter(new MusicListAdapter(songList, this, this::playSong));
        }
    }
}
