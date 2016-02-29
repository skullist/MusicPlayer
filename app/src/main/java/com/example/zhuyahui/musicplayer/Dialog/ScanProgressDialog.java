package com.example.zhuyahui.musicplayer.Dialog;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhuyahui on 2016/2/24.
 */
public class ScanProgressDialog extends SweetAlertDialog {


    public ScanProgressDialog(Context context) {
        super(context, SweetAlertDialog.PROGRESS_TYPE);
        this.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.setTitleText("Loading");
        this.setCancelable(false);
    }

}
