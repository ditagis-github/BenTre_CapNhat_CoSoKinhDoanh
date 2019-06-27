package bentre.ditagis.com.capnhatthongtin.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.TraCuuActivity;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryTableCoSoKinhDoanhAsync extends AsyncTask<String, List<TableCoSoKinhDoanhAdapter.Item>, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable serviceFeatureTable;
    private TableCoSoKinhDoanhAdapter tableCoSoKinhDoanhAdapter;
    private TextView txtTongItem;

    public QueryTableCoSoKinhDoanhAsync(TraCuuActivity traCuuActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, TableCoSoKinhDoanhAdapter adapter, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = traCuuActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.tableCoSoKinhDoanhAdapter = adapter;
        this.txtTongItem = txtTongItem;
        dialog = new ProgressDialog(traCuuActivity, android.R.style.Theme_Material_Dialog_Alert);
    }


    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<TableCoSoKinhDoanhAdapter.Item> items = new ArrayList<>();
        final List<Feature> features = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = params[0];
        queryParameters.setWhereClause(queryClause);
        queryParameters.setMaxFeatures(100);
        QueryParameters.OrderBy orderBy = new QueryParameters.OrderBy("TGCapNhat", QueryParameters.SortOrder.DESCENDING);
        queryParameters.getOrderByFields().add(orderBy);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = queryResultListenableFuture.get();
                    Iterator iterator = result.iterator();

                    while (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        TableCoSoKinhDoanhAdapter.Item item = new TableCoSoKinhDoanhAdapter.Item();
                        Map<String, Object> attributes = feature.getAttributes();
                        item.setObjectID(attributes.get(Constant.OBJECTID).toString());
                        item.setMaKinhDoanh(attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
                        item.setTenDoanhNghiep(attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
                        item.setToaDoX(attributes.get(Constant.CSKDTableFields.X).toString());
                        item.setToaDoY(attributes.get(Constant.CSKDTableFields.Y).toString());
                        item.setDiaChi(attributes.get(Constant.CSKDTableFields.DiaChi).toString());
                        items.add(item);
                        features.add(feature);
                    }
                    delegate.processFinish(features);
                    publishProgress(items);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(List<TableCoSoKinhDoanhAdapter.Item>... values) {
        tableCoSoKinhDoanhAdapter.clear();
        tableCoSoKinhDoanhAdapter.setItems(values[0]);
        tableCoSoKinhDoanhAdapter.notifyDataSetChanged();
        if (txtTongItem != null)
            txtTongItem.setText(mContext.getString(R.string.nav_tongsoluong) + values[0].size());
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        super.onProgressUpdate(values);

    }

    private String getValueAttributes(Feature feature, String fieldName) {
        if (feature.getAttributes().get(fieldName) != null)
            return feature.getAttributes().get(fieldName).toString();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog != null || dialog.isShowing()) dialog.dismiss();
        super.onPostExecute(result);

    }

}

