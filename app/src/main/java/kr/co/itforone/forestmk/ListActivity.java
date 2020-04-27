package kr.co.itforone.forestmk;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListActivity extends AppCompatActivity {
    @BindView(R.id.header_txt)    ImageView header_txt;
    @BindView(R.id.header_search)    ImageView header_search;
    @BindView(R.id.header_location)    ImageView header_location;
    @BindView(R.id.header_chatlist)    ImageView header_chatlist;
    @BindView(R.id.menu_home)    ImageView menu_home;
    @BindView(R.id.menu_notice)    ImageView menu_notice;
    @BindView(R.id.menu_category)    ImageView menu_category;
    @BindView(R.id.menu_recent)    ImageView menu_recent;
    @BindView(R.id.menu_mypage)    ImageView menu_mypage;
    @BindView(R.id.main_profile)    CircleImageView main_profile;
    @BindView(R.id.refreshlayout_list)    SwipeRefreshLayout refreshlayout_list;
    @BindView(R.id.listview)    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        Glide.with(this).load("http://forestmk.itforone.co.kr/img/logo_name.png").into(header_txt);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_icon01.png").into(header_search);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_icon02.png").into(header_location);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_icon03.png").into(header_chatlist);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_cate01.png").into(menu_home);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_cate02.png").into(menu_notice);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_cate03.png").into(menu_category);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_cate04.png").into(menu_recent);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/hd_cate05.png").into(menu_mypage);
        Glide.with(this).load("http://forestmk.itforone.co.kr/theme/basic_app/img/app/wing_mb_noimg2.png").into(main_profile);

        refreshlayout_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                refreshlayout_list.setRefreshing(false);
            }

        });

        refreshlayout_list.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(listview.getScrollY() == 0){
                    refreshlayout_list.setEnabled(true);
                }
                else{
                    refreshlayout_list.setEnabled(false);
                }
            }
        });



    }
}
