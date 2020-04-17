package kr.co.itforone.forestmk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public int flg_refresh = 1;
    @BindView(R.id.webView)   WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;
    private long backPrssedTime = 0;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Uri mCapturedImageURI;
    String token = "";
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    Dialogloading dialogloading;
    private LocationManager locationManager;
    private Location location;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    public Uri mImageCaptureUri,croppath;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }

    private boolean hasPermissions(String[] permissions) {
        // 퍼미션 확인
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        }else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){

                }else{
                   /* LocationPosition.act=MainActivity.this;
                    LocationPosition.setPosition(this);
                    if(LocationPosition.lng==0.0){
                        LocationPosition.setPosition(this);
                    }
                    String place= LocationPosition.getAddress(LocationPosition.lat,LocationPosition.lng);
                    webView.loadUrl("javascript:getAddress('"+place+"')");*/
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dialogloading = new Dialogloading(this);

        Intent splash = new Intent(MainActivity.this,SplashActivity.class);
        startActivity(splash);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if(hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        Intent push = getIntent();
        String pushurl = "";

        if(push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");

        webView.setWebChromeClient(new ChromeManager(this, this));
        webView.setWebViewClient(new ViewManager(this, this));
        webView.addJavascriptInterface(new WebviewJavainterface(this, this),"Android");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        ///settings.setUserAgentString(settings.getUserAgentString()+"//Brunei");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("D", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        // Log and toast
                    }
                });

        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        String id = pref.getString("id", "");
        String pwd = pref.getString("pwd", "");

        if(!id.isEmpty() && !pwd.isEmpty()) {
            String str = null;
            try {
                str = "mb_id=" + URLEncoder.encode(id, "UTF-8") + "&mb_password=" + URLEncoder.encode(pwd, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            webView.postUrl(getString(R.string.login), str.getBytes());
        }
        else{
            webView.loadUrl(getString(R.string.home));
        }

        if(!pushurl.isEmpty() && !id.isEmpty() && !pwd.isEmpty()) {
           // Toast.makeText(getApplicationContext(),pushurl,Toast.LENGTH_LONG).show();
            webView.loadUrl(pushurl);
        }

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                webView.clearCache(true);
                webView.reload();
                refreshlayout.setRefreshing(false);
            }

        });

        refreshlayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(webView.getScrollY() == 0 && flg_refresh ==1){
                    refreshlayout.setEnabled(true);
                }
                else{
                    refreshlayout.setEnabled(false);
                }
            }
        });
    }

    public void Norefresh(){
        refreshlayout.setEnabled(false);
    }
    public void Yesrefresh(){
        refreshlayout.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == ChromeManager.FILECHOOSER_LOLLIPOP_REQ_CODE) {
//            Uri[] result = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (resultCode == RESULT_OK) {
//                    if (data != null) {
//                        //String dataString = data.getDataString();
//                        ClipData clipData = data.getClipData();
//                        if (clipData != null) {
//                            result = new Uri[clipData.getItemCount()];
//                            for (int i = 0; i < clipData.getItemCount(); i++) {
//                                ClipData.Item item = clipData.getItemAt(i);
//                                result[i] = item.getUri();
//                            }
//                        }
//                        else {
//                            result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
//                            //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
//                        }
//                        filePathCallbackLollipop.onReceiveValue(result);
//                    }
//                    else{
//                        filePathCallbackLollipop.onReceiveValue(null);
//                        filePathCallbackLollipop = null;
//                    }
//                }
//                else{
//                    try {
//                        if (filePathCallbackLollipop != null) {
//                            filePathCallbackLollipop.onReceiveValue(null);
//                            filePathCallbackLollipop = null;
//                        }
//                    }catch (Exception e){
//
//                    }
//                }
//            }
//        }
        switch (requestCode) {
            case ChromeManager.FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            //String dataString = data.getDataString();
                          //  ClipData clipData = data.getClipData();
                            mImageCaptureUri = data.getData();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
                                croppath =  Uri.parse(path);
                        } catch (IOException e) {
                                e.printStackTrace();
                            }


                            try {
                                Intent intent = new Intent("com.android.camera.action.CROP");
                                intent.setDataAndType(mImageCaptureUri, "image/*");
                                intent.putExtra("outputX", 500);
                                intent.putExtra("outputY", 500);
                                intent.putExtra("aspectX", 1);
                                intent.putExtra("aspectY", 1);
                                intent.putExtra("scale", true);
                                intent.putExtra("return-data", true);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT , croppath);
                                startActivityForResult(intent, CROP_FROM_ALBUM);
                            } catch ( ActivityNotFoundException e){
                                String errorMessage = "your device doesn't support the crop action!";
                                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                                toast.show();
                            }
//                        if (clipData != null) {
//                            result = new Uri[clipData.getItemCount()];
//                            for (int i = 0; i < clipData.getItemCount(); i++) {
//                                ClipData.Item item = clipData.getItemAt(i);
//                                result[i] = item.getUri();
//                            }
//                        }
//                        else {
//                            result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
//                            //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
//                        }
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else {
                        try {
                            if (filePathCallbackLollipop != null) {
                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;

                            }
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case CROP_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    Log.d("imgresult",String.valueOf(resultCode));
                   Uri[] result = null;
                    result = new Uri[1];
                 // Bitmap photo = data.getExtras().getParcelable("data");
                    /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), photo, "Title", null);*/
                    //result[0] = Uri.parse(path);
                    result[0] = croppath;

                    if (result[0] != null) {
                         //  Toast.makeText(getApplicationContext(),"step1",Toast.LENGTH_LONG).show();
                        filePathCallbackLollipop.onReceiveValue(result);
                    } else {
                       //  Toast.makeText(getApplicationContext(),"step2",Toast.LENGTH_LONG).show();
                    }
                    break;

                }
                else {
                    try {
                        if (filePathCallbackLollipop != null) {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } catch (Exception e) {

                    }
                }
        }
    }
   // String[] twostempurls = new String[]{getString(R.string) };
    @Override
    public void onBackPressed(){
        //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
        WebBackForwardList list = null;
        String backurl ="";

        try {
            list = webView.copyBackForwardList();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(list.getSize() >1 ){
            backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
        }


        if(backurl.contains("write_comment_update.php") || backurl.contains("delete_comment.php")){
            webView.clearCache(true);
            webView.loadUrl(getString(R.string.home));
        }
        else if(webView.getUrl().contains("write.php")) {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("글쓰기를 종료하시겠습니까?")
                    .setIcon(android.R.drawable.ic_menu_save)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // 확인시 처리 로직
                            webView.goBack();
                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // 취소시 처리 로직
                        }})
                    .show();
        }

        else if(webView.getUrl().contains("chatting.php")) {
            webView.loadUrl("javascript:leavepage()");
        }

        else if(webView.getUrl().contains(getString(R.string.chatlist))){
            webView.clearCache(true);
            webView.loadUrl(getString(R.string.home));
        }
        else if(webView.getUrl().contains(getString(R.string.home)) && !webView.getUrl().contains("wr_id")){
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPrssedTime;
            if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            }
            else
            {
                backPrssedTime = tempTime;
                webView.clearCache(true);
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }

        else if(webView.canGoBack()){
//            String url = webView.copyBackForwardList().getItemAtIndex(webView.copyBackForwardList().getCurrentIndex()-1).getUrl();
//            webView.loadUrl(url);
            webView.goBack();
        }else{
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPrssedTime;
            if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            }
            else
            {
                backPrssedTime = tempTime;
                webView.clearCache(true);
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
