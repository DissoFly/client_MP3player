package com.example.mp3player.main.page;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.Downloading;
import com.example.mp3player.service.DataService;
import com.example.mp3player.service.HttpService;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FindMusicFragment extends Fragment implements View.OnClickListener{

    View view;
    TextView textView;
    List<Downloading> downloadingList;
    Dao<Downloading, Integer> downloadingDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page_find_music, null);
        textView = (TextView) view.findViewById(R.id.textView);
        initData();
        return view;
    }
    private void initData() {
        view.findViewById(R.id.test1).setOnClickListener(this);
        view.findViewById(R.id.test2).setOnClickListener(this);
        view.findViewById(R.id.test3).setOnClickListener(this);
        view.findViewById(R.id.test4).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test1:
                Downloading downloading=new Downloading();
                downloading.setTotalBytes(10);
                downloading.setContentLength(11);
                downloading.setBreakPoints(12);
                downloading.setLocalPath("/123/");
                downloading.setMusicId(1);
//                try {
//                    downloadingDao.create(downloading);
//                    Log.d("success","success");
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    Log.d(this.toString(),"fail");
//                }
                Toast.makeText(getActivity(),"禁止添加",Toast.LENGTH_SHORT).show();
                break;
            case R.id.test2:
                try {
                    downloadingDao.deleteById(7);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.test3:
                try {
                    downloadingList =downloadingDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String s="";
                for (Downloading dl:downloadingList){
                    s=s.concat(new Gson().toJson(dl)+"\n");
                }
                textView.setText(s);

//                try {
//                    List<Downloading>downloadings= downloadingDao.queryBuilder().
//                            where().
//                            eq("id", 1).query();
//                    textView.setText(new Gson().toJson(downloadings.get(0)));
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

                break;
            case R.id.test4:
                try {
                    Downloading dl=downloadingDao.queryForId(2);
                    dl.setLocalPath("12222222222222");
                    downloadingDao.update(dl);
                    Log.d(this.toString(),"success gai");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        try {
            DataService dataService = DataService.getInstance(getActivity());
            downloadingDao=dataService.getDownloadingDao();
            downloadingList =downloadingDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reload();

    }

    void reload() {
        Request request = HttpService.requestBuilderWithPath("").get().build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
//                textView.setText(e.toString());
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
