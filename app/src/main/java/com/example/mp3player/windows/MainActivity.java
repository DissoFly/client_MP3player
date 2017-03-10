package com.example.mp3player.windows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mp3player.R;
import com.example.mp3player.windows.main.AddMusicToListFragment;
import com.example.mp3player.windows.main.FooterPlayerFragment;
import com.example.mp3player.windows.main.LeftDrawerHeadMessageFragment;
import com.example.mp3player.windows.main.MusicItemSettingFragment;
import com.example.mp3player.windows.main.MusicListFragment;
import com.example.mp3player.windows.main.SearchFragment;
import com.example.mp3player.windows.main.page.FindMusicFragment;
import com.example.mp3player.windows.main.page.FriendsFragment;
import com.example.mp3player.windows.main.page.MineFragment;
import com.example.mp3player.windows.main.page.mine.download.DownloadFragment;
import com.example.mp3player.windows.main.page.mine.localMusic.LocalMusicFragment;

import static com.example.mp3player.R.id.btn_main_header_drawer;
import static com.example.mp3player.R.id.btn_main_header_find_music;
import static com.example.mp3player.R.id.btn_main_header_friends;
import static com.example.mp3player.R.id.btn_main_header_mine;
import static com.example.mp3player.R.id.btn_main_header_search;
import static com.example.mp3player.R.id.drawer_main;
import static com.example.mp3player.R.id.main_content_inside;
import static com.example.mp3player.R.id.main_content_outside;
import static com.example.mp3player.R.id.main_header;
import static com.example.mp3player.R.id.main_outside;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_ADD_MUSIC_TO_LIST_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_DOWNLOAD_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_FOOTER_PLAYING_LIST_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_LOCAL_MUSIC_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_ITEM_SETTING_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_LIST_FRAGMENT;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    int openFragInMain = 0;
    //left_drawer_head_message
    LeftDrawerHeadMessageFragment leftDrawerHeadMessageFragment = new LeftDrawerHeadMessageFragment();
    //main_outside
   //FooterPlayingListFragment footerPlayingListFragment = new FooterPlayingListFragment();
    MusicItemSettingFragment musicItemSettingFragment = new MusicItemSettingFragment();
    AddMusicToListFragment addMusicToListFragment = new AddMusicToListFragment();
    //main_footer
    FooterPlayerFragment footerPlayerFragment = new FooterPlayerFragment();

    //main_content_inside
    FindMusicFragment findMusicFragment = new FindMusicFragment();
    MineFragment mineFragment = new MineFragment();
    FriendsFragment friendsFragment = new FriendsFragment();
    //main_content_outside
    LocalMusicFragment localMusicFragment = new LocalMusicFragment();
    DownloadFragment downloadFragment = new DownloadFragment();
    SearchFragment searchFragment = new SearchFragment();
    MusicListFragment musicListFragment=new MusicListFragment();


    DrawerLayout drawable;
    LinearLayout drawableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawable = (DrawerLayout) findViewById(drawer_main);
        drawableLayout = (LinearLayout) findViewById(R.id.left_drawer);


        getFragmentManager().beginTransaction()
                .replace(main_content_inside, findMusicFragment).commit();
        //需要解决一开始点击findmusic会崩问题
        getFragmentManager().beginTransaction()
                .replace(R.id.main_footer, footerPlayerFragment).commit();
        getFragmentManager().beginTransaction()
                .replace(R.id.left_drawer_head_message, leftDrawerHeadMessageFragment).commit();

        drawable.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        initData();

        mineFragment.setOnBtnLocalMusicClickedListener(new MineFragment.OnBtnLocalMusicClickedListener() {
            @Override
            public void OnBtnLocalMusicClicked() {
                openFragInMain = mineFragment.getOpenFragmentInMain();
                if (openFragInMain==OPEN_MUSIC_LIST_FRAGMENT){
                    musicListFragment.setMineMusicList(mineFragment.getMineMusicList());
                }
                openNewFragInMain();
            }
        });

        footerPlayerFragment.setOnBtnPlayingListClickedListener(new FooterPlayerFragment.OnBtnPlayingListClickedListener() {

            @Override
            public void OnBtnPlayingListClicked() {
                openFragInMain = footerPlayerFragment.getOpenFragmentInMain();
                openNewFragInMain();
            }
        });

        localMusicFragment.setOnBtnMusicItemSettingClickedListener(new LocalMusicFragment.OnBtnMusicItemSettingClickedListener() {
            @Override
            public void OnMusicItemSettingClicked() {
                openFragInMain = localMusicFragment.getOpenFragmentInMain();
                musicItemSettingFragment.setSettingItem(localMusicFragment.getSelectMusic());
                openNewFragInMain();
            }
        });
        musicItemSettingFragment.setOnMusicItemSettingListener(new MusicItemSettingFragment.OnMusicItemSettingListener() {
            @Override
            public void OnMusicItemSetting() {
                openFragInMain = musicItemSettingFragment.getOpenFragmentInMain();
                addMusicToListFragment.setSettingItem(musicItemSettingFragment.getSelectMusic());
                openNewFragInMain();
            }
        });

        drawable.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawable.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawable.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void openNewFragInMain() {
        switch (openFragInMain) {
            case OPEN_LOCAL_MUSIC_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(main_content_outside, localMusicFragment).addToBackStack(null).commit();
                break;
            case OPEN_FOOTER_PLAYING_LIST_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_outside, new FooterPlayingListFragment()).addToBackStack(null).commit();
                break;
            case OPEN_DOWNLOAD_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(main_content_outside, downloadFragment).addToBackStack(null).commit();
                break;
            case OPEN_MUSIC_ITEM_SETTING_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(main_outside, musicItemSettingFragment).addToBackStack(null).commit();
                break;
            case OPEN_ADD_MUSIC_TO_LIST_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(main_outside, addMusicToListFragment).addToBackStack(null).commit();
                break;
            case OPEN_MUSIC_LIST_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(main_content_outside, musicListFragment).addToBackStack(null).commit();
                break;


        }

    }

    @Override
    public void finish() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    private void initData() {
        findViewById(btn_main_header_drawer).setOnClickListener(this);
        findViewById(btn_main_header_find_music).setOnClickListener(this);
        findViewById(btn_main_header_mine).setOnClickListener(this);
        findViewById(btn_main_header_friends).setOnClickListener(this);
        findViewById(btn_main_header_search).setOnClickListener(this);
        findViewById(main_header).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case btn_main_header_drawer:
                drawable.openDrawer(Gravity.LEFT);
                break;
            case btn_main_header_find_music:
                getFragmentManager().beginTransaction()
                        .replace(main_content_inside, findMusicFragment).commit();
                break;
            case btn_main_header_mine:
                getFragmentManager().beginTransaction()
                        .replace(main_content_inside, mineFragment).commit();
                break;
            case btn_main_header_friends:
                getFragmentManager().beginTransaction()
                        .replace(main_content_inside, friendsFragment).commit();
                break;
            case btn_main_header_search:
                getFragmentManager().beginTransaction()
                        .replace(main_content_outside, searchFragment).addToBackStack(null).commit();

                break;
            default:
                break;
        }
    }


}
