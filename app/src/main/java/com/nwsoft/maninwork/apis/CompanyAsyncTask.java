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
import com.nwsoft.maninwork.backend.companyApi.model.Company;
import com.nwsoft.maninwork.backend.companyApi.CompanyApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompanyAsyncTask extends AsyncTask<Pair<Context, Company>, Void, List<Company>> {
    private static CompanyApi myApiService = null;
    private Context context;
    private GoogleAccountCredential credential;
    private SharedPreferences mSettings;
    private String audi;
    private String mGmail;
    private String mCname;
    public CompanyAsyncTask(String audi) {
        this.audi = audi;
        Log.i("constr","CompanyAsync: " + audi);
    }

    @Override
    protected List<Company> doInBackground(Pair<Context, Company>... params) {
        context = (Context)params[0].first;     //ApplicationContext-o // activity-X
        //String web_client_id = context.getResources().getString(R.string.web_client_id);
        //String android_client_id_debug = context.getResources().getString(R.string.android_client_id_debug);
        //String android_client_id_release = context.getResources().getString(R.string.android_client_id_release);
        List<Company> result = new ArrayList<>();
        Company obj = (Company)params[0].second;
        mSettings =context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mGmail = mSettings.getString("GMAIL",null);   //email == accountName of AccountManager
        mCname= obj.getCname();
        Log.i("obj", mGmail +"/"+ mCname);
        Set<String> scopes = mSettings.getStringSet("SCOPES",null);
        //
        //credential = GoogleAccountCredential.usingAudience(context,"server:client_id:"+web_client_id)
        //        .setSelectedAccountName(mGmail);
        if(myApiService == null) {  // Only do this once
            CompanyApi.Builder builder = new CompanyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    //new AndroidJsonFactory(), credential)
                    new AndroidJsonFactory(),null)
                    /** Dev option-start
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    })
                     Dev option-end
                     **/
                    ;
            builder.setApplicationName("ManinWork");
            myApiService = builder.build();
        }

        try {
            Log.i("try", audi);
            if (audi.equalsIgnoreCase("insert")) {
                Company com= myApiService.insert(obj).execute();
                result.add(com);
                return result;
            } else if (audi.equalsIgnoreCase("update")) {
                Company com= myApiService.update(obj.getCid(), obj).execute();
                result.add(com);
                return result;
            } else if (audi.equalsIgnoreCase("delete")) {
                myApiService.remove(obj.getCid()).execute();
                result.add(obj);
                return  result; //delete Cid in obj.
            } else if (audi.equalsIgnoreCase("list")) {
                result=myApiService.listGmail(mGmail).execute().getItems();
                fillData(result);
                Log.i("doinback", "Listed Company");
                return result;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Company> result) {
        Log.i("postexe","Company......");
        if (audi.equalsIgnoreCase("list")) {
            Toast.makeText(context,"Sync Company info...", Toast.LENGTH_SHORT).show();
        } else if (audi.equalsIgnoreCase("insert")) {
            Company ll = result.get(0);
            Log.i("resInsert",String.valueOf(ll.getCid() + " inserted!!" ));
        } else if (audi.equalsIgnoreCase("update")) {
            Company ll = result.get(0);
            Log.i("resUpdate",String.valueOf(ll.getCid()) + " inserted!!" );
        }
    }

    private void fillData(List<Company> result) {
        SQLiteDatabase mDb = context.openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
        if (result == null) {
            return;
        }
        ArrayList<Company> objs = new ArrayList<>(result);
        if (audi.equalsIgnoreCase("list")) {
            mDb.execSQL("delete from TableCompany where gmail like '%{email}%'".replace("{email}",mGmail));
            String insertSql = "";
            for (Company obj : objs) {
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
                Log.i("insert_Company",obj.getGmail() + obj.getCname());
            }
        }
        mDb.close();
    }

}
