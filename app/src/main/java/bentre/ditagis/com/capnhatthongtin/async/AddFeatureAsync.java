package bentre.ditagis.com.capnhatthongtin.async;

import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.DAlertDialog;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;

public class AddFeatureAsync extends AsyncTask<Point, Object, Void> {
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication mApplication;
    private ServiceFeatureTable sft_CSKDLayer, sft_CSKDTable;
    private LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

    public AddFeatureAsync(MainActivity mainActivity, MapView mapView) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.mApplication = (DApplication) mainActivity.getApplication();
        this.sft_CSKDTable = (ServiceFeatureTable) this.mApplication.getTable_CoSoKinhDoanhChuaCapNhat().getFeatureTable();
        this.sft_CSKDLayer = (ServiceFeatureTable) this.mApplication.getLayer_CoSoKinhDoanhDTG().getFeatureTable();


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mApplication.getProgressDialog().show(mainActivity, mainActivity.getmRootView(), "Đang cập nhật CSKD...");
    }

    @Override
    protected Void doInBackground(Point... params) {
        Point clickPoint = params[0];
        addFeatureAsync(clickPoint);
        return null;
    }
    private void notifyWrongLocation() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.data_wrong_location), false);

    }

    private void notifyError() {
        MySnackBar.make(mapView, mainActivity.getString(R.string.error_occurred_notify), false);

    }
    private void addFeatureAsync(Point clickPoint) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setGeometry(clickPoint);
        final ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = this.mApplication.getSft_HanhChinhXa().queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = featureQueryResultListenableFuture.get();
                    Iterator<Feature> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        Feature feature = iterator.next();
                        if(feature != null){
                            String maHuyen = feature.getAttributes().get(Constant.HanhChinhFields.mahuyentp).toString();
                            String maXa = feature.getAttributes().get(Constant.HanhChinhFields.maxa).toString();
                            final Feature featureAdd = sft_CSKDLayer.createFeature();
                            featureAdd.setGeometry(clickPoint);
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, maHuyen);
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, maXa);

                            double[] logLat = pointToLogLat(clickPoint);
//                            updateCSKDTable(logLat);

                            Feature featureTBL = mApplication.getSelectedFeatureTBL();
                            Map<String, Object> attributes = featureTBL.getAttributes();
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.MaKinhDoanh, attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
//                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, attributes.get(Constant.CSKDTableFields.MaPhuongXa).toString());
//                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, attributes.get(Constant.CSKDTableFields.MaHuyenTP).toString());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.TenDoanhNghiep, attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.DiaChi, attributes.get(Constant.CSKDTableFields.DiaChi).toString());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.DienThoai, attributes.get(Constant.CSKDTableFields.DienThoai).toString());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.NguoiTao, mApplication.getUser().getUserName());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.TG_TAO, Calendar.getInstance());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.TGCAP_NHAT, Calendar.getInstance());
                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.NGUOI_CAP_NHAT, mApplication.getUser().getUserName());
//                            setAttributesLayer(featureAdd);
                            applyEditsAsync(featureAdd, logLat);
//                            Feature featureTBL = mApplication.getSelectedFeatureTBL();
//                            if (featureTBL == null) {
//
//
//                                final ListenableFuture<List<GeocodeResult>> listListenableFuture = loc.reverseGeocodeAsync(clickPoint);
//                                listListenableFuture.addDoneListener(() -> {
//                                    try {
//                                        List<GeocodeResult> geocodeResults = listListenableFuture.get();
//                                        if (geocodeResults.size() > 0) {
//                                            GeocodeResult geocodeResult = geocodeResults.get(0);
//                                            Map<String, Object> attrs = new HashMap<>();
//                                            for (String key : geocodeResult.getAttributes().keySet()) {
//                                                attrs.put(key, geocodeResult.getAttributes().get(key));
//                                            }
//                                            String address = geocodeResult.getAttributes().get("LongLabel").toString();
//                                            featureAdd.getAttributes().put(Constant.CSKDLayerFields.DiaChi, address);
//                                            applyEditsAsync(featureAdd, null);
//                                        }
//                                    } catch (InterruptedException e1) {
//                                        notifyError();
//                                        e1.printStackTrace();
//                                    } catch (ExecutionException e1) {
//                                        notifyError();
//                                        e1.printStackTrace();
//                                    }
//                                });
//                            }
                        }
                        else {
                            notifyWrongLocation();
                        }
                    }
                    else {
                        notifyWrongLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAttributesLayer(Feature feature) {
        Feature selectedFeatureTBL = mApplication.getSelectedFeatureTBL();
        Map<String, Object> attributes = selectedFeatureTBL.getAttributes();
        feature.getAttributes().put(Constant.CSKDLayerFields.MaKinhDoanh, attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, attributes.get(Constant.CSKDTableFields.MaPhuongXa).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, attributes.get(Constant.CSKDTableFields.MaHuyenTP).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.TenDoanhNghiep, attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.DiaChi, attributes.get(Constant.CSKDTableFields.DiaChi).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.DienThoai, attributes.get(Constant.CSKDTableFields.DienThoai).toString());
        feature.getAttributes().put(Constant.CSKDLayerFields.NguoiTao, mApplication.getUser().getUserName());
        feature.getAttributes().put(Constant.CSKDLayerFields.TG_TAO, Calendar.getInstance());
        feature.getAttributes().put(Constant.CSKDLayerFields.TGCAP_NHAT, Calendar.getInstance());
        feature.getAttributes().put(Constant.CSKDLayerFields.NGUOI_CAP_NHAT, mApplication.getUser().getUserName());
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
                        List<FeatureEditResult> featureEditResults = null;
                        try {
                            featureEditResults = listListenableEditAsync.get();
                            if (featureEditResults.size() > 0) {

                                if (logLat != null) {
                                    updateCSKDTable(logLat);
                                }
//                            } else {
//                                notifyCantInsert();
//                            }
                                else publishProgress();
                            }
                        } catch (InterruptedException e) {
                            publishProgress();
                        } catch (ExecutionException e) {
                            publishProgress();
                        }

                    }
                });
            }
        });

    }
    public void updateCSKDTable(double[] logLat) {
        Feature selectedFeatureTBL = mApplication.getSelectedFeatureTBL();
        if (selectedFeatureTBL == null) return;
        String toaDoX = "";
        String toaDoY = "";
        if (logLat != null) {
            toaDoX = String.valueOf(logLat[1]);
            toaDoY = String.valueOf(logLat[0]);
        }
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.X, toaDoX);
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.Y, toaDoY);
        Calendar c = Calendar.getInstance();
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.TGCAP_NHAT, c);
        ListenableFuture<Void> mapViewResult = sft_CSKDTable.updateFeatureAsync(selectedFeatureTBL);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDTable.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        mApplication.setSelectedFeatureTBL(null);
                        publishProgress(true);
                    } else publishProgress();
                } catch (InterruptedException e) {
                    MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
                    publishProgress();
                } catch (ExecutionException e) {
                    MySnackBar.make(mapView, mainActivity.getString(R.string.data_cant_add), false);
                    publishProgress();
                }

            });
        });
    }
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        mApplication.getProgressDialog().dismiss();
        if (values != null && values.length > 0 && values[0] instanceof Boolean && (Boolean) values[0])
            new DAlertDialog().show(mainActivity, "Cập nhật thành công");
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}
