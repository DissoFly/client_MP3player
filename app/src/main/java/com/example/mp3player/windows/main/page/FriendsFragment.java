package com.example.mp3player.windows.main.page;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.Friend;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.windows.inputcells.AvatarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/1/16.
 */

public class FriendsFragment extends Fragment implements View.OnClickListener {
    View view;
    Button btnNews;
    Button btnInbox;
    Button btnAdds;
    Button btnBeAdds;
    ListView listView;

    List<Friend> friends;

    final String NEWS_CHOOSE="news";
    final String INBOX_CHOOSE="inbox";
    final String ADD_CHOOSE="getAddList";
    final String BE_ADD_CHOOSE="getBeAddList";
    String choose;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page_friends, null);
        btnNews = (Button) view.findViewById(R.id.btn_friend_news_list);
        btnInbox = (Button) view.findViewById(R.id.btn_friend_inbox_list);
        btnAdds = (Button) view.findViewById(R.id.btn_friend_add_list);
        btnBeAdds = (Button) view.findViewById(R.id.btn_friend_be_add_list);
        listView = (ListView) view.findViewById(R.id.list_friend);
        initData();
        return view;
    }

    private void initData() {
        view.findViewById(R.id.fragment_main_page_mine_friends).setOnClickListener(this);
        btnNews.setOnClickListener(this);
        btnInbox.setOnClickListener(this);
        btnAdds.setOnClickListener(this);
        btnBeAdds.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_friend_news_list:
                choose=NEWS_CHOOSE;
                break;
            case R.id.btn_friend_inbox_list:
                choose=INBOX_CHOOSE;
                break;
            case R.id.btn_friend_add_list:
                choose=ADD_CHOOSE;
                addsConnect(ADD_CHOOSE);
                break;
            case R.id.btn_friend_be_add_list:
                choose=BE_ADD_CHOOSE;
                addsConnect(BE_ADD_CHOOSE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), LoginService.class), connection, Context.BIND_AUTO_CREATE);
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return friends == null ? 0 : friends.size();
        }

        @Override
        public Object getItem(int i) {
            return friends.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.widget_friend_item, null);

            } else {
                view = convertView;
            }
            AvatarView avatarView=(AvatarView)view.findViewById(R.id.head_avatar);
            TextView name=(TextView)view.findViewById(R.id.text_friend_name);
            Button btnAdd=(Button)view.findViewById(R.id.btn_friend_add);
            switch (choose){
                case  ADD_CHOOSE:
                    avatarView.load(friends.get(i).getFriendUserId());
                    btnAdd.setText("已关注");
                    break;
                case BE_ADD_CHOOSE:
                    avatarView.load(friends.get(i).getUserId());
                    btnAdd.setText("关注");
                    break;
                default:
                    break;
            }
            name.setText(friends.get(i).getFriendName());

            return view;
        }
    };


    void newsConnect() {

    }


    void addsConnect(String choose) {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        } else {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("friend/"+choose).post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                System.out.println(data);
                try {
                    friends = new Gson().fromJson(data, new TypeToken<List<Friend>>() {
                    }.getType());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeAllViewsInLayout();
                            listAdapter.notifyDataSetInvalidated();
                            listView.setAdapter(listAdapter);
                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "连接出错", Toast.LENGTH_SHORT).show();
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
