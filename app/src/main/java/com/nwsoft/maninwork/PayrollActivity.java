package com.nwsoft.maninwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nwsoft.maninwork.beans.Companyx;
import com.nwsoft.maninwork.beans.Laborx;
import com.nwsoft.maninwork.beans.Payrollx;
import com.nwsoft.maninwork.beans.Transactionx;
import com.nwsoft.maninwork.mylibs.AsyncResult;
import com.nwsoft.maninwork.mylibs.GetGoogleSheetTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PayrollActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Laborx mLbx;
    ArrayList<String> mTax;
    ListView listView;
    EditText txt_lname;
    TextView txt_cname;
    SharedPreferences mSettings;
    String mGmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        getTaxIncomeData();
        //
        setContentView(R.layout.activity_payroll);
        mGmail=getSharedPreferences("Settings",MODE_PRIVATE)
                .getString("GMAIL","");
        mLbx = (Laborx)getIntent().getSerializableExtra("ex_laborx");
        Log.i("11111",mLbx.lname);
        initUi();
        txt_lname.setText(mLbx.lname);
        txt_cname.setText(mLbx.cname);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ArrayList<Payrollx> list = getPayrollxList(mLbx.lname);
        Log.i("2222",list.toString());
        MyAdapter myAdapter = new MyAdapter(this, R.layout.row_payroll, list);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    private void initUi() {
        listView = (ListView)findViewById(R.id.list_payroll);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Payrollx px = (Payrollx)parent.getAdapter().getItem(position);
                Intent iTrans = new Intent(PayrollActivity.this,TransactionActivity.class);
                iTrans.putExtra("ex_ym",px.pym);
                iTrans.putExtra("ex_ser_labor",mLbx);
                startActivity(iTrans);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ///TODO: WebActivity for payroll launch....
                return false;
            }
        });
        txt_lname =(EditText)findViewById(R.id.txt_lname);
        txt_lname.setEnabled(false);
        txt_cname=(TextView)findViewById(R.id.txt_cname);

    }

    private ArrayList<Payrollx> getPayrollxList(String lname) {
        db = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        ArrayList<Payrollx> pxes = new ArrayList<>();
        String sql = "select gmail, cname, lname, tym " +
                ",sum(workstandard) w_stds, " +
                "sum(work_jc) w_jcs, " +
                "sum(work_yg) w_ygs, " +
                "sum(workovertime) w_ovrs, " +
                "sum(workspecial) w_spcs, " +
                "sum(workspecialover) w_spcovrs, " +
                "sum(worknight) w_ngts, " +
                "sum(worklateearly) w_lates " +
                " from TableTransaction ";
        sql += "where gmail='{gmail}' and lname='{lname}' " +
                " group by gmail, cname, lname, tym" +
                " order by gmail, cname, lname, tym desc;";
        sql= sql.replace("{gmail}",mGmail).replace("{lname}",lname);
        Cursor mC = db.rawQuery(sql,null);
        if (null==mC) return null;
        try {
            while (mC.moveToNext()) {
                Payrollx px = new Payrollx();
                px.pid=0;
                px.pym=mC.getString(mC.getColumnIndex("tym"));
                px.gmail=mC.getString(mC.getColumnIndex("gmail"));
                px.cname=mC.getString(mC.getColumnIndex("cname"));
                px.lname=mC.getString(mC.getColumnIndex("lname"));
                Laborx lbx = getLaborx(lname);
                px.family=(int)lbx.lfamily;
                px.child=(int)lbx.lchild;
                px.lmobile=lbx.lmobile;
                px.lemail=lbx.lemail;
                Companyx cmx = getCompany(px.cname);
                px.payhour= cmx.cpayperhour;
                px.payday = cmx.cpayperhour * 8; //일당
                px.paytransit = cmx.cpaytransition;
                px.paytransport = cmx.cpaytransport;
                px.bonusrate=cmx.cbonusrate;
                px.workstandards=mC.getLong(mC.getColumnIndex("w_stds"));
                px.d_stds=px.workstandards/8;     //정상=Sum(std) in transaction by month
                px.work_jcs=mC.getLong(mC.getColumnIndex("w_jcs"));
                px.d_jcs=px.work_jcs/8;       //주차=sum(jc) transaction
                px.work_ygs=mC.getLong(mC.getColumnIndex("w_ygs"));
                px.d_ygs=px.work_ygs/8;       //유급=sum(yg)
                px.d_total = px.d_stds + px.d_ygs + px.d_jcs;
                //
                px.d_transits=  0;  ; //교대일수(시간)-없어집
                px.m_transits= px.d_transits * px.paytransit; //교대수당=sum(transition * 교대일수)
                px.d_transports= 1; //tSumx.d_transport; //교통비 일수
                px.m_transports = px.d_transports * px.paytransport;
                px.pay_base = px.d_total * px.payday + px.m_transits + px.m_transports;
                px.pay_bonus = Math.round(px.bonusrate/100 * px.pay_base /12/10)*10;
                //
                px.workovertimes =mC.getLong(mC.getColumnIndex("w_ovrs"));
                px.m_overs =(long)(Math.round(px.workovertimes * 1.5 * px.payhour/10)*10);
                px.workspecials = mC.getLong(mC.getColumnIndex("w_spcs"));
                px.m_specials = (long)(Math.round(px.workspecials * 1.5 * px.payhour/10)*10);
                px.workspecialovers = mC.getLong(mC.getColumnIndex("w_spcovrs"));
                px.m_spovers= (long)(px.workspecialovers * 2.0 * px.payhour);
                px.worknights = mC.getLong(mC.getColumnIndex("w_ngts"));
                px.m_nights = (long)(Math.round(px.worknights * 0.5 * px.payhour/10)*10);
                px.worklateearlys = mC.getLong(mC.getColumnIndex("w_lates"));
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
    private Laborx getLaborx(String lname) {
        Laborx lbx = new Laborx();
        String sql= "select * from TableLabor where gmail='{gmail}' and lname='{lname}';"
                .replace("{gmail}",mGmail).replace("{lname}",lname);
        Cursor c = db.rawQuery(sql,null);
        try {
            while (c.moveToNext()) {
                lbx = moveTable2Laborx(c);
            }
            return lbx;
        } finally {
            c.close();
        }
    }
    private Companyx getCompany(String cname) {
        Companyx cmx = new Companyx();
        String sql = "select * from TableCompany where gmail='{gmail}' and cname='{cname}';";
        sql= sql.replace("{gmail}", mGmail).replace("{cname}",cname);
        Cursor c = db.rawQuery(sql,null);
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
    private Laborx moveTable2Laborx(Cursor cu) {
        Laborx lb = new Laborx();
        lb.lid= (cu.getLong(cu.getColumnIndex("lid")));
        Log.i("viewLid", String.valueOf(cu.getLong(cu.getColumnIndex("lid"))) + cu.getString(cu.getColumnIndex("lname")).toString());
        lb.gmail=cu.getString(cu.getColumnIndex("gmail")).toString();
        lb.lname=cu.getString(cu.getColumnIndex("lname")).toString();
        lb.lmobile=cu.getString(cu.getColumnIndex("lmobile")).toString();
        lb.cname=cu.getString(cu.getColumnIndex("cname")).toString();
        lb.lfamily=cu.getLong(cu.getColumnIndex("lfamily"));
        lb.lchild=cu.getLong(cu.getColumnIndex("lchild"));
        //fill from transaction
        lb.lworkday=cu.getLong(cu.getColumnIndex("lworkday"));
        lb.lbasepay=cu.getLong(cu.getColumnIndex("lbasepay"));
        lb.lpay=cu.getLong(cu.getColumnIndex("lpay"));
        //fill from transaction -end
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
                        mTax = incometaxes;
                    }
                    //Log.i("TAG1", incometaxes.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute("https://spreadsheets.google.com/tq?key=1xTSz4NpgXllc9rDS2UX2C2Kh-h9OCAqPrDzJd7x7PEo");
        Log.i("TAG", "income tax table end down...");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_company, menu);
        //menu.findItem(R.id.action_sendauto).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent iaud = new Intent(PayrollActivity.this, TransactionAUDActivity.class);
                iaud.putExtra("ser_lbx",mLbx);
                Transactionx trx = new Transactionx();
                if ("add".equalsIgnoreCase("add")) {
                    iaud.putExtra("aud","add");
                }
                startActivity(iaud);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db!=null) db.close();
    }
    class MyAdapter extends ArrayAdapter<Payrollx> {
        Context context;
        int resourceid;
        ArrayList<Payrollx> items;
        Payrollx px;
        class ViewHolder {
            //TextView txt_rhead;
            TextView txt_r10;    //work ym
            TextView txt_r12;    //work std
            TextView txt_r13;    //work jc
            TextView txt_r14;       //work yg
            TextView txt_r15;            //work total
            TextView txt_r20;            //pay base
            TextView txt_r22;            //pay bonus
            TextView txt_r23;    //paytotal
            TextView txt_r24;       //pay deduction
            TextView txt_r25;            //pay net
        }
        public MyAdapter(Context context, int resource, ArrayList<Payrollx> objects) {
            super(context, resource, objects);
            this.context=context;
            this.resourceid=resource;
            this.items=objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            px= items.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = li.inflate(resourceid,null);
                holder = new ViewHolder();
                holder.txt_r10=(TextView)convertView.findViewById(R.id.txt_r10);
                holder.txt_r12 = (TextView)convertView.findViewById(R.id.txt_r12);
                holder.txt_r13=(TextView)convertView.findViewById(R.id.txt_r13);
                holder.txt_r14=(TextView)convertView.findViewById(R.id.txt_r14);
                holder.txt_r15 = (TextView) convertView.findViewById(R.id.txt_r15);
                holder.txt_r20 = (TextView) convertView.findViewById(R.id.txt_r20);
                holder.txt_r22=(TextView)convertView.findViewById(R.id.txt_r22);
                holder.txt_r23=(TextView)convertView.findViewById(R.id.txt_r23);
                holder.txt_r24 = (TextView) convertView.findViewById(R.id.txt_r24);
                holder.txt_r25 = (TextView) convertView.findViewById(R.id.txt_r25);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.txt_r10.setText(px.pym);
            holder.txt_r12.setText(px.d_stds+"");
            holder.txt_r13.setText(px.d_jcs+"");
            holder.txt_r14.setText(px.d_ygs+"");
            holder.txt_r15.setText(px.d_total+"");
            holder.txt_r20.setText(px.pay_base+"");
            holder.txt_r22.setText(px.pay_bonus+"");
            holder.txt_r23.setText(px.pay_total+"");
            long duction = px.t_income + px.t_income10 + px.t_pension + px.t_health + px.t_health10 + px.t_employ + px.t_hurt + px.t_prepay;
            holder.txt_r24.setText(duction+"");
            holder.txt_r25.setText(px.pay_real+"");
            return convertView;
        }
    }
}
