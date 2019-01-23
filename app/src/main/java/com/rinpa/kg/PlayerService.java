package com.rinpa.kg;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

public class PlayerService extends Service {

    MediaPlayer mediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new MyBinder ();

    public class MyBinder extends Binder {
        PlayerService getService(){
            return PlayerService.this;
        }

    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playStream(intent.getStringExtra("url"));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public void playStream (String url) {
        if (mediaPlayer != null){
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null ;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playPlayer();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                   flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pausePlayer() {
        try {
            mediaPlayer.pause();
            flipPlayPauseButton(false);
        }catch (Exception e) {
            Log.d("EXCEPTION", "Failed to pause the player");
        }
    }
    public void playPlayer() {
        try {
            mediaPlayer.start();
           flipPlayPauseButton(true);
        }catch (Exception e) {
            Log.d("EXCEPTION", "Failed to play the player");
        }
    }

    public void flipPlayPauseButton(boolean isPlaying) {
        //Code To communicate with Main Thread
        Intent intent = new Intent("changePlayButton");
        //Add Data
        intent.putExtra("isPlaying", isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void togglePlayer () {
        try {
            if (mediaPlayer.isPlaying())
                pausePlayer();
            else
                playPlayer();
        }catch (Exception e) {
            Log.d("EXCEPTION", "Failed to toggle mediaPlayer");
        }
    }
}
