package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.MineMusicList;
import com.example.mp3player.entity.MusicList;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.entity.PublicSong;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.MusicPlayerService;
import com.example.mp3player.windows.inputcells.ImgView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/3/10.
 */

public class MusicListFragment extends Fragment implements View.OnClickListener {
    TextView listName;
    TextView listCreateTime;
    TextView listAbout;
    ImgView imgView;
    ImgView bgImgView;
    ListView listView;
    View view;
    MineMusicList mineMusicList = null;
    MusicList musicList = null;
    int type = 0;
    final int MINE_MUSIC_LIST = 11;
    final int MUSIC_LIST = 12;
    List<PlayingItem> playingItems = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().bindService(new Intent(getActivity(), MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_music_list, null);
        }
        imgView= (ImgView) view.findViewById(R.id.img_view);
        bgImgView= (ImgView) view.findViewById(R.id.bg_img_view);
        listView = (ListView) view.findViewById(R.id.music_playing_item_list);
        listName = (TextView) view.findViewById(R.id.music_list_name);
        listCreateTime = (TextView) view.findViewById(R.id.music_list_create_time);
        listAbout=(TextView) view.findViewById(R.id.music_list_about);
        initData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
        return view;
    }

    void onItemClicked(int position) {
        if (type == MINE_MUSIC_LIST)
            messenger.setNewMusic(position, mineMusicList.getMusicList());
        else if (type == MUSIC_LIST)
            messenger.setNewMusic(position, playingItems);

    }


    BaseAdapter musicListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if (type == MINE_MUSIC_LIST)
                return mineMusicList.getMusicList() == null ? 0 : mineMusicList.getMusicList().size();
            else if (type == MUSIC_LIST)
                return playingItems == null ? 0 : playingItems.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_local_music_list_item, null);
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.text_local_music_name);
            if (type == MINE_MUSIC_LIST)
                name.setText(mineMusicList.getMusicList().get(i).getSongName());
            else if (type == MUSIC_LIST)
                name.setText(playingItems.get(i).getSongName());
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        reload();

    }
    void reload(){
        if (type == MINE_MUSIC_LIST) {
            listName.setText(mineMusicList.getListName());
            String time= DateFormat.format("yyyy-MM-dd hh:mm",mineMusicList.getCreateDate()).toString();
            listCreateTime.setText(time);
            listAbout.setVisibility(View.INVISIBLE);
            listView.removeAllViewsInLayout();
            musicListAdapter.notifyDataSetInvalidated();
            listView.setAdapter(musicListAdapter);
            imgView.loadListNull();
            bgImgView.loadListNull();

        } else if (type == MUSIC_LIST) {
            listName.setText(musicList.getListName());
            String time= DateFormat.format("yyyy-MM-dd hh:mm",musicList.getCreateDate()).toString();
            listCreateTime.setText(time);
            listAbout.setVisibility(View.VISIBLE);
            listAbout.setText(musicList.getListAbout());
            imgView.loadById(musicList.getSrcPath());
            bgImgView.loadById(musicList.getSrcPath());
            findOnlineMusicConnect(musicList.getMusics());
        }
    }

    public void setMineMusicList(MineMusicList mineMusicList) {
        type = MINE_MUSIC_LIST;
        this.mineMusicList = mineMusicList;
    }

    public void setMusicList(MusicList musicList) {
        type = MUSIC_LIST;
        this.musicList = musicList;
    }

    private void initData() {
        view.findViewById(R.id.fragment_music_list).setOnClickListener(this);
        view.findViewById(R.id.btn_music_list_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_music_list_back:
                getActivity().onBackPressed();
                break;
        }
    }

    MusicPlayerService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((MusicPlayerService.ServiceBinder) service).getService();
            bound = true;
        }
    };

    void findOnlineMusicConnect(String musics) {
        RequestBody formBody = new FormBody.Builder()
                .add("musics", musics)
                .build();
        Request request = HttpService.requestBuilderWithPath("musicList/song_messages").post(formBody).build();

        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final List<PublicSong> data = new Gson().fromJson(response.body().string(), new TypeToken<List<PublicSong>>() {
                    }.getType());
                    playingItems = new ArrayList<>();
                    for (PublicSong publicSong : data) {
                        PlayingItem playingItem = new PlayingItem();
                        playingItem.setOnlineSongId(publicSong.getSongID());
                        playingItem.setSongName(publicSong.getSongName());
                        playingItem.setArtist(publicSong.getArtist());
                        playingItem.setAlbum(publicSong.getAlbum());
                        playingItem.setFilePath(HttpService.serverAddress + "api/online_song/" + publicSong.getSongID());
                        playingItem.setOnline(true);

                        playingItems.add(playingItem);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeAllViewsInLayout();
                            musicListAdapter.notifyDataSetInvalidated();
                            listView.setAdapter(musicListAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
