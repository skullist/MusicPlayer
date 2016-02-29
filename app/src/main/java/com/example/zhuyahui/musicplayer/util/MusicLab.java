package com.example.zhuyahui.musicplayer.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.zhuyahui.musicplayer.model.Music;
import com.example.zhuyahui.musicplayer.service.MediaPlayerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by zhuyahui on 2016/2/19.
 */
public class MusicLab {

    private ArrayList<Music> mMusics;
    private static Context mAppContext;
    private static MusicLab sMusicLab;
    private MusicPlayerJSONSerializer mSerializer;
    private static MediaMetadataRetriever retriever;
    private static final String MUSICLAB_CURRENT_PATH = "musiclab_current_path";
    private static String currentPath;
    private static final String MUSICLAB_STATE = "musiclab_state";
    private int state;

    private MusicLab(Context context) {
        this.mAppContext = context;
        this.retriever = new MediaMetadataRetriever();
        mSerializer = new MusicPlayerJSONSerializer(context, SdCardManager.MUSICINFO_FILENAME);
        loadMusic();
        state = PreferenceManager.getDefaultSharedPreferences(mAppContext)
                .getInt(MUSICLAB_STATE, 0);
        currentPath = PreferenceManager.getDefaultSharedPreferences(mAppContext)
                .getString(MUSICLAB_CURRENT_PATH, null);
        if(currentPath!=null){
            Intent intent = new Intent(mAppContext, MediaPlayerService.class);
            intent.putExtra(MediaPlayerService.EXTRA_ACTION_RESET,currentPath);
            mAppContext.startService(intent);
        }
    }


    /**
     * @param context 获取该实例的Content
     * @param scan    是否重新扫描
     * @return 返回音乐仓库
     * @deprecated 根据是否重新扫描从文件中获取音乐仓库单例
     */
    public static MusicLab getMusicLab(Context context, Boolean scan) {
        if (sMusicLab == null) {
            sMusicLab = new MusicLab(context);
        }
        if (scan) {
            scanMusic(scan);
        }
        checkCurrent();
        return sMusicLab;
    }

    /**
     * @deprecated 删除最后一个再扫描或第一次取musiclab时
     * mMusics.size大于0，current为空，getNextMusic报错
     */
    private static void checkCurrent(){
        if(currentPath==null&&sMusicLab.getmMusics().size()!=0)
            currentPath = sMusicLab.getmMusics().get(0).getmPath();
    }

    /**
     * @param scan 是否重新扫描
     * @deprecated 重新扫描音乐
     */
    public static void scanMusic(Boolean scan) {
        if (scan) {
            ArrayList<Music> newMusics = findAllMusic();
            setUpdateMusics(newMusics);
        }
    }


    /**
     * @return 返回扫面出的文件集合，未加入totlelsecond
     * @deprecated 扫描所有音频文件
     */
    public static ArrayList<Music> findAllMusic() {

        ArrayList<Music> musics = new ArrayList<Music>();
        ArrayList<File> files = new ArrayList<File>();
        SdCardManager.getVideoFiles(Environment.getExternalStorageDirectory(),files,"mp3", "wav");
        SdCardManager.getVideoFiles(Environment.getRootDirectory(),files,"mp3", "wav");

        for (File file : files) {
            musics.add(getMusicInfo(file));
        }

        return musics;
    }

    /**
     * @deprecated 获取当前音乐封面
     * @return 当前音乐封面
     */
    public Drawable getCurrentPic(){
        if(getCurrentMusic()!=null){
            return getPicture(getCurrentPath());
        }else {
            return null;
        }
    }

    /**
     * @deprecated 获取当前音乐
     * @return 当前播放音乐
     */
    public Music getCurrentMusic(){
        if(currentPath==null){
            return null;
        }

        return mMusics.get(getMusicIndex(currentPath));
    }

