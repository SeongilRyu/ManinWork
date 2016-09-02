package com.nwsoft.maninwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.nwsoft.maninwork.apis.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public static final int SIGN_RESULT_CODE_OK = 5008;
    public static final int SIGN_RESULT_CODE_CANCEL = 5009;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleSignInButton;
    private Button mBtnSignout;

    private TextView mStatus0;
    private TextView mStatus;
    private TextView mDetail;
    private String mFullName;
    private String mGmail;
    private SharedPreferences mSettings;
    private ProgressDialog mProgressDialog;

    private Button mBtnOk;
    private Button mBtnCancel;
    //private LinearLayout mLo_nw;
    NetworkImageView mNetworkImageView;
    private TextView txt_proceed;

    private String mSavInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        googleAds();
        // Views
        initUiButtions();
        /**********NwUser contatiner
        mLo_nw = (LinearLayout)findViewById(R.id.lo_nwuser);
        mLo_nw.setVisibility(View.INVISIBLE);
        mEdtCountry = (TextView)findViewById(R.id.txt_country);
        mEdtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder adBuilder = new AlertDialog.Builder(SignInActivity.this);
                adBuilder.setTitle("Select a Country");
                //adBuilder.setMessage("dialog message");
//                adBuilder.setItems(R.array.array_country, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String[] countries = getResources().getStringArray(R.array.array_country);
//                        String country=countries[which];
//                        Log.i("-->DItem", country);
//                        mEdtCountry.setText(country);
//                        dialog.dismiss();
//                    }
//                });
                adBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = adBuilder.create();
                dialog.show();
            }
        });
         ****/
        //https://developers.google.com/identity/sign-in/android/sign-in#before_you_begin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestIdToken(getResources().getString(R.string.default_web_client_id))
                .build();

        // [START build_client]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        //mGoogleSignInButton.setScopes(gso.getScopeArray());

    }

    private void initUiButtions() {
        mStatus0 = (TextView) findViewById(R.id.status0);
        // Get the NetworkImageView that will display the image.
        mNetworkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
        mStatus = (TextView) findViewById(R.id.status);
        mDetail = (TextView)findViewById(R.id.detail);

        // Button listeners
        mGoogleSignInButton =(SignInButton) findViewById(R.id.sign_in_button);
        mGoogleSignInButton.setOnClickListener(this);
        mBtnSignout = (Button) findViewById(R.id.sign_out_button);
        mBtnSignout.setOnClickListener(this);
        mBtnSignout.setVisibility(View.GONE);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        findViewById(R.id.sign_out_and_disconnect).setOnClickListener(this);

        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnCancel= (Button)findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnCancel.setVisibility(View.INVISIBLE);
        mBtnOk.setVisibility(View.INVISIBLE);
        txt_proceed = (TextView) findViewById(R.id.textView26);
        txt_proceed.setVisibility(View.INVISIBLE);
    }

    private void signIn() {
        Log.i("signin","signin");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mFullName = acct.getDisplayName();
            mGmail=acct.getEmail();
            if (mGmail.equalsIgnoreCase("parkinfo@gmail.com")) {
                Log.i("notallowed", "Google credential failed" + mGmail);
                setResult(SIGN_RESULT_CODE_CANCEL);
                finish();
                return;
            }
            String id = acct.getId();
            Set<Scope> scopes = acct.getGrantedScopes();
            String token= acct.getIdToken();
            Uri uri = acct.getPhotoUrl();
            netImageLoader(uri);
            Log.i("-->GSignAcct", id +"/"+ mGmail + mFullName +"/"+ scopes.toString() +"/"+ token +"/"+ uri);
            updateUI(true);
            setSettings(acct);
           // new GetNwUserAsyncTask().execute(mGmail);
            //new FarmAsyncTask("list").execute(new Pair<Context, Farm>(SignInActivity.this, new Farm()));
//            Pair<Context, String> parm = new Pair<Context, String>(this, mGmail);
//            new CattleAsyncTask("list").execute(parm);
            setResult(SIGN_RESULT_CODE_OK);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
            setResult(SIGN_RESULT_CODE_CANCEL);
        }
    }
    private void setSettings(GoogleSignInAccount acct) {
        SharedPreferences mSettings =getSharedPreferences("Settings",0);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("GMAIL",acct.getEmail());
        editor.putString("ACCOUNT_NAME", acct.getDisplayName());
        editor.putString("ID",acct.getId());
        Set<String> scopes = new HashSet<String>(Arrays.asList(acct.getGrantedScopes().toArray().toString()));
        editor.putStringSet("SCOPES",scopes);
        editor.putString("TOKEN",acct.getIdToken());
        editor.putString("URI",acct.getPhotoUrl()==null?"Null":acct.getPhotoUrl().toString());
        editor.commit();
    }

    public class UpsertNwUserAsyncTask extends AsyncTask<String[], Void, Void> {
        @Override
        protected Void doInBackground(String[]... params) {
            String[] rtnBean;
            rtnBean = getUserByEmail(params[0][1]);
            String id = null;
            if (rtnBean !=null && rtnBean[0] != null) {
                id=rtnBean[0]==null?"":rtnBean[0];
                Log.i("-->rtnBean", "[id" + id +"],[rtnBean0" + rtnBean[0] + "] " + rtnBean[1]);
            }
            if (id != null && id.length()>0) {
                if (!rtnBean[3].contains(params[0][3])) {
                    rtnBean[0] = id;
                    rtnBean[1]= params[0][1];
                    rtnBean[2]= params[0][2];
                    rtnBean[3] += ","+params[0][3];
                    rtnBean[4]= params[0][4];
                    rtnBean[5]= params[0][5];
                    rtnBean[6]= params[0][6];
                    putUpdateUser(rtnBean);
                } else {
                    rtnBean[5]= params[0][5];
                    putUpdateUser(rtnBean);
                }

            } else {
                postInsertUser(params[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    public class GetNwUserAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            return getUserByEmail(params[0]);
        }

        @Override
        protected void onPostExecute(String[] ss) {
            super.onPostExecute(ss);
            if (ss != null) {
                String email = ss[1];
                String apps = ss[3];
                String userEtc = ss[5];
                JSONObject json=null;
                String _fullname=null, _mobile=null, _farm=null, _country=null;
                try {
                    json=new JSONObject(userEtc);
                    _fullname = json.getString("fullname");
                    _mobile = json.getString("mobile");
                    _farm = json.getString("farm");
                    _country = json.getString("country");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (email.equals(mGmail) && apps.contains("ManinWork")) {
                    ((TextView)findViewById(R.id.textView10)).setText("You are a user of Maninwork..");
                    //mLo_nw.setVisibility(View.VISIBLE); //to be invisibled...
                    ((EditText)findViewById(R.id.edt_fullname)).setText(_fullname);
                    ((EditText)findViewById(R.id.edt_mobile)).setText(_mobile);
                    ((EditText)findViewById(R.id.edt_farmname)).setText(_farm);
                    ((TextView)findViewById(R.id.txt_country)).setText(_country);
                    mSavInfos = mGmail + _fullname + _mobile + _farm + _country;
                } else {
                    ((TextView)findViewById(R.id.textView10)).setText("But You are not a ManinWork user. Please Enter infos...");
                    //mLo_nw.setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView)findViewById(R.id.textView10)).setText("But, You are not a ManinWork user. Please Enter infos...");
                //mLo_nw.setVisibility(View.VISIBLE);
            }
        }
    }
    public static String[] getUserByEmail(String email) {
        String urlstr="https://nwsoftengine.appspot.com/_ah/api/nwuserendpoint/v1/nwusercollection/";
        String[] argBean=new String[7];
        Log.i("-->userBean",argBean.toString());
        try {
            email= URLEncoder.encode(email, "UTF-8");	//"realtor1%40aaa.com";
            URL url = new URL(urlstr + email);
            String line;
            String id="", userEmail="", userPass, userApps, userNick, userEtc, regDate;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            //
            JSONObject json=new JSONObject(sb.toString());
            Log.i("--->sb",String.format("sb=%s, json=%s",sb.toString(), json.toString()));
            JSONArray ja = json.optJSONArray("items");
            if (ja == null) return null;
            for(int i=0;i<ja.length();i++) {
                JSONObject obj = ja.getJSONObject(i);
                argBean[0]=id = obj.getString("id")==null?"":obj.getString("id");
                argBean[1]=userEmail = obj.getString("userEmail")==null?"":obj.getString("userEmail");
                argBean[2]=userPass = obj.getString("userPass")==null?"":obj.getString("userPass");
                argBean[3]=userApps=obj.getString("userApps")==null?"":obj.getString("userApps");
                argBean[4]=userNick = obj.getString("userNick")==null?"":obj.getString("userNick");
                argBean[5]=userEtc = obj.getString("userEtc")==null?"":obj.getString("userEtc");
                //argBean[6]=regDate = obj.getString("regDate")==null?"":obj.getString("regDate");
                Log.i("jsonResult", id+ userEmail+ userPass+ userApps+ userNick+ userEtc);
            }
            Log.i("-->userBean",argBean.toString());
            return argBean;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return argBean;
    }
    public static void putUpdateUser(String[] args) {
        //PUT should be used...
        String urlstr="https://nwsoftengine.appspot.com/_ah/api/nwuserendpoint/v1/nwuser";
        String id=args[0];
        String email=args[1]; String db=args[1].split("@")[1];
        String pass=args[2];
        String apps=args[3];
        String nick=args[4];
        String etc=args[5];
        String date=args[6];
        //POST should be used...
        Log.e("InLogin: ", email);
        String payload="{" +
                "\"id\":\"" +id+ "\"," +
                "\"userEmail\":\"" +email+ "\"," +
                "\"userPass\":\"" +pass+ "\"," +
                "\"userApps\":\"" +apps+ "\"," +
                "\"userNick\":\"" +nick+ "\"," +
                "\"userEtc\":\"" +etc+ "\"," +
                "\"regDate\":\"" +date+ "\"" +
                "}";
        try {
            //no need to encode
            //payload = URLEncoder.encode(payload, "UTF-8");

            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("PUT");  //PUT important

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(payload);
            writer.close();
            System.err.println(conn.getResponseCode()+conn.getResponseMessage());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e("SUCCESS Update:", " Updated..");
                //listUser();
            } else {
                Log.e("FAIL Update:", " Not Updated..");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void postInsertUser(String[] argsNew) {
        String urlstr="https://nwsoftengine.appspot.com/_ah/api/nwuserendpoint/v1/nwuser";
        String id=argsNew[0];
        String email=argsNew[1]; String db=argsNew[1].split("@")[1];
        String pass=argsNew[2];
        String apps=argsNew[3];
        String nick=argsNew[4];
        String etc=argsNew[5];
        String date=argsNew[6];
        //POST should be used...
        Log.e("InLogin: ", email);
        String payload="{" +
                "\"userEmail\":\"" +email+ "\"," +
                "\"userPass\":\"" +pass+ "\"," +
                "\"userApps\":\"" +apps+ "\"," +
                "\"userNick\":\"" +nick+ "\"," +
                "\"userEtc\":\"" +etc+ "\"," +
                "\"regDate\":\"" +date+ "\"" +
                "}";
        try {
            //No need to encoding
            //payload = URLEncoder.encode(payload, "UTF-8");
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(payload);
            writer.flush();
            writer.close();
            System.err.println(conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e("-->SUCCESS Insert:", " Inserted..");
                //listUser();
            } else {
                Log.e("-->FAIL Insert:", " Not Inserted..");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            mBtnCancel.setVisibility(View.VISIBLE);
            mBtnOk.setVisibility(View.VISIBLE);
            txt_proceed.setVisibility(View.VISIBLE);
            //
            mStatus0.setText(mGmail);
            mStatus.setText(mFullName);
            mDetail.setText(R.string.lbl_googlesigninsuccess);

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            //mStatus.setVisibility(View.GONE);
            //mDetail.setVisibility(View.GONE);
            //mLo_nw.setVisibility(View.GONE);
            mStatus0.setText(getResources().getString(R.string.lbl_welcome));
            //mNetworkImageView.setImageURI(null);
            mNetworkImageView.setImageResource(R.drawable.circle_view_blank);
            mStatus.setText(getResources().getString(R.string.app_name));
            mDetail.setText(getResources().getString(R.string.lbl_appsubject));
            mBtnCancel.setVisibility(View.INVISIBLE);
            mBtnOk.setVisibility(View.INVISIBLE);
            txt_proceed.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
            case R.id.btn_ok:
                //add ManinWork user info to NwUser
                //userEmail, userPass, userApps, userNick, userEtc(fullname, mobile, farm, country),regdate
                String[] userBean = new String[7];
                userBean[0] = null;
                userBean[1]= mGmail;
                userBean[2]= "pass";
                userBean[3]= "ManinWork";
                userBean[4]= "NickName";
                String _fullName = ((EditText)findViewById(R.id.edt_fullname)).getText().toString();
                String _mobile = ((EditText)findViewById(R.id.edt_mobile)).getText().toString();
                String _farm = ((EditText)findViewById(R.id.edt_farmname)).getText().toString();
                String _country = ((TextView)findViewById(R.id.txt_country)).getText().toString();
                String userEtc= "{'fullname':'" + _fullName + "'" +
                        ", 'mobile':'" + _mobile + "'" +
                        ", 'farm':'" + _farm + "'" +
                        ", 'country':'" + _country +"'}";

                userBean[5]= userEtc;
                String compInfos = mGmail + _fullName + _mobile + _farm + _country;

                Calendar cal = Calendar.getInstance();
                String date=String.format("%04d-%02d-%02d",
                        cal.get(Calendar.YEAR) ,
                        cal.get(Calendar.MONTH) +1,
                        cal.get(Calendar.DAY_OF_MONTH));
                userBean[6]= date;
                Log.i("regdate", date);
                Log.i("comp", mSavInfos + "comp"+ compInfos);
                if (mSavInfos==null) {
                    //new UpsertNwUserAsyncTask().execute(userBean);
                } else if (mSavInfos.equals(compInfos)) {
                    //No changes occured...
                    Log.i("-->No changes", compInfos);
                } else {
                    Log.i("-->!changes", compInfos +"->"+ mSavInfos);
                    //new UpsertNwUserAsyncTask().execute(userBean);
                }
                setResult(SIGN_RESULT_CODE_OK);
                finish();
                break;
            case R.id.btn_cancel:
                setResult(SIGN_RESULT_CODE_CANCEL);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                setResult(SIGN_RESULT_CODE_CANCEL);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void netImageLoader(Uri uri) {
        ImageLoader mImageLoader;
        String url;
        if (uri == null) {
            url="https://lh3.googleusercontent.com/SjoFKmlgfdV2xvhkh868G26S2i2g3I1fsYIbwAwhGPVkTYWvQt7sVxPcpkmwddeBAv05=h80-rw";
        } else {
            url = uri.toString();
        }
        final String IMAGE_URL = url;
        //"http://developer.android.com/images/training/system-ui.png";
        // Get the ImageLoader through your singleton class.
        mImageLoader = MySingleton.getInstance(this).getImageLoader();

        // Set the URL of the image that should be loaded into this view, and
        // specify the ImageLoader that will be used to make the request.
        mNetworkImageView.setImageUrl(IMAGE_URL, mImageLoader);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}