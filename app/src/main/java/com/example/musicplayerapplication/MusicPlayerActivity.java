package com.example.musicplayerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView title,current,total;
    ImageView previous,next,pausebtn,banner;
    SeekBar seekBar;
    int x=2;

    ArrayList<AudioModel> songList;
    MediaPlayer mediaPlayer= MyMediaPlayer.getInstance();

    AudioModel currentSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        title=findViewById(R.id.ttile);
        banner=findViewById(R.id.music_banner);
        seekBar=findViewById(R.id.seekbar);
        current=findViewById(R.id.current_time);
        total=findViewById(R.id.total_time);
        previous=findViewById(R.id.previous);
        next=findViewById(R.id.next);
        pausebtn=findViewById(R.id.pause_btn);

        title.setSelected(true);

        songList= (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        MyMediaPlayer.currentIndex = getIntent().getIntExtra("INDEX", 0);



        setResourcesWithData();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!= null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    current.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));


                    if(mediaPlayer.isPlaying()){
                        banner.setRotation(x++);

                        pausebtn.setImageResource(R.drawable.pause);
                    }else {
                        pausebtn.setImageResource(R.drawable.play);
                        banner.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!= null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    private void setResourcesWithData() {

        currentSong= songList.get(MyMediaPlayer.currentIndex);
        title.setText(currentSong.getTitle());

        total.setText(convertToMMSS(currentSong.getDuration()));

        pausebtn.setOnClickListener(v-> pausePlaySong());
        next.setOnClickListener(v-> nextSong());
        previous.setOnClickListener(v-> previousSong());

        if (!mediaPlayer.isPlaying() || mediaPlayer.getCurrentPosition() == 0) {
            playSong();
        }else {
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
        }



    }

    private void previousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithData();

    }
    private void nextSong(){
        if(MyMediaPlayer.currentIndex==songList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithData();

    }
    private void pausePlaySong(){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else mediaPlayer.start();

    }
    private void playSong(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private static String convertToMMSS(String duration) {
        Long millis= Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
                );
    }
}