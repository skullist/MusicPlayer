package com.example.zhuyahui.musicplayer.model;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/**
 * Created by zhuyahui on 2016/2/19.
 */
public class Music {

    private static final String JSON_DISPLAY_NAME = "displayname";
    private static final String JSON_ALBUM = "album";
    private static final String JSON_ARTIST = "artist";
    private static final String JSON_DURATION = "duration";
    private static final String JSON_TITLE = "title";
    private static final String JSON_BOOKMARK = "bookmark";
    private static final String JSON_PATH = "path";
    private static final String JSON_TOTAL_SECOND = "totalsecond";

    private String mDisplayName;
    private String mAlbum;
    private String mArtist;
    private Integer mDuration;
    private String mTitle;
    private Integer mBookmark;
    private String mPath;
    private Long mTotalSecond;


    public Music(String mDisplayName, String mAlbum, String mArtist, Integer mDuration, String mTitle, String mPath) {
        this.mDisplayName = mDisplayName;
        this.mAlbum = mAlbum;
        this.mArtist = mArtist;
        this.mDuration = mDuration;
        this.mTitle = mTitle;
        this.mPath = mPath;
    }

    /**
     * @return json对象
     * @throws JSONException
     * @deprecated music类转为json对象
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        if (mDisplayName != null) {
            json.put(JSON_DISPLAY_NAME, mDisplayName);
        }
        if (mAlbum != null) {
            json.put(JSON_ALBUM, mAlbum);
        }
        if (mArtist != null) {
            json.put(JSON_ARTIST, mArtist);
        }
        if (mDuration != null) {
            json.put(JSON_DURATION, mDuration.toString());
        }
        if (mTitle != null) {
            json.put(JSON_TITLE, mTitle);
        }
        if (mBookmark != null) {
            json.put(JSON_BOOKMARK, mBookmark.toString());
        }
        if (mPath != null) {
            json.put(JSON_PATH, mPath);
        }
        if (mTotalSecond != null) {
            json.put(JSON_TOTAL_SECOND, mTotalSecond.toString());
        }
        return json;
    }

    /**
     * @return 不存在返回false
     * @deprecated 判断该音乐是否还存在
     */
    public Boolean ifExist() {
        return new File(mPath).exists();
    }

    /**
     * @param json 配置文件中读取到的json对象
     * @throws JSONException
     * @deprecated 通过json对象构造music
     */
    public Music(JSONObject json) throws JSONException {
        if (json.has(JSON_DISPLAY_NAME)) {
            this.mDisplayName = json.getString(JSON_DISPLAY_NAME);
        }
        if (json.has(JSON_ALBUM)) {
            this.mAlbum = json.getString(JSON_ALBUM);
        }
        if (json.has(JSON_ARTIST)) {
            this.mArtist = json.getString(JSON_ARTIST);
        }
        if (json.has(JSON_DURATION)) {
            this.mDuration = Integer.parseInt(json.getString(JSON_DURATION));
        }
        if (json.has(JSON_TITLE)) {
            this.mTitle = json.getString(JSON_TITLE);
        }
        if (json.has(JSON_BOOKMARK)) {
            this.mBookmark = Integer.parseInt(json.getString(JSON_BOOKMARK));
        }
        if (json.has(JSON_PATH)) {
            this.mPath = json.getString(JSON_PATH);
        }
        if (json.has(JSON_TOTAL_SECOND)) {
            this.mTotalSecond = Long.parseLong(json.getString(JSON_TOTAL_SECOND));
        }
    }

    @Override
    public boolean equals(Object obj) {
        return mPath.equals(((Music) obj).mPath);
    }


    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public Integer getmDuration() {
        return mDuration;
    }

    public void setmDuration(Integer mDuration) {
        this.mDuration = mDuration;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Integer getmBookmark() {
        return mBookmark;
    }

    public void setmBookmark(Integer mBookmark) {
        this.mBookmark = mBookmark;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public Long getmTotalSecond() {
        return mTotalSecond;
    }

    public void setmTotalSecond(Long mTotalSecond) {
        this.mTotalSecond = mTotalSecond;
    }
}
