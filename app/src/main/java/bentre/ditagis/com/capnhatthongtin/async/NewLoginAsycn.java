package bentre.ditagis.com.capnhatthongtin.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.User;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.UserDangNhap;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.Preference;

public class NewLoginAsycn extends AsyncTask<String, Void, User> {
    private Exception exception;
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;
    private DApplication mApplication;

    public interface AsyncResponse {
        void processFinish(User output);
    }

    public NewLoginAsycn(Activity activity, AsyncResponse delegate) {
        this.mApplication = (DApplication) activity.getApplication();
        this.mContext = activity;
        this.mDelegate = delegate;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.connect_message));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected User doInBackground(String... params) {
        String userName = params[0];
        String pin = params[1];
//        String passEncoded = (new EncodeMD5()).encode(pin + "_DITAGIS");
        // Do some validation here
        String urlParameters = String.format("Username=%s&Password=%s", userName, pin);
        String urlWithParam = String.format("%s?%s", Constant.API_URL.LOGIN, urlParameters);
        try {
//            + "&apiKey=" + API_KEY
            URL url = new URL(urlWithParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("POST");
                JSONObject cred = new JSONObject();
                cred.put("Username", userName);
                cred.put("Password", pin);

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(cred.toString());
                wr.flush();
                conn.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    break;
                }
                bufferedReader.close();
                String token = stringBuilder.toString().replace("\"", "");
                Preference.getInstance().savePreferences(mContext.getString(R.string.preference_login_api),token);
                if (checkAccess()) {
                    UserDangNhap.getInstance().setUser(new User());
                    UserDangNhap.getInstance().getUser().setDisplayName(getDisplayName());
                    conn.disconnect();
                    return UserDangNhap.getInstance().getUser();
                } else {
                    conn.disconnect();
                    return null;
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }


    @Override
    protected void onPostExecute(User user) {
//        if (user != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(user);
//        }
    }

    private Boolean checkAccess() {
        boolean isAccess = false;
        try {
            URL url = new URL(Constant.API_URL.IS_ACCESS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = bufferedReader.readLine();
                if (line.equals("true"))
                    isAccess = true;

            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            return isAccess;
        }
    }

    private String getDisplayName() {
        String displayName = "";
        try {
            URL url = new URL(Constant.API_URL.DISPLAY_NAME);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    pajsonRouteeJSon(line);
                    break;
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            return displayName;
        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data != null) {
            String myData = "{ \"account\": [".concat(data).concat("]}");
            JSONObject jsonData = new JSONObject(myData);
            JSONArray jsonRoutes = jsonData.getJSONArray("account");
//        jsonData.getJSONArray("account");
            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                String displayName = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_displayname));
                String username = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_username));
                UserDangNhap.getInstance().setUser(new User());
                UserDangNhap.getInstance().getUser().setDisplayName(displayName);
                UserDangNhap.getInstance().getUser().setUserName(username);
            }
        }
    }
}