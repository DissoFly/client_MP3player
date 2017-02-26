package com.example.mp3player.main.page;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mp3player.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mp3player.main.OpenFragmentCount.OPEN_DOWNLOAD_FRAGMENT;
import static com.example.mp3player.main.OpenFragmentCount.OPEN_LOCAL_MUSIC_FRAGMENT;


/**
 * Created by DissoCapB on 2017/1/16.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    View view;
    int openFragInMain = 0;
    ListView listView;
    List<String> listItemName=new ArrayList<>();
    List<Integer> listItemNumber=new ArrayList<>();
    List<Integer> listItemSrc=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_page_mine, null);
            listView=(ListView)view.findViewById(R.id.page_mine_list);
            initData();
            setListItem();
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openFragInMain = listItemNumber.get(i);
                    OnBtnLocalMusicClickedListener.OnBtnLocalMusicClicked();
                }
            });
        }
        return view;
    }

    private void setListItem(){
        listItemName=new ArrayList<>();
        listItemNumber=new ArrayList<>();
        listItemSrc=new ArrayList<>();
        setOneListItem("本地音乐",OPEN_LOCAL_MUSIC_FRAGMENT,R.mipmap.ic_launcher);
        setOneListItem("下载管理",OPEN_DOWNLOAD_FRAGMENT,R.mipmap.ic_launcher);
    }
    private void setOneListItem(String listName,int itemNumber,int itemSrc){
        listItemName.add(listName);
        listItemNumber.add(itemNumber);
        listItemSrc.add(itemSrc);
    }

    BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listItemNumber==null?0:listItemNumber.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View convertView=null;
            if (convertView==null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_img_text_list_item, null);
            }else{
                view=convertView;
            }
            ImageView itemImg=(ImageView)view.findViewById(R.id.list_img);
            TextView itemName=(TextView)view.findViewById(R.id.list_text);
            itemImg.setImageResource(listItemSrc.get(i));
            itemName.setText(listItemName.get(i));

            return view;
        }
    };

    private void initData() {
        //view.findViewById(btn_main_header_drawer).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
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
