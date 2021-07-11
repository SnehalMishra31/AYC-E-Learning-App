package com.alphaCoachingAdmin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.alphaCoachingAdmin.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoDisplay extends AppCompatActivity implements YouTubePlayerFullScreenListener {
TextView textView;
String title,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        textView=findViewById(R.id.tvVideoTitle);

        Intent intent1=getIntent();
        title=intent1.getStringExtra("title");
        textView.setText(title);
        url=intent1.getStringExtra("url");

        //getting only ID from URL
        String[] parts= url.split("/");
        url=parts[3];


        YouTubePlayerView youTubePlayerView=findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(url,0);
            }
        });






    }

    @Override
    public void onYouTubePlayerEnterFullScreen() {

    }

    @Override
    public void onYouTubePlayerExitFullScreen() {

    }
}
