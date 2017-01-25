package com.example.mp3player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mp3player.inputcells.SongPictureFragment;
import com.example.mp3player.service.MusicPlayerService;

import java.util.List;




/**
 * Created by DissoCapB on 2017/1/21.
 */

public class PlayingActivity extends Activity implements View.OnClickListener{
    MusicPlayerService messenger;
    boolean bound;
    private List<String> audioList = null;
    private int listPosition=-1;
    int REFLASH_TIME=100;

    TextView playingName;
    TextView playingCurrentPosition;
    TextView playingDuration;
    SeekBar musicBar;
    boolean isMusicBarTouch=false;
    SongPictureFragment songPictureFragment=new SongPictureFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        playingName=(TextView)findViewById(R.id.text_play_name);
        playingCurrentPosition=(TextView)findViewById(R.id.text_playing_time);
        playingDuration=(TextView)findViewById(R.id.text_max_time);
        musicBar=(SeekBar)findViewById(R.id.play_music_bar) ;

        getFragmentManager().beginTransaction()
                .replace(R.id.frag_song_img, songPictureFragment).commit();

        initData();

        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch=false;
                if(listPosition>=0){
                    messenger.seekTo(seekBar.getProgress());
                }
            }
        });
    }



    private void initData() {
        findViewById(R.id.btn_playing_back).setOnClickListener(this);
        findViewById(R.id.btn_play_cycle).setOnClickListener(this);
        findViewById(R.id.btn_play_back).setOnClickListener(this);
        findViewById(R.id.btn_play_changes).setOnClickListener(this);
        findViewById(R.id.btn_play_next).setOnClickListener(this);
        findViewById(R.id.btn_play_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_playing_back:
                finish();
                break;
            case R.id.btn_play_cycle:
                break;
            case R.id.btn_play_back:
                messenger.back();
                break;
            case R.id.btn_play_changes:
                messenger.playOrPause();
                break;
            case R.id.btn_play_next:
                messenger.next();
                break;
            case R.id.btn_play_list:
                break;
            default:
                break;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this,MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);

    }



    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=((MusicPlayerService.ServiceBinder) service).getService();
            handler.postDelayed(runnable, REFLASH_TIME);
            bound=true;
        }
    };

    Handler handler =new Handler();
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            audioList=messenger.getAudioList();
            listPosition=messenger.getPlayingListPosition();

            if (listPosition==-1) {
                playingName.setText("音乐，让生活更美好");
                playingCurrentPosition.setText("00:00");
                playingDuration.setText("00:00");
            }else {
                int max=messenger.getDuration();
                int current=messenger.getCurrentPosition();
                playingName.setText(audioList.get(listPosition));
                playingCurrentPosition.setText(showTime(current));
                playingDuration.setText(showTime(max));
                musicBar.setMax(max);
                if(!isMusicBarTouch)
                    musicBar.setProgress(current);

            }

            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };

    public String showTime(int time){
        time/=1000;
        int minute=time/60;
        int second=time%60;
        return String.format("%02d:%02d", minute, second);
    }


}