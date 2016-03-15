package hu.edudroid.gume.pannoniaweb;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by gume7 on 9/9/2015.
 */
public class WebUtils {

    static final String COOKIES_HEADER = "Set-Cookie";

    public final static void getConnCookies(HttpURLConnection uc, CookieManager cm) {

        if (cm == null) return;

        Map<String, List<String>> headerFields = uc.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cm.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

    public final static void putConnCookies(HttpURLConnection uc, CookieManager cm) {

        if (cm == null) return;

        if(cm.getCookieStore().getCookies().size() > 0)
        {
            uc.setRequestProperty("Cookie", TextUtils.join(";", cm.getCookieStore().getCookies()));
        }
    }

    public final static void putConnParams(HttpURLConnection uc, Map<String, Object> params) {

        Uri.Builder ub = new Uri.Builder();
        for (Map.Entry<String, Object> me : params.entrySet()) {
            ub.appendQueryParameter(me.getKey(), (String) me.getValue());
        }
        //Log.i("putConnParam", ub.build().getEncodedQuery());

        try {
            OutputStream os = uc.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(ub.build().getEncodedQuery());
            writer.flush();
            writer.close();
            os.close();
        } catch (Exception e) {
            Log.e("putConnParams", "Exception", e);
        }
    }


    public final static String getConnResponse(HttpURLConnection uc) {

        StringBuilder resp = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) resp.append(line);
        } catch (Exception e) {
            Log.e("getConnResponse", "Exception", e);
        }

        return resp.toString();
    }

    public final static String httpGet(String url, CookieManager cm) {

        String response = null;

        try {
            HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
            if(cm != null) putConnCookies(uc, cm);
            uc.setInstanceFollowRedirects(false);
            uc.connect();
            getConnCookies(uc, cm);
            response = getConnResponse(uc);
            uc.disconnect();
        } catch (Exception e) {
            Log.e("httpGet", "Exception", e);
        }

        return response;
    }

    public final static String httpPost(String url, CookieManager cm, Map<String, Object> params) {

        String response = null;

        try {
            HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
            uc.setRequestMethod("POST");
            uc.setDoInput(true);
            uc.setDoOutput(true);

            if(cm != null) putConnCookies(uc, cm);
            putConnParams(uc, params);

            uc.setInstanceFollowRedirects(false);
            uc.connect();
            getConnCookies(uc, cm);
            response = getConnResponse(uc);
            uc.disconnect();

        } catch (Exception e) {
            Log.e("httpPost", "Exception", e);
        }

        return response;
    }

}

