package bentre.ditagis.com.capnhatthongtin.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;

public class SingleTapMapViewAsync extends AsyncTask<Point, Object, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication mApplication;
    private FeatureLayer featureLayer;
    private Popup popupInfos;
    public SingleTapMapViewAsync(MainActivity mainActivity,MapView mapView,Popup popupInfos) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.popupInfos = popupInfos;
        this.mApplication = (DApplication) mainActivity.getApplication();
        this.featureLayer = mApplication.getLayer_CoSoKinhDoanhDTG();
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xác định CSKD...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(android.graphics.Point... params) {
        final android.graphics.Point clickPoint = params[0];
        @SuppressLint("WrongThread") final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(featureLayer, clickPoint, 5, false, 1);
        identifyFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyLayerResult layerResult = identifyFuture.get();
                    List<GeoElement> resultGeoElements = layerResult.getElements();
                    if (resultGeoElements.size() > 0) {
                        if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                            ArcGISFeature feature = (ArcGISFeature) resultGeoElements.get(0);
                            getSelectedFeature(feature);
                        } else publishProgress();
                    } else {
                        publishProgress();
                    }
                    publishProgress();
                } catch (Exception e) {
                    Log.e(mainActivity.getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                    publishProgress();
                }
            }
        });
        return null;
    }

    public void getSelectedFeature(Feature item) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = String.format("%s = '%s'", Constant.CSKDTableFields.MaKinhDoanh, item.getAttributes().get(Constant.CSKDLayerFields.MaKinhDoanh));
        queryParameters.setWhereClause(query);
        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) mApplication.getTable_CoSoKinhDoanhChuaCapNhat().getFeatureTable();
        final ListenableFuture<FeatureQueryResult> feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature featureTBL = result.iterator().next();
                    mApplication.setSelectedFeatureTBL(featureTBL);

                } else {
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
            }
            publishProgress(item);
        });

    }
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (values != null && values.length > 0 && values[0] != null && values[0] instanceof Feature)
            popupInfos.showPopup((ArcGISFeature) values[0]);
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}