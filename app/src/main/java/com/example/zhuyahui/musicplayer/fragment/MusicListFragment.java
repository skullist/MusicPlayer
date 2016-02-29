package com.example.zhuyahui.musicplayer.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhuyahui.musicplayer.Dialog.RemoveMusicDialog;
import com.example.zhuyahui.musicplayer.R;
import com.example.zhuyahui.musicplayer.model.Music;
import com.example.zhuyahui.musicplayer.service.MediaPlayerService;
import com.example.zhuyahui.musicplayer.util.MusicLab;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by zhuyahui on 2016/2/20.
 */
public class MusicListFragment extends BaseFragment {


    private ListView listView;
    private MusicListAdapter musicListAdapter;
    private MusicLab musicLab;


    @Override
    public void updateUI(boolean updatePic) {
        musicListAdapter.notifyDataSetChanged();
    }

    private void afterDelete(String deletePath,String currentPath,String nextPath){

        Intent intent = new Intent(getActivity(), MediaPlayerService.class);

        if(musicLab.getmMusics().size()==0){
            musicLab.setCurrentPath(null);
            intent.putExtra(MediaPlayerService.EXTRA_ACTION_CLEAN,"clean");
        }else {
            if(deletePath.equals(currentPath)){
                if(deletePath.equals(nextPath)){
                    musicLab.setCurrentPath(musicLab.getNextMusic(true).getmPath());
                }else {
                    musicLab.setCurrentPath(nextPath);
                }
                intent.putExtra(MediaPlayerService.EXTRA_ACTION_RESET,musicLab.getCurrentPath());
            }else {
                updateUI(false);
                return;
            }
        }
        getActivity().startService(intent);
    }

    private void removeMusic(final String deletePath) {
        new RemoveMusicDialog(
                getActivity(),
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String currentPath = musicLab.getCurrentPath();
                        String nextPath = musicLab.getNextMusic(true).getmPath();
                        musicLab.deleteMusic(deletePath, false);
                        afterDelete(deletePath, currentPath, nextPath);
                        musicLab.saveMusics();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                },
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String currentPath = musicLab.getCurrentPath();
                        String nextPath = musicLab.getNextMusic(true).getmPath();
                        if(!musicLab.deleteMusic(deletePath, true)){
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("删除失败.")
                                    .setContentText("文件不存在或没有权限")
                                    .show();
                        }
                        afterDelete(deletePath,currentPath,nextPath);
                        musicLab.saveMusics();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI(false);
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
        musicLab = MusicLab.getMusicLab(getActivity(),false);
        musicListAdapter = new MusicListAdapter(musicLab.getmMusics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music_list, container, false);
        listView = (ListView) v.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = musicLab.getmMusics().get(position);
                Intent intent = new Intent(getActivity(), MediaPlayerService.class);
                if(music.getmPath().equals(musicLab.getCurrentPath())){
                    intent.putExtra(MediaPlayerService.EXTRA_PLAY_OR_PAUSE,"play_or_pause");
                }else {
                    intent.putExtra(MediaPlayerService.EXTRA_ACTION_PLAY_NEW,music.getmPath());
                    musicLab.setCurrentPath(music.getmPath());
                }
                getActivity().startService(intent);
            }
        });
        listView.setAdapter(musicListAdapter);
        return v;
    }


    private class MusicListAdapter extends ArrayAdapter {

        public MusicListAdapter(ArrayList<Music> mCrimes) {
            super(getActivity(), 0, mCrimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.music_list, null);
            }

            final Music m = (Music) getItem(position);

            ImageView soundImageView =
                    (ImageView) convertView.findViewById(R.id.list_start);
            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.list_title);
            if(m.getmTitle()!=null){
                titleTextView.setText(m.getmTitle());
            }else {
                titleTextView.setText("未知");
            }
            TextView artistTextView =
                    (TextView) convertView.findViewById(R.id.list_artist);
            if(m.getmArtist()!=null){
                artistTextView.setText(m.getmTitle());
            }else {
                artistTextView.setText("未知");
            }
            if (m.getmPath().equals(musicLab.getCurrentPath()) && MediaPlayerService.isPlaying()) {
                soundImageView.setVisibility(View.VISIBLE);
                titleTextView.setTextColor(getResources().getColor(R.color.background_strong));
                artistTextView.setTextColor(getResources().getColor(R.color.background_strong));
            } else {
                titleTextView.setTextColor(getResources().getColor(R.color.text));
                artistTextView.setTextColor(getResources().getColor(R.color.text));
                soundImageView.setVisibility(View.GONE);
            }
            artistTextView.setText("-" + m.getmArtist());

            ImageView removeImageView =
                    (ImageView) convertView.findViewById(R.id.list_remove);
            removeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMusic(m.getmPath());
                }

            });

            return convertView;
        }
    }


}
