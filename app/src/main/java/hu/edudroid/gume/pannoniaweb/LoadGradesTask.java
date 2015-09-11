package hu.edudroid.gume.pannoniaweb;

import android.os.AsyncTask;
import android.webkit.WebView;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hu.edudroid.gume.pannoniaweb.WebUtils.httpGet;
import static hu.edudroid.gume.pannoniaweb.WebUtils.httpPost;

/**
 * Created by gume7 on 9/10/2015.
 */
public class LoadGradesTask extends AsyncTask<String, Void, String> {

    protected WebView webView = null;

    public LoadGradesTask(WebView wv) {
        webView = wv;
    }
    @Override
    protected String doInBackground(String... params) {

        String omid = params[0];

        CookieManager cm = new CookieManager();
        String response1 = httpGet("http://pannonia.kispest.hu/interaktiv/szulo.php", cm);

        Map<String, Object> mparams = new HashMap<String, Object>();
        mparams.put("Button2SubmitEvent", (Object) "Button2_Button2Click");
        mparams.put("Edit1", (Object) params[0]);
        mparams.put("Edit3", (Object) params[1]);

        String response2 = httpPost("http://pannonia.kispest.hu/interaktiv/szulo.php", cm, mparams);

        //name="HiddenField1" value="501"
        Pattern p2 = Pattern.compile("name=\"HiddenField1\" value=\"(.*?)\"");
        Matcher m2 = p2.matcher(response2);
        if (!m2.find()) return null;

        mparams.clear();
        mparams.put("Edit1_hidden", (Object) params[0]);
        mparams.put("Edit2_hidden", (Object) "");
        mparams.put("Button1SubmitEvent", (Object) "Button1_Button1Click");
        mparams.put("Button3SubmitEvent", (Object) "");
        mparams.put("Button5SubmitEvent", (Object) "");
        mparams.put("Edit1", (Object) params[0]);
        mparams.put("Edit2", (Object) params[0]);
        mparams.put("Edit3", (Object) params[1]);
        mparams.put("HiddenField1", (Object) m2.group(1));

        String response3 = httpPost("http://pannonia.kispest.hu/interaktiv/szulo.php", cm, mparams);

        String response4 = httpGet("http://pannonia.kispest.hu/interaktiv/lek.php", cm);

        Pattern p4 = Pattern.compile("(<div id=\"Label1\".*?</div>).*?(<div id=\"Label2\".*?</div>).*?(<div id=\"Label3\".*?</div>)");
        Matcher m4 = p4.matcher(response4);
        if (m4.find()) {
            String ls = m4.group(1) + "\n" + m4.group(2) + "\n" + m4.group(3);
            return ls.replaceAll("height:.*?;width:.*?", "");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        webView.loadData(s, "text/html; charset=UTF-8", null);
    }
}
