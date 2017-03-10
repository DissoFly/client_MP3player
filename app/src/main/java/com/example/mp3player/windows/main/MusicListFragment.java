package com.example.mp3player.windows.main;

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
import com.example.mp3player.entity.MineMusicList;
import com.example.mp3player.service.MusicPlayerService;

/**
 * Created by DissoCapB on 2017/3/10.
 */

public class MusicListFragment extends Fragment implements View.OnClickListener {
    TextView listName;
    TextView listCreateTime;
    ListView listView;
    View view;
    MineMusicList mineMusicList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().bindService(new Intent(getActivity(),MusicPlayerService.class), connection, Context.BIND_AUTO_CREATE);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_music_list, null);
        }
        listView = (ListView) view.findViewById(R.id.music_playing_item_list);
        listName = (TextView) view.findViewById(R.id.music_list_name);
        listCreateTime = (TextView) view.findViewById(R.id.music_list_create_time);
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
            messenger.setNewMusic(position, mineMusicList.getMusicList());
    }


    BaseAdapter musicListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mineMusicList.getMusicList() == null ? 0 : mineMusicList.getMusicList().size();
        }

        @Override
        public Object getItem(int i) {
            return mineMusicList.getMusicList().get(i);
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
            name.setText(mineMusicList.getMusicList().get(i).getSongName());
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        listName.setText(mineMusicList.getListName());
        listCreateTime.setText(mineMusicList.getCreateDate().toString());
        listView.removeAllViewsInLayout();
        musicListAdapter.notifyDataSetInvalidated();
        listView.setAdapter(musicListAdapter);
    }

    public void setMineMusicList(MineMusicList mineMusicList) {
        this.mineMusicList = mineMusicList;
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
}
