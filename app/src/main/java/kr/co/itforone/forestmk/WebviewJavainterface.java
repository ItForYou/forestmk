package kr.co.itforone.forestmk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;

    public WebviewJavainterface(Activity activity, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.activity=activity;
    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

    @JavascriptInterface
    public void setlogout() {
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }


    @JavascriptInterface
    public void getlocation() {

        double lat = mainActivity.getlat() * 1000000;
        double lng = mainActivity.getlng() * 1000000;
        lat = Math.ceil(lat) / 1000000;
        lng = Math.ceil(lng) / 1000000;
        double finalLat = lat;
        double finalLng = lng;
        mainActivity.webView.post(new Runnable() {
            @Override
            public void run() {
                mainActivity.webView.loadUrl("javascript:sort_distance('" + finalLat + "','" + finalLng + "');");
            }
        });
        // Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void NoRefresh(){
        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        mainActivity.Norefresh();
        mainActivity.flg_refresh=0;
    }

    @JavascriptInterface
    public void YesRefresh(){
        mainActivity.Yesrefresh();
        mainActivity.flg_refresh=1;
    }

}
