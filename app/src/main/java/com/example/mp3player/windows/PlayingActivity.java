package com.example.mp3player.windows;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.service.MusicPlayerService;
import com.example.mp3player.windows.inputcells.SongPictureFragment;

import java.util.List;


/**
 * Created by DissoCapB on 2017/1/21.
 */

public class PlayingActivity extends Activity implements View.OnClickListener {
    MusicPlayerService messenger;
    boolean bound;
    private List<PlayingItem> audioList = null;
    private int listPosition = -1;

    int REFLASH_TIME = 50;

    TextView playingName;
    TextView playingCurrentPosition;
    TextView playingDuration;
    SeekBar musicBar;
    FrameLayout songImg;
    ObjectAnimator rotateAnimation;
    private float currentValue = 0f;
    boolean isMusicBarTouch = false;
    int oneReloadPosition = -1;
    SongPictureFragment songPictureFragment = new SongPictureFragment();

    TextView localOrOnline;
    Button btnDownload;
    Button btnComment;

    ImageView btnPlayChanges;
    ImageView btnPlayBack;
    ImageView btnPlayNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        playingName = (TextView) findViewById(R.id.text_play_name);
        playingCurrentPosition = (TextView) findViewById(R.id.text_playing_time);
        playingDuration = (TextView) findViewById(R.id.text_max_time);
        musicBar = (SeekBar) findViewById(R.id.play_music_bar);
        songImg = (FrameLayout) findViewById(R.id.frag_song_img);
        localOrOnline = (TextView) findViewById(R.id.text_playing_local_or_online);
        btnDownload = (Button) findViewById(R.id.btn_playing_download);
        btnComment = (Button) findViewById(R.id.btn_playing_comment);
        animation();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_song_img, songPictureFragment).commit();

        initData();

        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isMusicBarTouch = false;
                if (listPosition >= 0) {
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
        findViewById(R.id.btn_playing_download).setOnClickListener(this);
        findViewById(R.id.btn_playing_comment).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_playing_back:
                rotateAnimation.start();
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
            case R.id.btn_playing_comment:
                CommentFragment commentFragment=new CommentFragment();
                commentFragment.setSongId(audioList.get(listPosition).getOnlineSongId());
                getFragmentManager().beginTransaction()
                        .replace(R.id.layout_out, commentFragment).addToBackStack(null).commit();
                break;
            case R.id.btn_play_list:
                getFragmentManager().beginTransaction()
                        .replace(R.id.layout_out, new FooterPlayingListFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_playing_download:
                messengerDownload.downloadMusic(audioList.get(listPosition).getOnlineSongId(),audioList.get(listPosition).getSongName());
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, DownloadService.class), connectionDownload, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            audioList = messenger.getAudioList();
            listPosition = messenger.getPlayingListPosition();

            if (listPosition == -1) {
                playingName.setText("音乐，让生活更美好");
                playingCurrentPosition.setText("00:00");
                playingDuration.setText("00:00");
                localOrOnline.setText("无");
                btnDownload.setEnabled(false);
                btnDownload.setText("无下载");
            } else {
                playingName.setText(audioList.get(listPosition).getSongName());
                if (!audioList.get(listPosition).isOnline()) {
                    localOrOnline.setText("本地音乐");
                    btnDownload.setEnabled(false);
                    btnComment.setEnabled(false);
                    btnDownload.setText("本地");
                }else if (audioList.get(listPosition).isOnline()&&audioList.get(listPosition).isDownload()) {
                    localOrOnline.setText("网络下载");
                    btnDownload.setEnabled(false);
                    btnComment.setEnabled(true);
                    btnDownload.setText("已下载");
                }else {
                    localOrOnline.setText("网络在线");
                    btnDownload.setEnabled(true);
                    btnComment.setEnabled(true);
                    btnDownload.setText("下载");
                }
                if (messenger.isPlaying())
                    play();
                if (!rotateAnimation.isPaused()) {
                    if (!messenger.isPlaying()) {
                        rotateAnimation.pause();
                    }
                } else {
                    if (messenger.isPlaying()) {
                        rotateAnimation.resume();
                    }
                }
            }

            handler.postDelayed(runnable, REFLASH_TIME);
        }
    };

    private void play() {
        int max = messenger.getDuration();
        int current = messenger.getCurrentPosition();
        playingCurrentPosition.setText(showTime(current));
        playingDuration.setText(showTime(max));
        musicBar.setMax(max);
        if (messenger.isOnlinePlay())
            musicBar.setSecondaryProgress(messenger.getBufferingProgress() * max / 100);
        else
            musicBar.setSecondaryProgress(max);
        if (!isMusicBarTouch)
            musicBar.setProgress(current);
        if (oneReloadPosition != listPosition) {
            songPictureFragment.setImg(messenger.getImg());
            oneReloadPosition = listPosition;
        }

    }

    public String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", minute, second);
    }

    public void animation() {
        rotateAnimation = ObjectAnimator.ofFloat(songImg, "rotation", currentValue - 360, currentValue);
        rotateAnimation.setDuration(30000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimation.start();
        rotateAnimation.pause();
        rotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue = (Float) rotateAnimation.getAnimatedValue();
            }
        });
    }

    DownloadService messengerDownload;
    boolean boundDownload;
    private ServiceConnection connectionDownload = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {

            messengerDownload = null;
            boundDownload = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerDownload = ((DownloadService.ServicesBinder) service).getService();
            boundDownload = true;
        }
    };
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((MusicPlayerService.ServiceBinder) service).getService();
            handler.postDelayed(runnable, REFLASH_TIME);
            bound = true;
        }
    };

}


