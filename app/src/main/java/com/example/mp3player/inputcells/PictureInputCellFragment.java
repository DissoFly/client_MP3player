package com.example.mp3player.inputcells;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mp3player.R;

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
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
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

    public static void resizeImage(String srcImgPath, String distImgPath,
                                   int width, int height) throws IOException {
        //		param srcImgPath 原图片路径
        //	    param distImgPath  转换大小后图片路径
        //	    param width   转换后图片宽度
        //	    param height  转换后图片高度
//        File srcFile = new File(srcImgPath);
//        BufferedImage srcImg = ImageIO.read(srcFile);
//        BufferedImage buffImg = null;
//        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        buffImg.getGraphics().drawImage(
//                srcImg.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0,
//                0, null);
//
//        ImageIO.write(buffImg, "JPEG", new File(distImgPath));

    }
}
