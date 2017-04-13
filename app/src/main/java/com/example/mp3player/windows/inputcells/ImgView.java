package com.example.mp3player.windows.inputcells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.example.mp3player.R;
import com.example.mp3player.service.HttpService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DissoCapB on 2017/3/13.
 */

public class ImgView extends View {

    public ImgView(Context context) {
        super(context);
    }

    public ImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint paint;
    float srcWidth, srcHeight;
    Handler mainThreadHandler = new Handler();

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setPathEffect(new DashPathEffect(new float[] { 5, 10, 15, 20 }, 0));
            paint.setAntiAlias(true);
        } else {

            paint = new Paint();
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            paint.setAntiAlias(true);
            // radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;
            srcWidth = bitmap.getWidth();
            srcHeight = bitmap.getHeight();
        }
        invalidate();
    }

    public void loadById(String i) {
        load(HttpService.serverAddress + "api/musicList/listsrc/"+i);

    }

    public void loadListNull(){
        setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.bg_music_src));
    }

    public void load(String path,String id) {
        load(HttpService.serverAddress + "api/"+path+"/"+id);

    }

    public void load(String url) {
        OkHttpClient client = HttpService.getClient();

        Request request = new Request.Builder().url(url).method("GET", null).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    byte[] bytes = arg1.body().bytes();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mainThreadHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            setBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                mainThreadHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        setBitmap(null);
                    }
                });
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (paint != null) {
            canvas.save();

            float dstWidth = getWidth();
            float dstHeight = getHeight();

            float scaleX = srcWidth / dstWidth;
            float scaleY = srcHeight / dstHeight;

            canvas.scale(Math.max(1 / scaleX, 1 / scaleY), Math.max(1 / scaleX, 1 / scaleY));

            canvas.drawRect(0, 0, srcWidth, srcHeight, paint);

            canvas.restore();
        }
    }
}
