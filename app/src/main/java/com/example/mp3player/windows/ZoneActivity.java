package com.example.mp3player.windows;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.FriendRead;
import com.example.mp3player.service.HttpService;
import com.example.mp3player.service.LoginService;
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

public class ZoneActivity extends Activity implements View.OnClickListener{
    TextView title;
    TextView name;
    ListView listView;
    int friendId;
    List<FriendRead> friendReads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        name = (TextView)findViewById(R.id.text_zone_username);
        title = (TextView)findViewById(R.id.text_zone_title);
        listView = (ListView)findViewById(R.id.list_zone);
        initData();
        Bundle extras = getIntent().getExtras();
        friendId=extras.getInt("openZoneId");
        bindService(new Intent(this, LoginService.class), connection, Context.BIND_AUTO_CREATE);
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
                TextView textView=(TextView)view.findViewById(R.id.text_friendread);
                textView.setText(friendReads.get(i).getText());
            } else {
                view = convertView;
            }
            return view;
        }
    };

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }


    private void initData() {
        findViewById(R.id.fragment_zone).setOnClickListener(this);
        findViewById(R.id.btn_zone_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_zone_back:
                ZoneActivity.this.onBackPressed();
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
        }
    };
}
