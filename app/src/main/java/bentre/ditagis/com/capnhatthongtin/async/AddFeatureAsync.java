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

public class AddFeatureAsync extends AsyncTask<Point, Void, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication dApplication;
    private ServiceFeatureTable sft_CSKDLayer, sft_CSKDTable;
    private LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

    public AddFeatureAsync(MainActivity mainActivity, MapView mapView) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.sft_CSKDTable = (ServiceFeatureTable) this.dApplication.getTable_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
        this.sft_CSKDLayer = (ServiceFeatureTable) this.dApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();

        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);

    }

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
        addFeatureAsync(clickPoint);
        return null;
    }

    private void notifyCantInsert() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

    private void notifyError() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.error_occurred_notify), false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

    private void setAttributesLayer(Feature feature) {
        Feature selectedFeatureTBL = dApplication.getSelectedFeatureTBL();
        Map<String, Object> attributes = selectedFeatureTBL.getAttributes();
        feature.getAttributes().put(Constant.CSKDLayerFields.MaKinhDoanh, attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, attributes.get(Constant.CSKDTableFields.MaPhuongXa).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, attributes.get(Constant.CSKDTableFields.MaHuyenTP).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.TenDoanhNghiep, attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.DiaChi, attributes.get(Constant.CSKDTableFields.DiaChi).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.DienThoai, attributes.get(Constant.CSKDTableFields.DienThoai).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.GhiChu, dApplication.getUser().getUserName().toString());
    }

    private void addFeatureAsync(Point clickPoint) {
        final Feature feature = sft_CSKDLayer.createFeature();
        feature.setGeometry(clickPoint);
        Calendar c = Calendar.getInstance();
        feature.getAttributes().put(Constant.TGCAP_NHAT, c);
        Feature featureTBL = dApplication.getSelectedFeatureTBL();
        if (featureTBL != null) {
            double[] logLat = pointToLogLat(clickPoint);
            setAttributesLayer(feature);
            applyEditsAsync(feature, logLat);
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
                        feature.getAttributes().put(Constant.CSKDLayerFields.DiaChi, address);
                        applyEditsAsync(feature, null);
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

    private double[] pointToLogLat(Point point) {
        Geometry project = GeometryEngine.project(point, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        return location;
    }

    private void applyEditsAsync(Feature feature, double[] logLat) {
        ListenableFuture<Void> mapViewResult = sft_CSKDLayer.addFeatureAsync(feature);
        mapViewResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDLayer.applyEditsAsync();
                listListenableEditAsync.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                            if (featureEditResults.size() > 0) {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                                if (logLat != null) {
                                    updateCSKDTable(logLat);
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

                    }
                });
            }
        });

    }
    public void updateCSKDTable(double[] logLat) {
        Feature selectedFeatureTBL = dApplication.getSelectedFeatureTBL();
        if (selectedFeatureTBL == null) return;
        String toaDoX = "";
        String toaDoY = "";
        if (logLat != null) {
            toaDoX = String.valueOf(logLat[1]);
            toaDoY = String.valueOf(logLat[0]);
        }
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.X, toaDoX);
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.Y, toaDoY);
        ListenableFuture<Void> mapViewResult = sft_CSKDTable.updateFeatureAsync(selectedFeatureTBL);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDTable.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        dApplication.setSelectedFeatureTBL(null);
                    }
                } catch (InterruptedException e) {
                    MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
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
        super.onPostExecute(result);

    }

}
