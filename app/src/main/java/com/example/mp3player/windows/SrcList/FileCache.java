package com.example.mp3player.windows.SrcList;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by DissoCapB on 2017/4/2.
 */

public class FileCache {
    private File cacheDir;

    public FileCache(Context context){
        //找一个用来缓存图片的路径
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File( Environment.getExternalStorageDirectory() + "/MP3player/","LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url){

        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}
