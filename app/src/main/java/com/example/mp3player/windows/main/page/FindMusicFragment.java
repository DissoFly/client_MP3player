package com.example.mp3player.windows.main.page;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.Information;
import com.example.mp3player.entity.MusicList;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.windows.SrcList.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.mp3player.service.HttpService.serverAddress;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_LIST_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_NEWS_FRAGMENT;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FindMusicFragment extends Fragment implements View.OnClickListener {
    final static int NEWS_LIST = 11;
    final static int MUSIC_LIST = 12;
    int listChoose = 0;
    int settingSelect = -1;
    int newsIdSelect = -1;
    int openFragInMain = 0;
    List<MusicList> musicLists = new ArrayList<>();
    ListView listView;
    View view;
    List<Information> informations = new ArrayList<>();
    Button btnNews;
    Button btnList;
    ImageLoader imageLoader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page_find_music, null);
        listView = (ListView) view.findViewById(R.id.find_music_list);
        btnNews=(Button) view.findViewById(R.id.btn_find_music_news);
        btnList=(Button) view.findViewById(R.id.btn_find_music_lists);
        initData();
        imageLoader = new ImageLoader(container.getContext());
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

    BaseAdapter newsListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return informations.size() == 0 ? 0 : informations.size();
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
                view = inflater.inflate(R.layout.widget_news_item, null);
            } else {
                view = convertView;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragInMain = OPEN_NEWS_FRAGMENT;
                    newsIdSelect = informations.get(i).getInformationId();
                    OnFindMusicFragmentClickedListener.OnFindMusicFragmentClicked();
                }
            });
            TextView title = (TextView) view.findViewById(R.id.text_news_title);
            ImageView imgView=(ImageView)view.findViewById(R.id.img_news) ;
            imageLoader.DisplayImage(serverAddress()+"api/news/news_src/"+informations.get(i).getInformationId(), imgView);
            title.setText(informations.get(i).getTitle());
            return view;
        }
    };

    BaseAdapter musicListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return musicLists.size() == 0 ? 0 : ((musicLists.size() + 1) / 2);
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
                LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.layout1);
                LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.layout2);
                ImageView imgView1=(ImageView)view.findViewById(R.id.img_view1);
                ImageView imgView2=(ImageView)view.findViewById(R.id.img_view2);
                TextView name1 = (TextView) view.findViewById(R.id.text_list_name1);
                TextView name2 = (TextView) view.findViewById(R.id.text_list_name2);
                layout1.setVisibility(View.VISIBLE);
                imageLoader.DisplayImage(HttpService.serverAddress() + "api/musicList/listsrc/"+musicLists.get(i * 2).getSrcPath(), imgView1);
                name1.setText(musicLists.get(i * 2).getListName());
                layout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFragInMain = OPEN_MUSIC_LIST_FRAGMENT;
                        settingSelect = (i * 2);
                        OnFindMusicFragmentClickedListener.OnFindMusicFragmentClicked();
                    }
                });
                if (musicLists.size() >= (i + 1) * 2) {
                    layout2.setVisibility(View.VISIBLE);
                    imageLoader.DisplayImage(HttpService.serverAddress() + "api/musicList/listsrc/"+musicLists.get(i * 2 + 1).getSrcPath(), imgView2);
                    name2.setText(musicLists.get(i * 2 + 1).getListName());
                    layout2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFragInMain = OPEN_MUSIC_LIST_FRAGMENT;
                            settingSelect = (i * 2 + 1);
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
                btnNews.setTextColor(Color.parseColor("#d33a31"));
                btnNews.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnList.setTextColor(Color.parseColor("#000000"));
                btnList.setBackground(null);
                listChoose = NEWS_LIST;
                newsConnect();
                break;
            case MUSIC_LIST:
                btnList.setTextColor(Color.parseColor("#d33a31"));
                btnList.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnNews.setTextColor(Color.parseColor("#000000"));
                btnNews.setBackground(null);
                listChoose = MUSIC_LIST;
                musicListConnect();
                break;
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
                    final String data=response.body().string();
                    musicLists= new Gson().fromJson(data, new TypeToken<List<MusicList>>() {
                    }.getType());
                    if (musicLists == null)
                        musicLists = new ArrayList<>();
                    else
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

    void newsConnect() {
        Request request = HttpService.requestBuilderWithPath("news/getAll/" + 0).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data=response.body().string();
                    System.out.println(data);
                    informations= new Gson().fromJson(data, new TypeToken<List<Information>>() {
                    }.getType());
                    if (informations == null)
                        informations = new ArrayList<>();
                    else
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.removeAllViewsInLayout();
                                newsListAdapter.notifyDataSetInvalidated();
                                listView.setAdapter(newsListAdapter);
                            }
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public MusicList getMusicList() {
        return musicLists.get(settingSelect);
    }

    public int getNewsIdSelect() {
        return newsIdSelect;
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
