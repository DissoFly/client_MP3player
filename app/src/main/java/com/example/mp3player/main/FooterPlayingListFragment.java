package com.example.mp3player.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;

import java.util.List;


/**
 * Created by DissoCapB on 2017/1/21.
 */

public class FooterPlayingListFragment extends Fragment implements View.OnClickListener{
    View view;
    ListView listView;
    private List<String> audioList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.fragment_main_footer_playing_list, null);
            listView=(ListView)view.findViewById(R.id.footer_playing_list);
            initData();
            listView.setAdapter(listAdapter);

        }
        return view;
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return audioList==null?0:audioList.size();
        }

        @Override
        public Object getItem(int i) {
            return audioList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view=null;
            if (convertView==null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_local_music_list_item, null);
            }else{
                view=convertView;
            }

            TextView musicName=(TextView)view.findViewById(R.id.text_local_music_name);
            String s1[] =audioList.get(i).split("/");
            musicName.setText(s1[s1.length-1]);
            return view;
        }
    };

    private void initData() {
        view.findViewById(R.id.layout_in).setOnClickListener(this);
        view.findViewById(R.id.layout_out).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_in:
                break;
            case R.id.layout_out:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
}
