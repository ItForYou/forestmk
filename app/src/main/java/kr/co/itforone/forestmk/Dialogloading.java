package kr.co.itforone.forestmk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dialogloading extends Dialog {
        private Context context;
        ImageView loadingimg;
    public Dialogloading(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_loading);
        loadingimg = (ImageView) findViewById(R.id.loadingimg);
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.loadingani);
        loadingimg.setAnimation(anim);
    }
    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }
}