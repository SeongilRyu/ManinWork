package com.nwsoft.maninwork;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nwsoft.maninwork.apis.TransactionAsyncTask;
import com.nwsoft.maninwork.backend.transactionApi.model.Transaction;
import com.nwsoft.maninwork.beans.Laborx;
import com.nwsoft.maninwork.beans.Transactionx;
import com.nwsoft.maninwork.mylibs.MyUtils;

import java.util.Calendar;

public class TransactionAUDActivity extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener
        , CompoundButton.OnCheckedChangeListener {
    SQLiteDatabase mDb;
    Transactionx mTrx;
    //input Ui
    TextView txt_cname;
    TextView txt_lname;
    TextView txt_tym;
    TextView txt_tdate;
    TextView txt_weekday;
    TextView txt_starttime;
    TextView txt_endtime;
    EditText edt_workstandard;
    EditText edt_work_jc;
    EditText edt_work_yg;
    EditText edt_workovertime;  TextView txt_over_m;
    EditText edt_workspecial;
    EditText edt_workspecialover;   TextView txt_spcover_m;
    EditText edt_worknight;
    EditText edt_worklateearly;
    CheckBox checkBox;
    CheckBox checkBox2;
    CheckBox checkBox3;
    //input Ui
    //button Ui
    Button  btn_cancel;
    Button btn_delete;
    Button btn_update;
    Button btn_add;
    ImageButton ibtn_left;
    ImageButton ibtn_right;
    //button Ui
    private SharedPreferences mSettings;
    private String mGmail;
    private Laborx mLbx;
    private String mAud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_aud);
        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");
        Intent iaud = getIntent();
        mAud = iaud.getStringExtra("aud");
        mLbx = (Laborx)iaud.getSerializableExtra("ser_lbx");
        mTrx = (Transactionx)getIntent().getSerializableExtra("ser_trx");
        initUi();       initButton();

        switch (mAud) {
            case "add":
                moveData2Ui(mTrx);
                break;
            case "update":
                moveData2Ui(mTrx);
                break;
            case "delete":
                moveData2Ui(mTrx);
                break;
            default:
                Log.i("taud","no case catched...");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGmail.isEmpty()) {
            Log.i("AsyncStart", "Start AsyncTasks");
            //new CompanyAsyncTask("list").execute(new Pair<Context, Company>(this, new Company()));
            //new LaborAsyncTask("list").execute(new Pair<Context, Labor>(this, new Labor()));
            new TransactionAsyncTask("list").execute(new Pair<Context, Transaction>(this, new Transaction()));
        }
    }

    private void initButton() {
        btn_cancel=(Button)findViewById(R.id.btn_cancel);       btn_cancel.setOnClickListener(this);
        btn_delete=(Button)findViewById(R.id.btn_delete);       btn_delete.setOnClickListener(this);
        btn_update=(Button)findViewById(R.id.btn_update);       btn_update.setOnClickListener(this);
        btn_add=(Button)findViewById(R.id.btn_add);              btn_add.setOnClickListener(this);
        ibtn_left= (ImageButton)findViewById(R.id.ibtn_left);   ibtn_left.setOnClickListener(this);
        ibtn_right=(ImageButton)findViewById(R.id.ibtn_right);  ibtn_right.setOnClickListener(this);
        btn_cancel.setVisibility(View.INVISIBLE);
        btn_delete.setVisibility(View.INVISIBLE);
        btn_update.setVisibility(View.INVISIBLE);
        btn_add.setVisibility(View.INVISIBLE);
        if (mAud.equalsIgnoreCase("add")) {
            btn_add.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            checkBox2.setVisibility(View.VISIBLE);
            checkBox3.setVisibility(View.VISIBLE);
        } else if (mAud.equalsIgnoreCase("update")) {
            btn_update.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
        } else if (mAud.equalsIgnoreCase("delete")) {
            btn_delete.setVisibility(View.VISIBLE);
        }
    }

    private void initUi() {
        txt_cname=(TextView)findViewById(R.id.txt_cname);
        txt_lname=(TextView)findViewById(R.id.txt_lname);
        txt_tym=(TextView)findViewById(R.id.txt_tym);
        txt_tdate=(TextView)findViewById(R.id.txt_tdate);
        txt_tdate.setOnClickListener(this);
        txt_weekday=(TextView)findViewById(R.id.txt_weekday);
        txt_starttime=(TextView) findViewById(R.id.txt_starttime);
        txt_starttime.setOnClickListener(this);
        txt_endtime=(TextView) findViewById(R.id.txt_endtime);
        txt_endtime.setOnClickListener(this);
        edt_workstandard=(EditText)findViewById(R.id.edt_workstandard);
        TextView txt_jc_help=(TextView)findViewById(R.id.txt_jc_help);
        txt_jc_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://developeryou.blogspot.kr/p/man-in-work.html#work_jc"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        TextView txt_yg_help=(TextView)findViewById(R.id.txt_yg_help);
        txt_yg_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://developeryou.blogspot.kr/p/man-in-work.html#work_yg"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        edt_work_jc=(EditText)findViewById(R.id.edt_work_jc);
        edt_work_yg=(EditText)findViewById(R.id.edt_work_yg);
        edt_workovertime=(EditText)findViewById(R.id.edt_workovertime);
        txt_over_m=(TextView)findViewById(R.id.txt_over_m);
        txt_over_m.setOnClickListener(this);
        edt_workspecial=(EditText)findViewById(R.id.edt_workspecial);
        edt_workspecialover=(EditText)findViewById(R.id.edt_workspecialover);
        txt_spcover_m=(TextView)findViewById(R.id.txt_spcover_m);
        txt_spcover_m.setOnClickListener(this);
        edt_worknight=(EditText)findViewById(R.id.edt_worknight);
        edt_worklateearly=(EditText)findViewById(R.id.edt_worklateearly);

        checkBox=(CheckBox)findViewById(R.id.checkBox);
        checkBox2=(CheckBox)findViewById(R.id.checkBox2);
        checkBox3=(CheckBox)findViewById(R.id.checkBox3);
        checkBox.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        checkBox.setVisibility(View.INVISIBLE);
        checkBox2.setVisibility(View.INVISIBLE);
        checkBox3.setVisibility(View.INVISIBLE);
    }
    private void moveData2Ui(Transactionx trx) {
        //key from lbx on case of add condition
        if (trx == null) {
            txt_cname.setText(mLbx.cname);
            txt_lname.setText(mLbx.lname);
            Calendar cal = Calendar.getInstance();
            String calYmd=String.format("%04d-%02d-%02d",
                    cal.get(Calendar.YEAR) ,
                    cal.get(Calendar.MONTH) +1,
                    cal.get(Calendar.DAY_OF_MONTH));
            txt_tym.setText(calYmd.replaceAll("-","").substring(0,6));
            txt_tdate.setText(calYmd);
            int iday = cal.get(Calendar.DAY_OF_WEEK);
            String sday = setWeekday(iday);
            txt_weekday.setText(sday);
            txt_starttime.setText("08:00");
            txt_endtime.setText("17:00");
        } else {
            txt_cname.setText(trx.cname);
            txt_lname.setText(trx.lname);
            txt_tym.setText(trx.tym);
            txt_tym.setTag(trx.tid);
            txt_tdate.setText(trx.tdate);
            String[] symd = trx.tdate.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR,Integer.parseInt(symd[0]));
            cal.set(Calendar.MONTH,Integer.parseInt(symd[1])-1);
            cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(symd[2]));
            int iday = cal.get(Calendar.DAY_OF_WEEK);
            String sday = setWeekday(iday);
            txt_weekday.setText(sday);
            txt_starttime.setText(trx.starttime);
            txt_endtime.setText(trx.endtime);
            edt_workstandard.setText(String.valueOf(trx.workstandard));
            edt_work_jc.setText(String.valueOf(trx.work_jc));
            edt_work_yg.setText(String.valueOf(trx.work_yg));
            //overtime=150 int
            String[] shm= MyUtils.convertIntTime2String(trx.workovertime).split(":");
            edt_workovertime.setText(shm[0]);   txt_over_m.setHint(shm[1]);
            edt_workspecial.setText(String.valueOf(trx.workspecial));
            shm= MyUtils.convertIntTime2String(trx.workspecialover).split(":");
            edt_workspecialover.setText(shm[0]);    txt_spcover_m.setHint(shm[1]);
            edt_worknight.setText(String.valueOf(trx.worknight));
            edt_worklateearly.setText(String.valueOf(trx.worklateearly));
            Log.i("moveD2Ui",trx.toString());
        }
    }

    @NonNull
    private String setWeekday(int iday) {
        //Google Calendar for national holiday...
        //https://developers.google.com/google-apps/calendar/quickstart/android
        String sday ="";
        switch (iday) {
            case Calendar.SUNDAY:
                sday=getResources().getString(R.string.lbl_sun); //Calendar.SUNDAY==1;
                txt_weekday.setTextColor(Color.RED);
                break;
            case Calendar.MONDAY:
                sday=getResources().getString(R.string.lbl_mon);
                break;
            case Calendar.TUESDAY:
                sday=getResources().getString(R.string.lbl_tue);
                break;
            case Calendar.WEDNESDAY:
                sday=getResources().getString(R.string.lbl_wed);
                break;
            case Calendar.THURSDAY:
                sday=getResources().getString(R.string.lbl_thu);
                break;
            case Calendar.FRIDAY:
                sday=getResources().getString(R.string.lbl_fri);
                break;
            case Calendar.SATURDAY:
                sday=getResources().getString(R.string.lbl_sat);
                txt_weekday.setTextColor(Color.RED);
                break;
        }
        return sday;
    }

    private Transaction moveUi2Data() {
        Transaction tr = new Transaction();
        tr.setTid((Long)txt_tym.getTag());
        tr.setGmail(mGmail);
        tr.setCname(txt_cname.getText().toString());
        tr.setLname(txt_lname.getText().toString());
        tr.setTym(txt_tym.getText().toString());
        tr.setTdate(txt_tdate.getText().toString());
        tr.setWeekday(txt_weekday.getText().toString());
        tr.setStarttime(txt_starttime.getText().toString());
        tr.setEndtime(txt_endtime.getText().toString());
        String tmp = edt_workstandard.getText().toString().equals("")?"0":edt_workstandard.getText().toString();
        tr.setWorkstandard(Long.parseLong(tmp));
        tmp = edt_work_jc.getText().toString().equals("")?"0":edt_work_jc.getText().toString();
        tr.setWorkJc(Long.parseLong(tmp));
        tmp = edt_work_yg.getText().toString().equals("")?"0":edt_work_yg.getText().toString();
        tr.setWorkYg(Long.parseLong(tmp));
        tmp = edt_workovertime.getText().toString().equals("")?"0":edt_workovertime.getText().toString();
        long lhm = MyUtils.convertString2IntTime(tmp+":"+ txt_over_m.getHint());  //0:30=015, 2:30=230
        tr.setWorkovertime(lhm);
        tmp = edt_workspecial.getText().toString().equals("")?"0":edt_workspecial.getText().toString();
        tr.setWorkspecial(Long.parseLong(tmp));
        tmp = edt_workspecialover.getText().toString().equals("")?"0":edt_workspecialover.getText().toString();
        lhm = MyUtils.convertString2IntTime(tmp +":"+ txt_spcover_m.getHint());  //0:30=015, 2:30=230
        tr.setWorkspecialover(lhm);
        tmp = edt_worknight.getText().toString().equals("")?"0":edt_worknight.getText().toString();
        tr.setWorknight(Long.parseLong(tmp));
        tmp = edt_worklateearly.getText().toString().equals("")?"0":edt_worklateearly.getText().toString();
        tr.setWorklateearly(Long.parseLong(tmp));
        return tr;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch  (buttonView.getId()) {
                case R.id.checkBox:
                    showMyDialog(R.id.checkBox);
                    break;
                case R.id.checkBox2:
                    showMyDialog(R.id.checkBox2);
                    break;
                case R.id.checkBox3:
                    showMyDialog(R.id.checkBox3);
                    break;
                default:
                    break;
            }
        }
    }
    private void showMyDialog(final int viewId) {
        //Make view for dialog...
        LinearLayout li = new LinearLayout(this);
        li.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        li.setLayoutParams(lp);
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setLayoutParams(lp);
        final RadioButton chk_aCom = new RadioButton(this);
        final RadioButton chk_allCom = new RadioButton(this);
        radioGroup.addView(chk_aCom);
        radioGroup.addView(chk_allCom);
        chk_aCom.setLayoutParams(lp);
        chk_allCom.setLayoutParams(lp);
        chk_aCom.setChecked(true);
        chk_allCom.setEnabled(false);
        String opt ="";
        if (viewId==R.id.checkBox) opt=getResources().getString(R.string.lbl_work_std);
        if (viewId==R.id.checkBox2) opt=getResources().getString(R.string.lbl_work_jc);
        if (viewId==R.id.checkBox3) opt=getResources().getString(R.string.lbl_work_yg);
        chk_aCom.setHint(String.format(getResources().getString(R.string.msg_radio_acom),opt));
        chk_allCom.setHint(String.format(getResources().getString(R.string.msg_radio_allcom),opt));
        TextView tvt = new TextView(this);
        tvt.setText("Select Add option");
        li.addView(tvt);
        li.addView(radioGroup);
        new AlertDialog.Builder(this)
                .setTitle("Select option")   //.setCustomTitle(tvt)
                .setView(li)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkWhich =(CheckBox)findViewById(viewId);
                        checkWhich.setChecked(false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkWhich =(CheckBox)findViewById(viewId);
                        if (chk_aCom.isChecked()) {
                            checkWhich.setText("Add_thisCom");
                            Snackbar.make(chk_allCom, "Add process to labors in this company", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (chk_allCom.isChecked()){
                            checkWhich.setText("Add_allCom");
                            Snackbar.make(chk_allCom, "Add process to all labors in all companies", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }).create().show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_delete:
                Transaction trdel = moveUi2Data();
                new TransactionAsyncTask("delete").execute(new Pair<Context, Transaction>(this,trdel));
                dbAud("delete",trdel);
                break;
            case R.id.btn_update:
                Transaction trupd = moveUi2Data();
                new TransactionAsyncTask("update").execute(new Pair<Context, Transaction>(this,trupd));
                dbAud("update",trupd);
                break;
            case R.id.btn_add:
                Transaction tradd = moveUi2Data();
                tradd.setTid(null);
                if (checkBox.isChecked() || checkBox2.isChecked() || checkBox3.isChecked()) {
                    Snackbar.make(v, "Multi Add", Snackbar.LENGTH_LONG)
                            .setAction("OK?", null).show();
                    String sql = String.format("select lname from TableLabor where cname='%s'", tradd.getCname());
                    Cursor c = mDb.rawQuery(sql,null);
                    try {
                        while (c.moveToNext()) {
                            String lname = c.getString(c.getColumnIndex("lname"));
                            Transaction trClone = tradd.clone();
                            trClone.setLname(lname);
                            new TransactionAsyncTask("insert").execute(new Pair<Context, Transaction>(this, trClone));
                        }
                    } finally {
                        c.close();
                    }
                    //((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,0);
                } else {
                    new TransactionAsyncTask("insert").execute(new Pair<Context, Transaction>(this, tradd));
                    dbAud("add", tradd);
                    Snackbar.make(v, "Transaction Added...!", Snackbar.LENGTH_LONG)
                            .setAction("Finish?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                }

                break;
            case R.id.ibtn_left:
            case R.id.ibtn_right:
                btnLeftRight_onClick(v);
                break;
            case R.id.txt_tdate:
                showDatePicker(v);
                break;
            case R.id.txt_starttime:
            case R.id.txt_endtime:
                showTimePicker(v);
                break;
            case R.id.txt_over_m:
                if (((TextView)v).getHint().toString().equals("00")) {
                    ((TextView)v).setHint("30");
                } else {
                    ((TextView)v).setHint("00");
                }
                break;
            case R.id.txt_spcover_m:
                if (((TextView)v).getHint().toString().equals("00")) {
                    ((TextView)v).setHint("30");
                } else {
                    ((TextView)v).setHint("00");
                }
                break;
            default:
                break;
        }
    }
    private void btnLeftRight_onClick(View v) {
        String tym = txt_tym.getText().toString();
        int yy = Integer.parseInt(tym.substring(0,4));
        int mm = Integer.parseInt(tym.substring(4));
        Calendar cal = Calendar.getInstance();
        cal.set(yy,mm-1,01);
        cal.add(Calendar.MONTH,v.getId()==R.id.ibtn_left?-1:1);
        Log.i("CAL", cal.getTime().toString());

        txt_tym.setText(String.format("%04d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1 ));
    }
    private void dbAud(String aud, Transaction obj) {
        if (aud.equalsIgnoreCase("add")) {
            String insertSql = "INSERT INTO TableTransaction VALUES (tid, 'gmail', 'lname', 'cname', 'tym', 'tdate', " +
                    "'weekday', 'starttime', 'endtime', workstandard,work_jc, work_yg, " +
                    "workvoertime,{workspecial},workspecialover,worknight,worklateearly,'tdesc', 'tregdate')";
            insertSql = insertSql.replace("tid",String.valueOf(obj.getTid()));
            insertSql = insertSql.replace("gmail",obj.getGmail()==null?"":obj.getGmail());
            insertSql = insertSql.replace("lname",obj.getLname()==null?"":obj.getLname());
            insertSql = insertSql.replace("cname",obj.getCname()==null?"":obj.getCname());
            insertSql = insertSql.replace("tym",obj.getTym()==null?"":obj.getTym());
            insertSql = insertSql.replace("tdate",obj.getTdate()==null?"":obj.getTdate());
            insertSql = insertSql.replace("weekday",obj.getWeekday()==null?"":obj.getWeekday());
            insertSql = insertSql.replace("starttime",obj.getStarttime()==null?"":obj.getStarttime());
            insertSql = insertSql.replace("endtime",obj.getEndtime()==null?"":obj.getEndtime());
            insertSql = insertSql.replace("workstandard",String.valueOf(obj.getWorkstandard()==null?"0":obj.getWorkstandard()));
            insertSql = insertSql.replace("work_jc",String.valueOf(obj.getWorkJc()==null?"0":obj.getWorkJc()));
            insertSql = insertSql.replace("work_yg",String.valueOf(obj.getWorkYg()==null?"0":obj.getWorkYg()));
            insertSql = insertSql.replace("workvoertime",String.valueOf(obj.getWorkovertime()==null?"0":obj.getWorkovertime()));
            insertSql = insertSql.replace("{workspecial}",String.valueOf(obj.getWorkspecial()==null?"0":obj.getWorkspecial()));
            insertSql = insertSql.replace("workspecialover",String.valueOf(obj.getWorkspecialover()==null?"0":obj.getWorkspecialover()));
            insertSql = insertSql.replace("worknight",String.valueOf(obj.getWorknight()==null?"0":obj.getWorknight()));
            insertSql = insertSql.replace("worklateearly",String.valueOf(obj.getWorklateearly()==null?"0":obj.getWorklateearly()));
            insertSql = insertSql.replace("tdesc",obj.getTdesc()==null?"":obj.getTdesc());
            insertSql = insertSql.replace("tregdate",obj.getTregdate()==null?"":obj.getTregdate());
            Log.i("SQL", insertSql);
            mDb.execSQL(insertSql);
        } else if (aud.equalsIgnoreCase("update")) {
            String updateSql = "UPDATE TableTransaction SET tid={tid}, gmail='{gmail}', lname='{lname}', cname='{cname}', tym='{tym}', tdate='{tdate}', " +
                    "weekday='{weekday}', starttime='{starttime}', endtime='{endtime}', workstandard={workstandard}, work_jc={work_jc}, work_yg={work_yg}, " +
                    "workovertime={workvoertime},workspecial={workspecial},workspecialover={workspecialover},worknight={worknight},worklateearly={worklateearly},tdesc='{tdesc}', tregdate='{tregdate}' " +
                    "where tid={tid};";
            updateSql = updateSql.replace("{tid}",String.valueOf(obj.getTid()));
            updateSql = updateSql.replace("{gmail}",obj.getGmail()==null?"":obj.getGmail());
            updateSql = updateSql.replace("{lname}",obj.getLname()==null?"":obj.getLname());
            updateSql = updateSql.replace("{cname}",obj.getCname()==null?"":obj.getCname());
            updateSql = updateSql.replace("{tym}",obj.getTym()==null?"":obj.getTym());
            updateSql = updateSql.replace("{tdate}",obj.getTdate()==null?"":obj.getTdate());
            updateSql = updateSql.replace("{weekday}",obj.getWeekday()==null?"":obj.getWeekday());
            updateSql = updateSql.replace("{starttime}",obj.getStarttime()==null?"":obj.getStarttime());
            updateSql = updateSql.replace("{endtime}",obj.getEndtime()==null?"":obj.getEndtime());
            updateSql = updateSql.replace("{workstandard}",String.valueOf(obj.getWorkstandard()==null?"0":obj.getWorkstandard()));
            updateSql = updateSql.replace("{work_jc}",String.valueOf(obj.getWorkJc()==null?"0":obj.getWorkJc()));
            updateSql = updateSql.replace("{work_yg}",String.valueOf(obj.getWorkYg()==null?"0":obj.getWorkYg()));
            updateSql = updateSql.replace("{workvoertime}",String.valueOf(obj.getWorkovertime()==null?"0":obj.getWorkovertime()));
            updateSql = updateSql.replace("{workspecial}",String.valueOf(obj.getWorkspecial()==null?"0":obj.getWorkspecial()));
            updateSql = updateSql.replace("{workspecialover}",String.valueOf(obj.getWorkspecialover()==null?"0":obj.getWorkspecialover()));
            updateSql = updateSql.replace("{worknight}",String.valueOf(obj.getWorknight()==null?"0":obj.getWorknight()));
            updateSql = updateSql.replace("{worklateearly}",String.valueOf(obj.getWorklateearly()==null?"0":obj.getWorklateearly()));
            updateSql = updateSql.replace("{tdesc}",obj.getTdesc()==null?"":obj.getTdesc());
            updateSql = updateSql.replace("{tregdate}",obj.getTregdate()==null?"":obj.getTregdate());
            Log.i("SQL", updateSql);
            mDb.execSQL(updateSql);
        } else if (aud.equalsIgnoreCase("delete")) {
            String deleteSql = "DELETE from TableTransaction where tid=" + String.valueOf(obj.getTid());
            Log.i("SQL", deleteSql);
            mDb.execSQL(deleteSql);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) mDb.close();
    }

    public void showDatePicker(View v) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(TransactionAUDActivity.this,TransactionAUDActivity.this,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    public void showTimePicker(View v) {
        Calendar cal = Calendar.getInstance();
        int ihh = cal.get(Calendar.HOUR_OF_DAY);
        int imm = cal.get(Calendar.MINUTE);
        if (v.getId()==R.id.txt_starttime) {
            new TimePickerDialog(TransactionAUDActivity.this, listenerStart, ihh, imm, true)
                    .show();
        } else if (v.getId()==R.id.txt_endtime) {
            new TimePickerDialog(TransactionAUDActivity.this, listenerEnd, ihh, imm, true)
                    .show();
        }
    }
    TimePickerDialog.OnTimeSetListener listenerStart = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                txt_starttime.setText(String.format("%02d:%02d",hourOfDay, minute));
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                cal.add(Calendar.HOUR_OF_DAY, +9);
                txt_endtime.setText(String.format("%02d:%02d",cal.get(Calendar.HOUR_OF_DAY), minute));
            }
        }
    };
    TimePickerDialog.OnTimeSetListener listenerEnd = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                Calendar calEnd = Calendar.getInstance();
                calEnd.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calEnd.set(Calendar.MINUTE,minute);
                txt_endtime.setText(String.format("%02d:%02d",hourOfDay, minute));

                Calendar calStart = Calendar.getInstance();
                String[] strStart = txt_starttime.getText().toString().split(":");
                calStart.set(Calendar.HOUR_OF_DAY,Integer.parseInt(strStart[0]));
                calStart.set(Calendar.MINUTE,Integer.parseInt(strStart[1]));

                Calendar calComp = Calendar.getInstance();
                Calendar calComp2 = Calendar.getInstance();
                calComp.set(Calendar.HOUR_OF_DAY,8);    calComp2.set(Calendar.HOUR_OF_DAY,17);
                calComp.set(Calendar.MINUTE,01);        calComp2.set(Calendar.MINUTE,00);
                String std="", ovr="";
                if (calStart.before(calComp) && calEnd.after(calComp2)) {
                    std="08:00";
                }
                calComp2.set(Calendar.MINUTE,30);
                if (calEnd.after(calComp2)) {
                    int days, hours, mins;
                    long gap = calEnd.getTimeInMillis() - calComp2.getTimeInMillis();
                    days = (int) (gap / (1000 * 60 * 60 * 24));
                    hours = (int) ((gap - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                    mins = (int) (gap - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                    ovr=hours +":"+mins;
                }
                Log.i("CCCC", std + " " + ovr );
            }
        }
    };
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (view.isShown()) {
            Calendar cal = Calendar.getInstance();
            String sdate = String.format("%4d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
            txt_tdate.setText(sdate);
            cal.set(year, monthOfYear,dayOfMonth);
            int iday = cal.get(Calendar.DAY_OF_WEEK);
            String sday =setWeekday(iday);

            txt_weekday.setText(sday);
        }
    }

}
