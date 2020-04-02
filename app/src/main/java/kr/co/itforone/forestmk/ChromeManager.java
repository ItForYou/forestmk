package kr.co.itforone.forestmk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class ChromeManager extends WebChromeClient {

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    Activity activity;
    MainActivity mainActivity;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;


    public ChromeManager(Activity activity, MainActivity mainActivity) {
        this.activity = activity;
        this.mainActivity = mainActivity;
    }


    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mainActivity.set_filePathCallbackLollipop(filePathCallback);

        Intent i = new Intent();
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{audioIntent});

        mainActivity.startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        return true;
    }

    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
        return true;
    }

}
