package com.example.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mp3player.main.page.FindMusicFragment;
import com.example.mp3player.main.page.FriendsFragment;
import com.example.mp3player.main.page.MineFragment;

import static com.example.mp3player.R.id.btn_main_header_drawer;
import static com.example.mp3player.R.id.btn_main_header_find_music;
import static com.example.mp3player.R.id.btn_main_header_friends;
import static com.example.mp3player.R.id.btn_main_header_mine;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    FindMusicFragment findMusicFragment=new FindMusicFragment();
    MineFragment mineFragment=new MineFragment();
    FriendsFragment friendsFragment =new FriendsFragment();

    DrawerLayout drawable;
    LinearLayout drawableLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawable=(DrawerLayout)findViewById(R.id.drawer_main);
        drawableLayout=(LinearLayout)findViewById(R.id.left_drawer);

        initData();


    }

    private void initData() {
       findViewById(btn_main_header_drawer).setOnClickListener(this);
        findViewById(btn_main_header_find_music).setOnClickListener(this);
        findViewById(btn_main_header_mine).setOnClickListener(this);
        findViewById(btn_main_header_friends).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent itnt;
        switch (view.getId()) {
            case btn_main_header_drawer :
                drawable.openDrawer(Gravity.LEFT);
                break;
            case btn_main_header_find_music :
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content,findMusicFragment).commit();
                break;
            case btn_main_header_mine :
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content,mineFragment).commit();
                break;
            case btn_main_header_friends :
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content,friendsFragment).commit();
                break;
            default:
                break;
        }
    }
}
