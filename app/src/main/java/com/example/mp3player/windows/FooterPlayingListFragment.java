package com.example.mp3player.windows;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;
import com.example.mp3player.service.MusicPlayerService;

import java.util.List;


/**
 * Created by DissoCapB on 2017/1/21.
 */

public class FooterPlayingListFragment extends Fragment implements View.OnClickListener{
    View view;
    ListView listView;
    MusicPlayerService messenger;
    boolean bound;
    private List<String> audioList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.fragment_main_footer_playing_list, null);
            getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
            listView=(ListView)view.findViewById(R.id.footer_playing_list);
            initData();
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemClicked(position);

                }
            });
        }
        return view;
    }
    void onItemClicked(int position) {
        messenger.start(position);
        getActivity().onBackPressed();
    }
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger=((MusicPlayerService.ServiceBinder) service).getService();
            audioList=messenger.getAudioList();
            setTipsWithNoMusic();
            bound=true;
        }
    };

    void setTipsWithNoMusic(){
        if (audioList.size()>0)
            view.findViewById(R.id.text_footer_no_music).setVisibility(View.GONE);
        else
            view.findViewById(R.id.text_footer_no_music).setVisibility(View.VISIBLE);
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
