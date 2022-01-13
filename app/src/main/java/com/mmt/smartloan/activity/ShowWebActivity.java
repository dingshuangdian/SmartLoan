package com.mmt.smartloan.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mmt.smartloan.R;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class ShowWebActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String KEY_URL = "KEY_URL";
    public final static String KEY_TITLE = "KEY_TITLE";
    private WebView webView;
    private TextView title;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_showweb);
        webView = findViewById(R.id.webview);
        title = findViewById(R.id.actionbar_title);
        back = findViewById(R.id.back);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
        if (android.os.Build.MODEL == "LIO-TL00" || android.os.Build.MODEL == "LIO-AL00" ||
                android.os.Build.MODEL == "LIO-AN10" || android.os.Build.MODEL == "LIO-AN00") {
            LinearLayout.LayoutParams layoutParan = (LinearLayout.LayoutParams) webView.getLayoutParams();
            float dp10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10F, getResources().getDisplayMetrics());
            layoutParan.leftMargin = (int) dp10;
            layoutParan.rightMargin = (int) dp10;
            webView.setLayoutParams(layoutParan);
        }
        title.setText(getIntent().getStringExtra(KEY_TITLE));
        webView.loadUrl(getIntent().getStringExtra(KEY_URL));
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }

    }
}
