package bentre.ditagis.com.capnhatthongtin.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.TraCuuActivity;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryTableCoSoKinhDoanhAsync extends AsyncTask<String, List<Feature>, Void> {
    private Context mContext;
    private ServiceFeatureTable serviceFeatureTable;
    private TableCoSoKinhDoanhAdapter tableCoSoKinhDoanhAdapter;
    private TextView txtTongItem;
    private int mTop = 100;

    public QueryTableCoSoKinhDoanhAsync(TraCuuActivity traCuuActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, TableCoSoKinhDoanhAdapter adapter, AsyncResponse asyncResponse,
                                        int... top) {
        this.delegate = asyncResponse;
        mContext = traCuuActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.tableCoSoKinhDoanhAdapter = adapter;
        this.txtTongItem = txtTongItem;
        if (top.length > 0 && top[0] != 0)
            mTop = top[0];
    }


    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<Feature> items = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();

        String queryClause = params[0];
        queryParameters.setWhereClause(queryClause);

        queryParameters.setMaxFeatures(mTop);
        QueryParameters.OrderBy orderBy = new QueryParameters.OrderBy("TGCapNhat", QueryParameters.SortOrder.DESCENDING);
        queryParameters.getOrderByFields().add(orderBy);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = queryResultListenableFuture.get();

                    for (Object feature : result) {
                        if (feature instanceof Feature)
                            items.add((Feature) feature);
                    }
                    publishProgress(items);

                } catch (Exception e) {
                    publishProgress();
                }
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(List<Feature>... values) {
        tableCoSoKinhDoanhAdapter.clear();
        if (values != null && values.length > 0) {
            tableCoSoKinhDoanhAdapter.setItems(values[0]);
            if (txtTongItem != null)
                txtTongItem.setText(mContext.getString(R.string.nav_tongsoluong) + values[0].size());
            delegate.processFinish(values[0]);
        } else delegate.processFinish(null);
        tableCoSoKinhDoanhAdapter.notifyDataSetChanged();

        super.onProgressUpdate(values);

    }

    private String getValueAttributes(Feature feature, String fieldName) {
        if (feature.getAttributes().get(fieldName) != null)
            return feature.getAttributes().get(fieldName).toString();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);

    }

}