    /**
     * @deprecated 根据文件路径获得歌曲图片
     * @param path 文件路径
     * @return 歌曲图片
     */
    public Drawable getPicture(String path){
        retriever.setDataSource(path);
        byte[] bytes = retriever.getEmbeddedPicture();
        if(bytes==null){
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return new BitmapDrawable(bitmap);
    }

    /**
     * @param file 搜索到的音频文件
     * @return 音频信息类
     * @deprecated 通过File类获取音频信息
     */
    private static Music getMusicInfo(File file) {

        String title, album, artist, duration;
        retriever.setDataSource(file.getAbsolutePath());
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return new Music(file.getName(), album, artist, new Integer(duration), title, file.getAbsolutePath());
    }

    /**
     * @deprecated 读取配置中音乐表单
     */
    public void loadMusic() {

        if(mMusics==null){
            mMusics = new ArrayList<Music>();
        }

        try {
            if (SdCardManager.ExistSDCard()) {
                SdCardManager.createInfoIfNotExist();
                mSerializer.loadMusics(SdCardManager.SDcardFile, mMusics);
            } else {
                mSerializer.loadMusics(null, mMusics);
            }
        } catch (Exception e) {
            Toast.makeText(mAppContext,
                    "无列表，请按左上角扫描",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * @deprecated 保存音乐列表到文件
     */
    public void saveMusics() {
        try {
            if (SdCardManager.ExistSDCard()) {
                mSerializer.saveMusics(mMusics, SdCardManager.SDcardFile);
            } else {
                mSerializer.saveMusics(mMusics, null);
            }
        } catch (Exception e) {
            Toast.makeText(mAppContext,
                    "保存列表失败！！",
                    Toast.LENGTH_LONG)
                    .show();
        }
        PreferenceManager.getDefaultSharedPreferences(mAppContext)
                .edit()
                .putInt(MUSICLAB_STATE, state)
                .putString(MUSICLAB_CURRENT_PATH, currentPath)
                .commit();
    }

    /**
     * @param newMusics 新扫描的表单
     * @deprecated 将配置中音乐表单的totalsecond对应的存入新扫描的表单
     */
    public static void setUpdateMusics(ArrayList<Music> newMusics) {
        if (newMusics.size() == 0) return;
        for (Music oldMusic : sMusicLab.getmMusics()) {
            for (Music newMusic : newMusics) {
                if (oldMusic.equals(newMusic)) {
                    newMusics.remove(newMusic);
                    break;
                }
            }
        }
        for (Music newMusic : newMusics) {
            sMusicLab.getmMusics().add(newMusic);
        }

    }



    /**
     * @return 下个音乐
     * @deprecated 获取下一个音乐
     */
    public Music getNextMusic(Boolean direction) {

        if (mMusics.size()==0) return null;

        int index = getMusicIndex(currentPath);

        switch (state){
            case 0:
                if(direction){
                    if (index == mMusics.size() - 1) {
                        return mMusics.get(0);
                    } else {
                        return mMusics.get(index + 1);
                    }
                }else {
                    if (index == 0) {
                        return mMusics.get(mMusics.size()-1);
                    } else {
                        return mMusics.get(index - 1);
                    }
                }
            case 1:
                return getCurrentMusic();
            case 2:
                index = new Random().nextInt(mMusics.size());
                return mMusics.get(index);
            default:
                return null;
        }

    }


    /**
     * @param path 需要找的path
     * @return 当前下标
     * @deprecated 根据path获取音乐集合中的下标
     */
    private int getMusicIndex(String path) {
        for (Music m : mMusics) {
            if (path.equals(m.getmPath())) {
                return mMusics.indexOf(m);
            }
        }
        return 0;
    }


    public void setCurrentPath(String path) {
        currentPath = path;
    }

    public String getCurrentPath(){
        return currentPath;
    }

    public ArrayList<Music> getmMusics() {
        return mMusics;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

    public void changeState() {
        if(state==2){
            setState(0);
        }else {
            setState(++state);
        }

        switch (state){
            case 0:
                Toast.makeText(mAppContext,
                        "列表循环",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            case 1:
                Toast.makeText(mAppContext,
                        "单曲循环",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            case 2:
                Toast.makeText(mAppContext,
                        "随机循环",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    public Boolean deleteMusic(String path,Boolean deleteFile){
        if(getmMusics()!=null){
            mMusics.remove(getMusicIndex(path));
        }
        if(deleteFile){
            return SdCardManager.deleteFile(path);
        }
        return true;
    }
}
