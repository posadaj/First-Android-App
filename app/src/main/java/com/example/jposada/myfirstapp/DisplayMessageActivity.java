package com.example.jposada.myfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/* Javascript interface to extract content from a WebView and pass it back to current activity */
class ContentExtractor
{
    private TextView contentView;  // After JS extracts content from webview, place it here

    public ContentExtractor(TextView aContentView) {
        contentView = aContentView;
    }

    @JavascriptInterface
    public void processContent(String aContent)
    {
        final String content = aContent;
        contentView.post(new Runnable()
        {
            public void run()
            {
                contentView.setText(content);

            }
        });
    }

    @JavascriptInterface
    public void compareContent()
    {
        contentView.post(new Runnable()
        {
            public void run() {
                Log.v("JP: ", (String) contentView.getText());
            }
        });
    }
}

public class DisplayMessageActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get view components
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        final TextView contentView = (TextView) findViewById(R.id.contentView);

        // Use JS interface to create a custom web client
        webview.addJavascriptInterface(new ContentExtractor(contentView), "INTERFACE");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
                view.loadUrl("javascript:window.INTERFACE.compareContent();");
            }
        });

        // Handle user-input url
        Intent intent = getIntent();
        String input_url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        webview.loadUrl(input_url);
    }
}