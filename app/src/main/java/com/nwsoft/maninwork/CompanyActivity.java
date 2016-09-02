package com.nwsoft.maninwork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nwsoft.maninwork.apis.CompanyAsyncTask;
import com.nwsoft.maninwork.backend.companyApi.model.Company;
import com.nwsoft.maninwork.backend.laborApi.model.Labor;
import com.nwsoft.maninwork.beans.Laborx;

import org.w3c.dom.Text;

import java.util.ArrayList;

//import com.firebase.client.ChildEventListener;
//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;

public class CompanyActivity extends AppCompatActivity implements View.OnClickListener {
    private String mGmail;
    private SharedPreferences mSettings;
    private SQLiteDatabase mDb;
//UI
    EditText cname;
    EditText crep;
    EditText crepmobile;
    EditText cemail;
    EditText cpayperhour;
    EditText cpaytransport;
    EditText cpaytransition;
    EditText cbonusrate;
    TextView caddress;
    TextView cregdate;
    ListView listView;
    TextView txt_folder;
    LinearLayout lo_folder;
    LinearLayout lo_button; LinearLayout lo_header;
    TextView btnAdd;
    TextView btnUpdate;
    TextView btnDelete;
    TextView btnClear;
    boolean folding = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        mSettings = getSharedPreferences("Settings",MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL","");
        initUI();
        mDb = openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        //https://cloud.google.com/solutions/mobile/firebase-app-engine-android-studio#creating_an_android_studio_project
        //Start above to make setting firebase.s
        txt_folder= (TextView)findViewById(R.id.txt_folder);
        txt_folder.setOnClickListener(this);
        lo_header = (LinearLayout)findViewById(R.id.lo_header);
        lo_folder = (LinearLayout)findViewById(R.id.lo_folder);

        listView = (ListView)findViewById(R.id.listView2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Company c = (Company)parent.getAdapter().getItem(position);
                Intent iLabor = new Intent(CompanyActivity.this, LaborActivity.class);
                iLabor.putExtra("ex_audi","list");
                iLabor.putExtra("ex_cname", c.getCname());
                startActivity(iLabor);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Company c = (Company)parent.getAdapter().getItem(position);
                setUI(c);
                folding=false;
                txt_folder_onClick(view);
                btnAdd.setVisibility(View.INVISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                onResume();
                return true;
            }
        });
        lo_button =(LinearLayout)findViewById(R.id.lo_button);

        txt_folder.performClick();

