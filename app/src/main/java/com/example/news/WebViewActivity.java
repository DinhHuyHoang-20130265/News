package com.example.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebViewActivity extends AppCompatActivity
        implements View.OnClickListener {

    private ImageView mImgBackHomeRight;
    private WebSettings webSettings = null;
    private WebView webView;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        link = intent.getStringExtra("linknews");
        initView();

        webView.setWebChromeClient(new WebChromeClient());
        MyAsyncTask myAsynTask = new MyAsyncTask(link);
        myAsynTask.execute();
    }

    private void initView() {
        mImgBackHomeRight = findViewById(R.id.img_back_right);
        mImgBackHomeRight.setOnClickListener(this);
        webView = findViewById(R.id.wv_news);
        webView.setWebViewClient(new WebViewClient());
        webSettings = webView.getSettings();
        configWebView();
    }

    private void configWebView() {
        webSettings.setRenderPriority(WebSettings.RenderPriority.LOW );
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        private String url;

        public MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... s) {

            Document document;

            String content = null;
            try {
                document = Jsoup.connect(url).get();
                Elements element = document.getElementsByClass("noidung");
                content = element.get(0).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String document) {
            super.onPostExecute(document);
            webView.loadDataWithBaseURL(link, document, "text/html", "utf-8", "");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back_right) {
            super.onBackPressed();
        }
    }
}
