package com.example.mp3player.main.page;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FindMusicFragment extends Fragment {

    View view;
    TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page_find_music, null);
        textView = (TextView) view.findViewById(R.id.textView);
        return view;
    }


    @Override
    public void onResume() {

        super.onResume();
        reload();
    }

    void reload() {
        Request request = HttpService.requestBuilderWithPath("").get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                textView.setText(e.toString());
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String data = response.body().string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(data);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }
}
