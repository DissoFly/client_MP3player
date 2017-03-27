package com.example.mp3player.windows.main.page.findMusic;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.entity.Information;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.windows.inputcells.ImgView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/3/26.
 */

public class NewsFragment extends Fragment implements View.OnClickListener{
    View view;
    TextView title;
    TextView text;
    ImgView imgView;
    int newsId;

    Information information;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_news, null);
        imgView=(ImgView)view.findViewById(R.id.img_news);
        title=(TextView)view.findViewById(R.id.text_news_title);
        text=(TextView)view.findViewById(R.id.text_news);
        initData();
        newsConnect();
        return view;
    }
    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }


    private void initData() {
        view.findViewById(R.id.fragment_news).setOnClickListener(this);
        view.findViewById(R.id.btn_news_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_news_back:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
    void newsConnect() {
        Request request = HttpService.requestBuilderWithPath("news/getOne/" + newsId).get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();
                    information = new Gson().fromJson(data, Information.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgView.load("news/news_src", newsId + "");
                            title.setText(information.getTitle());
                            text.setText(information.getText());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


