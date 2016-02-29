package com.example.zhuyahui.musicplayer.Dialog;

import android.content.Context;
import android.graphics.Color;

import com.example.zhuyahui.musicplayer.util.MusicLab;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhuyahui on 2016/2/24.
 */
public class RemoveMusicDialog extends SweetAlertDialog {


    public RemoveMusicDialog(final Context context,OnSweetClickListener deleteMusicListener,OnSweetClickListener deleteFileListener) {
        super(context, SweetAlertDialog.WARNING_TYPE);
        this.setTitleText("确定要删除么");
        this.setContentText("点击空白出返回");
        this.setConfirmText("删除文件");
        this.setCancelText("删除记录");
        this.setCanceledOnTouchOutside(true);
        this.setConfirmClickListener(deleteFileListener);
        this.setCancelClickListener(deleteMusicListener);
    }



}
