package cn.com.jiutairural;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.com.jiutairural.web.CommonWebChromeClientEx;
import cn.com.jiutairural.web.CommonWebView;
import cn.com.jiutairural.web.CommonWebViewClientEx;

public class WebActivity extends AppCompatActivity {

    private CommonWebView mWebView;
    private TextView mTitleView;
    private ProgressBar mProgress;

    private String mCurrentUrl;
    //title和url对应的map，用于返回的时候刷新显示title
    private Map<String, String> mUrlTitleMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GetResId.getId(this, "layout", "activity_web"));
        mWebView = findViewById(GetResId.getId(this, "id", "web_view"));
        mTitleView = findViewById(GetResId.getId(this, "id", "title"));
        mProgress = findViewById(GetResId.getId(this, "id", "progress_bar"));
        setUpWebView();
    }


    private void setUpWebView() {
        WebSettings ws = mWebView.getSettings();
        // 加上这句话才能使用javascript方法
        ws.setJavaScriptEnabled(true);
        ws.setLoadsImagesAutomatically(true);
        ws.setSupportZoom(true);
        ws.setSaveFormData(true);
        ws.setSavePassword(false);

        //ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //解决加载微信公众号h5页面，图片显示不出来的问题, 5.0以上手机有这个问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //支持页面定位
        ws.setDatabaseEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setGeolocationDatabasePath(getApplicationContext().getFilesDir().getPath());
        //解决神州租车页面一直刷新的问题
        ws.setDomStorageEnabled(true);

        mWebView.setWebChromeClient(mChromeClient);
        mWebView.setWebViewClient(mWebViewClient);

        mCurrentUrl = "https://www.baidu.com";

        mWebView.loadUrl(mCurrentUrl);
    }

    private final CommonWebChromeClientEx mChromeClient = new CommonWebChromeClientEx() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (!TextUtils.isEmpty(title)) {
                mTitleView.setText(title);
                String url = mWebView.getUrl();
                mUrlTitleMap.put(url, title);
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress == 100) {
                mProgress.setVisibility(View.GONE);
            }else {
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(newProgress);
            }
        }
    };

    private final CommonWebViewClientEx mWebViewClient = new CommonWebViewClientEx() {
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadUrl("javascript:document.body.innerHTML=null");
            view.clearView();
            mCurrentUrl = failingUrl;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (isFinishing()) {//线上异常,加载网页完成之前杀掉ui,就可能重现
                return;
            }
            mCurrentUrl = url;
            String title = mUrlTitleMap.get(mCurrentUrl);
            if (!TextUtils.isEmpty(title)) {
                mTitleView.setText(title);
            }
            mProgress.setVisibility(View.GONE);
            Log.i("Mr.Kang", "onPageFinished: 网页加载完成");
            super.onPageFinished(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            mCurrentUrl = url;
            mProgress.setVisibility(View.VISIBLE);
            Log.i("Mr.kang", "onPageStarted: 网页开始加载");
            super.onPageStarted(view, url, favicon);

        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        //重写此接口是为了避免此时注入js接口，导致快的打车页面的键盘被自动隐藏
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }


    };

    /**
     * 返回事件
     */
    private void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        if (mWebView != null) {
            goBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUrlTitleMap.clear();
        //退出活动进程
        if (mWebView != null) {
            //webView.removeJavascriptInterface("android"); //删除jsbridge
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearCache(true);
            mWebView.removeAllViews();

            if (mWebView.getParent() instanceof ViewGroup) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.destroy();
        }
    }


}
