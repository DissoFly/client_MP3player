package com.example.mp3player.windows.inputcells;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mp3player.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Created by DissoCapB on 2017/2/19.
 */

public class PictureInputCellFragment extends Fragment {
    final int REQUESTCODE_CAMERA=1;
    final int REQUESTCODE_ALBUM=2;

    ImageView imageView;
    TextView labelText;
    TextView hintText;
    byte[] pngData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view=inflater.inflate(R.layout.fragment_inputcell_avatar, container);

        imageView=(ImageView) view.findViewById(R.id.image);
        labelText=(TextView) view.findViewById(R.id.label);
        hintText=(TextView) view.findViewById(R.id.hint);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onImageViewClicked();

            }
        });

        return view;
    }

    void onImageViewClicked(){

        String[] items={"拍照","相册"};

        new AlertDialog.Builder(getActivity())
                .setTitle(labelText.getText())
                .setItems(items,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                pickFromAlbum();
                                break;

                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    void takePhoto(){
        Intent itnt=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(itnt,REQUESTCODE_CAMERA);
    }

    void pickFromAlbum(){
        Intent itnt=new Intent(Intent.ACTION_PICK);
        itnt.setType("image/*");
        startActivityForResult(itnt, REQUESTCODE_ALBUM);
    }


    void saveBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        comp(bmp).compress(Bitmap.CompressFormat.PNG, 100, baos);
        pngData = baos.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_CANCELED)
            return;

        if(requestCode==REQUESTCODE_CAMERA){
            Bitmap bmp=(Bitmap) data.getExtras().get("data");
            saveBitmap(bmp);

            imageView.setImageBitmap(bmp);

        }else if(requestCode==REQUESTCODE_ALBUM){

            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                imageView.setImageBitmap(bmp);
                saveBitmap(bmp);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLabelText(String labelText) {
        this.labelText.setText(labelText);
    }

    public void setHintText(String hintText) {
        this.hintText.setHint(hintText);

    }
    public byte[] getPngData(){
        return pngData;
    }


    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 800f;//这里设置宽度为800f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        System.out.println("----------------"+baos.toByteArray().length / 1024);
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            System.out.println("----------------"+baos.toByteArray().length / 1024);

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


}
