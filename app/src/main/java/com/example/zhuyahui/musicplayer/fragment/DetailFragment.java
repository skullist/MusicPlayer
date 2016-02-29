package com.example.zhuyahui.musicplayer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zhuyahui.musicplayer.R;
import com.example.zhuyahui.musicplayer.model.Music;
import com.example.zhuyahui.musicplayer.playview.MaterialMusicPlayerView;
import com.example.zhuyahui.musicplayer.service.MediaPlayerService;
import com.example.zhuyahui.musicplayer.util.MusicLab;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhuyahui on 2016/2/20.
 */
public class DetailFragment extends BaseFragment {

    private MusicLab musicLab = MusicLab.getMusicLab(getActivity(), false);
    private MaterialMusicPlayerView musicPlayerView;
    private TextView musicTitleView;
    private TextView musicArtistView;
    private ImageView loopTypeView;
    private ImageView preMusicView;
    private ImageView nextMusicView;
    private ImageView closeTimeView;
    private Handler closeHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private Runnable closeIt;
    private int closeTime = 600000;
    private int choseItem;

    private void updateStateButton() {
        switch (musicLab.getState()) {
            case 0:
                loopTypeView.setImageResource(R.drawable.repeat);
                break;
            case 1:
                loopTypeView.setImageResource(R.drawable.replay);
                break;
            case 2:
                loopTypeView.setImageResource(R.drawable.shuffle);
                break;
        }
    }

    @Override
    public void updateUI(boolean updatePic) {

        if (musicLab.getCurrentMusic() == null) {
            musicTitleView.setText(getResources().getString(R.string.music_title_default));
            musicArtistView.setText(getResources().getString(R.string.music_artist_default));
            musicPlayerView.setMax(100);
            musicPlayerView.stop();
            return;
        } else {
            if (updatePic) {
                if (musicLab.getCurrentPic() != null) {
                    musicPlayerView.setCoverDrawable(musicLab.getCurrentPic());
                } else {
                    musicPlayerView.setCoverDrawable(getResources().getDrawable(R.drawable.default_cover));
                }
            }
            musicPlayerView.setMax(musicLab.getCurrentMusic().getmDuration() / 1000);
            musicPlayerView.setProgress(MediaPlayerService.currentSecond() / 1000);
            musicTitleView.setText(musicLab.getCurrentMusic().getmTitle());
            musicArtistView.setText(musicLab.getCurrentMusic().getmArtist());
            if (MediaPlayerService.isPlaying()) {
                musicPlayerView.start();
            } else {
                musicPlayerView.stop();
            }
        }

        updateStateButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        musicLab.saveMusics();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        closeHandler = new Handler();
        closeIt = new Runnable() {
            @Override
            public void run() {
                if (MediaPlayerService.isPlaying()) {
                    Intent intent = new Intent(getActivity(), MediaPlayerService.class);
                    intent.putExtra(MediaPlayerService.EXTRA_ACTION_STOP, "stop");
                    getActivity().startService(intent);
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (MediaPlayerService.isPlaying() && musicPlayerView != null) {
                    musicPlayerView.setProgress(MediaPlayerService.currentSecond() / 1000);
                }
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        musicPlayerView = (MaterialMusicPlayerView) v.findViewById(R.id.detail_player_view);
        musicPlayerView.setAutoProgress(false);
        musicTitleView = (TextView) v.findViewById(R.id.music_title);
        musicArtistView = (TextView) v.findViewById(R.id.music_artist);
        musicPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPause();
            }
        });
        loopTypeView = (ImageView) v.findViewById(R.id.loop_type);
        loopTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicLab.changeState();
                updateStateButton();
            }
        });
        preMusicView = (ImageView) v.findViewById(R.id.pre_music);
        preMusicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNew(musicLab.getNextMusic(false));
            }
        });
        nextMusicView = (ImageView) v.findViewById(R.id.next_music);
        nextMusicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNew(musicLab.getNextMusic(true));
            }
        });
        closeTimeView = (ImageView) v.findViewById(R.id.close_time);
        closeTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("选择关闭时间")
                        .setSingleChoiceItems(R.array.close_time, choseItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        closeTime = 600000;
                                        choseItem = 0;
                                        break;
                                    case 1:
                                        closeTime = 900000;
                                        choseItem = 1;
                                        break;
                                    case 2:
                                        closeTime = 1800000;
                                        choseItem = 2;
                                        break;
                                }
                            }
                        })
                        .setPositiveButton("立即设定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!MediaPlayerService.isPlaying()) {
                                    dialog.dismiss();
                                    return;
                                }
                                closeHandler.removeCallbacks(closeIt);
                                closeHandler.postDelayed(closeIt, closeTime);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消设定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeHandler.removeCallbacks(closeIt);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });
        return v;
    }


    private void playOrPause() {
        if (musicLab.getCurrentMusic() == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerService.EXTRA_PLAY_OR_PAUSE, musicLab.getCurrentPath());
        getActivity().startService(intent);
    }

    private void playNew(Music music) {
        if (music == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        intent.putExtra(MediaPlayerService.EXTRA_ACTION_PLAY_NEW, music.getmPath());
        getActivity().startService(intent);
        musicLab.setCurrentPath(music.getmPath());
    }

}
