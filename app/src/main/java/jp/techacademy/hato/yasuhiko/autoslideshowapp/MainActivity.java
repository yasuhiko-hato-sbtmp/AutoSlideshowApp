package jp.techacademy.hato.yasuhiko.autoslideshowapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
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
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hatoy37
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE_INIT = 100;

    private Button mNextButton;
    private Button mBackButton;
    private Button mStartButton;
    private ImageView mImageView;
    private Cursor mCursor;

    Timer mTimer;
    Handler mHandler = new Handler();
    private int SLIDESHOW_INTERVAL_MSEC = 2000;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Marshmallow or more
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // permitted
                initCursor();
                setImageView();
            } else {
                // not permitted
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE_INIT);
            }
        } else { // KitKat or less
            initCursor();
            setImageView();
        }
    }

    @Override
    public void onStop(){
        super.onDestroy();
        if(mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_INIT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("request permission", "permitted");
                    initCursor();
                    setImageView();
                }
                else{
                    Log.d("request permission", "denied");
                    (new RuntimePermissionUtils()).showSettingsDialog(this);
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

    private void setNextImage(){
        if(mCursor.moveToNext() == false){
            mCursor.moveToFirst();
        }
        setImageView();
    }

    private void setPreviousImage(){
        if(mCursor.moveToPrevious() == false){
            mCursor.moveToLast();
        }
        setImageView();
    }

    @Override
    public void onClick(View v) {
        // Marshmallow or more
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // permitted
               onClickBranch(v);
            } else {
                // not permitted
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE_INIT);
            }
        } else { // KitKat or less
            onClickBranch(v);
        }
    }


    private void onClickBranch(View v){
        if(v.getId() == R.id.button_next){
            Log.d("Main", "button_next");
            if(mTimer == null) {
                setNextImage();
            }
            else{
                Toast.makeText(this, "Stop slideshow first.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.button_back){
            Log.d("Main", "button_back");
            if(mTimer == null) {
                setPreviousImage();
            }
            else{
                Toast.makeText(this, "Stop slideshow first.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v.getId() == R.id.button_start){
            Log.d("Main", "button_start");
            if(mTimer == null) {
                Log.d("Main", "Slideshow start");
                mStartButton.setText(R.string.button_stop);
                mNextButton.setText("");
                mBackButton.setText("");
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setNextImage();
                            }
                        });
                    }
                }, SLIDESHOW_INTERVAL_MSEC, SLIDESHOW_INTERVAL_MSEC);
            }
            else{
                Log.d("Main", "Slideshow stop");
                mStartButton.setText(R.string.button_start);
                mNextButton.setText(R.string.button_next);
                mBackButton.setText(R.string.button_back);
                mTimer.cancel();
                mTimer = null;
            }
        }

    }

}
