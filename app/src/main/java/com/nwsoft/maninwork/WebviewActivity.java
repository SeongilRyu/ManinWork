package com.nwsoft.maninwork;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.IOUtils;
import com.nwsoft.maninwork.backend.companyApi.model.Company;
import com.nwsoft.maninwork.beans.Companyx;
import com.nwsoft.maninwork.beans.Laborx;
import com.nwsoft.maninwork.beans.Payrollx;
import com.nwsoft.maninwork.beans.Transactionx;
import com.nwsoft.maninwork.mylibs.AsyncResult;
import com.nwsoft.maninwork.mylibs.GetGoogleSheetTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WebviewActivity extends AppCompatActivity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    //https://developer.chrome.com/multidevice/webview/gettingstarted
    SQLiteDatabase mDb;
    SharedPreferences mSettings;
    String mGmail;
    Cursor mC;
    AlertDialog mDialog;
    private ListView listView;
    private WebView webView;
    private String mYm=null;
    private TextView txt_payym;
    private String mLname;
    private ArrayList<String> mTax;
    private String mCurrentPhotoPath = "";
    private int mWidth; private int mHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");
        mLname = getIntent().getStringExtra("lname");
        mYm = getIntent().getStringExtra("ex_ym");

        getTaxIncomeData();
        if ("listWebview".equalsIgnoreCase("listWebview")) {
            setContentView(R.layout.activity_webview);
            listView=(ListView)findViewById(R.id.listView4);
            registerForContextMenu(listView);
        } else {
            Log.i("direct","run direct webview");
            setContentView(R.layout.row_webview);
        }
        initUi();
        if (mYm==null || mYm.length() < 1) {
            showMyDialog();
        } else {
            txt_payym.setText("Payroll for " + mYm);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mYm==null || mYm.isEmpty()) return;
        if ("listWebview".equalsIgnoreCase("listWebview")) {
            ArrayList<Pair<Payrollx,String> > mList = getHtml();
            MyWebAdapter myAdapter = new MyWebAdapter(WebviewActivity.this,
                    R.layout.row_webview,mList);
            listView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        } else {
            Log.i("direct","run direct webview");
            displayWebview();
        }
    }
    private void initUi() {
        txt_payym = (TextView)findViewById(R.id.textView40);

    }
    private void showMyDialog() {
        //Make view for dialog...
        LinearLayout li = new LinearLayout(this);
        li.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
        li.setLayoutParams(lp);
        final EditText edt_ym = new EditText(this);
        edt_ym.setLayoutParams(lp);
        edt_ym.setLines(1);
        Calendar c = Calendar.getInstance();
        String tmpYm = String.format("%04d%02d",c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
        edt_ym.setHint(tmpYm);
        edt_ym.setText(tmpYm);
        li.addView(edt_ym);
        TextView tvt = new TextView(this);
        tvt.setText("Enter Payroll Year/month");
        mDialog = new AlertDialog.Builder(WebviewActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Enter Payroll YYYYmm").setCustomTitle(tvt)
                .setView(li)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String retYm = edt_ym.getText().toString();
                        Log.i("dialog", "clicked OK" + retYm);
                        if (retYm!=null && !retYm.isEmpty()) {
                            mYm = retYm;
                            txt_payym.setText("Payroll for " + mYm);
                            onResume();
                        } else {
                            Snackbar.make(edt_ym, "No Data to process", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            finish();
                        }
                    }
                }).create();
        mDialog.show();
    }
    private void displayWebview() {
        //TEST METHOD...NOT USED.
        ArrayList<Pair<Payrollx,String>> mList = getHtml();
        Payrollx px = (Payrollx) mList.get(0).first;
        String shtm= mList.get(0).second;
        TextView tv = (TextView)findViewById(R.id.txt_webtitle);
        tv.setText("Test Webview...");
        webView=(WebView)findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setUseWideViewPort(true);
        //webView.loadDataWithBaseURL(null, shtm, "text/HTML", "UTF-8", null);
        // shtm = "<html><head></head><body><h1>hello</h1></body></html>";
        webView.loadData(shtm,"text/html; charset=utf-8","UTF-8");
        //webView.loadUrl("http://www.google.com");
    }

    class MyWebAdapter extends ArrayAdapter<Pair<Payrollx, String>> {
        Context context;
        int resourceid;
        ArrayList<Pair<Payrollx, String>> items;
        Pair<Payrollx, String> pair;
        Payrollx px;
        String shtm;
        class ViewHolder {
            TextView txt_webtitle;
            WebView webView;
        }
        public MyWebAdapter(Context context, int resource, ArrayList<Pair<Payrollx, String>> objects) {
            super(context, resource, objects);
            this.context=context;
            this.resourceid=resource;
            this.items=objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            pair= items.get(position);
            px=(Payrollx)pair.first;
            shtm = pair.second;
            ViewHolder holder = null;
            if (convertView == null) {

                LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = li.inflate(resourceid,null);
                holder = new ViewHolder();
                holder.txt_webtitle=(TextView)convertView.findViewById(R.id.txt_webtitle);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.webView.enableSlowWholeDocumentDraw();
                }
                holder.webView=(WebView)convertView.findViewById(R.id.webView);
                WebSettings settings = holder.webView.getSettings();
                settings.setDefaultTextEncodingName("UTF-8");
//                //settings.setJavaScriptEnabled(true);
                settings.setLoadWithOverviewMode(true); //html fit to screen size
                settings.setUseWideViewPort(true);  //support viewport
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                settings.setJavaScriptEnabled(true);
                /****
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                float scaleX = ((float)metrics.widthPixels)/480f/metrics.scaledDensity;
                float scaleY = ((float)metrics.heightPixels)/320f/metrics.scaledDensity;
                try {
                    holder.webView.setScaleX(scaleX);
                    holder.webView.setScaleY(scaleY);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                ***/
                final Activity activity = WebviewActivity.this;
                holder.webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        // Activities and WebViews measure progress with different scales.
                        // The progress meter will automatically disappear when we reach 100%
                        activity.setProgress(progress * 1000);
                    }
                });
                holder.webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.i("webview",url.toString());
                        createImageFile(shtm, view);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.txt_webtitle.setText(String.valueOf( position + 1) +"/" + px.cname
                    +"/" +px.lname+"/" +px.lmobile);
            holder.webView.loadDataWithBaseURL("nwsoft.co.kr", shtm, "text/HTML", "UTF-8", null);
            //holder.webView.loadData(shtm,"text/html; charset=utf-8","UTF-8");
            //holder.webView.loadUrl("http://www.google.com");
            return convertView;
        }
    }
    private ArrayList<Pair<Payrollx, String>> getHtml() {
        ArrayList<Pair<Payrollx, String>> listObjStr = new ArrayList<>();
        String HtmlTemplete=getTempleteHtml();
        String strHtml="";
        ArrayList<Payrollx> pxes = getPayrollxList();
        String mycomp=mSettings.getString("mycompany","nocompany");
        for (Payrollx px : pxes) {
            strHtml = HtmlTemplete;
            strHtml=strHtml.replace("{company}",mycomp); //px.cname);
            strHtml=strHtml.replace("{tym}",px.pym);
            strHtml=strHtml.replace("{lname}",px.lname);
            strHtml=strHtml.replace("{payhour}",String.valueOf(px.payhour));    //시급=company
            strHtml=strHtml.replace("{payday}",String.valueOf(px.payday));     //일당=시급*8시간
            strHtml=strHtml.replace("{d_std}",String.valueOf(px.d_stds));     //정상=Sum(std) in transaction by month
            strHtml=strHtml.replace("{d_jc}",String.valueOf(px.d_jcs));       //주차=sum(jc) transaction
            strHtml=strHtml.replace("{d_yg}",String.valueOf(px.d_ygs));       //유급=sum(yg)
            strHtml=strHtml.replace("{d_total}",String.valueOf(px.d_total)); //근무일수합=정상+주차+유급
            strHtml=strHtml.replace("{d_transit}",String.valueOf(px.d_transits)); //교대일수(시간)
            strHtml=strHtml.replace("{m_transit}",String.valueOf(px.m_transits)); //교대수당=sum(transition * 시급)
            strHtml=strHtml.replace("{d_transport}",String.format("%d",px.d_transports)); //교통비 일수
            strHtml=strHtml.replace("{m_transport}",String.valueOf(px.m_transports));   //교통비= 교통비일수 * 교통비
            strHtml=strHtml.replace("{pay_base}",String.valueOf(px.pay_base));       //기본급=근무일수*일당 + 교대비 + 교통비
            strHtml=strHtml.replace("{h_over}",String.valueOf(px.workovertimes/100f));
            strHtml=strHtml.replace("{m_over}",String.valueOf(px.m_overs));
            strHtml=strHtml.replace("{h_special}",String.valueOf(px.workspecials));
            strHtml=strHtml.replace("{m_special}",String.valueOf(px.m_specials));
            strHtml=strHtml.replace("{h_spover}",String.valueOf(px.workspecialovers/100f));
            strHtml=strHtml.replace("{m_spover}",String.valueOf(px.m_spovers));
            strHtml=strHtml.replace("{h_night}",String.valueOf(px.worknights));
            strHtml=strHtml.replace("{m_night}",String.format("%d",px.m_nights));
            strHtml=strHtml.replace("{h_latea}",String.valueOf(px.worklateearlys));
            strHtml=strHtml.replace("{m_latea}",String.valueOf(px.m_lateas));
            strHtml=strHtml.replace("{pay_bonus}",String.valueOf(px.pay_bonus));
            strHtml=strHtml.replace("{pay_total}",String.valueOf(px.pay_total));

            strHtml=strHtml.replace("{t_income}",String.format("%d",px.t_income));     //소득세 구간별 적용
            strHtml=strHtml.replace("{t_income10}",String.format("%d",px.t_income10));    //주민세
            strHtml=strHtml.replace("{t_pension}",String.format("%d",px.t_pension));
            strHtml=strHtml.replace("{t_health}",String.format("%d",px.t_health));
            strHtml=strHtml.replace("{t_health10}",String.format("%d",px.t_health10));
            strHtml=strHtml.replace("{t_employ}",String.format("%d",px.t_employ));
            strHtml=strHtml.replace("{t_hurt}",String.format("%d",px.t_hurt));
            strHtml=strHtml.replace("{t_prepay}",String.format("%d",px.t_prepay));
            strHtml=strHtml.replace("{pay_real}",String.format("%d",px.pay_real));
            Pair<Payrollx, String> pair = new Pair<>(px, strHtml);
            listObjStr.add(pair);  //0
        }
        return listObjStr;
    }

    private long makeIncomeTax(long pay_total, int families) {
        long incomeTax=0;
        families= families==0?1:families;
        for (String row : mTax) {
            String key = row.split("/")[0];
            String data = row.split("/")[1];
            float keyLow = Float.parseFloat( key.split(":")[0]);
            float keyUpp = Float.parseFloat(key.split(":")[1]);
            if (pay_total >= keyLow && pay_total < keyUpp) {
                incomeTax = (long)Float.parseFloat(data.split(":")[families-1]);
                Log.i("incomTaxIN", String.format("low=%f,upp=%f,pay=%d,tax=%d,family=%d",
                        keyLow,keyUpp,pay_total,incomeTax,families));
                return incomeTax;
            }
        }
        return incomeTax;
    }
    private String getTempleteHtml() {
        StringBuilder sb=new StringBuilder();
        InputStream stream= null;
        BufferedReader in=null;
        try {
            stream = getAssets().open("ManinWorkPayroll.html");
            in= new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sbtr = transLateHeader(sb.toString());
        return sbtr;
    }
    private String transLateHeader(String in) {
        String out = "";
        out=in.replace("업체명",getResources().getString(R.string.hdr_company))
                .replace("년월",getResources().getString(R.string.hdr_ym))
                .replace("성명",getResources().getString(R.string.hdr_name))
                .replace("기본급 일수",getResources().getString(R.string.hdr_basedays))
                .replace("교대수당",getResources().getString(R.string.hdr_transit))
                .replace("교통비",getResources().getString(R.string.hdr_transport))
                .replace("기본급",getResources().getString(R.string.hdr_paybase))
                .replace("시급",getResources().getString(R.string.hdr_payperhour))
                .replace("일당",getResources().getString(R.string.hdr_payperday))
                .replace("출근",getResources().getString(R.string.hdr_standard))
                .replace("주차",getResources().getString(R.string.hdr_jc))
                .replace("유급",getResources().getString(R.string.hdr_yg))
                .replace("일수",getResources().getString(R.string.hdr_days))
                .replace("잔업",getResources().getString(R.string.hdr_overtime))
                .replace("특근연장",getResources().getString(R.string.hdr_spover))
                .replace("특근",getResources().getString(R.string.hdr_special))
                .replace("야간",getResources().getString(R.string.hdr_night))
                .replace("지각조퇴외출",getResources().getString(R.string.hdr_lateearly))
                .replace("상여금",getResources().getString(R.string.hdr_bonus))
                .replace("임금합계",getResources().getString(R.string.hdr_paytotal))
                .replace("계",getResources().getString(R.string.hdr_daytotal))
                .replace("공제금액",getResources().getString(R.string.hdr_deduction))
                .replace("갑근세",getResources().getString(R.string.hdr_incomtax))
                .replace("주민세",getResources().getString(R.string.hdr_taxresidant))
                .replace("국민연금",getResources().getString(R.string.hdr_taxpension))
                .replace("건강보험",getResources().getString(R.string.hdr_medi))
                .replace("장기요양",getResources().getString(R.string.hdr_medilong))
                .replace("고용보험",getResources().getString(R.string.hdr_insu_employ))
                .replace("산재보험",getResources().getString(R.string.hdr_insu_hurt))
                .replace("가불금",getResources().getString(R.string.hdr_prepayed))
                .replace("지급총액",getResources().getString(R.string.hdr_paynet))
                .replace("시간",getResources().getString(R.string.hdr_hour))
                .replace("금액",getResources().getString(R.string.hdr_amount))
                ;
        return out;
    }
    private ArrayList<Payrollx> getPayrollxList() {
        ArrayList<Payrollx> pxes = new ArrayList<>();
        String sql = "select * from TableLabor ;";
        if (mLname==null) {
            return null;
        } else if (mLname.equalsIgnoreCase("all")) {
            sql= "select * from TableLabor where gmail ='{gmail}';".replace("{gmail}",mGmail);
        } else {
            sql= "select * from TableLabor where gmail='{gmail}' and lname='{lname}';"
                    .replace("{gmail}",mGmail).replace("{lname}",mLname);
        }
        mC = mDb.rawQuery(sql,null);
        if (null==mC) return null;
        try {
            while (mC.moveToNext()) {
                Payrollx px = new Payrollx();
                px.pid=0;
                px.pym=mYm;
                px.gmail=mC.getString(mC.getColumnIndex("gmail"));
                px.cname=mC.getString(mC.getColumnIndex("cname"));
                px.lname=mC.getString(mC.getColumnIndex("lname"));
                px.family=mC.getInt(mC.getColumnIndex("lfamily"));
                px.child=mC.getInt(mC.getColumnIndex("lchild"));
                px.lmobile=mC.getString(mC.getColumnIndex("lmobile"));
                px.lemail=mC.getString(mC.getColumnIndex("lemail"));
                Companyx cmx = getCompany(px.cname);
                px.payhour= cmx.cpayperhour;
                px.payday = cmx.cpayperhour * 8; //일당
                px.paytransit = cmx.cpaytransition;
                px.paytransport = cmx.cpaytransport;
                px.bonusrate=cmx.cbonusrate;
                Transactionx tSumx = getTransactions(mYm, px.lname);
                px.workstandards=tSumx.workstandard;
                px.d_stds=tSumx.workstandard/8;     //정상=Sum(std) in transaction by month
                px.work_jcs=tSumx.work_jc;
                px.d_jcs=tSumx.work_jc/8;       //주차=sum(jc) transaction
                px.work_ygs=tSumx.work_yg;
                px.d_ygs=tSumx.work_yg/8;       //유급=sum(yg)
                px.d_total = px.d_stds + px.d_ygs + px.d_jcs;
                //
                px.d_transits=  0;  ; //교대일수(시간)-없어집
                px.m_transits= px.d_transits * px.paytransit; //교대수당=sum(transition * 교대일수)
                px.d_transports= 1; //tSumx.d_transport; //교통비 일수
                px.m_transports = px.d_transports * px.paytransport;
                px.pay_base = px.d_total * px.payday + px.m_transits + px.m_transports;
                px.pay_bonus = Math.round(px.bonusrate/100 * px.pay_base /12/10)*10;
                //
                px.workovertimes =tSumx.workovertime;
                px.m_overs =(long)(Math.round(px.workovertimes/100f * 1.5 * px.payhour/10)*10);
                px.workspecials = tSumx.workspecial;
                px.m_specials = (long)(Math.round(px.workspecials * 1.5 * px.payhour/10)*10);
                px.workspecialovers = tSumx.workspecialover;
                px.m_spovers= (long)(px.workspecialovers/100f * 2.0 * px.payhour);
                px.worknights = tSumx.worknight;
                px.m_nights = (long)(Math.round(px.worknights * 2.0 * px.payhour/10)*10);
                px.worklateearlys = tSumx.worklateearly;
                px.m_lateas = (long)(px.worklateearlys * 1.0 * px.payhour/10)*10;

                px.pay_total=px.pay_base + px.pay_bonus;

                px.t_income = makeIncomeTax(px.pay_total/1000, (int)(px.family + px.child)); //pay_total * 8/100; //소득세
                px.t_income10 = Math.round(px.t_income *10/100/10)*10;     //주민세 = 소득세 * 10%
                px.t_pension = (long)(Math.round(0.045 * px.pay_total/10)*10);
                px.t_health = (long)(Math.round(0.0305 * px.pay_total/10)*10);
                px.t_health10 = (long)(Math.round(0.0655 * px.t_health/10)*10);
                px.t_employ = (long)(Math.round(0.09 * px.pay_total/10)*10);  //고용보험
                px.t_hurt = (long)(Math.round(0.016 * px.pay_total/10)*10); //산재보험
                px.t_prepay = 0;    //가불금
                px.pay_real = px.pay_total
                        - (px.t_income + px.t_income10 + px.t_pension + px.t_health
                        + px.t_health10 + px.t_employ + px.t_hurt + px.t_prepay);
                pxes.add(px);
            }
        } finally {
            mC.close();
        }
        return pxes;
    }

    private Companyx getCompany(String cname) {
        Companyx cmx = new Companyx();
        String sql = "select * from TableCompany where gmail='{gmail}' and cname='{cname}';";
        sql= sql.replace("{gmail}", mGmail).replace("{cname}",cname);
        Cursor c = mDb.rawQuery(sql,null);
        try {
            while (c.moveToNext()) {
                cmx.cid = c.getLong(c.getColumnIndex("cid"));
                cmx.gmail = c.getString(c.getColumnIndex("gmail"));
                cmx.cname = c.getString(c.getColumnIndex("cname"));
                cmx.crep = c.getString(c.getColumnIndex("crep"));
                cmx.crepmobile = c.getString(c.getColumnIndex("crepmobile"));
                cmx.cemail = c.getString(c.getColumnIndex("cemail"));
                cmx.cpayperhour = c.getLong(c.getColumnIndex("cpayperhour"));
                cmx.cpaydays = c.getLong(c.getColumnIndex("cpaydays"));
                cmx.cpaytransport = c.getLong(c.getColumnIndex("cpaytransport"));
                cmx.cpaytransition = c.getLong(c.getColumnIndex("cpaytransition"));
                cmx.cbonusrate = c.getLong(c.getColumnIndex("cbonusrate"));
                cmx.caddress = c.getString(c.getColumnIndex("caddress"));
                cmx.cdata = c.getString(c.getColumnIndex("cdata"));
                cmx.cregdate = c.getString(c.getColumnIndex("cregdate"));
            }
        } finally {
            c.close();
        }
        return cmx;
    }
    private Transactionx getTransactions(String ym, String lname) {
        String sql = "select gmail, lname, tym " +
                ",sum(workstandard) w_stds, " +
                "sum(work_jc) w_jcs, " +
                "sum(work_yg) w_ygs, " +
                "sum(workovertime) w_ovrs, " +
                "sum(workspecial) w_spcs, " +
                "sum(workspecialover) w_spcovrs, " +
                "sum(worknight) w_ngts, " +
                "sum(worklateearly) w_lates " +
                " from TableTransaction " +
                "where gmail='{gmail}' and lname='{lname}' and tym='{tym}' " +
                "group by gmail, lname, tym;";
        sql=sql.replace("{gmail}",mGmail)
                .replace("{lname}",lname)
                .replace("{tym}",ym);
        Cursor c = mDb.rawQuery(sql,null);
        Transactionx tSumx = new Transactionx();
        try {
            while (c.moveToNext()) {
                tSumx.gmail= c.getString(c.getColumnIndex("gmail"));
                tSumx.lname= c.getString(c.getColumnIndex("lname"));
                tSumx.tym= c.getString(c.getColumnIndex("tym"));
                tSumx.workstandard= c.getLong(c.getColumnIndex("w_stds"));
                tSumx.work_jc= c.getLong(c.getColumnIndex("w_jcs"));
                tSumx.work_yg= c.getLong(c.getColumnIndex("w_ygs"));
                tSumx.workovertime= c.getLong(c.getColumnIndex("w_ovrs"));
                tSumx.workspecial= c.getLong(c.getColumnIndex("w_spcs"));
                tSumx.workspecialover= c.getLong(c.getColumnIndex("w_spcovrs"));
                tSumx.worknight= c.getLong(c.getColumnIndex("w_ngts"));
                tSumx.worklateearly= c.getLong(c.getColumnIndex("w_lates"));
            }
        }  finally {
            c.close();
        }
        return tSumx;
    }
    private Laborx moveTable2Obj_Labor(Cursor cu) {
        Laborx lb = new Laborx();
        lb.lid= (cu.getLong(cu.getColumnIndex("lid")));
        Log.i("viewLid", String.valueOf(cu.getLong(cu.getColumnIndex("lid"))) + cu.getString(cu.getColumnIndex("lname")).toString());
        lb.gmail=cu.getString(cu.getColumnIndex("gmail")).toString();
        lb.lname=cu.getString(cu.getColumnIndex("lname")).toString();
        lb.lmobile=cu.getString(cu.getColumnIndex("lmobile")).toString();
        lb.cname=cu.getString(cu.getColumnIndex("cname")).toString();
        lb.lfamily=cu.getLong(cu.getColumnIndex("lfamily"));
        lb.lchild=cu.getLong(cu.getColumnIndex("lchild"));
        lb.lworkday=cu.getLong(cu.getColumnIndex("lworkday"));
        //lb.lworkday=getworkday(lb.lname);
        lb.lbasepay=cu.getLong(cu.getColumnIndex("lbasepay"));
        lb.lpay=cu.getLong(cu.getColumnIndex("lpay"));
        lb.lregdate=cu.getString(cu.getColumnIndex("lregdate"));
        return  lb;
    }
    private void getTaxIncomeData() {
        Log.i("TAG", "income tax table start down...");
        mTax = new ArrayList<String>();
        new GetGoogleSheetTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                ArrayList<String> incometaxes = new ArrayList<String>();
                try {
                    JSONArray rows = object.getJSONArray("rows");
                    for (int r = 0; r < rows.length(); ++r) {
                        JSONObject row = rows.getJSONObject(r);
                        JSONArray columns = row.getJSONArray("c");
                        String lower = columns.getJSONObject(0).getString("v");	//1st Column
                        String upper = columns.getJSONObject(1).getString("v");	//2nd Column
                        String col01 = columns.getJSONObject(2).getString("v");	//3nd Column
                        String col02 = columns.getJSONObject(3).getString("v");	//3nd Column
                        String col03 = columns.getJSONObject(4).getString("v");	//3nd Column
                        String col04 = columns.getJSONObject(5).getString("v");	//3nd Column
                        String col05 = columns.getJSONObject(6).getString("v");	//3nd Column
                        String col06 = columns.getJSONObject(7).getString("v");	//3nd Column
                        String col07 = columns.getJSONObject(8).getString("v");	//3nd Column
                        String col08 = columns.getJSONObject(9).getString("v");	//3nd Column
                        String col09 = columns.getJSONObject(10).getString("v");	//3nd Column
                        String col10 = columns.getJSONObject(11).getString("v");	//3nd Column
                        String col11 = columns.getJSONObject(12).getString("v");	//3nd Column
                        incometaxes.add(lower +":"+upper+"/"+col01+":"+col02+":"+col03+":"+col04
                                +":"+col05+":"+col06+":"+col07+":"+col08+":"+col09+":"+col10+":"+col11);
                        //Log.i("TAG1", upper +"/"+lower+"/");
                        mTax = incometaxes;
                    }
                    Log.i("TAG1", incometaxes.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute("https://spreadsheets.google.com/tq?key=1xTSz4NpgXllc9rDS2UX2C2Kh-h9OCAqPrDzJd7x7PEo");
        Log.i("TAG", "income tax table end down...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_payroll).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_sendauto).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sendauto:
                sendSmsAuto();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_actionmode, menu);
        menu.findItem(R.id.menu_edit).setVisible(false);
        menu.findItem(R.id.menu_phone).setVisible(false);
        menu.findItem(R.id.menu_payroll).setVisible(false);
        menu.findItem(R.id.menu_sms).setVisible(true);
        menu.findItem(R.id.menu_mailto).setVisible(true);
        menu.findItem(R.id.menu_kakao).setVisible(true);
        menu.findItem(R.id.menu_share).setVisible(false);
        menu.findItem(R.id.menu_print).setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //ref...
        int pos=info.position;		//첫째 info.position=0 info.id=1(ListView의 position이다.)
        long idx = info.id;				//첫째 info.id=1
        Pair<Payrollx,String> pair = (Pair<Payrollx, String>) listView.getAdapter().getItem(pos);
        Payrollx px = (Payrollx) pair.first;
        String wageHtml = pair.second;
        LinearLayout rowView = (LinearLayout)listView.getAdapter().getView(pos,null,listView);
        TextView rowText =(TextView) rowView.findViewById(R.id.txt_webtitle);
        WebView rowWebview =(WebView)rowView.findViewById(R.id.webView);
        Log.i("VeriRow",String.valueOf(pos) +":"+ rowText.getText().toString() +
                String.valueOf(rowWebview.getId()));

        String title = "Payroll of " + px.lname;
        String mobile=px.lmobile;
        String email = px.lemail;
        String content = "\n"+ px.pym + "-\nTotal Pay: "+px.pay_total
                +"\nDeduction: " + (px.t_income + px.t_income10 + px.t_pension + px.t_health + px.t_health10 + px.t_employ + px.t_hurt + px.t_prepay)
                +"\nNet Pay: "+ px.pay_real +"\nhttp://www.google.com"; //wageHtml;
        Uri imageUri = Uri.parse(mCurrentPhotoPath);
        //ref....
        switch (item.getItemId()) {
            case R.id.menu_phone:
                if (mobile.length()<1) return false;
                Intent iphonecall = new Intent(Intent.ACTION_DIAL);
                iphonecall.setData(Uri.parse("tel:" + mobile));
                startActivity(iphonecall);
                return true;
            case R.id.menu_sms:
                composeMmsMessage(mobile, title, content, imageUri);
                return true;
            case R.id.menu_mailto:
                composeEmail(new String[] {email}, content, imageUri);
                return true;
            case R.id.menu_kakao:
                composeKakao(title, content, imageUri);
                return true;
            case R.id.menu_share:
                Intent intentshare = new Intent();
                intentshare.setAction(Intent.ACTION_SEND);
                intentshare.setType("text/plain");
                //intentshare.setType("text/html");
                intentshare.putExtra(Intent.EXTRA_SUBJECT, title);
                intentshare.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(content));
                Spanned html = Html.fromHtml(content);
                //intentshare.putExtra(Intent.EXTRA_HTML_TEXT,Html.fromHtml(content));
                startActivity(Intent.createChooser(intentshare,"SHARE"));
                return true;
            case R.id.menu_print:
                if ("print".equalsIgnoreCase("print")) {
                    print2browser();
                } else {
                    doWebViewPrint(content);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void sendSmsAuto() {
        //<uses-permission android:name="android.permission.SEND_SMS"/>
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("5554",null,"my text",null,null);
        //manager.sendDataMessage("desti","scAddr",1521,new byte[0]{0},null,null);
        //manager.sendMultimediaMessage(getApplicationContext(),uri,"loUri",Bundle,null);
        //manager.sendMultipartTextMessage("desti","scAddr",new ArrayList<String>(),null,null);

    }
    private void composeKakao(String title, String content, Uri uri) {
        //단문 메세지만 보낼 수 있다.
        //이미지 보내려면 kkoLink API이용해야 한다.
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        List<ResolveInfo> resolveInfoList =
                getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolveInfoList) {
            ////페이스북 : com.facebook.katana
            if(resolveInfo.activityInfo.packageName.startsWith("com.kakao.talk")){
                intent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }
        if(resolved) {
            startActivity(intent);
        } else {
            Toast.makeText(WebviewActivity.this, "카카오 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createImageFile(String wageHtml, final WebView rowWebview) {
        rowWebview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rowWebview.getViewTreeObserver().removeOnPreDrawListener(this);
                Log.v("TAGTAG",rowWebview.getMeasuredWidth() + "width : " + rowWebview.getWidth());
                Log.v("TAGTAG", rowWebview.getMeasuredHeight() + "height : " + rowWebview.getHeight());
                if (rowWebview.getMeasuredWidth()>0) mWidth=rowWebview.getMeasuredWidth();
                if (rowWebview.getMeasuredHeight()>0) mHeight=rowWebview.getMeasuredHeight();
                Bitmap bm = Bitmap.createBitmap(mWidth,
                        mHeight, Bitmap.Config.ARGB_8888);
                Canvas screenShotCanvas = new Canvas(bm);
                rowWebview.draw(screenShotCanvas);
                FileOutputStream fosImg;
                //String path =WebviewActivity.this.getFilesDir().toString();
                try {
                    //http://androidhuman.com/432
                    //외부저장소-공용영역
                    //Environment.DIRECTORY_PICTURES-/mnt/sdcard/Pictures
                    File storageDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    //File fileImg = File.createTempFile("cap",".png",storageDir);
                    File fileImg = new File(storageDir + "/cap.jpg");
                    mCurrentPhotoPath = "file:" + fileImg.getAbsolutePath();
                    fosImg=new FileOutputStream(fileImg);
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, fosImg);
                    fosImg.flush();    fosImg.close();
                    Log.i("fileImg",mCurrentPhotoPath);
                    rowWebview.setTag(fileImg);
                    addImageToGallery(mCurrentPhotoPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
    public void addImageToGallery(final String filePath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, mCurrentPhotoPath);
        getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    WebView mWebView;
    private void doWebViewPrint(String html) {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(WebviewActivity.this);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("webview", "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });
        // Generate an HTML document on the fly:
        String strHtml = "<html><body><h1>Test Content</h1><p>Testing, " +
                "testing, testing...</p></body></html>";
        strHtml=html;
        webView.loadDataWithBaseURL(null, strHtml, "text/HTML", "UTF-8", null);
        //webView.loadUrl("file:///android_asset/ManinWorkforExcel.htm");

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }
    @TargetApi(19)
    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        //mPrintJobs.add(printJob);
    }
    private void print2browser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        String path = WebviewActivity.this.getFilesDir().toString();
        //intent.setDataAndType(Uri.parse("file:///android_asset/ManinWorkPayroll.html"), "text/html");
        intent.setDataAndType(Uri.fromFile(new File(path, "cap.html")), "text/html");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void composeMmsMessage(String mobile, String title, String content, Uri attachment) {
        //intent-common
        //https://developer.android.com/guide/components/intents-common.html
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+mobile));
        //intent.setData(Uri.parse("smsto:"+mobile));  // This ensures only SMS apps respond
        //intent.setType("image/*");
        //intent.setType("text/plain");
        intent.putExtra("sms_body", title + content);
        //intent.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile);
        //intent.putExtra(Intent.EXTRA_TEXT,title);
        //intent.putExtra(Intent.EXTRA_HTML_TEXT,Html.fromHtml(content));
        //intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+addresses[0])); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) {
            mDb.close();
        }
    }
}
