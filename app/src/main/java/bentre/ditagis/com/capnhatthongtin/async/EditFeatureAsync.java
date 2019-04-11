package bentre.ditagis.com.capnhatthongtin.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;

public class EditFeatureAsync extends AsyncTask<Point, Void, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication dApplication;
    private ServiceFeatureTable sft_CSKDLayer;
    private LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

    public EditFeatureAsync(MainActivity mainActivity, MapView mapView,AsyncResponse asyncResponse) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.sft_CSKDLayer = (ServiceFeatureTable) this.dApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
        this.delegate = asyncResponse;
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);

    }
    public interface AsyncResponse {
        void processFinish();
    }
    private AsyncResponse delegate = null;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(Point... params) {
        Point clickPoint = params[0];
        editFeatureAsync(clickPoint);
        return null;
    }

    private void notifyCantInsert() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            delegate.processFinish();
        }

    }

    private void notifyError() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.error_occurred_notify), false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            delegate.processFinish();
        }

    }

    private void editFeatureAsync(Point clickPoint) {
        Feature featureLYR = dApplication.getSelectedFeatureLYR();
        featureLYR.setGeometry(clickPoint);
        Calendar c = Calendar.getInstance();
        featureLYR.getAttributes().put(Constant.TGCAP_NHAT, c);
        Object maKinhDoanh = featureLYR.getAttributes().get(Constant.CSKDLayerFields.MaKinhDoanh);
        if(maKinhDoanh != null){
            applyEditsAsync(featureLYR);
        } else {
            final ListenableFuture<List<GeocodeResult>> listListenableFuture = loc.reverseGeocodeAsync(clickPoint);
            listListenableFuture.addDoneListener(() -> {
                try {
                    List<GeocodeResult> geocodeResults = listListenableFuture.get();
                    if (geocodeResults.size() > 0) {
                        GeocodeResult geocodeResult = geocodeResults.get(0);
                        Map<String, Object> attrs = new HashMap<>();
                        for (String key : geocodeResult.getAttributes().keySet()) {
                            attrs.put(key, geocodeResult.getAttributes().get(key));
                        }
                        String address = geocodeResult.getAttributes().get("LongLabel").toString();
                        featureLYR.getAttributes().put(Constant.CSKDLayerFields.DiaChi, address);
                        applyEditsAsync(featureLYR);
                    }
                } catch (InterruptedException e1) {
                    notifyError();
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    notifyError();
                    e1.printStackTrace();
                }


            });
        }

    }
    private void applyEditsAsync(Feature feature) {
        ListenableFuture<Void> mapViewResult = sft_CSKDLayer.updateFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDLayer.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                            delegate.processFinish();
                        }
                    } else {
                        notifyCantInsert();
                    }
                } catch (InterruptedException e) {
                    notifyError();
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    notifyError();
                    e.printStackTrace();
                }

            });
        });

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Void result) {
        dApplication.getMainActivity().addFeatureClose();
        super.onPostExecute(result);

    }

}
