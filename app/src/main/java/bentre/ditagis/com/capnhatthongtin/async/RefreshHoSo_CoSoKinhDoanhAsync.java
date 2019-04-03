package bentre.ditagis.com.capnhatthongtin.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.MauKiemNghiemApdapter;
import bentre.ditagis.com.capnhatthongtin.libs.Action;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class RefreshHoSo_CoSoKinhDoanhAsync extends AsyncTask<String, List<MauKiemNghiemApdapter.MauKiemNghiem>, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable table_thoigiancln;
    private MauKiemNghiemApdapter mauKiemNghiemApdapter;
    private Action action;

    public interface AsyncResponse {
        void processFinish(List<Feature> features, List<MauKiemNghiemApdapter.MauKiemNghiem> thoiGianChatLuongNuocs);
    }

    private AsyncResponse delegate = null;

    public RefreshHoSo_CoSoKinhDoanhAsync(Context context, ServiceFeatureTable table_thoigiancln, MauKiemNghiemApdapter mauKiemNghiemApdapter, Action action, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = context;
        this.table_thoigiancln = table_thoigiancln;
        this.mauKiemNghiemApdapter = mauKiemNghiemApdapter;
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        this.action = action;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<Feature> features = new ArrayList<>();
        final List<MauKiemNghiemApdapter.MauKiemNghiem> mauKiemNghiems = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = "IDDiemDanhGia = '" + params[0] + "'";
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = table_thoigiancln.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = queryResultListenableFuture.get();
                    Iterator iterator = result.iterator();

                    while (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        features.add(feature);
                        feature.getAttributes().get("OBJECTID");
                        MauKiemNghiemApdapter.MauKiemNghiem mauKiemNghiem = new MauKiemNghiemApdapter.MauKiemNghiem();
                        mauKiemNghiem.setOBJECTID(feature.getAttributes().get("OBJECTID").toString());
                        mauKiemNghiem.setIdMauKiemNghiem(getValueAttributes(feature, mContext.getString(R.string.IDMAUKIEMNGHIEM)));
                        mauKiemNghiem.setTenMau(getValueAttributes(feature, mContext.getString(R.string.TENMAU)));
                        mauKiemNghiem.setView(action.isView());
                        mauKiemNghiems.add(mauKiemNghiem);

                    }
                    delegate.processFinish(features, mauKiemNghiems);
                    publishProgress(mauKiemNghiems);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    private String getValueAttributes(Feature feature, String fieldName) {
        if (feature.getAttributes().get(fieldName) != null)
            return feature.getAttributes().get(fieldName).toString();
        return null;
    }


    @Override
    protected void onProgressUpdate(List<MauKiemNghiemApdapter.MauKiemNghiem>... values) {
        mauKiemNghiemApdapter.clear();
        mauKiemNghiemApdapter.setMauKiemNghiems(values[0]);
        mauKiemNghiemApdapter.notifyDataSetChanged();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

