package com.nwsoft.maninwork.apis;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Seongil on 2016-04-21.
 */
public class ListQuotesAsync extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {
        //1. Http Request 사용하지 않고.
        //String result= getQuoteGoogle("");
        String urlstr="https://www.google.com/finance?q=%s&ei=BmAYV5GaO4aO0gSCiJmIBg";
        String exchange="KRX:";
        String symbol="005930";
        String stockcode= exchange + symbol;
        urlstr = String.format(urlstr, stockcode);
        //2. Jsoup을 바로 사용하거나
        String resultPrice = getQuoteJsoup(urlstr);
        //
//        Log.i("-->HTML:", result);
//        int idstart = result.indexOf("<span class=\"pr\">");
//        String strPrice = result.substring(idstart, idstart + 100);
//        Log.i("--HTML:", strPrice);
        return null;
        //3. Activity에서 Volley(Async호출)+Jsoup(Html parsing)을 사용
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    //
    private String getQuoteJsoup(String urlstr) {
        String price ="";
        try {
            Document doc = Jsoup.connect(urlstr).get();
            Elements spans = doc.select("span.pr");
            for (Element span: spans) {
                price = span.text();
                Log.i("-->PRC", price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return price;
    }
    public static String getQuoteGoogle(String urlstr) {
        urlstr="https://www.google.com/finance?q=%s&ei=BmAYV5GaO4aO0gSCiJmIBg";
        String exchange="KRX:";
        String symbol="005930";
        String stockcode= exchange + symbol;
        Log.i("-->005930",urlstr);
        try {
            stockcode= URLEncoder.encode(stockcode, "UTF-8");	//"realtor1%40aaa.com";
            URL url = new URL(String.format(urlstr, stockcode));
            String line;
            String id="", userEmail="", userPass, userApps, userNick, userEtc, regDate;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            /*****
            JSONObject json=new JSONObject(sb.toString());
            Log.i("--->sb",String.format("sb=%s, json=%s",sb.toString(), json.toString()));
            JSONArray ja = json.optJSONArray("items");
            if (ja == null) return null;
            for(int i=0;i<ja.length();i++) {
                JSONObject obj = ja.getJSONObject(i);
                argBean[0]=id = obj.getString("id")==null?"":obj.getString("id");
                argBean[1]=userEmail = obj.getString("userEmail")==null?"":obj.getString("userEmail");
                argBean[2]=userPass = obj.getString("userPass")==null?"":obj.getString("userPass");
                argBean[3]=userApps=obj.getString("userApps")==null?"":obj.getString("userApps");
                argBean[4]=userNick = obj.getString("userNick")==null?"":obj.getString("userNick");
                argBean[5]=userEtc = obj.getString("userEtc")==null?"":obj.getString("userEtc");
                //argBean[6]=regDate = obj.getString("regDate")==null?"":obj.getString("regDate");
                Log.i("jsonResult", id+ userEmail+ userPass+ userApps+ userNick+ userEtc);
            }
             ****/
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
