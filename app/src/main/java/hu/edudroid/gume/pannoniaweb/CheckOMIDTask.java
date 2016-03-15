package hu.edudroid.gume.pannoniaweb;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.net.CookieManager;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hu.edudroid.gume.pannoniaweb.WebUtils.httpGet;
import static hu.edudroid.gume.pannoniaweb.WebUtils.httpPost;

public class CheckOMIDTask extends AsyncTask<String, Void, String> {

    private ListAdapter listAdapter = null;
    private List<Map<String, String>> list = null;

    private HashMap<String, String> listEntry = null;

    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";

    private String name = null;
    private String omid = null;

    private AsyncResponse delegate = null;

    public CheckOMIDTask(ListAdapter la, List<Map<String, String>> l, AsyncResponse d) {

        listAdapter = la;
        list = l;
        delegate = d;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listEntry = new HashMap<String, String>();
        listEntry.put(TEXT1, "Checking");
        listEntry.put(TEXT2, "");
        list.add(listEntry);

        ((SimpleAdapter) listAdapter).notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (listEntry != null) {
            if (result == null || result.equals("")) {
                int i = list.indexOf(listEntry);
                list.remove(i);
            } else {
                listEntry.clear();
                listEntry.put(TEXT1, name);
                listEntry.put(TEXT2, omid);
            }
            ((SimpleAdapter) listAdapter).notifyDataSetChanged();
        }

        if (delegate != null) {
            delegate.processFinish(omid, result);
        }
    }

    @Override
    protected String doInBackground(String... params) {

        omid = params[0];

        CookieManager cm = new CookieManager();
        String response1 = httpGet("http://pannonia.kispest.hu/interaktiv/szulo.php", cm);
        //Log.i("response1", response1);
        //Log.i("cookies1", TextUtils.join(";", cm.getCookieStore().getCookies()));

        Map<String, Object> mparams = new HashMap<String, Object>();
        mparams.put("Button2SubmitEvent", (Object) "Button2_Button2Click");
        mparams.put("Edit1", (Object) params[0]);
        mparams.put("Edit3", (Object) params[1]);

        String response2 = httpPost("http://pannonia.kispest.hu/interaktiv/szulo.php", cm, mparams);
        //Log.i("response2", response2);
        //Log.i("cookies2", TextUtils.join(";", cm.getCookieStore().getCookies()));

        Pattern p = Pattern.compile("<div id=\"Label6\"[^>]*>([^<]*)</div>");
        Matcher m = p.matcher(response2);
        if (m.find()) {
            name = m.group(1);
            //Log.i("match", m.group(1));
        }

        return name;
    }

}
