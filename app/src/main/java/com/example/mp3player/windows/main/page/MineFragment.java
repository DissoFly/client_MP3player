package com.example.mp3player.windows.main.page;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.MineMusicList;
import com.example.mp3player.service.LoginService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_DOWNLOAD_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_LOCAL_MUSIC_FRAGMENT;
import static com.example.mp3player.windows.main.OpenFragmentCount.OPEN_MUSIC_LIST_FRAGMENT;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    View view;
    int openFragInMain = 0;
    ListView localListView;
    ListView musicListView;
    List<String> listItemName = new ArrayList<>();
    List<Integer> listItemNumber = new ArrayList<>();
    List<Integer> listItemSrc = new ArrayList<>();
    List<MineMusicList> mineMusicLists = new ArrayList<>();
    int settingSelect=-1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            getActivity().bindService(new Intent(getActivity(), LoginService.class), connection, Context.BIND_AUTO_CREATE);
            view = inflater.inflate(R.layout.fragment_main_page_mine, null);
            localListView = (ListView) view.findViewById(R.id.page_mine_local_list);
            musicListView = (ListView) view.findViewById(R.id.page_mine_music_list);

            initData();
            setListItem();


            localListView.setAdapter(localListAdapter);

            localListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openFragInMain = listItemNumber.get(i);
                    OnBtnLocalMusicClickedListener.OnBtnLocalMusicClicked();
                }
            });
            musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    onResume();
                    openFragInMain = OPEN_MUSIC_LIST_FRAGMENT;
                    settingSelect=i;
                    OnBtnLocalMusicClickedListener.OnBtnLocalMusicClicked();
                }
            });

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
        musicListView.removeAllViewsInLayout();
        musicListAdapter.notifyDataSetInvalidated();
        musicListView.setAdapter(musicListAdapter);


    }


    private void setListItem() {
        listItemName = new ArrayList<>();
        listItemNumber = new ArrayList<>();
        listItemSrc = new ArrayList<>();
        setOneListItem("本地音乐", OPEN_LOCAL_MUSIC_FRAGMENT, R.mipmap.ic_music_red);
        setOneListItem("下载管理", OPEN_DOWNLOAD_FRAGMENT, R.mipmap.ic_download_red);
    }

    private void setOneListItem(String listName, int itemNumber, int itemSrc) {
        listItemName.add(listName);
        listItemNumber.add(itemNumber);
        listItemSrc.add(itemSrc);
    }

    BaseAdapter musicListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mineMusicLists == null ? 0 : mineMusicLists.size();
        }

        @Override
        public Object getItem(int i) {
            return mineMusicLists.get(i);
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
                view = inflater.inflate(R.layout.widget_mine_music_list, null);
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.music_list_name);
            TextView size = (TextView) view.findViewById(R.id.music_list_size);
            name.setText(mineMusicLists.get(i).getListName());
            size.setText("共"+mineMusicLists.get(i).getMusicList().size()+"首");
            return view;
        }
    };

    BaseAdapter localListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listItemNumber == null ? 0 : listItemNumber.size();
        }

        @Override
        public Object getItem(int i) {
            return listItemNumber.get(i);
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
                view = inflater.inflate(R.layout.widget_img_text_list_item, null);
            } else {
                view = convertView;
            }
            ImageView itemImg = (ImageView) view.findViewById(R.id.list_img);
            TextView itemName = (TextView) view.findViewById(R.id.list_text);
            itemImg.setImageResource(listItemSrc.get(i));
            itemName.setText(listItemName.get(i));

            return view;
        }
    };

    private void initData() {
        view.findViewById(R.id.btn_mine_add_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mine_add_list:
                final EditText editText = new EditText(getActivity());
                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入歌单名")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = editText.getText().toString();
                                if (text.equals("")) {
                                    Toast.makeText(getActivity(), "不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    addMusicList(text);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            default:
                break;
        }
    }

    public void addMusicList(String text) {
        MineMusicList mineMusicList = new MineMusicList();
        mineMusicList.setListName(text);
        mineMusicList.onPrePersist();
        if (bound) {
            if (messenger.getUser() != null) {
                mineMusicList.setUserId(messenger.getUser().getUserId());
            } else {
                mineMusicList.setUserId(-1);
            }
        } else {
            mineMusicList.setUserId(-1);
        }
        mineMusicLists.add(mineMusicList);
        save();
    }

    LoginService messenger;
    boolean bound;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            bound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = ((LoginService.ServicesBinder) service).getService();
            bound = true;
            onResume();
        }
    };

    /////////////////////////////////本地文件路径储存↓///////////////////////////////////////
    public void save() {                            //写入文件
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = getActivity().openFileOutput("mineMusicLists", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(new Gson().toJson(mineMusicLists));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        mineMusicLists = new ArrayList<>();
        try {
            in = getActivity().openFileInput("mineMusicLists");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null)
                content.append(line);
            mineMusicLists = new Gson().fromJson(content.toString(), new TypeToken<List<MineMusicList>>() {
            }.getType());

        } catch (IOException e) {
            System.out.println("可能没有本地文件：mineMusicLists");
            e.printStackTrace();
        }
    }


    public MineMusicList getMineMusicList(){
        return mineMusicLists.get(settingSelect);
    }

    public int getOpenFragmentInMain() {
        return openFragInMain;
    }


    public static interface OnBtnLocalMusicClickedListener {
        void OnBtnLocalMusicClicked();
    }

    OnBtnLocalMusicClickedListener OnBtnLocalMusicClickedListener;

    public void setOnBtnLocalMusicClickedListener(OnBtnLocalMusicClickedListener onBtnLocalMusicClickedListener) {
        this.OnBtnLocalMusicClickedListener = onBtnLocalMusicClickedListener;
    }
}
