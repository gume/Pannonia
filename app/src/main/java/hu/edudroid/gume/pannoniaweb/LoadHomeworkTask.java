package hu.edudroid.gume.pannoniaweb;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hu.edudroid.gume.pannoniaweb.WebUtils.httpGet;
import static hu.edudroid.gume.pannoniaweb.WebUtils.httpPost;

/**
 * Created by gume7 on 9/9/2015.
 */
public class LoadHomeworkTask extends AsyncTask<String, Void, String> {

    protected final ArrayList<String> classids = new ArrayList<String>(
            Arrays.asList("1a", "1b", "2a", "2b", "3a", "3b", "4a", "4b",
                    "5a", "5b", "6a", "6b", "7a", "7b", "8a", "8b"));

    protected WebView webView = null;

    public LoadHomeworkTask(WebView wv) {
        webView = wv;
    }

    @Override
    protected String doInBackground(String... params) {

        int clasid = classids.indexOf(params[0]) + 1;

        CookieManager cm = new CookieManager();
        httpGet("http://pannonia.kispest.hu/interaktiv/hf.php", cm);

        Map<String, Object> mparams = new HashMap<String, Object>();
        mparams.put("ComboBox1SubmitEvent", (Object) "ComboBox1_ComboBox1Change");
        mparams.put("ComboBox2SubmitEvent", (Object) "");
        mparams.put("ComboBox1", (Object) new Integer(clasid).toString());
        mparams.put("ComboBox2", (Object) "5");
        String response = httpPost("http://pannonia.kispest.hu/interaktiv/hf.php", cm, mparams);

        Pattern p = Pattern.compile("<div id=\"Label1\".*?</div>");
        Matcher m = p.matcher(response);
        if (m.find()) {
            return m.group();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        webView.loadData(s, "text/html; charset=UTF-8", null);
    }

}
