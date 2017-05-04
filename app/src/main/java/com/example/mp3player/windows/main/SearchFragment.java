package com.example.mp3player.windows.main;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.PlayingItem;
import com.example.mp3player.entity.PublicSong;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.MusicPlayerService;
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
 * Created by DissoCapB on 2017/3/6.
 */

public class SearchFragment extends Fragment implements View.OnClickListener {

    View view;
    Spinner spinnerSearch;
    EditText editSearch;
    ListView listView;
    int searchType = 0;
    List<PublicSong> searchSongs=new ArrayList<>();

    final String SEARCH_SONGNAME = "songName";
    final String SEARCH_ARTIST = "artist";
    final String SEARCH_ALBUM = "album";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_search, null);
        spinnerSearch = (Spinner) view.findViewById(R.id.spinner_search);
        editSearch = (EditText) view.findViewById(R.id.edit_search);
        listView = (ListView) view.findViewById(R.id.search_list);

        setListView();
        setSpinner();
        initData();
        return view;
    }

    private void setListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlayingItem playingItem=new PlayingItem();
                playingItem.setSongName(searchSongs.get(i).getSongName());
                playingItem.setArtist(searchSongs.get(i).getArtist());
                playingItem.setAlbum(searchSongs.get(i).getAlbum());
                playingItem.setFilePath(HttpService.serverAddress()+"api/online_song/"+searchSongs.get(i).getSongID());
                playingItem.setOnline(true);
                playingItem.setOnlineSongId(searchSongs.get(i).getSongID());
                messenger.setOneAndPlay(playingItem);
            }
        });
        listView.setAdapter(listAdapter);
    }

    private void setSpinner() {
        final List<String> data_list = new ArrayList<>();
        data_list.add("歌曲");
        data_list.add("歌手");
        data_list.add("专辑");
        searchType = 0;

        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(getActivity(),R.layout.widget_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerSearch.setAdapter(arr_adapter);
        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return searchSongs.size();
        }

        @Override
        public Object getItem(int i) {
            return searchSongs.size()==0?null:searchSongs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_search_item, null);
                TextView songName = (TextView) view.findViewById(R.id.text_song_name);
                TextView artist = (TextView) view.findViewById(R.id.text_artist);
                TextView album = (TextView) view.findViewById(R.id.text_album);
                songName.setText(searchSongs.get(i).getSongName());
                artist.setText(searchSongs.get(i).getArtist());
                album.setText(searchSongs.get(i).getAlbum());
            } else {
                view = convertView;
            }
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void initData() {
        view.findViewById(R.id.fragment_main_search).setOnClickListener(this);
        view.findViewById(R.id.btn_search_back).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_search:
                String text = editSearch.getText().toString();
                if (text.equals(""))
                    Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
                else
                    switch (searchType) {
                        case 0:
                            searchConnect(SEARCH_SONGNAME, text);
                            break;
                        case 1:
                            searchConnect(SEARCH_ARTIST, text);
                            break;
                        case 2:
                            searchConnect(SEARCH_ALBUM, text);
                            break;
                        default:
                            break;
                    }
                break;
            default:
                break;

        }
    }
    MusicPlayerService messenger;
    boolean bound;
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=((MusicPlayerService.ServiceBinder) service).getService();
            bound=true;
        }
    };

    void searchConnect(String searchType, String searchText) {
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = HttpService.requestBuilderWithPath("song_search/" + searchType + "/" + searchText).post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "搜索失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                searchSongs = new Gson().fromJson(data, new TypeToken<List<PublicSong>>() {
                }.getType());
                System.out.println(data);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.removeAllViewsInLayout();
                        listAdapter.notifyDataSetInvalidated();
                        listView.setAdapter(listAdapter);
                    }
                });

            }
        });
    }
}
