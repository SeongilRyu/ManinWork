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
import com.nwsoft.maninwork.backend.transactionApi.TransactionApi;
import com.nwsoft.maninwork.backend.transactionApi.model.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransactionAsyncTask extends AsyncTask<Pair<Context, Transaction>, Void, List<Transaction>> {
    private static TransactionApi myApiService = null;
    private Context context;
    private GoogleAccountCredential credential;
    private SharedPreferences mSettings;
    private String audi;
    private String mGmail;
    private String mCname;

    public TransactionAsyncTask(String audi) {
        this.audi = audi;
        Log.i("constr", audi);
    }

    @Override
    protected List<Transaction> doInBackground(Pair<Context, Transaction>... params) {
        context = (Context)params[0].first;     //ApplicationContext-o // activity-X
        //String web_client_id = context.getResources().getString(R.string.web_client_id);
        //String android_client_id_debug = context.getResources().getString(R.string.android_client_id_debug);
        //String android_client_id_release = context.getResources().getString(R.string.android_client_id_release);
        List<Transaction> result = new ArrayList<>();
        Transaction obj = (Transaction)params[0].second;
        mSettings =context.getSharedPreferences("Settings", 0);
        mGmail = mSettings.getString("GMAIL",null);   //email == accountName of AccountManager
        mCname= obj.getCname();
        Log.i("obj", mGmail +"/"+ mCname);
        Set<String> scopes = mSettings.getStringSet("SCOPES",null);
        //
        //credential = GoogleAccountCredential.usingAudience(context,"server:client_id:"+web_client_id)
        //        .setSelectedAccountName(mGmail);
        if(myApiService == null) {  // Only do this once
            TransactionApi.Builder builder = new TransactionApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            if (audi.equalsIgnoreCase("insert")) {
                Transaction tran= myApiService.insert(obj).execute();
                result.add(tran);
                return  result;
            } else if (audi.equalsIgnoreCase("update")) {
                Transaction tran= myApiService.update(obj.getTid(), obj).execute();
                result.add(tran);
                return  result;
            } else if (audi.equalsIgnoreCase("delete")) {
                myApiService.remove(obj.getTid()).execute();
                result.add(obj);
                return  result; //delete Cid in obj.
            } else if (audi.equalsIgnoreCase("list")) {
                result=myApiService.listGmail(mGmail).execute().getItems();
                fillData(result);
                return result;
            }
            return null;
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Transaction> result) {
        Log.i("postexe","Transaction......");
        if (audi.equalsIgnoreCase("list")) {
            Toast.makeText(context,"Sync Transaction info...", Toast.LENGTH_SHORT).show();
        } else if (audi.equalsIgnoreCase("insert")) {
            Transaction ll = result.get(0);
            Log.i("resInsert",String.valueOf(ll.getTid() + " inserted!!" ));
        } else if (audi.equalsIgnoreCase("update")) {
            Transaction ll = result.get(0);
            Log.i("resUpdate",String.valueOf(ll.getTid()) + " inserted!!" );
        }
    }

    private void fillData(List<Transaction> result) {
        if (result == null) {
            Log.i("-->insert", "null-inserted return");
            return;
        }
        ArrayList<Transaction> objs = new ArrayList<>(result);
        if (audi.equalsIgnoreCase("list")) {
            SQLiteDatabase mDb = context.openOrCreateDatabase("ManinWork.db", Context.MODE_PRIVATE, null);
            mDb.execSQL("delete from TableTransaction where gmail ='{gmail}'".replace("{gmail}",mGmail));
            String insertSql = "";
            for (Transaction obj : objs) {
                insertSql = "INSERT INTO TableTransaction VALUES (tid, 'gmail', 'lname', 'cname', 'tym', 'tdate', " +
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
                mDb.execSQL(insertSql);
                Log.i("insert_Transaction",obj.getGmail() + obj.getCname());
            }
            mDb.close();
        }
    }

}
