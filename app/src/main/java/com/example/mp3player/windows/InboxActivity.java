package com.example.mp3player.windows;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.R;
import com.example.mp3player.entity.Inbox;
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
 * Created by DissoCapB on 2017/3/24.
 */

public class InboxActivity extends Activity implements View.OnClickListener {

    int friendId;
    int userId;

    TextView title;
    EditText editText;
    Button btnSend;
    ListView listView;
    String userName;
    String friendName;
    List<Inbox> inboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Bundle extras = getIntent().getExtras();
        friendId = extras.getInt("friendId");
        userId = extras.getInt("userId");
        title = (TextView) findViewById(R.id.text_inbox_title);
        editText = (EditText) findViewById(R.id.edit_inbox);
        btnSend = (Button) findViewById(R.id.btn_inbox_send);
        listView = (ListView) findViewById(R.id.list_inbox);
        bindService(new Intent(this, LoginService.class), connection, Context.BIND_AUTO_CREATE);
        initData();
    }


    private void initData() {
        findViewById(R.id.btn_inbox_back).setOnClickListener(this);
        findViewById(R.id.btn_inbox_send).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_inbox_back:
                finish();
                break;
            case R.id.btn_inbox_send:
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(InboxActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                } else {
                    sendConnect();
                }
                break;
            default:
                break;
        }
    }

    void reload(){
        listView.removeAllViewsInLayout();
        listAdapter.notifyDataSetInvalidated();
        listView.setAdapter(listAdapter);
    }


    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return inboxes == null ? 0 : inboxes.size();
        }

        @Override
        public Object getItem(int i) {
            return inboxes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view = null;
            if (convertView == null) {
                int avatarId;
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                if(inboxes.get(i).getUserId()==userId) {
                    avatarId=userId;
                    view = inflater.inflate(R.layout.widget_inbox_right, null);
                }
                else{
                    avatarId=friendId;
                    view = inflater.inflate(R.layout.widget_inbox_left, null);
                }

                TextView inboxTime=(TextView)view.findViewById(R.id.inbox_time);
                TextView inboxText=(TextView)view.findViewById(R.id.inbox_text);
                AvatarView avatar=(AvatarView)view.findViewById(R.id.avatar);
                String time= DateFormat.format("yyyy-MM-dd hh:mm:ss",inboxes.get(i).getCreateDate()).toString();
                inboxTime.setText(time);
                avatar.load(avatarId);
                inboxText.setText(inboxes.get(i).getText());
            } else {
                view = convertView;
            }
            return view;
        }
    };

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
            userName = messenger.getUser().getAccount();
            getFriendNameConnect();
        }
    };

    void sendConnect() {
        RequestBody formBody = new FormBody.Builder()
                .add("friendId", friendId + "")
                .add("userId", userId + "")
                .add("text", editText.getText().toString())
                .build();
        Request request = HttpService.requestBuilderWithPath("inbox/save").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                if(data.equals("SUCCESS")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InboxActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }
                    });
                    getInboxConnect();
                }else if(data.equals("WRONG")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InboxActivity.this, "连接错误3", Toast.LENGTH_SHORT).show();
                            System.out.println(data);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InboxActivity.this, "连接错误4", Toast.LENGTH_SHORT).show();
                            System.out.println(data);
                        }
                    });
                }
            }

    }

    );
}

    void getFriendNameConnect() {
        RequestBody formBody = new FormBody.Builder()
                .add("userId", friendId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("getUserName/").post(formBody).build();
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
                            title.setText("与"+friendName+"对话");
                        }
                    });
                    getInboxConnect();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InboxActivity.this, "连接错误2", Toast.LENGTH_SHORT).show();
                            System.out.println(data);
                        }
                    });
                }

            }
        });
    }

    void getInboxConnect() {
        RequestBody formBody = new FormBody.Builder()
                .add("friendId", friendId + "")
                .add("userId", userId + "")
                .build();
        Request request = HttpService.requestBuilderWithPath("inbox/getInbox").post(formBody).build();
        HttpService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                try {
                    inboxes = new Gson().fromJson(data, new TypeToken<List<Inbox>>() {
                    }.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reload();
                        }
                    });
                    System.out.println(data);
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(data);
                            e.printStackTrace();
                        }
                    });
                }

            }
        });
    }
}