        initButton();
    }

    private void initButton() {
        TextView txtbtnAdd =(TextView)findViewById(R.id.txtbtn_add);
        txtbtnAdd.setOnClickListener(this);
        TextView txtbtnUpdate =(TextView)findViewById(R.id.txtbtn_update);
        txtbtnUpdate.setOnClickListener(this);
        TextView txtbtnDelete =(TextView)findViewById(R.id.txtbtn_delete);
        txtbtnDelete.setOnClickListener(this);
        TextView txtbtnClear =(TextView)findViewById(R.id.txtbtn_clear);
        txtbtnClear.setOnClickListener(this);
        //real button
        btnAdd =(TextView)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        btnUpdate =(TextView)findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        btnDelete =(TextView)findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
        btnClear =(TextView)findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);

    }

    private void initUI() {
        cname = (EditText)findViewById(R.id.edt_cname);
         crep=(EditText)findViewById(R.id.edt_crep);
         crepmobile=(EditText)findViewById(R.id.edt_crepmobile);
         cemail = (EditText)findViewById(R.id.edt_cemail);
         cpayperhour = (EditText)findViewById(R.id.edt_cpayperhour);
         cpaytransport = (EditText)findViewById(R.id.edt_cpaytransportation);
         cpaytransition = (EditText)findViewById(R.id.edt_cpaytransition);
         cbonusrate = (EditText)findViewById(R.id.edt_bonusrate);
         caddress = (TextView)findViewById(R.id.txt_caddress);
         cregdate = (TextView)findViewById(R.id.txt_cregdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        new CompanyAsyncTask("list").execute(new Pair<Context, Company>(this,new Company()));
        ArrayList<Company> companies = getCompany();
        MyAdapter adapter = new MyAdapter(this,R.layout.row_company, companies);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDb != null) mDb.close();
    }
    private void dbAud(String aud, Company obj) {
        String insertSql="";
        if (aud.equalsIgnoreCase("add")) {
            insertSql = "INSERT INTO TableCompany VALUES (cid, 'gmail', 'cname', '{crep}', 'crepmobile', " +
                    "'cemail', cpayperhour, cpaydays, cpaytransport,cpaytransition, " +
                    "cbonusrate,'caddress','cdata', 'cregdate')";
            insertSql = insertSql.replace("cid",String.valueOf(obj.getCid()));
            insertSql = insertSql.replace("gmail",obj.getGmail());
            insertSql = insertSql.replace("cname",obj.getCname());
            insertSql = insertSql.replace("{crep}",obj.getCrep()==null?"":obj.getCrep());
            insertSql = insertSql.replace("crepmobile",obj.getCrepmobile()==null?"":obj.getCrepmobile());
            insertSql = insertSql.replace("cemail",obj.getCemail()==null?"":obj.getCemail());
            insertSql = insertSql.replace("cpayperhour",String.valueOf(obj.getCpayperhour()==null?"0":obj.getCpayperhour()));
            insertSql = insertSql.replace("cpaydays",String.valueOf(obj.getCpaydays()==null?"0":obj.getCpaydays()));
            insertSql = insertSql.replace("cpaytransport",String.valueOf(obj.getCpaytransport()==null?"0":obj.getCpaytransport()));
            insertSql = insertSql.replace("cpaytransition",String.valueOf(obj.getCpaytransition()==null?"0":obj.getCpaytransition()));
            insertSql = insertSql.replace("cbonusrate",String.valueOf(obj.getCbonusrate()==null?"0":obj.getCbonusrate()));
            insertSql = insertSql.replace("caddress",obj.getCaddress()==null?"":obj.getCaddress());
            insertSql = insertSql.replace("tdata",obj.getCdata()==null?"":obj.getCdata());
            insertSql = insertSql.replace("cregdate",obj.getCregdate()==null?"":obj.getCregdate());
            mDb.execSQL(insertSql);
        } else if (aud.equalsIgnoreCase("update")) {
            String updateSql = "UPDATE TableCompany SET cid={cid}, gmail='{gmail}', cname='{cname}', " +
                                            "crep='{crep}', crepmobile='{crepmobile}'," +
                                            "cemail='{cemail}', cpayperhour={cpayperhour}, " + 
                                            "cpaydays={cpaydays}, cpaytransport={cpaytransport}, " + 
                                            "cpaytransition={cpaytransition}, " +
                                            "cbonusrate={cbonusrate},caddress='{caddress}', " + 
                                            "cdata='{cdata}', cregdate='{cregdate}' " +
                    "where cid={cid}" ;
            updateSql = updateSql.replace("{cid}",String.valueOf(obj.getCid()));
            updateSql = updateSql.replace("{gmail}",obj.getGmail());
            updateSql = updateSql.replace("{cname}",obj.getCname());
            updateSql = updateSql.replace("{crep}",obj.getCrep()==null?"":obj.getCrep());
            updateSql = updateSql.replace("{crepmobile}",obj.getCrepmobile()==null?"":obj.getCrepmobile());
            updateSql = updateSql.replace("{cemail}",obj.getCemail()==null?"":obj.getCemail());
            updateSql = updateSql.replace("{cpayperhour}",String.valueOf(obj.getCpayperhour()==null?"0":obj.getCpayperhour()));
            updateSql = updateSql.replace("{cpaydays}",String.valueOf(obj.getCpaydays()==null?"0":obj.getCpaydays()));
            updateSql = updateSql.replace("{cpaytransport}",String.valueOf(obj.getCpaytransport()==null?"0":obj.getCpaytransport()));
            updateSql = updateSql.replace("{cpaytransition}",String.valueOf(obj.getCpaytransition()==null?"0":obj.getCpaytransition()));
            updateSql = updateSql.replace("{cbonusrate}",String.valueOf(obj.getCbonusrate()==null?"0":obj.getCbonusrate()));
            updateSql = updateSql.replace("{caddress}",obj.getCaddress()==null?"":obj.getCaddress());
            updateSql = updateSql.replace("{tdata}",obj.getCdata()==null?"":obj.getCdata());
            updateSql = updateSql.replace("{cregdate}",obj.getCregdate()==null?"":obj.getCregdate());
            mDb.execSQL(updateSql);
        } else if (aud.equalsIgnoreCase("delete")) {
            String deleteSql = "DELETE from TableCompany where cid=" +String.valueOf(obj.getCid());
            mDb.execSQL(deleteSql);
        }
    }
    private ArrayList<Company> getCompany() {
        ArrayList<Company> companies = new ArrayList<>();
        Cursor c1 = mDb.rawQuery("select * from TableCompany where gmail='{}' order by cname"
                .replace("{}",mGmail),null);
        try {
            while (c1.moveToNext()) {
                Company company = new Company();
                company.setCid(c1.getLong(c1.getColumnIndex("cid")));
                company.setGmail(c1.getString(c1.getColumnIndex("gmail")));
                company.setCname(c1.getString(c1.getColumnIndex("cname")));
                company.setCrep(c1.getString(c1.getColumnIndex("crep")));
                company.setCrepmobile(c1.getString(c1.getColumnIndex("crepmobile")));
                company.setCemail(c1.getString(c1.getColumnIndex("cemail")));
                company.setCpayperhour(c1.getLong(c1.getColumnIndex("cpayperhour")));
                company.setCpaydays(c1.getLong(c1.getColumnIndex("cpaydays")));
                company.setCpaytransport(c1.getLong(c1.getColumnIndex("cpaytransport")));
                company.setCpaytransition(c1.getLong(c1.getColumnIndex("cpaytransition")));
                company.setCbonusrate(c1.getLong(c1.getColumnIndex("cbonusrate")));
                company.setCaddress(c1.getString(c1.getColumnIndex("caddress")));
                company.setCregdate(c1.getString(c1.getColumnIndex("cregdate")));
                companies.add(company);
            }
        } finally {
            c1.close();
        }
        return companies;
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
                btnAdd.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtbtn_clear:
            case R.id.btn_clear:
                clearUI();
                Log.i("clear","clearUI...");
                break;
            case R.id.txtbtn_add:
            case R.id.btn_add:
                btnAdd_onClick(v);
                Log.i("txtbtn_add","txtbtn_add...");
                break;
            case R.id.txtbtn_update:
            case R.id.btn_update:
                btnUpdate_onClick(v);
                Log.i("txtbtn_update","txtbtn_update...");
                break;
            case R.id.txtbtn_delete:
            case R.id.btn_delete:
                btnDelete_onClick(v);
                Log.i("txtbtn_delete","txtbtn_delete...");
                break;
            case R.id.txt_folder:
                txt_folder_onClick(v);
                break;
            default:
                break;
        }
        onResume();
    }
    private void txt_folder_onClick(View v) {
        lo_folder.setVisibility(folding==true?View.GONE:View.VISIBLE);
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
    private void btnAdd_onClick(View v) {
        Company ca=getUI2Data();
        if (ca.getCname()==null || ca.getCname().length() < 1) {
            Snackbar.make(v, "No company exist to add!", Snackbar.LENGTH_LONG)
                    .setAction("noAction", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("noAction","noaction");
                        }
                    }).show();
        } else {
            if (ca.getCid()==null) {
                new CompanyAsyncTask("insert").execute(new Pair<Context, Company>(CompanyActivity.this, ca));
                dbAud("add", ca);
            }
        }
    }
    private void btnUpdate_onClick(View v) {
        Company ca=getUI2Data();
        if (ca.getCid()==null || ca.getCname().length() < 1) {
            Snackbar.make(v, "No company exist to UPDATE!", Snackbar.LENGTH_LONG)
                    .setAction("noAction", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("noAction","noaction");
                        }
                    }).show();
        } else {
            new CompanyAsyncTask("update").execute(new Pair<Context, Company>(CompanyActivity.this, ca));
            dbAud("update",ca);
        }
    }
    private void btnDelete_onClick(View v) {
        Company ca=getUI2Data();
        if (ca.getCid()==null || ca.getCname().length() < 1) {
            Snackbar.make(v, "No company exist to DELETE!", Snackbar.LENGTH_LONG)
                    .setAction("noAction", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("noAction","noaction");
                        }
                    }).show();
        } else {
            new CompanyAsyncTask("delete").execute(new Pair<Context, Company>(CompanyActivity.this, ca));
            dbAud("delete",ca);
        }
    }
    private void clearUI() {
        cname.setTag(null);
        cname.setText("");
        crep.setText("");
        crepmobile.setText("");
        cpayperhour.setText("");
        cpaytransport.setText("");
        cpaytransition.setText("");
        cbonusrate.setText("");
        //hidden field
        caddress.setText("");
        cregdate.setText("");
    }
    private void setUI(Company c) {
        if (c.getCid()==null) {
            cname.setTag(null);
        } else {
            cname.setTag(c.getCid());
        }
        cname.setText(c.getCname());
        crep.setText(c.getCrep());
        crepmobile.setText(c.getCrepmobile());
        cemail.setText(c.getCemail());
        cpayperhour.setText(String.valueOf(c.getCpayperhour()));
        cpaytransport.setText(String.valueOf(c.getCpaytransport()));
        cpaytransition.setText(String.valueOf(c.getCpaytransition()));
        cbonusrate.setText(String.valueOf(c.getCbonusrate()));
        //hidden field
        caddress.setText(c.getCaddress());
        cregdate.setText(c.getCregdate());
    }
    private Company getUI2Data() {
        Company c = new Company();
        if (cname.getTag()==null) {
            c.setCid(null);
        } else {
            c.setCid((Long)cname.getTag());
        }
        c.setGmail(mGmail);
        c.setCname(cname.getText().toString());
        c.setCrep(crep.getText().toString());
        c.setCrepmobile(crepmobile.getText().toString());
        c.setCemail(cemail.getText().toString());
        String st1 = cpayperhour.getText().toString().equals("")?"0":cpayperhour.getText().toString();
        c.setCpayperhour(Long.parseLong(st1));
        st1 = cpaytransport.getText().toString().equals("")?"0":cpaytransport.getText().toString();
        c.setCpaytransport(Long.parseLong(st1));
        st1 = cpaytransition.getText().toString().equals("")?"0":cpaytransition.getText().toString();
        c.setCpaytransition(Long.parseLong(st1));
        st1 = cbonusrate.getText().toString().equals("")?"0":cbonusrate.getText().toString();
        c.setCbonusrate(Long.parseLong(st1));
        //hidden fields
        c.setCaddress(caddress.getText().toString());
        c.setCregdate(cregdate.getText().toString());
        return c;
    }
    class MyAdapter extends ArrayAdapter<Company> {
        private Context context;
        private Cursor mCursor;
        private int layoutid;
        private ArrayList<Company> items;
        Company lb;
        class ViewHolder {
            TextView t10;    //Company
            TextView t12;    //rep name
            TextView t13;
            TextView t14;    //Pay per Hour
            TextView t20;
            TextView t22;
            TextView t23;
            TextView t25;    //Bonus Rate
        }
        public MyAdapter(Context context, int textViewResourceId, ArrayList<Company> items) {
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
                ((TextView)convertView.findViewById(R.id.txt_rhead)).setVisibility(View.GONE);
                holder.t10=(TextView)convertView.findViewById(R.id.txt_r10);
                holder.t12 = (TextView)convertView.findViewById(R.id.txt_r12);
                holder.t13=(TextView)convertView.findViewById(R.id.txt_r13);
                holder.t14 = (TextView) convertView.findViewById(R.id.txt_r14);
                holder.t20=(TextView)convertView.findViewById(R.id.txt_r20);
                holder.t22=(TextView)convertView.findViewById(R.id.txt_r22);
                holder.t23=(TextView)convertView.findViewById(R.id.txt_r23);
                holder.t25 = (TextView) convertView.findViewById(R.id.txt_r25);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            //if (q != null) {
            holder.t10.setText(lb.getCname());
            holder.t12.setText(lb.getCrep());
            holder.t13.setText(lb.getCrepmobile());
            holder.t14.setText(String.valueOf(lb.getCpayperhour()));
            holder.t20.setText(lb.getCemail());
            holder.t22.setText(lb.getCpaytransport()+"");
            holder.t23.setText(String.valueOf(lb.getCpaytransition()));
            holder.t25.setText(String.valueOf(lb.getCbonusrate())+"%");
            //}
            return convertView;
        }
    }
}
