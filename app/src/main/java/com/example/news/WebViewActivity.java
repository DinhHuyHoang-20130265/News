package com.example.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Locale;

public class WebViewActivity extends AppCompatActivity
        implements View.OnClickListener {
    private TextToSpeech textToSpeech;
    private ImageView mImgBackHomeRight, micro;
    private WebSettings webSettings = null;
    private WebView webView;
    private String link;
    private String contentRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        link = intent.getStringExtra("linknews");
        initView();

        initVoice();
        webView.setWebChromeClient(new WebChromeClient());
        MyAsyncTask myAsynTask = new MyAsyncTask(link);
        myAsynTask.execute();
    }

    private void initVoice() {
        micro = findViewById(R.id.micro);
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("vi_VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Xử lý trường hợp ngôn ngữ không được hỗ trợ
                }
            } else {
                // Xử lý trường hợp không khởi tạo được TextToSpeech
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Xử lý sự kiện khi bắt đầu chuyển đổi giọng nói
            }

            @Override
            public void onDone(String utteranceId) {
                // Xử lý sự kiện khi hoàn tất chuyển đổi giọng nói
            }

            @Override
            public void onError(String utteranceId) {
                // Xử lý sự kiện khi xảy ra lỗi trong quá trình chuyển đổi giọng nói
            }
        });
        textToSpeech.setPitch(1.2f);
        textToSpeech.setSpeechRate(1f);
        micro.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            } else
                textToSpeech.speak(contentRead, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
        });
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
        webSettings.setRenderPriority(WebSettings.RenderPriority.LOW);
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
                contentRead += document.selectFirst("h1.title_detail").text() + " ";
                Elements ps = document.select("#entry-body p");
                for (Element e : ps) {
                    contentRead += e.text() + " ";
                }
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
            textToSpeech.stop();
            textToSpeech.shutdown();
            super.onBackPressed();
        }
    }
}
