package jp.techacademy.hato.yasuhiko.autoslideshowapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private Button mNextButton;
    private Button mBackButton;
    private Button mStartButton;
    private ImageView mImageView;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button)findViewById(R.id.button_next);
        mBackButton = (Button)findViewById(R.id.button_back);
        mStartButton = (Button)findViewById(R.id.button_start);
        mNextButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
        mImageView = (ImageView)findViewById(R.id.imageView);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                initCursor();
                setImageView();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            initCursor();
            setImageView();
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        mCursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCursor();
                    setImageView();
                }
                else{
                    // TODO
                    // grantされなかったら設定アプリを起動してgrantしてもらう、か、終了
                }
                break;
            default:
                break;
        }
    }

    private void initCursor(){
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        mCursor = cursor;
        mCursor.moveToFirst();
    }

    private Uri getCurrentContentUri(){
        Uri retVal = null;
        if(mCursor != null){
            int fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
            Log.d("getCurrentContentUri", String.valueOf(fieldIndex));
            Long id = mCursor.getLong(fieldIndex);
            retVal = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d("ANDROID", "URI : " + retVal.toString());
        }
        return retVal;
    }

    private boolean setImageView(){
        Uri uri = getCurrentContentUri();
        if(uri != null){
            mImageView.setImageURI(uri);
            Log.d("setImageView", "set");
            return true;
        }
        Log.d("setImageView", "uri == null");
        return false;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_next){
            Log.d("Main", "button_next");
            mCursor.moveToNext();
            setImageView();
        }
        else if(v.getId() == R.id.button_back){
            Log.d("Main", "button_back");
        }
        else if(v.getId() == R.id.button_start){
            Log.d("Main", "button_start");
        }

    }
}
