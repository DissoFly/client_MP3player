package com.example.mp3player.windows;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
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
import com.example.mp3player.StatusBarUtils;
import com.example.mp3player.entity.FriendRead;
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
 * Created by DissoCapB on 2017/3/21.
 */

public class ZoneActivity extends Activity implements View.OnClickListener {
    TextView title;
    TextView name;
    ListView listView;
    int friendId;
    List<FriendRead> friendReads;
    Button btnAddFriend;
    Button btnInbox;
    String addFriendMessage = "";
    String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        StatusBarUtils.setWindowStatusBarColor(this, Color.parseColor("#d33a31"));
        name = (TextView) findViewById(R.id.text_zone_username);
        title = (TextView) findViewById(R.id.text_zone_title);
        listView = (ListView) findViewById(R.id.list_zone);
        btnAddFriend = (Button) findViewById(R.id.btn_zone_add_friend);
        btnInbox = (Button) findViewById(R.id.btn_zone_inbox);
        btnAddFriend.setVisibility(View.GONE);
        initData();
        Bundle extras = getIntent().getExtras();
        friendId = extras.getInt("openZoneId");
        bindService(new Intent(this, LoginService.class), connection, Context.BIND_AUTO_CREATE);
        getNameConnect();
    }

    BaseAdapter listAdapter = new BaseAdapter() {

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
                TextView name=(TextView)view.findViewById(R.id.text_friendread_name);
                TextView time=(TextView)view.findViewById(R.id.text_friendread_time);
                TextView text=(TextView)view.findViewById(R.id.text_friendread_text);
                AvatarView avatarView=(AvatarView)view.findViewById(R.id.avatar);
                avatarView.load(friendReads.get(i).getUserId());
                name.setText(friendReads.get(i).getUserName());
                String times= DateFormat.format("MM-dd hh:mm",friendReads.get(i).getCreateDate()).toString();
                time.setText(times);
                text.setText(friendReads.get(i).getText());
            } else {
                view = convertView;
            }
            return view;
        }
    };


    private void initData() {
        findViewById(R.id.fragment_zone).setOnClickListener(this);
        findViewById(R.id.btn_zone_back).setOnClickListener(this);
        findViewById(R.id.btn_zone_add_friend).setOnClickListener(this);
        findViewById(R.id.btn_zone_inbox).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_zone_back:
                ZoneActivity.this.onBackPressed();
                break;
            case R.id.btn_zone_add_friend:
                switch (addFriendMessage) {
                    case "FALSE_ISADD":
                        addFriend(friendId);
                        break;
                    case "TRUE_ISADD":
                        addFriend(friendId);
                        break;
                    case "IN_BLACKLIST":
                        Toast.makeText(ZoneActivity.this, "请在设置中管理黑名单", Toast.LENGTH_SHORT).show();
                        break;
                    case "PLEASE_LOGIN":
                        Toast.makeText(ZoneActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_zone_inbox:
                if (messenger.getUser() != null) {
                    int userId = messenger.getUser().getUserId();
                    if (userId != friendId) {
                        Intent itnt = new Intent(this, InboxActivity.class);
                        itnt.putExtra("userId", userId);
                        itnt.putExtra("friendId", friendId);
                        startActivityForResult(itnt, 0);
                    }else{
                        Toast.makeText(this, "不能给自己发私信", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void getFriendZone(int friendId) {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        }
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .add("friendId", friendId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("friend/getFriendZone/").post(formBody).build();
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
                    ZoneActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeAllViewsInLayout();
                            listAdapter.notifyDataSetInvalidated();
                            listView.setAdapter(listAdapter);
                        }
                    });
                } catch (final Exception e) {
                    ZoneActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data.equals("IN_BLACKLIST")) {
                                Toast.makeText(ZoneActivity.this, "访问限制", Toast.LENGTH_SHORT).show();
                            } else if (data.equals("GET_ALL")) {
                                Toast.makeText(ZoneActivity.this, "最后一页了", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                                Toast.makeText(ZoneActivity.this, "连接出错", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void getIsFriend(int friendId) {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        }
        final RequestBody formBody = new FormBody.Builder()
                .add("userId", userId + "")
                .add("friendId", friendId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("friend/isAdd/").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                addFriendMessage = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (data) {
                            case "FALSE_ISADD":
                                btnAddFriend.setVisibility(View.VISIBLE);
                                btnAddFriend.setText("关注");
                                break;
                            case "TRUE_ISADD":
                                btnAddFriend.setVisibility(View.VISIBLE);
                                btnAddFriend.setText("已关注");
                                break;
                            case "IN_BLACKLIST":
                                btnAddFriend.setVisibility(View.VISIBLE);
                                btnAddFriend.setText("黑名单");
                                break;
                            case "WRONG":
                                btnAddFriend.setVisibility(View.GONE);
                                Toast.makeText(ZoneActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                                break;
                            case "PLEASE_LOGIN":
                                btnAddFriend.setVisibility(View.VISIBLE);
                                btnAddFriend.setText("请登录");
                                break;
                            default:
                                btnAddFriend.setVisibility(View.GONE);
                                Toast.makeText(ZoneActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                                System.out.println(data);
                                break;

                        }
                    }
                });

            }
        });

    }


    private void addFriend(final int friendId) {
        int userId = -1;
        if (messenger.getUser() != null) {
            userId = messenger.getUser().getUserId();
        } else {
            Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (messenger.getUser().getUserId() == friendId) {
            Toast.makeText(this, "不能关注自己", Toast.LENGTH_SHORT).show();
            return;
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (data) {
                            case "SUCCESS_ADD":
                                Toast.makeText(ZoneActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                                getIsFriend(friendId);
                                break;
                            case "SUCCESS_DELECT":
                                Toast.makeText(ZoneActivity.this, "已取消关注", Toast.LENGTH_SHORT).show();
                                getIsFriend(friendId);
                                break;
                            case "IN_BLACKLIST":
                                Toast.makeText(ZoneActivity.this, "请在设置中管理黑名单", Toast.LENGTH_SHORT).show();
                                getIsFriend(friendId);
                                break;
                            case "WRONG":
                                Toast.makeText(ZoneActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                                break;
                            case "PLEASE_LOGIN":
                                Toast.makeText(ZoneActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(ZoneActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                                System.out.println(data);
                                break;

                        }
                    }
                });

            }
        });

    }


    void getNameConnect() {
        final RequestBody formBody = new FormBody.Builder()
                .add("userId", friendId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("getUserName").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                if (data.startsWith("NAME:")) {
                    friendName = data.replace("NAME:", "");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(friendName + " 的空间");
                            name.setText(friendName);
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
            getFriendZone(friendId);
            getIsFriend(friendId);
        }
    };


}
