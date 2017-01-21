package com.example.mp3player.main.page.mine.localMusic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.mp3player.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DissoCapB on 2017/1/18.
 */

public class FindMusicInLocalActivity extends Activity {
    private List<String> audioList = null; //本地音频列表
    private boolean isSearch=false;
    String fileUpdate = null;
    TextView textResult;
    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_mine_find_music_in_local);
        textResult = (TextView) findViewById(R.id.text_find_music_result);


        findViewById(R.id.btn_find_music_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler=new Handler();
                LooperThread thread = new LooperThread();
                thread.start();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        load();
        textResult.setText("共" + audioList.size() + "首");

    }

    ////////////////////////////////////搜索本地文件↓////////////////////////////////////
    public class LooperThread extends Thread {
        @Override
        public void run() {
            audioList = new ArrayList<String>();
            isSearch=true;      //更新数据ui放getFiles()内会显示错乱
            getFiles(Environment.getExternalStorageDirectory() + "/");
            save();
            isSearch=false;
        }
    }


    private void getFiles(String url) {

        File files = new File(url); // 创建文件对象
        File[] file = files.listFiles();
        int fileQuantity=file.length;
        int i=0;

        handler.post(runnableUi);
        try {
            for (File f : file) { // 通过for循环遍历获取到的文件数组
                i++;
                //percent=(i*100)/fileQuantity;
                fileUpdate = f.getPath();
                if (f.isDirectory()) { // 如果是目录，也就是文件夹
                    getFiles(f.getAbsolutePath()); //递归调用
                } else {
                    if (isAudioFile(f.getPath())) { // 如果是音频文件
                        audioList.add(f.getPath()); // 将文件的路径添加到list集合中
                        //                        取路径最后的名称
                        //                        String s1[] =f.getPath().split("/");
                        //                        musicList.add(s1[s1.length-1]);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Runnable runnableUi=new Runnable() {                //更新ui
        @Override
        public void run() {

                textResult.setText("正在查找：已找到" + audioList.size() + "首，"+fileUpdate);
            if(!isSearch)
                textResult.setText("已完成：已找到" + audioList.size() + "首");
        }
    };

    private static boolean isAudioFile(String path) {
        if (path.endsWith(".mp3") || path.endsWith(".wav") || path.endsWith(".3gp")) {         // 判断是否为有合法的音频文件
            return true;
        }
        return false;
    }

    //////////////////////////////////搜索本地文件↑//////////////////////////////////////
    /////////////////////////////////本地文件路径储存↓///////////////////////////////////////

    public void save(){							//写入文件
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("localMusicData", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            for(int i=0;i<audioList.size();i++){

                writer.write(audioList.get(i)+"###");
                writer.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void load(){
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        audioList = new ArrayList<String>();
        try{
            in=openFileInput("localMusicData");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
            String s1[] =content.toString().split("###");
            for(int c=0;c<s1.length;c++)
                audioList.add(s1[c]);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
