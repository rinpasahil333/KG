package com.rinpa.kg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import static com.rinpa.kg.Constants.ACTION.NOTIFICATION_CHANNEL_ID;
import static com.rinpa.kg.Constants.ACTION.channelName;

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
        if(intent.getStringExtra("url") != null )
            playStream(intent.getStringExtra("url"));

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i("info", "Start Foreground Action");
            showNoftification();
        }
        else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i("info", "Prev Pressed");
        }
        else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i("info", "Play Pressed");
            togglePlayer();
        }
        else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i("info", "Next Pressed");
        }
        else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i("info", "Stop Forground Action");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNoftification () {
        Intent notificationIntent = new Intent(this, PlayerService.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
//        notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent,0);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getActivity(this,0, previousIntent,0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getActivity(this,0, playIntent,0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getActivity(this,0, nextIntent,0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.logo);

        int playPauseButtonId = android.R.drawable.ic_media_play;
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            playPauseButtonId =android.R.drawable.ic_media_pause;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            channel();
//        }
//        else{
//        }

        Notification notification = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Music Player")
        .setTicker("Playing Music")
        .setContentText("My song")
        .setSmallIcon(R.drawable.logo)
        .setLargeIcon(Bitmap.createScaledBitmap(icon,128,128,false))
        .setContentIntent(pendingIntent)
        .setOngoing(true)
        .addAction(android.R.drawable.ic_media_previous,"Previous", ppreviousIntent)
        .addAction(playPauseButtonId,"Play",pplayIntent)
        .addAction(android.R.drawable.ic_media_next,"Next", pnextIntent)
        .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    public void channel () {
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }
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
            Log.d("NOTIFY","Media Paused");
            showNoftification();
        }catch (Exception e) {
            Log.d("EXCEPTION", "Failed to pause the player");
        }
    }
    public void playPlayer() {
        try {
            mediaPlayer.start();
           flipPlayPauseButton(true);
           showNoftification();
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
