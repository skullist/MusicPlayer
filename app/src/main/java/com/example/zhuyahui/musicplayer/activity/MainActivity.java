package com.example.zhuyahui.musicplayer.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhuyahui.musicplayer.Dialog.ScanProgressDialog;
import com.example.zhuyahui.musicplayer.fragment.DetailFragment;
import com.example.zhuyahui.musicplayer.fragment.MusicListFragment;
import com.example.zhuyahui.musicplayer.R;
import com.example.zhuyahui.musicplayer.fragment.WebFragment;
import com.example.zhuyahui.musicplayer.service.MediaPlayerService;
import com.example.zhuyahui.musicplayer.util.MusicLab;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


/**
 * Created by zhuyahui on 2016/2/19.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private ImageView scanImageView;
    private ImageView menuImageView;
    private TextView exitButton;
    private Toolbar toolbar;
    private FragmentPagerItemAdapter adapter;
    private ScanProgressDialog scanProgressDialog;
    private static OnKeydownListener onKeydownListener;
    private SlidingMenu slidingMenu;
    private MusicLab musicLab;

    public interface OnKeydownListener{
        Boolean OnKeyDown(int keyCode, KeyEvent event);
    }

    public static void setOnKeydownListener(OnKeydownListener onKeydownListener) {
        MainActivity.onKeydownListener = onKeydownListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(viewPager.getCurrentItem()==2){
            if(onKeydownListener.OnKeyDown(keyCode, event)){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updatePage() {
        ((DetailFragment) adapter.getPage(0)).updateUI(false);
        ((MusicListFragment) adapter.getPage(1)).updateUI(false);
    }


    private void setPagerAdapter(ViewPager viewPager, SmartTabLayout viewPagerTab) {
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_1, DetailFragment.class)
                .add(R.string.tab_2, MusicListFragment.class)
                .add(R.string.tab_3, WebFragment.class)
                .create()
        );

        viewPager.setAdapter(adapter);
        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView tab = (ImageView) getLayoutInflater().inflate(R.layout.tab_icon, container, false);
                switch (position) {
                    case 0:
                        tab.setImageResource(R.drawable.headphones);
                        return tab;
                    case 1:
                        tab.setImageResource(R.drawable.view_as_list);
                        return tab;
                    case 2:
                        tab.setImageResource(R.drawable.web_site);
                        return tab;
                    default:
                        return tab;
                }
            }
        });
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(1);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else {
                    slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setSlidingMenu(){
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setShadowDrawable(R.drawable.drawer_shadow);
        View sidingMenu = getLayoutInflater().inflate(R.layout.siding_menu,null);
        exitButton = (TextView) sidingMenu.findViewById(R.id.menu_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicLab.saveMusics();
                stopService(new Intent(MainActivity.this,MediaPlayerService.class));
                MainActivity.super.finish();
                System.exit(0);
            }
        });
        slidingMenu.setMenu(sidingMenu);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicLab = MusicLab.getMusicLab(getApplicationContext(), false);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar = (Toolbar) findViewById(R.id.id_tool_bar);
        toolbar.setTitle("");
        scanImageView = (ImageView) toolbar.findViewById(R.id.main_search);
        scanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanProgressDialog = new ScanProgressDialog(MainActivity.this);
                scanProgressDialog.show();
                new ScanMusicTask().execute();
            }
        });
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.view_pager_tab);
        setSlidingMenu();
        menuImageView = (ImageView) toolbar.findViewById(R.id.main_menu);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!slidingMenu.isMenuShowing()){
                    slidingMenu.showMenu(true);
                }
            }
        });
        setPagerAdapter(viewPager, viewPagerTab);
    }

    private class ScanMusicTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            MusicLab.getMusicLab(MainActivity.this, true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updatePage();
            scanProgressDialog.dismiss();
        }
    }
}
