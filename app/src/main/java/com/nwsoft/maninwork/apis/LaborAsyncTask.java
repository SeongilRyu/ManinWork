package com.nwsoft.maninwork.apis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.nwsoft.maninwork.backend.laborApi.LaborApi;
import com.nwsoft.maninwork.backend.laborApi.model.Labor;
import com.nwsoft.maninwork.beans.Laborx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LaborAsyncTask extends AsyncTask<Pair<Context, Labor>, Void, List<Labor>> {
    private static LaborApi myApiService = null;
    private Context context;
    private GoogleAccountCredential credential;
    private SharedPreferences mSettings;
    private String audi;
    private String mGmail;
    private String mLname;

    public LaborAsyncTask(String audi) {
        this.audi = audi;
        Log.i("constrLaborAsc", audi);
    }

    @Override
    protected List<Labor> doInBackground(Pair<Context, Labor>... params) {
        context = (Context)params[0].first;     //ApplicationContext-o // activity-X
        //String web_client_id = context.getResources().getString(R.string.web_client_id);
        //String android_client_id_debug = context.getResources().getString(R.string.android_client_id_debug);
        //String android_client_id_release = context.getResources().getString(R.string.android_client_id_release);
        List<Labor> result = new ArrayList<>();
        Labor objLabor = (Labor)params[0].second;
        mSettings =context.getSharedPreferences("Settings", 0);
        mGmail = mSettings.getString("GMAIL",null);   //email == accountName of AccountManager
        mLname= objLabor.getLname();
        Log.i("objLabor", mGmail +"/"+ mLname);
        Set<String> scopes = mSettings.getStringSet("SCOPES",null);
        //
        //credential = GoogleAccountCredential.usingAudience(context,"server:client_id:"+web_client_id)
        //        .setSelectedAccountName(mGmail);
        if(myApiService == null) {  // Only do this once
            LaborApi.Builder builder = new LaborApi.Builder(AndroidHttp.newCompatibleTransport(),
                    //new AndroidJsonFactory(), credential)
                    new AndroidJsonFactory(),null)
                    /** Dev option-start**/
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    //.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    })
                     /**Dev option-end
                     **/
                    ;
            //builder.setApplicationName("xManinWork");
            myApiService = builder.build();
        }

        try {
            Log.i("try", audi);
            if (audi.equalsIgnoreCase("insert")) {
                Labor rLabor = new Labor();
                rLabor= myApiService.insert(objLabor).execute();
                Log.i("doinback", "Inserted Labor");
                result.add(rLabor);
                return result;
            } else if (audi.equalsIgnoreCase("update")) {
                Labor rLabor = new Labor();
                long tid = objLabor.getLid();
                rLabor=myApiService.update(tid, objLabor).execute();
                Log.i("doinback", "Updated Labor");
                result.add(rLabor);
                return result;
            } else if (audi.equalsIgnoreCase("delete")) {
                long tid= objLabor.getLid();
                Long tidL= objLabor.getLid();
                Log.i("long_chk_id", String.valueOf(tid) + "Long" + String.valueOf(tidL));
                myApiService.remove(tid).execute();
                Log.i("doinback", "Remove/Deleted Labor");
                return null;
            } else if (audi.equalsIgnoreCase("list")) {
                result=myApiService.listGmail(mGmail).execute().getItems();
                fillData(result);
                Log.i("doinback", "Listed Labor");
                return result;
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("WhatErr", e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Labor> result) {
        Log.i("postexe","Labor......");
        if (audi.equalsIgnoreCase("list")) {
            //fillData(result);
            Toast.makeText(context,"Sync Labor info...", Toast.LENGTH_SHORT).show();
        } else if (audi.equalsIgnoreCase("insert")) {
            Labor ll = result.get(0);
            Log.i("resInsert",String.valueOf(ll.getLid()) + " inserted!!" );
        } else if (audi.equalsIgnoreCase("update")) {
            Labor ll = result.get(0);
            Log.i("resUpdate",String.valueOf(ll.getLid()) + " inserted!!" );
        }
    }

    private void fillData(List<Labor> result) {
        if (result == null) {
            Log.i("-->insert", "null-No data to be inserted, so return");
            return;
        }
        ArrayList<Labor> objLabors = new ArrayList<>(result);
        if (audi.equalsIgnoreCase("list")) {
            SQLiteDatabase mDb = context.openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
            mDb.execSQL("delete from TableLabor where gmail ='{gmail}'".replace("{gmail}",mGmail));
            String insertSql = "";
            for (Labor objLabor : objLabors) {
                insertSql = "INSERT INTO TableLabor VALUES (lid, 'gmail', 'lname'," +
                        "'cname', 'lmobile',lfamily,lchild,'lemail', lbasepay,lworkday,lpay, 'lregdate')";
                insertSql = insertSql.replace("lid",String.valueOf(objLabor.getLid()));
                insertSql = insertSql.replace("gmail",objLabor.getGmail());
                insertSql = insertSql.replace("lname",objLabor.getLname());
                insertSql = insertSql.replace("cname",objLabor.getCname()==null?"":objLabor.getCname());
                insertSql = insertSql.replace("lmobile",objLabor.getLmobile()==null?"":objLabor.getLmobile());
                insertSql = insertSql.replace("lfamily", String.valueOf( objLabor.getLfamily()==null?"0":objLabor.getLfamily()));
                insertSql = insertSql.replace("lchild", String.valueOf( objLabor.getLchild()==null?"0":objLabor.getLchild()));
                insertSql = insertSql.replace("lemail",objLabor.getLemail()==null?"":objLabor.getLemail());
                insertSql = insertSql.replace("lbasepay", String.valueOf( objLabor.getLbasepay()==null?"0":objLabor.getLbasepay()));
                insertSql = insertSql.replace("lworkday", String.valueOf( objLabor.getLworkday()==null?"0":objLabor.getLworkday()));
                insertSql = insertSql.replace("lpay", String.valueOf( objLabor.getLpay()==null?"0":objLabor.getLpay()));
                insertSql = insertSql.replace("lregdate",objLabor.getLregdate()==null?"":objLabor.getLregdate());
                mDb.execSQL(insertSql);
                Log.i("insert_Labor",objLabor.getGmail() + objLabor.getCname());
            }
            mDb.close();
        }
    }

}
