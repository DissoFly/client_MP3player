package com.example.mp3player.windows.main.page;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
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
import com.example.mp3player.entity.FriendRead;
import com.example.mp3player.entity.InboxList;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
import com.example.mp3player.windows.InboxActivity;
import com.example.mp3player.windows.ZoneActivity;
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
    List<FriendRead> friendReads;
    List<InboxList> inboxLists;

    final String NEWS_CHOOSE="news";
    final String INBOX_CHOOSE="inbox";
    final String ADD_CHOOSE="getAddList";
    final String BE_ADD_CHOOSE="getBeAddList";
    String choose=NEWS_CHOOSE;
    int openZoneId;



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
                setList(choose);
                break;
            case R.id.btn_friend_inbox_list:
                choose=INBOX_CHOOSE;
                setList(choose);
                break;
            case R.id.btn_friend_add_list:
                choose=ADD_CHOOSE;
                setList(choose);
                break;
            case R.id.btn_friend_be_add_list:
                choose=BE_ADD_CHOOSE;
                setList(choose);
                break;
            default:
                break;
        }
    }

    private void setList(String choose) {
        buttonChange(choose);
        switch (choose) {
            case NEWS_CHOOSE:
                newsConnect();
                break;
            case INBOX_CHOOSE:
                inboxListConnect();
                break;
            case ADD_CHOOSE:
                addsConnect(ADD_CHOOSE);
                break;
            case BE_ADD_CHOOSE:
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

    BaseAdapter addListAdapter = new BaseAdapter() {

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
                    openZoneId=friends.get(i).getFriendUserId();
                    avatarView.load(openZoneId);
                    btnAdd.setText("已关注");
                    break;
                case BE_ADD_CHOOSE:
                    openZoneId=friends.get(i).getUserId();
                    avatarView.load(openZoneId);
                    btnAdd.setText("关注");
                    break;
                default:
                    break;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itnt=new Intent(getActivity(), ZoneActivity.class);
                    itnt.putExtra("openZoneId",openZoneId);
                    startActivityForResult(itnt, 0);
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFriend(openZoneId);
                }
            });
            name.setText(friends.get(i).getFriendName());

            return view;
        }
    };


    BaseAdapter inboxListAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return inboxLists == null ? 0 : inboxLists.size();
        }

        @Override
        public Object getItem(int i) {
            return inboxLists.get(i);
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
                view = inflater.inflate(R.layout.widget_inboxlist_item, null);
                final InboxList inboxList = inboxLists.get(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (messenger.getUser() != null) {
                            int userId = messenger.getUser().getUserId();
                            Intent itnt=new Intent(getActivity(), InboxActivity.class);
                            itnt.putExtra("userId",userId);
                            itnt.putExtra("friendId",inboxList.getFriendId());
                            startActivityForResult(itnt, 0);
                        }else{
                            Toast.makeText(getActivity(),"请登录",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                TextView name = (TextView) view.findViewById(R.id.inboxlist_name);
                TextView time = (TextView) view.findViewById(R.id.inboxlist_last_time);
                TextView text = (TextView) view.findViewById(R.id.inboxlist_last_text);
                AvatarView avatar = (AvatarView) view.findViewById(R.id.avatar);
                name.setText(inboxList.getFriendName());
                avatar.load(inboxList.getFriendId());

                String t = DateFormat.format("yyyy年MM月dd日   hh:mm:ss", inboxList.getCreateDate())
                        .toString();
                time.setText(t);
                String s="";
                if (inboxList.getUnReadNumber()>0){
                    s="(未读"+inboxList.getUnReadNumber()+"条) ";
                }
                text.setText(s+inboxList.getText());
            } else {
                view = convertView;
            }
            return view;
        }
    };

    BaseAdapter newsListAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return friendReads == null ? 0 : friendReads.size();
        }

        @Override
        public Object getItem(int i) {
            return friendReads.get(i);
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
                view = inflater.inflate(R.layout.widget_friendread_item, null);
                TextView textView=(TextView)view.findViewById(R.id.text_friendread);
                textView.setText(friendReads.get(i).getText());
            } else {
                view = convertView;
            }
            return view;
        }
    };


    void newsConnect() {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        }
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("friend/getFriendNews/").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                try {
                    friendReads = new Gson().fromJson(data, new TypeToken<List<FriendRead>>() {
                    }.getType());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeAllViewsInLayout();
                            newsListAdapter.notifyDataSetInvalidated();
                            listView.setAdapter(newsListAdapter);
                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data.equals("IN_BLACKLIST")) {
                                Toast.makeText(getActivity(), "访问限制", Toast.LENGTH_SHORT).show();
                            } else if (data.equals("GET_ALL")) {
                                Toast.makeText(getActivity(), "最后一页了", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "连接出错", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    void reload(){
        switch (choose){
            case NEWS_CHOOSE:
                listView.removeAllViewsInLayout();
                newsListAdapter.notifyDataSetInvalidated();
                listView.setAdapter(newsListAdapter);
                break;
            case INBOX_CHOOSE:
                listView.removeAllViewsInLayout();
                inboxListAdapter.notifyDataSetInvalidated();
                listView.setAdapter(inboxListAdapter);
                break;
            case ADD_CHOOSE:
                listView.removeAllViewsInLayout();
                addListAdapter.notifyDataSetInvalidated();
                listView.setAdapter(addListAdapter);
                break;
            case BE_ADD_CHOOSE:
                listView.removeAllViewsInLayout();
                addListAdapter.notifyDataSetInvalidated();
                listView.setAdapter(addListAdapter);
                break;

        }

    }

    void inboxListConnect() {
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
        Request request = HttpService.requestBuilderWithPath("inbox/getInboxList").post(formBody).build();
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
                    inboxLists = new Gson().fromJson(data, new TypeToken<List<InboxList>>() {
                    }.getType());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reload();
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
                            reload();
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

    private void addFriend(final int friendId) {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        }
        final RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .add("friendId", friendId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("friend/addFriend").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (data){
                            case "SUCCESS_ADD":
                                Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
                                reload();
                                break;
                            case "SUCCESS_DELECT":
                                Toast.makeText(getActivity(), "已取消关注", Toast.LENGTH_SHORT).show();
                                reload();
                                break;
                            case "IN_BLACKLIST":
                                Toast.makeText(getActivity(), "请在设置中管理黑名单", Toast.LENGTH_SHORT).show();
                                reload();
                                break;
                            case "WRONG":
                                Toast.makeText(getActivity(), "数据错误", Toast.LENGTH_SHORT).show();
                                break;
                            case "PLEASE_LOGIN":
                                Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "连接错误", Toast.LENGTH_SHORT).show();
                                System.out.println(data);
                                break;

                        }
                    }
                });

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
            setList(choose);
        }
    };
    void buttonChange(String choose){
        switch (choose){
            case NEWS_CHOOSE:
                btnNews.setTextColor(Color.parseColor("#d33a31"));
                btnNews.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnInbox.setTextColor(Color.parseColor("#000000"));
                btnInbox.setBackground(null);
                btnAdds.setTextColor(Color.parseColor("#000000"));
                btnAdds.setBackground(null);
                btnBeAdds.setTextColor(Color.parseColor("#000000"));
                btnBeAdds.setBackground(null);
                break;
            case INBOX_CHOOSE:
                btnNews.setTextColor(Color.parseColor("#000000"));
                btnNews.setBackground(null);
                btnInbox.setTextColor(Color.parseColor("#d33a31"));
                btnInbox.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnAdds.setTextColor(Color.parseColor("#000000"));
                btnAdds.setBackground(null);
                btnBeAdds.setTextColor(Color.parseColor("#000000"));
                btnBeAdds.setBackground(null);
                break;
            case ADD_CHOOSE:
                btnNews.setTextColor(Color.parseColor("#000000"));
                btnNews.setBackground(null);
                btnInbox.setTextColor(Color.parseColor("#000000"));
                btnInbox.setBackground(null);
                btnAdds.setTextColor(Color.parseColor("#d33a31"));
                btnAdds.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                btnBeAdds.setTextColor(Color.parseColor("#000000"));
                btnBeAdds.setBackground(null);
                break;
            case BE_ADD_CHOOSE:
                btnNews.setTextColor(Color.parseColor("#000000"));
                btnNews.setBackground(null);
                btnInbox.setTextColor(Color.parseColor("#000000"));
                btnInbox.setBackground(null);
                btnAdds.setTextColor(Color.parseColor("#000000"));
                btnAdds.setBackground(null);
                btnBeAdds.setTextColor(Color.parseColor("#d33a31"));
                btnBeAdds.setBackground(getResources().getDrawable(R.mipmap.bg_choose));
                break;
            default:
                break;
        }
    }
}
