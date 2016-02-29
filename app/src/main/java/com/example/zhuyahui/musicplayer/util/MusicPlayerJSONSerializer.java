package com.example.zhuyahui.musicplayer.util;

import android.content.Context;

import com.example.zhuyahui.musicplayer.model.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by zhuyahui on 2016/2/19.
 */
public class MusicPlayerJSONSerializer {

    private Context mContext;
    private String mMusicInfoName;


    /**
     * @deprecated 构造配置文件序列化类
     * @param context 获取该实例的Content
     * @param musicInfoName 配置文件名
     */
    public MusicPlayerJSONSerializer(Context context, String musicInfoName) {
        this.mContext = context;
        this.mMusicInfoName = musicInfoName;
    }

    /**
     * @deprecated 序列化音乐信息集并覆盖到信息文件，
     * 如果file为null则保存到私有目录，否则保存到sd卡默认目录
     * @param musics 需要保存的音乐信息集
     * @param file 保存到的文件位置
     * @throws IOException
     * @throws JSONException
     */
    public void saveMusics(ArrayList<Music> musics, File file) throws IOException, JSONException {
        JSONArray array = new JSONArray();
        for (Music music : musics
                ) {
            array.put(music.toJSON());
        }
        Writer writer = null;
        try {
            if (file == null) {
                OutputStream out
                        = mContext.openFileOutput(mMusicInfoName, Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
            } else {
                writer = new OutputStreamWriter(new FileOutputStream(file));
            }
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * @deprecated 如果file为null则读取私有目录文件，否则读取sd卡默认文件
     * @param file 需要读取的配置文件
     * @throws IOException
     * @throws JSONException
     */
    public void loadMusics(File file,ArrayList<Music> musics) throws IOException, JSONException {

        BufferedReader reader = null;
        InputStream in = null;
        try {
            if (file == null) {
                in = mContext.openFileInput(mMusicInfoName);
            }else{
                in = new FileInputStream(file);
            }
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                Music music = new Music(array.getJSONObject(i));
                if(music.ifExist()){
                    musics.add(new Music(array.getJSONObject(i)));
                }
            }
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
