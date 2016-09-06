package com.nwsoft.maninwork;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nwsoft.maninwork.apis.CompanyAsyncTask;
import com.nwsoft.maninwork.apis.LaborAsyncTask;
import com.nwsoft.maninwork.apis.TransactionAsyncTask;
import com.nwsoft.maninwork.backend.companyApi.model.Company;
import com.nwsoft.maninwork.backend.laborApi.model.Labor;
import com.nwsoft.maninwork.backend.transactionApi.model.Transaction;
import com.nwsoft.maninwork.beans.Laborx;
import com.nwsoft.maninwork.beans.MyAdapter;
import com.nwsoft.maninwork.mylibs.AsyncResult;
import com.nwsoft.maninwork.mylibs.GetGoogleSheetTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int SignInActivity_REQUEST=5001;
    private Spinner mSpinner;
    private ListView listView;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private ArrayList<Laborx> mList;
    private TextView mTxtCompany;
    private AlertDialog mDialog;
    private boolean isOnCreate;
    private SharedPreferences mSettings;
    private ActionMode mActionMode;
    private int mSelectedItem;
    private AutoCompleteTextView mActv;
    private Button mBtnSearch;
    private boolean mSignResultOK;
    private String mGmail;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        initDatabase();
        isOnCreate=true;
        setContentView(R.layout.activity_main);
        Intent is = new Intent(this, SignInActivity.class);
        startActivityForResult(is, SignInActivity_REQUEST);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        googleAds();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new labor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, LaborActivity.class);
                intent.putExtra("ex_audi","insert");
                startActivity(intent);
            }
        });
        //http://www.tutorialspoint.com/android/android_auto_complete.htm
        mActv = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        mBtnSearch = (Button)findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLabor();
                Log.i("search", "Search button clicked...");

            }
        });
        mSpinner = (Spinner)findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   String cnameSel =parent.getItemAtPosition(position).toString();
                   onResume();
               }
               @Override
               public void onNothingSelected(AdapterView<?> parent) {
                    Log.i("NoSpin","Selected one");
               }
           });
        mTxtCompany = (TextView)findViewById(R.id.textView2);
        mTxtCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Add company", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent iCom = new Intent(MainActivity.this,CompanyActivity.class);
                startActivity(iCom);
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.YELLOW);
                Laborx laborx = (Laborx) parent.getAdapter().getItem(position);
                Log.i("list_onClick", laborx.lname);
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                //intent.putExtra("ex_audi", "update");
                intent.putExtra("ex_ser_labor", laborx);
                startActivity(intent);
            }
        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Laborx laborx = (Laborx) parent.getAdapter().getItem(position);
