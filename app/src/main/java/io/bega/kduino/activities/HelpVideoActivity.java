package io.bega.kduino.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;

import io.bega.kduino.R;
import io.bega.kduino.fragments.help.HTML5WebView;
import io.bega.kduino.kdUINOApplication;

public class HelpVideoActivity extends BaseActivity {

    private HTML5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setContentView(R.layout.activity_help_video);
        this.loadHelpVideo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help_video, menu);
        return true;
    }

    private void loadHelpVideo()
    {
        mWebView = new HTML5WebView(this);

        //Auto playing vimeo videos in Android webview
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.OFF);
        mWebView.getSettings().setAllowFileAccess(true);

        mWebView.loadUrl("http://player.vimeo.com/video/115469736?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800");
//		mWebView.loadUrl("http://player.vimeo.com/api/examples/simple");
//        mWebView.loadUrl("file:///android_asset/1_0.html");
//		mWebView.loadUrl("http://192.168.1.4:9090/playground.html");
//		mWebView.loadUrl("http://ua.brad.is");
        setContentView(mWebView.getLayout());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.getItemId() == R.id.reload){
            mWebView.destroyDrawingCache();
            Log.d(kdUINOApplication.TAG, "Reloading..");
            mWebView.reload();
        }

        return super.onOptionsItemSelected(item);
    }
}
