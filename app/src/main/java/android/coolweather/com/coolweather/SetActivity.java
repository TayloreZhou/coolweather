package android.coolweather.com.coolweather;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.coolweather.com.coolweather.manager.WeatherManager;
import android.coolweather.com.coolweather.util.SetButton;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.annotation.Target;

public class SetActivity extends AppCompatActivity {
    private SetButton setBackground;
    private static final int CHOOSE_PHOTO=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Button bcButton=(Button)findViewById(R.id.back_weather);
        setBackground=(SetButton)findViewById(R.id.button_bgset);
        setBackground.setStringLeft("背景设置");
        setBackground.setStringRight("每日一图");
        bcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(WeatherManager.backGrounfNumber==WeatherActivity.BG_DAY){
                    if(ContextCompat.checkSelfPermission(SetActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        openAlbum();
                    }
                }else{
                    WeatherManager.backGrounfNumber=WeatherActivity.BG_DAY;
                    setBackground.setStringRight("每日一图");
                }
            }
        });
    }

    private  void openAlbum(){
        Intent intent =new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"哎呀！没给权限呢",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(Build.VERSION.SDK_INT>=19){
                    handleImageOnKitKat(data);
                }else{
                    handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri =data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id =docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath =getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            WeatherManager.weatherActivity.bingPicImg.setImageBitmap(bitmap);

            WeatherManager.backGrounfNumber=WeatherActivity.BG_FILE;
            setBackground.setStringRight("文件图片");
        }else {
            Toast.makeText(this,"哎，获取图片失败啦",Toast.LENGTH_SHORT).show();
        }
    }
}
