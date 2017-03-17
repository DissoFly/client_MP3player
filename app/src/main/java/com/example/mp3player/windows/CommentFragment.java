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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.Comment;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/3/17.
 */

public class CommentFragment extends Fragment implements View.OnClickListener{
    View view;
    ListView listView;
    EditText editText;
    int songId;
    List<Comment> comments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_comment, null);
        }
        listView = (ListView) view.findViewById(R.id.comment_list);
        editText=(EditText)view.findViewById(R.id.edit_comment);
        initData();
        return view;
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return comments==null?0:comments.size();
        }

        @Override
        public Object getItem(int i) {
            return comments.get(i);
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
               // view = inflater.inflate(R.layout.widget_local_music_list_item, null);
            }else{
                view=convertView;
            }
            return view;
        }
    };

    //    void setTipsWithNoMusic() {
    //        if (audioList.size() > 0)
    //            view.findViewById(R.id.text_footer_no_music).setVisibility(View.GONE);
    //        else
    //            view.findViewById(R.id.text_footer_no_music).setVisibility(View.VISIBLE);
    //    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(),LoginService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private void initData() {
        view.findViewById(R.id.layout_out).setOnClickListener(this);
        view.findViewById(R.id.layout_in).setOnClickListener(this);
        view.findViewById(R.id.btn_comment_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_out:
                getActivity().onBackPressed();
                break;
            case R.id.btn_comment_send:
                commentSend();
                break;
            default:
                break;
        }
    }

    private void commentSend(){
        String text=editText.getText().toString();
        if(text.equals("")){
            Toast.makeText(getActivity(),"请输入内容",Toast.LENGTH_SHORT).show();
        }else if(messenger.getUser()==null){
            Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
        }else{
            commentSendConnent(text);
        }
    }







    private void commentSendConnent(String text) {
        RequestBody formBody = new FormBody.Builder()
                .add("songId", songId+"")
                .add("text", text)
                .add("userId", messenger.getUser().getUserId()+"")
                .build();
        Request request = HttpService.requestBuilderWithPath("comment/save").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"发送失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                if(data.equals("SUCCESS_SAVE")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"评论成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"评论失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

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
        }
    };
}
