package com.nwsoft.maninwork.beans;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nwsoft.maninwork.R;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Laborx> {
    private Context context;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private int layoutid;
    private ArrayList<Laborx> items;
    Laborx lb;
    class ViewHolder {
        TextView t1;    //name
        TextView t2;    //custname
        TextView t23_mobile;
        TextView t3;
        TextView t4;
        TextView t5;
        TextView t56_lemail;
        TextView t6;
    }
    public MyAdapter(Context context, int textViewResourceId, ArrayList<Laborx> items) {
        super(context, textViewResourceId, items);

        this.context=context;
        this.layoutid=textViewResourceId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        lb = items.get(position);
        ViewHolder holder=null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(layoutid, null);
            holder = new ViewHolder();
            ((TextView)convertView.findViewById(R.id.textView21)).setVisibility(View.GONE);
            holder.t1=(TextView)convertView.findViewById(R.id.txt_r1);
            holder.t2 = (TextView)convertView.findViewById(R.id.txt_r2);
            holder.t23_mobile=(TextView)convertView.findViewById(R.id.txt_mobile);
            holder.t3 = (TextView) convertView.findViewById(R.id.txt_r3);
            holder.t4 = (TextView) convertView.findViewById(R.id.txt_r4);
            holder.t56_lemail=(TextView)convertView.findViewById(R.id.txt_lemail);
            holder.t5 = (TextView) convertView.findViewById(R.id.txt_r5);

            holder.t6 = (TextView) convertView.findViewById(R.id.txt_r6);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        //if (q != null) {
        holder.t1.setText(lb.lname);                 holder.t1.setTag(lb.lid );
        holder.t2.setText(lb.cname);  holder.t2.setTag(lb.cname);
        holder.t23_mobile.setText(lb.lmobile);
        holder.t3.setText(String.valueOf(lb.lbasepay));
        holder.t4.setText(String.valueOf( lb.lworkday));
        holder.t5.setText(String.valueOf( lb.lpay));
        holder.t56_lemail.setText(lb.lemail);
        holder.t6.setText(lb.lregdate);
            //findQuotesVolley(holder);
        //}
        return convertView;
    }
    private String getCustName(String cid) {
        mDb = context.openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        if (cid==null) {
            cid="0";
        }
        mCursor= mDb.rawQuery("select cname from TableCompany where cid={cid}"
                .replace("{cid}",cid),null);
        String custName = "";
        try {
            while (mCursor.moveToNext()) {
                custName = mCursor.getString(mCursor.getColumnIndex("cname"));
            }
            return custName==null?"":custName;
        } finally {
            mCursor.close();
            mDb.close();
        }
    }
/****************************************
    private void findQuotesVolley(final ViewHolder holder) {
        //Volley test
        //1. Async + HttpRequest가 기본
        //2. Async + Jsoup은 편리
        //3. Volley + Jsoup은 메인로직에서 코드 심플하게.
        final ArrayList<Laborx> labours = new ArrayList<>();
        //RequestQueue rq = Volley.newRequestQueue(context.getApplicationContext()); //required
        RequestQueue rq = MySingleton.getInstance(context.getApplicationContext()).getRequestQueue();
        String urlstr="http://www.google.com/finance?q=samsung".replace("samsung", holder.t2.getText().toString() +":"+ holder.t1.getText().toString());
        //Log.i("URL",urlstr);
        StringRequest req = new StringRequest(Request.Method.GET, urlstr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Document doc = Jsoup.parse(data);
                        if (doc.select("title").text().contains("Search")) {
                            Elements trs = doc.select("tr.snippet");
                            for (Element tr: trs) {
                                Laborx labour = new Laborx();
                                labour.lid  =tr.select("td:nth-child(1)").text();
                                labour.name =tr.select("td:nth-child(2)").text();
                                labour.workday=tr.select("td:nth-child(3)").text();
                                labour.price=tr.select("td:nth-child(4)").text();
                                labour.priceChange = tr.select("td:nth-child(6)").select("span:nth-child(1)").text(); //chg + pct
                                labour.priceChangePercent = tr.select("td:nth-child(6)").select("span:nth-child(2)").text(); //chg + pct
                                labours.add(labour);
                            }
                        }  else {
                            Elements names = doc.select("meta[itemprop=name]");
                            Elements imsgeUrls = doc.select("meta[itemprop=imageUrl");
                            Elements symbols = doc.select("meta[itemprop=tickerSymbol]");
                            Elements exchages = doc.select("meta[itemprop=exchange]");
                            Elements prices = doc.select("meta[itemprop=price]");
                            Elements pricesChange = doc.select("meta[itemprop=priceChange]");
                            Elements pricesChangePercent = doc.select("meta[itemprop=priceChangePercent]");
                            Elements quoteTime = doc.select("meta[itemprop=quoteTime]");

                            Laborx labour = new Laborx();
                            labour.company= names.get(0).attr("content").toString();
                            String imgUrl=imsgeUrls.get(0).attr("content").toString();
                            labour.symbol= symbols.get(0).attr("content").toString();
                            labour.exchange=exchages.get(0).attr("content").toString();
                            labour.price=prices.get(0).attr("content").toString();
                            labour.priceChange=pricesChange.get(0).attr("content").toString();
                            labour.priceChangePercent=pricesChangePercent.get(0).attr("content").toString();
                            labours.add(labour);

                            holder.t3.setText(labour.price);
                            holder.t4.setText(labour.company);
                            if (labour.priceChange != null && !labour.priceChange.contains(new String("+"))) {
                                holder.t5.setTextColor(Color.RED);
                                holder.t6.setTextColor(Color.RED);
                            }
                            holder.t5.setText(labour.priceChange);
                            holder.t6.setText(String.format("(%s)",  labour.priceChangePercent));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Handle error
                    }
                }
        );
        rq.add(req);  //required
        //MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(req);
    }
************************////

}
