package com.nwsoft.maninwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nwsoft.maninwork.apis.LaborAsyncTask;
import com.nwsoft.maninwork.backend.laborApi.model.Labor;
import com.nwsoft.maninwork.beans.Laborx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LaborActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mTxtLaborName;
    private TextView txt_cname;
    private EditText mTxtMobile;
    private EditText txt_email;
    private EditText mTxtLFamily;
    private EditText mTxtLChild;
    private EditText mTxtWorkDay;
    private EditText mTxtPay;
    private TextView mTxtRegDate;
    private Button btn_clear;
    private Button btn_cancel;
    private Button btn_delete;
    private Button btn_upsert;

    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private String mOptionAudi;
    private String mGmail;
    private SharedPreferences mSettings;
    TextView txt_folder;
    LinearLayout lo_laborinfo;
    public boolean folding = true;
    private ArrayList<Laborx> mList;
    private ListView listView;
    LinearLayout lo_header;
    LinearLayout lo_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        setContentView(R.layout.activity_labor);
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");

        Intent fromIntent = getIntent();
        mOptionAudi = fromIntent.getStringExtra("ex_audi");
        String cname= fromIntent.getStringExtra("ex_cname");
        Laborx rlb = (Laborx) fromIntent.getSerializableExtra("ex_ser_labor");
        initUI(cname);
        initButtons();
        txt_folder.performClick();
    }
    private void listLabor(String cname) {
        new LaborAsyncTask("list").execute(new Pair<Context, Labor>(LaborActivity.this, new Labor()));
        String sql1 = ("select * from TableLabor where gmail='{gmail}' and cname like '%{cname}%' order by lname"
                .replace("{gmail}",mGmail)
                .replace("{cname}",cname));
        Log.i("SQL", sql1);
        Cursor cu = mDb.rawQuery(sql1,null);
        mList = new ArrayList<>();
        try {
            while (cu.moveToNext()) {
                Laborx lb = moveTable2_Laborx(cu);
                mList.add(lb);
            }
            Log.i("GetDBrow",String.valueOf(cu.getCount()) + " listed.");
        } finally {
            cu.close();
        }

        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
        //android.R.layout.simple_list_item_1,mList);
        MyAdapter myAdapter = new MyAdapter(LaborActivity.this, R.layout.row_main, mList);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        //
    }
    private Laborx moveTable2_Laborx(Cursor cu) {
        Laborx lbx = new Laborx();
        lbx.lid= (cu.getLong(cu.getColumnIndex("lid")));
        Log.i("viewLid", String.valueOf(cu.getLong(cu.getColumnIndex("lid"))) + cu.getString(cu.getColumnIndex("lname")).toString());
        lbx.gmail=cu.getString(cu.getColumnIndex("gmail")).toString();
        lbx.lname=cu.getString(cu.getColumnIndex("lname")).toString();
        lbx.lmobile=cu.getString(cu.getColumnIndex("lmobile")).toString();
        lbx.cname=cu.getString(cu.getColumnIndex("cname")).toString();
        lbx.lfamily=cu.getLong(cu.getColumnIndex("lfamily"));
        lbx.lchild=cu.getLong(cu.getColumnIndex("lchild"));
        lbx.lemail=cu.getString(cu.getColumnIndex("lemail"));
        lbx.lworkday=cu.getLong(cu.getColumnIndex("lworkday"));
        lbx.lworkday=getworkday(lbx.lname);
        lbx.lbasepay=cu.getLong(cu.getColumnIndex("lbasepay"));
        lbx.lpay=cu.getLong(cu.getColumnIndex("lpay"));
        //fill from transaction -end
        lbx.lregdate=cu.getString(cu.getColumnIndex("lregdate"));
        return  lbx;
    }
    private long getworkday(String lname) {
        long tworkday=0;
        String qry = String.format("select sum(workstandard/8) as wday from TableTransaction where gmail='%s' and lname='%s' group by gmail, lname",
                mGmail, lname );
        Cursor ct = mDb.rawQuery(qry, null);
        try {
            while (ct.moveToNext()) {
                tworkday=ct.getLong(ct.getColumnIndex("wday"));
            }
        } finally {
            ct.close();
        }
        return  tworkday;
    }
    @Override
    protected void onResume() {
        super.onResume();
        String cname= getIntent().getStringExtra("ex_cname");
        Laborx rlb = (Laborx) getIntent().getSerializableExtra("ex_ser_labor");
        if (mOptionAudi.equalsIgnoreCase("list")) {
            listLabor(cname);
        } else if (mOptionAudi.equalsIgnoreCase("insert")) {
            Laborx lb = new Laborx();
            moveLaborx_2UI(lb);
        } else {
            //update
            moveLaborx_2UI(rlb);
        }
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
                folding=false;
                txt_folder_onClick(null);
                btn_upsert.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.INVISIBLE);
                btn_delete.setVisibility(View.INVISIBLE);
                btn_clear.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void googleAds() {
        AdView adView;
        AdRequest adRequest;
        adView=(AdView)findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_folder:
                txt_folder_onClick(v);
                break;
            case R.id.txt_cname:
                txt_cname_onClick(v);
                break;
            default:
                break;
        }
    }
    public void txt_cname_onClick(View v) {
        Snackbar.make(v, "Select Company", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(LaborActivity.this);
        adBuilder.setTitle("Select a Company");
        ArrayList<String> al = getCompanys(null);
        String[] cNames = new String[al.size()];
        cNames = al.toArray(cNames);
        final String[] finalCNames = cNames;
        adBuilder.setItems(cNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String[] items = getResources().getStringArray(R.array.array_cowgender);
                String[] items = finalCNames.clone();
                String item=items[which];
                Log.i("-->DItem", item);
                txt_cname.setText(item);
                dialog.dismiss();
            }
        });
        adBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = adBuilder.create();
        dialog.show();
    }
    private void txt_folder_onClick(View v) {
        lo_laborinfo.setVisibility(folding==true?View.GONE:View.VISIBLE);
        txt_folder.setBackgroundResource(folding==true?
                android.R.drawable.arrow_down_float:android.R.drawable.arrow_up_float);
        if (folding) {
            lo_header.setVisibility(View.GONE);
            lo_button.setVisibility(View.GONE);
        } else {
            lo_header.setVisibility(View.VISIBLE);
            lo_button.setVisibility(View.VISIBLE);
        }
        folding= !folding;

    }
    private void initUI(String cname) {
        mTxtLaborName = (EditText)findViewById(R.id.txt_lname);
        txt_cname= (TextView) findViewById(R.id.txt_cname);
        txt_cname.setOnClickListener(this);
        txt_cname.setText(cname);
        TextView txt_hcnmae=(TextView)findViewById(R.id.txt_hcname);
        txt_hcnmae.setText(cname);
        mTxtMobile= (EditText)findViewById(R.id.txt_lmobile);
        txt_email = (EditText)findViewById(R.id.txt_email);
        mTxtLFamily= (EditText)findViewById(R.id.edt_lfamily);
        mTxtLChild= (EditText)findViewById(R.id.edt_lchild);
        mTxtWorkDay= (EditText)findViewById(R.id.txt_lworkday);
        mTxtPay= (EditText)findViewById(R.id.txt_lpay);
        mTxtRegDate= (TextView)findViewById(R.id.txt_lregdate);
        listView=(ListView)findViewById(R.id.list_labor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Laborx lbx = (Laborx)parent.getAdapter().getItem(position);
                Intent iPayroll = new Intent(LaborActivity.this, PayrollActivity.class);
                iPayroll.putExtra("ex_laborx", lbx);
                startActivity(iPayroll);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Laborx lbx = (Laborx)parent.getAdapter().getItem(position);
                moveLaborx_2UI(lbx);
                folding=false;
                txt_folder_onClick(view);
                btn_clear.setVisibility(View.VISIBLE);
                btn_upsert.setVisibility(View.VISIBLE);
                btn_delete.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.INVISIBLE);
                onResume();
                return true;
            }
        });
        lo_header=(LinearLayout)findViewById(R.id.lo_header);
        lo_button=(LinearLayout)findViewById(R.id.lo_button);
        txt_folder= (TextView)findViewById(R.id.txt_folder);
        txt_folder.setOnClickListener(this);
        lo_laborinfo = (LinearLayout)findViewById(R.id.lo_laborinfo);
    }
    private Laborx moveUI2_Laborx() {
        Laborx lbx = new Laborx();
        if (mTxtLaborName.getTag()==null) {
            lbx.lid=0;
        } else {
            lbx.lid=((Long)mTxtLaborName.getTag());
        }
        lbx.gmail=mGmail;
        lbx.lname= mTxtLaborName.getText().toString();
        lbx.cname= txt_cname.getText().toString();
        lbx.lmobile=mTxtMobile.getText().toString();
        lbx.lfamily=Long.parseLong(mTxtLFamily.getText().toString());
        lbx.lchild=Long.parseLong(mTxtLChild.getText().toString());
        lbx.lemail   = txt_email.getText().toString();
        lbx.lbasepay = 0;
        lbx.lworkday= Long.parseLong( mTxtWorkDay.getText().toString().equals("")?"0":mTxtWorkDay.getText().toString());
        lbx.lpay=Long.parseLong( mTxtPay.getText().toString().equals("")?"0":mTxtPay.getText().toString());
        Calendar c = Calendar.getInstance();
        int[] ymd ={0,0,0};
        ymd[0]=c.get(Calendar.YEAR);
        ymd[1]=c.get(Calendar.MONTH) + 1;
        ymd[2]=c.get(Calendar.DAY_OF_MONTH);
        String symd= String.format("%04d-%02d-%02d",ymd[0],ymd[1],ymd[2]);
        lbx.lregdate=mTxtRegDate.getText().toString()==""?symd:mTxtRegDate.getText().toString();   //.replaceAll("-","");
        return lbx;
    }
    private void clearUi() {
        mTxtLaborName.setText("");
        txt_cname.setText("");
        mTxtMobile.setText("");
        mTxtLFamily.setText("");
        mTxtLChild.setText("");
        txt_email.setText("");
        mTxtWorkDay.setText("") ;
        mTxtPay.setText("");
        Calendar c = Calendar.getInstance();
        int[] ymd ={0,0,0};
        ymd[0]=c.get(Calendar.YEAR);
        ymd[1]=c.get(Calendar.MONTH) + 1;
        ymd[2]=c.get(Calendar.DAY_OF_MONTH);
        String symd= String.format("%04d-%02d-%02d",ymd[0],ymd[1],ymd[2]);
        mTxtRegDate.setText(symd);
    }
    private void moveLaborx_2UI(Laborx lb) {
        if (lb ==null) return;
        mTxtLaborName.setText(lb.lname==null?"":lb.lname);
        mTxtLaborName.setTag(lb.lid);
        txt_cname.setText(lb.cname);
        mTxtMobile.setText(lb.lmobile==null?"":lb.lmobile);
        mTxtLFamily.setText(lb.lfamily+"");
        mTxtLChild.setText(lb.lchild+"");
        txt_email.setText(String.valueOf(lb.lemail));
        mTxtWorkDay.setText(String.valueOf(lb.lworkday)) ;
        mTxtPay.setText(String.valueOf(lb.lpay));
        Calendar c = Calendar.getInstance();
        int[] ymd ={0,0,0};
        ymd[0]=c.get(Calendar.YEAR);
        ymd[1]=c.get(Calendar.MONTH) + 1;
        ymd[2]=c.get(Calendar.DAY_OF_MONTH);
        String symd= String.format("%04d-%02d-%02d",ymd[0],ymd[1],ymd[2]);
        mTxtRegDate.setText(lb.lregdate==null?symd:lb.lregdate);
    }

    private ArrayList<String> getCompanys(String custid) {
        ArrayList<String> companies = new ArrayList<>();
        String sql="";
        if (custid==null) {
            sql="select cname from TableCompany order by cname;";
            custid="0";
        } else {
            sql="select cname from TableCompany where cid={cid} order by cname;".replace("{cid}",custid);
        }
        mCursor= mDb.rawQuery(sql,null);
        try {
            while (mCursor.moveToNext()) {
                companies.add( mCursor.getString(mCursor.getColumnIndex("cname")));
            }
            return companies;
        } finally {
            mCursor.close();
        }
    }
    private void initButtons() {
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_upsert  = (Button) findViewById(R.id.btn_upsert);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUi();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Laborx lbx = moveUI2_Laborx();
                Labor plb = lbx.fill2Labor();
                int cnt = getTransCnt(lbx.lname);
                if (cnt > 0) {
                    String msg = String.format("Cannot delete.! There are %d work time transactions on labor.",cnt);
                    Snackbar.make(v,msg,Snackbar.LENGTH_LONG)
                            .setAction("Cannot delete",null)
                            .show();
                } else {
                    new LaborAsyncTask("delete").execute(new Pair<Context, Labor>(getApplicationContext(), plb));
                    dbAud("delete", lbx);
                    Log.i("deleteLabor", "a row deleted from TableLabor");
                }
            }
        });
        btn_upsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Insert/Update to TableLabor
                Laborx lbx = moveUI2_Laborx();
                Labor plb = lbx.fill2Labor();
                if (lbx.lid == 0 ) {
                    plb.setLid(null);   //type long =0 x:null, Long = null
                    new LaborAsyncTask("insert").execute(new Pair<Context, Labor>(getApplicationContext(), plb));
                    dbAud("add",lbx);
                } else {
                    new LaborAsyncTask("update").execute(new Pair<Context, Labor>(getApplicationContext(), plb));
                    dbAud("update",lbx);
                }
                Toast.makeText(LaborActivity.this,"Upserted labor!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int getTransCnt(String lname) {
        int cnt =0;
        String sql = String.format("select count(*) cnt from TableTransaction where lname='%s'"
                ,lname);
        Cursor c = mDb.rawQuery(sql,null);
        try {
            while(c.moveToNext()) {
                cnt=c.getInt(c.getColumnIndex("cnt"));
            }
        } finally {
            c.close();
        }
        return cnt;
    }
    private void dbAud(String aud, Laborx obj) {
        if (aud.equalsIgnoreCase("add")) {
            //Should not run insert due to key sync mismatch with cloud
            /****
            String sql = "Insert into TableLabor values (" +
                    "null, 'gmail','lname', 'cname', 'lmobile', lfamily, lchild, lbasepay, lworkday, lpay, 'lregdate')";
            sql=sql.replace("gmail",mGmail);
            sql=sql.replace("lname", obj.lname);
            sql=sql.replace("cname", obj.cname);
            sql=sql.replace("lmobile", obj.lmobile);
            sql=sql.replace("lfamily", String.valueOf(obj.lfamily)) ;
            sql=sql.replace("lchild", String.valueOf(obj.lchild));
            sql=sql.replace("lbasepay", String.valueOf(obj.lbasepay));
            sql=sql.replace("lworkday", String.valueOf( obj.lworkday));
            sql=sql.replace("lpay",String.valueOf( obj.lpay));
            sql=sql.replace("lregdate", obj.lregdate);
            mDb.execSQL(insertSql);
             ****/
        } else if (aud.equalsIgnoreCase("update")) {
            String uSql = "UPDATE TableLabor set lid={lid}, gmail='{gmail}', lname='{lname}'," +
                    "cname='{cname}', lmobile='{lmobile}', " +
                    "lfamily={lfamily},lchild={lchild},lemail='{lemail}',lbasepay= {lbasepay}," +
                    "lworkday={lworkday},lpay={lpay}, lregdate='{lregdate}' " +
                    "where lid={lid}";
            uSql=uSql.replace("{lid}",String.valueOf(obj.lid));
            uSql=uSql.replace("{gmail}",obj.gmail==null?"":obj.gmail);
            uSql=uSql.replace("{lname}",obj.lname==null?"":obj.lname);
            uSql=uSql.replace("{cname}",obj.cname==null?"":obj.cname);
            uSql=uSql.replace("{lmobile}",obj.lmobile==null?"":obj.lmobile);
            uSql=uSql.replace("{lfamily}",String.valueOf(obj.lfamily));
            uSql=uSql.replace("{lchild}",String.valueOf(obj.lchild));
            uSql=uSql.replace("{lemail}",obj.lemail==null?"":obj.lemail);
            uSql=uSql.replace("{lbasepay}",String.valueOf(obj.lbasepay));
            uSql=uSql.replace("{lworkday}",String.valueOf(obj.lworkday));
            uSql=uSql.replace("{lpay}",String.valueOf(obj.lpay));
            uSql=uSql.replace("{lregdate}",obj.lregdate==null?"":obj.lregdate);
            mDb.execSQL(uSql);
        } else if (aud.equalsIgnoreCase("delete")) {
            String deleteSql = "DELETE from TableCompany where cid=" +String.valueOf(obj.lid);
            mDb.execSQL(deleteSql);
        }
    }
    private String getLocale() {
        String syslocale ="ko";
        Locale locale = getResources().getConfiguration().locale;
        syslocale = locale.getLanguage();  //locale.getCountry() + "/"+ locale.getLanguage();    //KR, ko
        Log.i("LOCALE", syslocale);
        return syslocale;
    }
    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) {
            mDb.close();
        }
        if (mCursor != null) {
            mCursor.close();
        }

    }
    class MyAdapter extends ArrayAdapter<Laborx> {
        private Context context;
        private Cursor mCursor;
        private int layoutid;
        private ArrayList<Laborx> items;
        Laborx lbx;
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
            lbx = items.get(position);
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
            holder.t1.setText(lbx.lname);                 holder.t1.setTag(lbx.lid );
            holder.t2.setText(lbx.cname);
            holder.t23_mobile.setText(lbx.lmobile);
            holder.t3.setText(lbx.lfamily+"");
            holder.t4.setText(lbx.lchild+"");
            holder.t5.setText(String.valueOf( lbx.lpay));
            holder.t56_lemail.setText(lbx.lemail);
            holder.t6.setText(lbx.lregdate);
            return convertView;
        }
    }
}
