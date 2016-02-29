package com.example.zhuyahui.musicplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhuyahui on 2016/1/8.
 */
public class SdCardManager {

    public static final String MUSICINFO_FOLD = "/musicplayer";
    public static final String MUSICINFO_FILENAME = "musicinfo.json";
    public static File SDcardFile = new File(Environment.getExternalStorageDirectory()
            .toString()+MUSICINFO_FOLD,MUSICINFO_FILENAME);

    /**
     * @deprecated 判断内存卡是否可用
     * @return true:可用，false不可用
     */
    public static Boolean ExistSDCard(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * @deprecated 如果没有目录或文件，则创建音乐信息目录和文件
     */
    public static void createInfoIfNotExist(){
        if(!SDcardFile.exists()){
            SDcardFile.getParentFile().mkdirs();
            try {
                SDcardFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @deprecated 删除文件
     * @param path 要删除的路径
     */
    public static Boolean deleteFile(String path){
        File deleteFile = new File(path);
        if(deleteFile.exists()){
            return deleteFile.delete();
        }
        return false;
    }


    /**
     * @param dir     要搜索的目录
     * @param suffixs 指定的后缀
     * @deprecated 搜索指定目录下指定后缀文件
     */
    public static void getVideoFiles(File dir,ArrayList<File> musicFiles,String... suffixs) {
        File files[] = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getVideoFiles(f,musicFiles,suffixs);
                } else {
                    for (String s : suffixs) {
                        if (f.getAbsolutePath().endsWith(s)) {
                            musicFiles.add(f);
                            break;
                        }
                    }
                }
            }
        }
    }
}
