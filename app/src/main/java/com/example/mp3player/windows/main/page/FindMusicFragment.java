package com.example.mp3player.windows.main.page;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.MusicList;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.windows.inputcells.ImgView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_LIST_FRAGMENT;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FindMusicFragment extends Fragment implements View.OnClickListener {
    final static int NEWS_LIST = 11;
    final static int MUSIC_LIST = 12;
    int listChoose = 0;
    int settingSelect=-1;
    int openFragInMain = 0;
    List<MusicList> musicLists = new ArrayList<>();
    ListView listView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page_find_music, null);
        listView = (ListView) view.findViewById(R.id.find_music_list);
        listView.setAdapter(listAdapter);
        initData();
        return view;
    }

    private void initData() {
        view.findViewById(R.id.btn_find_music_news).setOnClickListener(this);
        view.findViewById(R.id.btn_find_music_lists).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find_music_news:
                setList(NEWS_LIST);
                break;
            case R.id.btn_find_music_lists:
                setList(MUSIC_LIST);
                break;
            default:
                break;
        }
    }

    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return getListCount();
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
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_music_list, null);
                LinearLayout layout1=(LinearLayout)view.findViewById(R.id.layout1);
                LinearLayout layout2=(LinearLayout)view.findViewById(R.id.layout2);
                ImgView imgView1=(ImgView)view.findViewById(R.id.img_view1);
                ImgView imgView2=(ImgView)view.findViewById(R.id.img_view2);
                TextView name1=(TextView)view.findViewById(R.id.text_list_name1);
                TextView name2=(TextView)view.findViewById(R.id.text_list_name2);
                layout1.setVisibility(View.VISIBLE);
                imgView1.loadById(musicLists.get(i * 2 ).getSrcPath());
                name1.setText(musicLists.get(i * 2 ).getListName());
                layout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFragInMain = OPEN_MUSIC_LIST_FRAGMENT;
                        settingSelect=(i * 2);
                        OnFindMusicFragmentClickedListener.OnFindMusicFragmentClicked();
                    }
                });
                if (musicLists.size() >= (i + 1) * 2) {
                    layout2.setVisibility(View.VISIBLE);
                    imgView2.loadById(musicLists.get(i * 2 +1).getSrcPath());
                    name2.setText(musicLists.get(i * 2 +1).getListName());
                    layout2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFragInMain = OPEN_MUSIC_LIST_FRAGMENT;
                            settingSelect=(i * 2 +1);
                            OnFindMusicFragmentClickedListener.OnFindMusicFragmentClicked();
                        }
                    });
                } else {
                    layout2.setVisibility(View.INVISIBLE);
                }
            } else {
                view = convertView;
            }
            return view;
        }
    };

    private void setList(int choose) {
        switch (choose) {
            case NEWS_LIST:
                listChoose = NEWS_LIST;
                break;
            case MUSIC_LIST:
                listChoose = MUSIC_LIST;
                musicListConnect();
                break;
        }
    }

    private int getListCount() {
        switch (listChoose) {
            case NEWS_LIST:
                return 0;
            case MUSIC_LIST:
                return musicLists.size() == 0 ? 0 :( (musicLists.size()+1) / 2 );
            default:
                return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(NEWS_LIST);
    }

    void musicListConnect() {
        Request request = HttpService.requestBuilderWithPath("musicList/allList/" + 0).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final List<MusicList> data = new Gson().fromJson(response.body().string(), new TypeToken<List<MusicList>>() {
                    }.getType());
                    musicLists = data;
                    if (musicLists == null)
                        musicLists = new ArrayList<MusicList>();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeAllViewsInLayout();
                            listAdapter.notifyDataSetInvalidated();
                            listView.setAdapter(listAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }




    public MusicList getMusicList(){
        return musicLists.get(settingSelect);
    }

    public int getOpenFragmentInMain() {
        return openFragInMain;
    }


    public static interface OnFindMusicFragmentClickedListener {
        void OnFindMusicFragmentClicked();
    }

    FindMusicFragment.OnFindMusicFragmentClickedListener OnFindMusicFragmentClickedListener;

    public void setOnFindMusicFragmentClickedListener(OnFindMusicFragmentClickedListener onFindMusicFragmentClickedListener) {
        this.OnFindMusicFragmentClickedListener = onFindMusicFragmentClickedListener;
    }
}
