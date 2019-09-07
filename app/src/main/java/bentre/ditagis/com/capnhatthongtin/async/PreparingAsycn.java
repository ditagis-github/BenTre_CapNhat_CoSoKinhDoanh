package bentre.ditagis.com.capnhatthongtin.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.LayerInfoDTG;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.ListObjectDB;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.Preference;


public class PreparingAsycn extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Void output);
    }

    public PreparingAsycn(Context context, AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(Constant.API_URL.LAYER_INFO);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                pajsonRouteeJSon(buffer.toString());
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
//            ListFeatureLayerDTGDB listFeatureLayerDTGDB = new ListFeatureLayerDTGDB(mContext);
//            ListObjectDB.getInstance().setLstFeatureLayerDTG(listFeatureLayerDTGDB.find(Preference.getInstance().loadPreference(
//                    mContext.getString(R.string.preference_username)
//            )));
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {
        this.mDelegate.processFinish(value);
//        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data == null)
            return;
        String myData = "{ \"layerInfo\": ".concat(data).concat("}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("layerInfo");
        List<LayerInfoDTG> layerDTGS = new ArrayList<>();
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);


//           LayerInfoDTG layerInfoDTG = new LayerInfoDTG();
            layerDTGS.add(new LayerInfoDTG(jsonRoute.getString(mContext.getString(R.string.sql_coloumn_sys_id)),
                    jsonRoute.getString(mContext.getString(R.string.sql_coloumn_sys_title)),
                    jsonRoute.getString(mContext.getString(R.string.sql_coloumn_sys_url)),
                    jsonRoute.getBoolean(mContext.getString(R.string.sql_coloumn_sys_iscreate)), jsonRoute.getBoolean(mContext.getString(R.string.sql_coloumn_sys_isdelete)),
                    jsonRoute.getBoolean(mContext.getString(R.string.sql_coloumn_sys_isedit)), jsonRoute.getBoolean(mContext.getString(R.string.sql_coloumn_sys_isview)),
                    jsonRoute.getString(mContext.getString(R.string.sql_coloumn_sys_outfield)), jsonRoute.getString(mContext.getString(R.string.sql_coloumn_sys_definition))));


        }
        ListObjectDB.getInstance().setLstFeatureLayerDTG(layerDTGS);

    }

}
