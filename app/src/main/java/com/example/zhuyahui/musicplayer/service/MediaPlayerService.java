package com.example.zhuyahui.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhuyahui.musicplayer.fragment.BaseFragment;
import com.example.zhuyahui.musicplayer.model.Music;
import com.example.zhuyahui.musicplayer.util.MusicLab;

import java.io.IOException;

/**
 * Created by zhuyahui on 2016/2/23.
 */
public class MediaPlayerService extends Service {

    public static final String EXTRA_PLAY_OR_PAUSE = "play_or_pause";
    public static final String EXTRA_ACTION_PLAY_NEW = "play_new";
    public static final String EXTRA_ACTION_STOP = "stop";
    public static final String EXTRA_ACTION_RESET = "reset";
    public static final String EXTRA_ACTION_CLEAN = "clean";

    private static MediaPlayer mPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setOnCompletionListener(new MusicCompletionListener());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getStringExtra(EXTRA_PLAY_OR_PAUSE) != null) {
            playOrPause(false);
        }
        if (intent.getStringExtra(EXTRA_ACTION_PLAY_NEW) != null) {
            playNew(intent.getStringExtra(EXTRA_ACTION_PLAY_NEW),true);
        }
        if (intent.getStringExtra(EXTRA_ACTION_STOP) != null) {
            stop(false);
        }
        if (intent.getStringExtra(EXTRA_ACTION_RESET) != null) {
            reset(intent.getStringExtra(EXTRA_ACTION_RESET),true);
        }
        if (intent.getStringExtra(EXTRA_ACTION_CLEAN) != null) {
            clean(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
    }

    private void updateBroadcast(Boolean updatePic) {
        Intent i = new Intent(BaseFragment.ACTION_UPDATE_UI);
        i.putExtra(BaseFragment.EXTRA_ACTION_UPDATE_PIC, updatePic);
        sendBroadcast(i);
    }

    private void playOrPause(Boolean updatePic) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            updateBroadcast(updatePic);
            return;
        }
        mPlayer.start();
        updateBroadcast(updatePic);
    }

    private void playNew(String newPath,Boolean updatePic) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(newPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newPath.equals(MusicLab.getMusicLab(getApplicationContext(), false).getCurrentPath())) {
            updateBroadcast(updatePic);
        } else {
            MusicLab.getMusicLab(getApplicationContext(), false).setCurrentPath(newPath);
            updateBroadcast(updatePic);
        }
    }


    private void stop(Boolean updatePic) {
        if (!isPlaying()) {
            return;
        }
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateBroadcast(updatePic);
    }

    private void reset(String newPath,Boolean updatePic) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(newPath);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateBroadcast(updatePic);
    }

    private void clean(Boolean updatePic){
        mPlayer.reset();
        updateBroadcast(updatePic);
    }


    public static Boolean isPlaying() {
        if (mPlayer == null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    public static int currentSecond() {
        if(mPlayer==null){
            return 0;
        }
        return mPlayer.getCurrentPosition();
    }


    private class MusicCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Music music = MusicLab.getMusicLab(getApplicationContext(), false).getNextMusic(true);
            playNew(music.getmPath(),true);
        }
    }

}
