package com.example.zhuyahui.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

import com.example.zhuyahui.musicplayer.service.MediaPlayerService;

/**
 * Created by zhuyahui on 2016/2/23.
 */
public class BaseFragment extends Fragment {

    public static final String ACTION_UPDATE_UI = "update_ui";
    public static final String EXTRA_ACTION_UPDATE_PIC = "update_pic";

    private BroadcastReceiver updateUiNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent.getBooleanExtra(EXTRA_ACTION_UPDATE_PIC,false));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_UI);
        getActivity().registerReceiver(updateUiNotification, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateUiNotification);
    }

    public void updateUI(boolean updatePic){
    }
}
