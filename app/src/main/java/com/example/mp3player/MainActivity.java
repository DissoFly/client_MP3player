package com.example.mp3player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mp3player.main.FooterPlayerFragment;
import com.example.mp3player.main.page.mine.localMusic.LocalMusicFragment;
import com.example.mp3player.main.page.FindMusicFragment;
import com.example.mp3player.main.page.FriendsFragment;
import com.example.mp3player.main.page.MineFragment;
import com.example.mp3player.service.MusicPlayerService;

import static com.example.mp3player.R.id.btn_main_header_drawer;
import static com.example.mp3player.R.id.btn_main_header_find_music;
import static com.example.mp3player.R.id.btn_main_header_friends;
import static com.example.mp3player.R.id.btn_main_header_mine;
import static com.example.mp3player.R.id.btn_main_header_search;
import static com.example.mp3player.R.id.drawer_main;
import static com.example.mp3player.R.id.main_content_inside;
import static com.example.mp3player.R.id.main_footer;
import static com.example.mp3player.R.id.main_header;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    int openFragInMain = 0;
    final int OPEN_LOCAL_MUSIC_FRAGMENT = 11;
    final int OPEN_FOOTER_PLAYING_LIST_FRAGMENT = 12;

    FooterPlayingListFragment footerPlayingListFragment=new FooterPlayingListFragment();
    FooterPlayerFragment footerPlayerFragment = new FooterPlayerFragment();
    //page
    FindMusicFragment findMusicFragment = new FindMusicFragment();
    MineFragment mineFragment = new MineFragment();
    FriendsFragment friendsFragment = new FriendsFragment();
    //inPage
    LocalMusicFragment localMusicFragment = new LocalMusicFragment();

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
        getFragmentManager().beginTransaction()
                .replace(main_footer, footerPlayerFragment).commit();

        drawable.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        initData();

        mineFragment.setOnBtnLocalMusicClickedListener(new MineFragment.OnBtnLocalMusicClickedListener() {
            @Override
            public void OnBtnLocalMusicClicked() {
                openFragInMain = mineFragment.getOpenFragmentInMain();
                openNewFragInMain();
            }
        });

        footerPlayerFragment.setOnBtnPlayingListClickedListener(new FooterPlayerFragment.OnBtnPlayingListClickedListener(){

            @Override
            public void OnBtnPlayingListClicked() {
                openFragInMain = footerPlayerFragment.getOpenFragmentInMain();
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
                        .replace(R.id.main_content_outside, localMusicFragment).addToBackStack(null).commit();
                break;
            case OPEN_FOOTER_PLAYING_LIST_FRAGMENT:
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_outside, footerPlayingListFragment).addToBackStack(null).commit();
                break;


        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this,MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);

    }

    MusicPlayerService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {				//判断有没有绑定Service

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            Toast.makeText(getApplicationContext(), " fail!", Toast.LENGTH_SHORT).show();
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定后可调用MusicPlayerService的方法来达到控制
            messenger=((MusicPlayerService.ServiceBinder) service).getService();

            Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
            bound=true;
        }
    };



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
        Intent itnt;
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       ///Toast.makeText(MainActivity.this,"this is search",Toast.LENGTH_LONG).show();
//                        Message msg = Message.obtain(null, 0);
//                        msg.replyTo=messenger;
                        if (bound)
                            try {
                                Toast.makeText(getApplicationContext(), "connect send!", Toast.LENGTH_SHORT).show();
//                                messenger.send(msg);

                                Toast.makeText(getApplicationContext(), "connect send!+"+messenger.getConnect(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        else
                            Toast.makeText(getApplicationContext(), "connect fail!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }





}