//                //*************setQuoteVolley(laborx.portfolio, laborx.exchange +":"+ laborx.symbol);
//                Intent intent = new Intent(MainActivity.this, LaborActivity.class);
//                intent.putExtra("ex_audi", "update");
//                intent.putExtra("ex_ser_labor", laborx);
//                startActivity(intent);
//                return true;
//            }
//        });
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position,
//                                                  long id, boolean checked) {
//                // Here you can do something when items are selected/de-selected,
//                // such as update the title in the CAB
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                // Respond to clicks on the actions in the CAB
//                switch (item.getItemId()) {
//                    case R.id.menu_delete:
//                        deleteSelectedItems();
//                        mode.finish(); // Action picked, so close the CAB
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                // Inflate the menu for the CAB
//                MenuInflater inflater = mode.getMenuInflater();
//                inflater.inflate(R.menu.menu_actionmode, menu);
//                return true;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                // Here you can make any necessary updates to the activity when
//                // the CAB is removed. By default, selected items are deselected/unchecked.
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                // Here you can perform updates to the CAB due to
//                // an invalidate() request
//                return false;
//            }
//        });

        getMinWageData();
        //getTaxIncomeData();
    }
    private void showMyCompanyDialog() {
        //Make view for dialog...
        LinearLayout li = new LinearLayout(this);
        li.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        li.setLayoutParams(lp);
        final EditText edt_mycom = new EditText(this);
        edt_mycom.setLayoutParams(lp);
        edt_mycom.setLines(1);
        TextInputLayout tilo = new TextInputLayout(this);
        tilo.setLayoutParams(lp);
        tilo.addView(edt_mycom);
        String mycomp=mSettings.getString("mycompany","nocompany");
        if (mycomp.equalsIgnoreCase("nocompany")) {
            edt_mycom.setHint("Enter company name");
        } else {
            edt_mycom.setText(mycomp);
        }
        TextView tvt = new TextView(this);
        tvt.setText("Set your company");
        li.addView(tvt);
        li.addView(tilo);
        new AlertDialog.Builder(this)
                .setTitle("InputBox")   //.setCustomTitle(tvt)
                .setView(li)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String retcomp = edt_mycom.getText().toString();
                        Log.i("dialog", "clicked OK" + retcomp);
                        if (retcomp!=null && !retcomp.isEmpty()) {
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString("mycompany",retcomp);
                            editor.commit();
                        } else {
                            Snackbar.make(edt_mycom, "No Data to process", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }).create().show();
    }
    private void getMinWageData() {
        Log.i("TAG", "start down...");
        new GetGoogleSheetTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                ArrayList<String> payperhours = new ArrayList<String>();
                try {
                    JSONArray rows = object.getJSONArray("rows");
                    for (int r = 0; r < rows.length(); ++r) {
                        JSONObject row = rows.getJSONObject(r);
                        JSONArray columns = row.getJSONArray("c");
                        String pph = columns.getJSONObject(0).getString("v");	//1st Column
                        String stdate = columns.getJSONObject(1).getString("v");	//2nd Column
                        String ftdate = columns.getJSONObject(1).getString("f");	//2nd Column
                        payperhours.add(pph);
                        Log.i("TAG",pph + stdate + ftdate); //6470.0Date(2016,6,19)2016-07-19
                    }
                    Log.i("TAG", payperhours.toString());
                    if (payperhours.contains("PayPerHour")) {
                        Log.i("TAG", "PayPerHour...Exist");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute("https://spreadsheets.google.com/tq?key=1IRB_XLVfDaB4JgLhz7x3iJ6h_yJETtIhr6rsPZj9eUU");
        Log.i("TAG", "end down...");
    }

    private ActionMode.Callback mActionModeCallback=new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            mActionMode=null;
            mSelectedItem=-1;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_actionmode, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    //int pos = mListView.getSelectedItemPosition();
                    int pos = mSelectedItem;
                    Laborx q = (Laborx) listView.getAdapter().getItem(pos);
                    Labor ll = q.fill2Labor();
                    try {
                        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(q);
                        //new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(ll);
                        //backend Class cannot be serialize
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("SerializerTest", e.getMessage());
                    }
                    new LaborAsyncTask("delete").execute(new Pair<Context, Labor>(MainActivity.this,ll));
                    //columns:
                    mDb.execSQL("delete from TableLabor where lid='{lid}' ".replace("{lid}",String.valueOf( q.lid)));
                    mode.finish(); // Action picked, so close the CAB
                    onResume();
                    return true;
                default:
                    return false;
            }
        }
    };
    private void deleteSelectedItems() {
//        Log.i("multiselect", listView.getSelectedItem().toString());
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
    private String[] getLabors() {
        ArrayList<String> ls = new ArrayList<>();

        Cursor c1 = mDb.rawQuery("select lname from TableLabor where gmail='{}';".replace("{}",mGmail),null);
        if (null==c1) return null;
        try {
            while (c1.moveToNext()) {
                ls.add(c1.getString(c1.getColumnIndex("lname")));
            }
        } finally {
            c1.close();
        }
        String[] labors= new String[ls.size()];
        labors = ls.toArray(labors);
        return labors;
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
    public void onClick(View v) {
        
    }

    private void spinnerUpdate() {
        String sql = "select cname from TableCompany where gmail='{}'".replace("{}",mGmail);
        mCursor=mDb.rawQuery(sql, null);
        int colcnt= mCursor.getColumnCount();   String[] cols = mCursor.getColumnNames();
        Log.i("DBquery",sql + String.valueOf(colcnt) + cols.toString());
        ArrayList<String> companys = new ArrayList<>();
        try {
            while (mCursor.moveToNext()) {
                companys.add(mCursor.getString(mCursor.getColumnIndex("cname")).toString());
            }
        } finally {
            mCursor.close();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, companys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnCreate) {
            isOnCreate=false;
        } else {
            spinnerUpdate();
            String[] labors = getLabors();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_expandable_list_item_1, labors);
            mActv.setAdapter(adapter);
            listLabor();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SignInActivity.SIGN_RESULT_CODE_OK:
                mSignResultOK =true;
                displaySplash();
                break;
            case SignInActivity.SIGN_RESULT_CODE_CANCEL:
                mSignResultOK =false;
                finish();
            default:
                mSignResultOK =false;
                break;
        }
    }
    private void listLabor() {
        //new LaborAsyncTask("list").execute(new Pair<Context, Labor>(MainActivity.this, new Labor()));
        String lname = mActv.getText().toString();
        String sql1 = ("select * from TableLabor where gmail='{gmail}' and lname like '%{lname}%' order by lname"
                .replace("{gmail}",mGmail)
                .replace("{lname}",lname));
        Log.i("SQL", sql1);
        Cursor cu = mDb.rawQuery(sql1,null);
        mList = new ArrayList<>();
        try {
            while (cu.moveToNext()) {
                Laborx lb = moveTable2Obj_Labor(cu);
                mList.add(lb);
            }
            Log.i("GetDBrow",String.valueOf(cu.getCount()) + " listed.");
        } finally {
            cu.close();
        }

        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
        //android.R.layout.simple_list_item_1,mList);
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, R.layout.row_main, mList);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        //
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
        //fill from transaction
        lb.lworkday=cu.getLong(cu.getColumnIndex("lworkday"));
        lb.lworkday=getworkday(lb.lname);
        lb.lbasepay=cu.getLong(cu.getColumnIndex("lbasepay"));
        lb.lpay=cu.getLong(cu.getColumnIndex("lpay"));
        //fill from transaction -end
        lb.lregdate=cu.getString(cu.getColumnIndex("lregdate"));
        return  lb;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_sendauto).setVisible(false);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.context_menu, menu);
        getMenuInflater().inflate(R.menu.menu_actionmode, menu);
        menu.findItem(R.id.menu_sms).setVisible(false);
        menu.findItem(R.id.menu_mailto).setVisible(false);
        menu.findItem(R.id.menu_kakao).setVisible(false);
        menu.findItem(R.id.menu_share).setVisible(false);
        menu.findItem(R.id.menu_print).setVisible(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showMyCompanyDialog();
            return true;
        } else if (id==R.id.action_payroll) {
            Intent in = new Intent(MainActivity.this, WebviewActivity.class);
            in.putExtra("lname","all");
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //ref...
        int pos=info.position;		//첫째 info.position=0 info.id=1
        long idx = info.id;				//첫째 info.id=1
        Laborx q = (Laborx) listView.getAdapter().getItem(pos);
        String title = q.lname;
        String content = q.lname + q.lmobile + q.lemail;
        Log.i("contextmenu", q.lname);
        //ref....
        switch (item.getItemId()) {
            case R.id.menu_edit:
                edit_Labor(q);
                return true;
            case R.id.menu_payroll:
                Intent ipayroll = new Intent(this, WebviewActivity.class);
                ipayroll.putExtra("lname", q.lname);
                startActivity(ipayroll);
                return true;
            case R.id.menu_phone:
                if (q.lmobile.length()<1) return false;
                Intent iphonecall = new Intent(Intent.ACTION_DIAL);
                iphonecall.setData(Uri.parse("tel:" + q.lmobile));
                startActivity(iphonecall);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void edit_Labor(Laborx laborx) {
        Log.i("context", laborx.lname + " edited...");
        Intent intent = new Intent(MainActivity.this, LaborActivity.class);
        intent.putExtra("ex_audi", "update");
        intent.putExtra("ex_ser_labor", laborx);
        startActivity(intent);
    }
    private void initDatabase() {
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        int version = mSettings.getInt("ver_tables",0);
        int upgrade = 4;
        Cursor cu = mDb.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null); // where name like 'Laborx%'", null);
        try {
            while (cu.moveToNext()) {
                String tablenameexist =cu.getString(cu.getColumnIndex("name"));
                Log.i("initdb","Existing Table: "+ tablenameexist);
                if (version !=0 && version < upgrade ) {
                    mDb.execSQL("Drop table " + tablenameexist);
                    Log.i("initdb","Table Upgraded: "+ tablenameexist);
                }
            }
        } finally {
            cu.close();
        }
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("ver_tables",upgrade);
        Log.i("dbversion", "database version "+String.valueOf(upgrade) + " install...");
        editor.commit();
//고객
        Log.i("initDB","TableCompany creating....");
        mDb.execSQL("CREATE TABLE IF NOT EXISTS TableCompany (" +
                "cid	    long PRIMARY KEY," +
                "gmail	TEXT," +
                "cname	TEXT," +
                "crep   TEXT," +
                "crepmobile   TEXT," +
                "cemail   TEXT," +
                "cpayperhour long, " +
                "cpaydays   long," +
                "cpaytransport   long," +
                "cpaytransition   long," +
                "cbonusrate   long," +
                "caddress   TEXT," +
                "cdata   TEXT," +
                "cregdate   TEXT" +
                ");");
//인력
        Log.i("initDB","TableLabor creating....");
        //mDb.execSQL("Drop Table TableLabor");
        String tableLabor="CREATE TABLE IF NOT EXISTS TableLabor (" +
                "lid        long PRIMARY KEY, " +
                "gmail       TEXT," +
                "lname       TEXT," +
                "cname     text," +
                "lmobile     TEXT, " +
                "lfamily    NUMERIC, " +
                "lchild    NUMERIC, " +
                "lemail       TEXT," +
                "lbasepay       numeric," +
                "lworkday       numeric," +
                "lpay       numeric," +
                "lregdate    text " +
                ");";
        mDb.execSQL(tableLabor);
/*
        Log.i("initDB","TableTransHeader creating....");
        String sql ="CREATE TABLE IF NOT EXISTS TableTransHeader (" +
                "thid long PRIMARY KEY, lid long, lname text, cid long, cname text, thym text, " +
                "payperday number, wstd_day number, wstd_day_jucha number, wstd_day_yg number, " +
                "workovertime number, workspecial number, workspecialover number, " +
                "worknight number, transport number, worklateearly number, " +
                "paybase number, payovertime number, payspecial number, payspecialover number, " +
                "paynight number, paytransport number, " +
                "paytransition number, bonus number, paytotal number, " +
                "ipension number, ihealth number, ilongrecuperate number, ilabor number, " +
                "iindusaccident number, itaxbiz number, " +
                "mexpense number, chargetotal number" +
                ");";
        mDb.execSQL(sql);
*/
        //Generate Table externally..haha
        //mDb.execSQL("drop table TransactionTable");
        Log.i("initDB","TableTransaction creating....");
        mDb.execSQL("CREATE TABLE IF NOT EXISTS TableTransaction (" +
                "tid	long PRIMARY KEY," +
                "gmail	text," +
                "lname   TEXT," +
                "cname   TEXT," +
                "tym   TEXT," +
                "tdate   TEXT," +
                "weekday   TEXT," +
                "starttime    Number," +
                "endtime      number," +
                "workstandard   number," +
                "work_jc   number," +
                "work_yg   number," +
                "workovertime   number," +
                "workspecial   number," +
                "workspecialover   number," +
                "worknight   number," +
                "worklateearly   number," +
                "tdesc   text," +
                "tregdate   TEXT" +
                ");");

    }
    private void displaySplash() {
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");
        Log.i("mGmail", mGmail);
        if (!mGmail.isEmpty()) {
            Log.i("AsyncStart", "Start AsyncTasks");
            new CompanyAsyncTask("list").execute(new Pair<Context, Company>(MainActivity.this, new Company()));
            new LaborAsyncTask("list").execute(new Pair<Context, Labor>(MainActivity.this, new Labor()));
            new TransactionAsyncTask("list").execute(new Pair<Context, Transaction>(MainActivity.this, new Transaction()));
        }
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
    }
    private void mailto() {
        Intent it = new Intent(Intent.ACTION_SEND);
        String[] tos = {"me@abc.com"};
        String[] ccs = {"you@abc.com"};
        it.putExtra(Intent.EXTRA_EMAIL, tos);
        it.putExtra(Intent.EXTRA_CC, ccs);
        it.putExtra(Intent.EXTRA_SUBJECT, "The email subject text");
        it.putExtra(Intent.EXTRA_TEXT, "The email body text");
        it.setType("message/rfc822");
//        it.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/mysong.mp3");
//        it.setType("audio/mp3");
        if (it.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(it, "Choose Email Client"));
        }
    }
    private void print2browser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setDataAndType(Uri.parse("file:///android_asset/ManinWorkforExcel.htm"), "text/html");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void doWebViewPrint() {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(MainActivity.this);
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
        strHtml=getStringFromFile();
        strHtml=strHtml.replace("{company}","이에스테크");
        strHtml=strHtml.replace("{tym}","201608");
        strHtml=strHtml.replace("{lname}","김일꾼");
        webView.loadDataWithBaseURL(null, strHtml, "text/HTML", "UTF-8", null);
        //webView.loadUrl("file:///android_asset/ManinWorkforExcel.htm");

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }
    private String getStringFromFile() {
        StringBuilder sb=new StringBuilder();
        InputStream stream= null;
        BufferedReader in=null;
        try {
            stream = getAssets().open("ManinWorkforExcel.htm");
            in= new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
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

/***************************************
    private void setQuoteVolley(final String portfolio, final String ex_symbol) {
        //Volley test
        //1. Async + HttpRequest가 기본
        //2. Async + Jsoup은 편리
        //3. Volley + Jsoup은 메인로직에서 코드 심플하게.
        //RequestQueue rq = Volley.newRequestQueue(context.getApplicationContext()); //required
        if (ex_symbol == null || ex_symbol.length() <1 ) return;
        RequestQueue rq = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        String urlstr="http://www.google.com/finance?q=samsung".replace("samsung", ex_symbol);
        //Log.i("URL",urlstr);
        StringRequest req = new StringRequest(Request.Method.GET, urlstr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Document doc = Jsoup.parse(data);
                        if (doc.select("title").text().contains("Search")) {
                        }  else {
                            Elements names = doc.select("meta[itemprop=name]");
                            Elements imsgeUrls = doc.select("meta[itemprop=imageUrl");
                            Elements symbols = doc.select("meta[itemprop=tickerSymbol]");
                            Elements exchages = doc.select("meta[itemprop=exchange]");
                            Elements prices = doc.select("meta[itemprop=price]");
                            Elements pricesChange = doc.select("meta[itemprop=priceChange]");
                            Elements pricesChangePercent = doc.select("meta[itemprop=priceChangePercent]");
                            Elements quoteTime = doc.select("meta[itemprop=quoteTime]");

                            Laborx quote = new Laborx();
                            quote.company= names.get(0).attr("content").toString();
                            quote.portfolio= portfolio;
                            String imgUrl=imsgeUrls.get(0).attr("content").toString();
                            quote.symbol= symbols.get(0).attr("content").toString();
                            quote.exchange=exchages.get(0).attr("content").toString();
                            quote.price=prices.get(0).attr("content").toString();
                            quote.priceChange=pricesChange.get(0).attr("content").toString();
                            quote.priceChangePercent=pricesChangePercent.get(0).attr("content").toString();
                            Log.i("result", quote.toStringSimple());
                            Double price = Double.parseDouble(quote.price.replaceAll(",",""));
                            Double priceC = Double.parseDouble(quote.priceChange.replaceAll(",",""));
                            Double priceCP = Double.parseDouble(quote.priceChangePercent);
                            Log.i("format",String.valueOf(price) +","+ String.valueOf(priceCP));
                            Cursor cu = mDb.rawQuery(String.format("select count(*) cnt from TableLabor where " +
                                    "portfolio='%s' and exchangesymbol = '%s'",portfolio, ex_symbol),null);
                            try {
                                while (cu.moveToNext()) {
                                    if (cu == null || cu.getInt(cu.getColumnIndex("cnt")) == 0) {
                                        mDb.execSQL(String.format("INSERT INTO TableLabor " +
                                                        "select null, '%s', '%s', '%s', %.2f, %.2f, %.2f ",
                                                quote.portfolio, quote.exchange + ":" + quote.symbol, quote.company, price, priceC, priceCP));
                                    } else {
                                        mDb.execSQL(String.format(Locale.getDefault(), "UPDATE TableLabor set " +
                                                        " company='%s'" +
                                                        ", price=%.2f" +
                                                        ", pricechange=%.2f" +
                                                        ", pricechangepercent=%.2f where exchangesymbol='%s'"
                                                , quote.company
                                                , price
                                                , priceC
                                                , priceCP
                                                , quote.exchange + ":" + quote.symbol));

                                    }
                                }
                            } finally {
                                cu.close();
                            }
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
******************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) {
            mDb.close();
        }
        if (mCursor != null) {
            mCursor.close();
        }
//        if (mAdView != null) {
//            Log.i("DESTROY", "mAdView closing");
//            mAdView.destroy();
//        }
    }
}
