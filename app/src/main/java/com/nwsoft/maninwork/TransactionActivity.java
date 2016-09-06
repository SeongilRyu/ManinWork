package com.nwsoft.maninwork;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nwsoft.maninwork.backend.transactionApi.model.Transaction;
import com.nwsoft.maninwork.beans.Laborx;
import com.nwsoft.maninwork.beans.Transactionx;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener {
    SQLiteDatabase mDb;
    Cursor mCursor;
    Laborx mLaborx;
    Transactionx mTransactionx;
    TextView mTxtTid;
    //Upper display labor information ---start
    private EditText mTxtLaborName;
    private EditText mEdtCName;
    private EditText mTxtMobile;
    private EditText mTxtBasePay;
    private EditText mTxtLFamily;
    private EditText mTxtLChild;
    private EditText mTxtWorkDay;
    private EditText mTxtPay;
    private TextView mTxtRegDate;
    //Upper display labor information ---end
    TextView txt_folder;    public boolean folding = true; LinearLayout lo_laborinfo;
    TextView txt_go_transaud;
    ImageButton ibtn_left;
    Spinner spinner;
    ImageButton ibtn_right;
    private String mGmail;
    private SharedPreferences mSettings;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_transaction);
        setContentView(R.layout.activity_transaction);
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");

        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        mLaborx = (Laborx)getIntent().getSerializableExtra("ex_ser_labor");
        String pym = getIntent().getStringExtra("ex_ym");
        initUI();   initButton();
        if (pym!= null) {
            spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(pym));
        }
        moveData2UI(mLaborx);

        listView = (ListView)findViewById(R.id.listView3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction tr = (Transaction)parent.getAdapter().getItem(position);
                txt_go_transaud_onClick(view, "update", tr);
            }
        });

    }

    @NonNull
    private ArrayList<Transaction> listTransaction(String pym) {
        if (pym==null) {
            pym=init_Date("yyyymm");
        }
        String ym = spinner.getSelectedItem()==null?pym:spinner.getSelectedItem().toString();
        String sql ="select * from TableTransaction " +
                "where gmail='{gmail}' and lname='{lname}' and tym='{tym}'" +
                "order by tym, tdate";
        sql= sql.replace("{gmail}", mGmail);
        sql = sql.replace("{lname}", mLaborx.lname);
        sql= sql.replace("{tym}",ym);
        mCursor = mDb.rawQuery(sql,null);
        ArrayList<Transaction> transList = new ArrayList<>();
        try {
            while (mCursor.moveToNext()) {
                Transaction trans = new Transaction();
                trans.setGmail(mCursor.getString(mCursor.getColumnIndex("gmail")));
                trans.setTid( mCursor.getLong(mCursor.getColumnIndex("tid")));
                trans.setCname(mCursor.getString(mCursor.getColumnIndex("cname")));
                trans.setLname(mCursor.getString(mCursor.getColumnIndex("lname")));
                trans.setTym(mCursor.getString(mCursor.getColumnIndex("tym")));
                trans.setTdate(mCursor.getString(mCursor.getColumnIndex("tdate")));
                trans.setWeekday(mCursor.getString(mCursor.getColumnIndex("weekday")));
                trans.setStarttime(mCursor.getString(mCursor.getColumnIndex("starttime")));
                trans.setEndtime(mCursor.getString(mCursor.getColumnIndex("endtime")));
                trans.setWorkstandard(mCursor.getLong(mCursor.getColumnIndex("workstandard")));
                trans.setWorkJc(mCursor.getLong(mCursor.getColumnIndex("work_jc")));
                trans.setWorkYg(mCursor.getLong(mCursor.getColumnIndex("work_yg")));
                trans.setWorkovertime(mCursor.getLong(mCursor.getColumnIndex("workovertime")));
                trans.setWorkspecial(mCursor.getLong(mCursor.getColumnIndex("workspecial")));
                trans.setWorkspecialover(mCursor.getLong(mCursor.getColumnIndex("workspecialover")));
                trans.setWorknight(mCursor.getLong(mCursor.getColumnIndex("worknight")));
                trans.setWorklateearly(mCursor.getLong(mCursor.getColumnIndex("worklateearly")));
                transList.add(trans);
            }
        } finally {
            mCursor.close();
        }
        return transList;
    }

    private void spinnerUpdate() {
        String sql = "select distinct tym from TableTransaction " +
                "where gmail='{gmail}' and lname like '%{lname}%' " +
                "order by tym desc;";
        sql = sql.replace("{gmail}", mGmail)
                .replace("{lname}", mLaborx==null?"":mLaborx.lname);
        Log.i("spinnerupdate", sql);
        mCursor=mDb.rawQuery(sql, null);
        ArrayList<String> spitems = new ArrayList<>();
        try {
            while (mCursor.moveToNext()) {
                spitems.add(mCursor.getString(mCursor.getColumnIndex("tym")).toString());
            }
        } finally {
            mCursor.close();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spitems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String pym = getIntent().getStringExtra("ex_ym");
        ArrayList<Transaction> transList = listTransaction(pym);
        MyAdapter myAdapter = new MyAdapter(TransactionActivity.this, R.layout.row_transaction, transList);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        //fillTransactionViews();
    }

    private void initUI() {
        mTxtLaborName = (EditText)findViewById(R.id.txt_lname);
        mTxtLaborName.setEnabled(false);
        mEdtCName= (EditText)findViewById(R.id.txt_cname);  mEdtCName.setEnabled(false );
        mTxtMobile= (EditText)findViewById(R.id.txt_lmobile);
        mTxtBasePay= (EditText)findViewById(R.id.txt_basepay);
        mTxtLFamily= (EditText)findViewById(R.id.edt_lfamily);
        mTxtLChild= (EditText)findViewById(R.id.edt_lchild);
        mTxtWorkDay= (EditText)findViewById(R.id.txt_lworkday);
        mTxtPay= (EditText)findViewById(R.id.txt_lpay);
        mTxtRegDate= (TextView)findViewById(R.id.txt_lregdate);
        txt_folder= (TextView)findViewById(R.id.txt_folder);    txt_folder.setOnClickListener(this);
        lo_laborinfo = (LinearLayout)findViewById(R.id.lo_laborinfo);   lo_laborinfo.setVisibility(View.GONE);
        txt_go_transaud = (TextView)findViewById(R.id.txt_go_transaud); txt_go_transaud.setOnClickListener(this);
    }
    private void initButton() {
        spinner = (Spinner)findViewById(R.id.spinner2); spinnerUpdate();
        ibtn_left= (ImageButton)findViewById(R.id.ibtn_left); ibtn_left.setOnClickListener(this);
        ibtn_right=(ImageButton)findViewById(R.id.ibtn_right);  ibtn_right.setOnClickListener(this);

    }
    private void fillTransactionViews() {
        //Fill Buy Transaction
        mTxtTid = (TextView)findViewById(R.id.txt_tid);
        if (mTransactionx != null) {
            mTxtTid.setText(String.valueOf(mTransactionx.tid));
            //
        } else {
        }
    }

    private Laborx moveUI2Data() {
        Laborx lb = new Laborx();
        lb.lid=Long.parseLong( mTxtLaborName.getTag().toString());
        lb.gmail=mGmail;
        lb.lname= mTxtLaborName.getText().toString();
        lb.cname= mEdtCName.getText().toString();
        lb.lmobile=mTxtMobile.getText().toString();
        lb.lbasepay = Long.parseLong( mTxtBasePay.getText().toString());
        lb.lworkday= Long.parseLong( mTxtWorkDay.getText().toString());
        lb.lpay=Long.parseLong( mTxtPay.getText().toString());
        lb.lregdate=mTxtRegDate.getText().toString();   //.replaceAll("-","");
        return lb;
    }
    private void moveData2UI(Laborx lb) {
        if (lb ==null) return;
        mTxtLaborName.setText(lb.lname==null?"":lb.lname);
        mTxtLaborName.setTag(lb.lid);
        mEdtCName.setText(lb.cname);
        //mEdtCName.setTag(lb.custid);
        mTxtMobile.setText(lb.lmobile==null?"":lb.lmobile);
        mTxtBasePay.setText(String.valueOf(lb.lbasepay));
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_folder:
                txt_folder_onClick(v);
                break;
            case R.id.txt_go_transaud:
                Log.i("trans", "Go WorkTime Transaction");
                txt_go_transaud_onClick(v,"add",null);
                break;
            case R.id.ibtn_left:
            case R.id.ibtn_right:
                btnLeftRight_onClick(v);
                onResume();
                break;
            default:
                break;
        }
    }
    private void btnLeftRight_onClick(View v) {
        //String tym = txt_tym.getText().toString();
        int pos = spinner.getSelectedItemPosition();
        int maxpos = spinner.getCount() -1;
        //String tym = spinner.getAdapter().getItem(pos).toString();
        //int yy = Integer.parseInt(tym.substring(0,4));
        //int mm = Integer.parseInt(tym.substring(4));
//        Calendar cal = Calendar.getInstance();
//        cal.set(yy,mm-1,01);
//        cal.add(Calendar.MONTH,v.getId()==R.id.ibtn_left?-1:1);
//        Log.i("CAL", cal.getTime().toString());
        //String sym = String.format("%04d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1 );
        //txt_tym.setText(sym);
        int npos = pos + (v.getId()==R.id.ibtn_left?-1:1);
        if (npos < 0) {
            npos=0;
            Snackbar.make(v, "End of low Year/month", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        if (npos > maxpos)  {
            npos=maxpos;
            Snackbar.make(v, "End of upper Year/month", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        spinner.setSelection(npos);
    }
    public void txt_go_transaud_onClick(View v,String aud, Transaction tr) {
        Intent iaud = new Intent(TransactionActivity.this, TransactionAUDActivity.class);
        iaud.putExtra("ser_lbx",mLaborx);
        Transactionx trx = new Transactionx();
        if (aud.equalsIgnoreCase("add")) {
            iaud.putExtra("aud","add");
        } else if (aud.equalsIgnoreCase("update")) {
            iaud.putExtra("aud","update");
            trx.fillme(tr);
            iaud.putExtra("ser_trx",trx);
        } else if (aud.equalsIgnoreCase("delete")) {
            iaud.putExtra("aud","delete");
            trx.fillme(tr);
            iaud.putExtra("ser_trx",trx);
        }
        startActivity(iaud);
    }
    private void txt_folder_onClick(View v) {
        if ("hidefunc"=="yes") {
            int goneshow = folding == true ? View.GONE : View.VISIBLE;
            lo_laborinfo.setVisibility(goneshow);
            int updown = folding == true ? android.R.drawable.arrow_down_float : android.R.drawable.arrow_up_float;
            txt_folder.setBackgroundResource(updown);
            folding = !folding;
        }

    }
    public void txtShares_onClick(View v) {
        AlertDialog mDialog;
        Snackbar.make(v, "Share list", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(TransactionActivity.this);
        adBuilder.setTitle("Select number of shares to buy");
        //adBuilder.setView(R.layout.content_dialog);
        final String[] shares = {"10","20","30","100","200"};
        adBuilder.setItems(shares, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("which", shares[which]);
            }
        });
        adBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog = adBuilder.create();
        mDialog.show();
    }
    private String init_Date(String rtn_fmt) {
        //TextView dateView = (TextView) v;
        int[] iymd = {0,0,0};
        Calendar c = Calendar.getInstance();
        iymd[0] = c.get(Calendar.YEAR);
        iymd[1] = c.get(Calendar.MONTH);
        iymd[2] = c.get(Calendar.DAY_OF_MONTH);
        String initDate = "";
        if (rtn_fmt.equalsIgnoreCase("yyyy-mm-dd")) {
            initDate=String.format("%04d-%02d-%02d", iymd[0],iymd[1]+1, iymd[2]);
        } else if (rtn_fmt.equalsIgnoreCase("yyyym")) {
            initDate=String.format("%04d%02d", iymd[0],iymd[1]+1);
        }
        return  initDate;
    }
    public void txtDate_onClick(View v) {
        TextView txtDate = (TextView)v;
        int[] iymd = {0,0,0};
        String[] symd= new String[3];
        if (txtDate.getText() ==null ||
                txtDate.getText().toString().isEmpty()) {
            Calendar c = Calendar.getInstance();
            iymd[0] = c.get(Calendar.YEAR);
            iymd[1] = c.get(Calendar.MONTH);
            iymd[2] = c.get(Calendar.DAY_OF_MONTH);
        } else {
            symd = txtDate.getText().toString().split("-");
            iymd[0] = Integer.parseInt(symd[0]);
            iymd[1] = Integer.parseInt(symd[1])-1;
            iymd[2] = Integer.parseInt(symd[2]);
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String msg = String.format("%04d-%02d-%02d", year,monthOfYear+1, dayOfMonth);
            Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    };
    private DatePickerDialog.OnDateSetListener dateSetListenerSell
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String msg = String.format("%04d-%02d-%02d", year,monthOfYear+1, dayOfMonth);
            Toast.makeText(TransactionActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    };

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
    class MyAdapter extends ArrayAdapter<Transaction> {
        private Context context;
        private Cursor mCursor;
        private int layoutid;
        private ArrayList<Transaction> items;
        Transaction rs;
        class ViewHolder {
            TextView trid;
            TextView tr01;  TextView tr02;  TextView tr03;  TextView tretc;
            TextView tr11;  TextView tr12;  TextView tr13;  TextView tr14;
            TextView tr21;  TextView tr22;  TextView tr23;  TextView tr24;

        }
        public MyAdapter(Context context, int textViewResourceId, ArrayList<Transaction> items) {
            super(context, textViewResourceId, items);
            this.context=context;
            this.layoutid=textViewResourceId;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            rs = items.get(position);
            ViewHolder holder=null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(layoutid, null);
                holder = new ViewHolder();
                holder.trid = (TextView)convertView.findViewById(R.id.txt_tid);
                holder.tr01=(TextView)convertView.findViewById(R.id.txt_tr01);
                holder.tr02 = (TextView)convertView.findViewById(R.id.txt_tr02);
                holder.tr03=(TextView)convertView.findViewById(R.id.txt_tr03);
                holder.tretc=(TextView)convertView.findViewById(R.id.txt_etc);
                holder.tr11 = (TextView) convertView.findViewById(R.id.txt_tr11);
                holder.tr12 = (TextView) convertView.findViewById(R.id.txt_tr12);
                holder.tr13=(TextView)convertView.findViewById(R.id.txt_tr13);
                holder.tr14 = (TextView) convertView.findViewById(R.id.txt_tr14);
                holder.tr21 = (TextView) convertView.findViewById(R.id.txt_tr21);
                holder.tr22 = (TextView) convertView.findViewById(R.id.txt_tr22);
                holder.tr23=(TextView)convertView.findViewById(R.id.txt_tr23);
                holder.tr24 = (TextView) convertView.findViewById(R.id.txt_tr24);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            //if (q != null) {
            holder.trid.setTag(String.valueOf( rs.getTid()));
            holder.trid.setText(rs.getTdate());
            String wday = rs.getWeekday();
            holder.tr01.setText(wday);
            if (wday.equalsIgnoreCase("SAT") || wday.equalsIgnoreCase("SUN")) {
                holder.tr01.setTextColor(Color.RED);
            } else {
                holder.tr01.setTextColor(holder.trid.getTextColors());
            }
            holder.tr02.setText(rs.getStarttime());
            holder.tr03.setText(rs.getEndtime());
            holder.tretc.setText("");
            holder.tr11.setText(String.valueOf(rs.getWorkstandard()));
            holder.tr12.setText(String.valueOf(rs.getWorkJc()));
            holder.tr13.setText(String.valueOf(rs.getWorkYg()));
            holder.tr14.setText(String.valueOf(rs.getWorkovertime()/100.0f));
            holder.tr21.setText(String.valueOf(rs.getWorkspecial()));
            holder.tr22.setText(String.valueOf(rs.getWorkspecialover()/100.0f));
            holder.tr23.setText(String.valueOf(rs.getWorknight()));
            holder.tr24.setText(String.valueOf(rs.getWorklateearly()));
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
    }
}
