package bentre.ditagis.com.capnhatthongtin.async;

import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryHanhChinhsAsync extends AsyncTask<Void, ArrayList<Feature>, Void> {
    public interface AsyncResponse {
        void processFinish(ArrayList<Feature> output);
    }

    public AsyncResponse delegate = null;

    private Context mContext;
    private ServiceFeatureTable mServiceFeatureTable;
    public  ArrayList<Feature> features;

    public QueryHanhChinhsAsync(Context context, ServiceFeatureTable serviceFeatureTable, AsyncResponse delegate) {
        mContext = context;
        mServiceFeatureTable = serviceFeatureTable;
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "1=1";
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = featureQueryResultListenableFuture.get();
                    Iterator<Feature> iterator = result.iterator();
                    features = new ArrayList<>();
                    while (iterator.hasNext()) {
                        Feature feature = iterator.next();
                        features.add(feature);
                    }
                    publishProgress(features);
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
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onProgressUpdate(ArrayList... values) {
        super.onProgressUpdate(values);
        delegate.processFinish(values[0]);

    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}


