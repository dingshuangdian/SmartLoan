package com.mmt.smartloan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.gyf.immersionbar.ImmersionBar;
import com.mmt.smartloan.BR;
import com.mmt.smartloan.R;
import com.mmt.smartloan.base.AddressConfig;
import com.mmt.smartloan.base.BaseActivity;
import com.mmt.smartloan.databinding.LoginLayoutBinding;
import com.mmt.smartloan.databinding.VerifyLayoutBinding;
import com.mmt.smartloan.module.LoginModule;
import com.mmt.smartloan.module.VerifyModule;
import com.mmt.smartloan.repository.AppViewModelFactory;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/11<p>
 */
public class VerifyActivity extends BaseActivity<VerifyLayoutBinding, VerifyModule> {
    private static final long COUNT_DOWN_TOTAL = 60 * 1000;
    public CountDownTimer mCountDownTimer;
    public String mobile, gaid;
    public boolean isExisted;

    @Override
    public int getLayout() {
        return R.layout.verify_layout;
    }

    @Override
    public int initVariableId() {
        return BR.verify;
    }

    @Override
    public VerifyModule initViewModel(AppViewModelFactory factory) {
        return ViewModelProviders.of(this, factory).get(VerifyModule.class);
    }

    @Override
    public void initView() {
        ImmersionBar.with(this).statusBarColor("#AD2648").fitsSystemWindows(true).init();
        mobile = getIntent().getStringExtra("mobile");
        gaid = getIntent().getStringExtra("gaid");
        String text = "Al continuar,acepta nuestros<Términos de Servicio>,\n" +
                "<Política de privacidad>y recibe avisos por SMS y\n" +
                "correo electrónico.";
        SpannableString spanStr = new SpannableString(text);

        String ysText = "<Términos de Servicio>";
        String xyText = "<Política de privacidad>";
        int firstIndex = text.indexOf(ysText);
        int secondIndex = text.indexOf(xyText);
        spanStr.setSpan(new MyURLSpan(Color.parseColor("#A11134")) {
            @Override
            public void onClick(View widget) {
                try {
                    Intent intent = new Intent(VerifyActivity.this, ShowWebActivity.class);
                    intent.putExtra(ShowWebActivity.KEY_URL, AddressConfig.WEB_URL + "/#/provicy");
                    intent.putExtra(ShowWebActivity.KEY_TITLE, "Términos de Servicio");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, firstIndex, firstIndex + ysText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyURLSpan(Color.parseColor("#A11134")) {
            @Override
            public void onClick(View widget) {
                try {
                    Intent intent = new Intent(VerifyActivity.this, ShowWebActivity.class);
                    intent.putExtra(ShowWebActivity.KEY_URL, AddressConfig.WEB_URL + "/#/termsCondition");
                    intent.putExtra(ShowWebActivity.KEY_TITLE, "Política de privacidad");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, secondIndex, secondIndex + xyText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.tvYs.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvYs.setText(spanStr, TextView.BufferType.SPANNABLE);
        isExisted = getIntent().getBooleanExtra("isExisted", false);
        //计时器
        mCountDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tv01.setTextColor(Color.parseColor("#4A4A4A"));
                binding.tv01.setText(millisUntilFinished / 1000 + "S");
                binding.tv01.setClickable(false);
            }

            @Override
            public void onFinish() {
                binding.tv01.setText("Conseguir");
                binding.tv01.setTextColor(Color.parseColor("#4A4A4A"));
                binding.tv01.setClickable(true);
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    class MyURLSpan extends ClickableSpan {
        private int colorId;

        public MyURLSpan(int colorId) {
            this.colorId = colorId;
        }

        @Override
        public void onClick(@NonNull View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(colorId);
            ds.setUnderlineText(false);
        }
    }
}
