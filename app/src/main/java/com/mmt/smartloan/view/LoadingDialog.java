package com.mmt.smartloan.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mmt.smartloan.R;
import static android.content.Context.WINDOW_SERVICE;
public class LoadingDialog extends Dialog {

    private ProgressBar progressBar;
    private RoundProgressBar roundProgressBar;
    private TextView mTvMessage = null;
    private Context context;
    private boolean cancelabe = true;//默认可以取消

    public LoadingDialog(Context context, boolean cancelabe) {
        super(context, R.style.Alert_Dialog_Style);
        this.cancelabe = cancelabe;
        initProgressDialog(context, this.cancelabe);
    }

    public LoadingDialog(Context context) {
        super(context, R.style.Alert_Dialog_Style);
        initProgressDialog(context, this.cancelabe);
    }

    private void initProgressDialog(Context context, boolean cancelabe) {
        setCancelable(cancelabe);
        setCanceledOnTouchOutside(false);
        this.context = context;

        setContentView(R.layout.widget_dialog_loading);
        mTvMessage = findViewById(R.id.tv_message);
        progressBar = findViewById(R.id.progress_bar);
        roundProgressBar = findViewById(R.id.round_progress_bar);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.width = -2;
        params.height = -2;
        window.setAttributes(params);
    }

    public void setMessage(String msg) {
        if (mTvMessage != null) {
            mTvMessage.setText(msg);
        }
    }

    public void showRoundProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        roundProgressBar.setVisibility(View.VISIBLE);
        roundProgressBar.setProgress(0);
//        roundProgressBar.setCircleColor(Color.WHITE);
//        roundProgressBar.setCircleProgressColor(R.color.common_gray_second);
    }

    public void setProgress(int progress) {
        if (roundProgressBar.getVisibility() != View.VISIBLE) {
            roundProgressBar.setVisibility(View.VISIBLE);
        }
        roundProgressBar.setProgress(progress);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        // 解决横竖屏切换的适配问题
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = display.getWidth();
        this.getWindow().setAttributes(params);
        this.getWindow().setGravity(Gravity.CENTER);
        Configuration configuration =context.getResources().getConfiguration();
        configuration.fontScale = 1;
        //0.85 小, 1 标准大小, 1.15 大，1.3 超大 ，1.45 特大
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getResources().updateConfiguration(configuration, metrics);
    }
}

