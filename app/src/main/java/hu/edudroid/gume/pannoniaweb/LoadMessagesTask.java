package hu.edudroid.gume.pannoniaweb;

import android.os.AsyncTask;
import android.webkit.WebView;

import static hu.edudroid.gume.pannoniaweb.WebUtils.httpGet;

/**
 * Created by gume7 on 9/11/2015.
 */
public class LoadMessagesTask extends AsyncTask<String, Void, String> {

    protected WebView webView = null;

    public LoadMessagesTask(WebView wv) {
        webView = wv;
    }

    @Override
    protected String doInBackground(String... params) {

        String response = httpGet("http://pannonia.kispest.hu/interaktiv/uzenet.php", null);
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //String zoom = "<meta name=\"viewport\" content =\"width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0\"/>";
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadData(s, "text/html; charset=UTF-8", null);
    }
}
