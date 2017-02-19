package com.example.mp3player.inputcells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.mp3player.R;
import com.example.mp3player.entity.User;
import com.example.mp3player.service.HttpService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/2/18.
 */

public class AvatarView extends View{
    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    static Paint paint;
    static float srcWidth;
    static float srcHeight;
    Handler mainThreadHandler = new Handler();

    public void setBitmap(Bitmap bmp){					//确认头像内容
        if(bmp==null) {
            paint = new Paint();
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.user_null);
            paint.setShader(new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT));
            srcWidth = bitmap.getWidth();
            srcHeight = bitmap.getHeight();

            paint.setAntiAlias(true);

        }else{
            paint = new Paint();
            paint.setShader(new BitmapShader(bmp, TileMode.REPEAT, TileMode.REPEAT));
            paint.setAntiAlias(true);

            srcWidth = bmp.getWidth();
            srcHeight = bmp.getHeight();
        }
        invalidate();
    }
    //从service获取头像数据
    public void load(User user){
        load(HttpService.serverAddress + user.getAvatar());
    }


    public void load(String url){

        OkHttpClient client = HttpService.getClient();

        Request request = new Request.Builder()
                .url(url)
                .method("get", null)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try{
                    byte[] bytes = arg1.body().bytes();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mainThreadHandler.post(new Runnable() {
                        public void run() {
                            setBitmap(bmp);
                        }
                    });
                }catch(Exception ex){
                    mainThreadHandler.post(new Runnable() {
                        public void run() {
                            setBitmap(null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                mainThreadHandler.post(new Runnable() {
                    public void run() {
                        Log.d("paint123", "5555");
                        setBitmap(null);
                    }
                });
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {					//头像处理
        super.draw(canvas);
        if(paint!=null){
            canvas.save();

            float dstWidth = getWidth();
            float dstHeight = getHeight();

            float scaleX = srcWidth / dstWidth;
            float scaleY = srcHeight / dstHeight;

            canvas.scale(Math.max(1/scaleX,1/scaleY), Math.max(1/scaleX,1/scaleY));

            canvas.drawCircle(Math.min(srcWidth, srcHeight) /2, Math.min(srcWidth, srcHeight)/2, Math.min(srcWidth, srcHeight)/2, paint);

            canvas.restore();
        }


    }
}
