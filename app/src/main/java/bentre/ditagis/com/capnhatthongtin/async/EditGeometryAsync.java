package bentre.ditagis.com.capnhatthongtin.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

public class EditGeometryAsync extends AsyncTask<Point, Object, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication mApplication;
    private ServiceFeatureTable sft_CSKDLayer;
    private LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private Feature mFeatureEdit;

    public EditGeometryAsync(MainActivity mainActivity, MapView mapView, AsyncResponse asyncResponse) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.mApplication = (DApplication) mainActivity.getApplication();
        this.sft_CSKDLayer = (ServiceFeatureTable) this.mApplication.getLayer_CoSoKinhDoanhDTG().getFeatureTable();
        this.delegate = asyncResponse;
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        mFeatureEdit = mApplication.getSelectedFeatureLYR();
    }
    public interface AsyncResponse {
        void processFinish(Object o);
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

    private void editFeatureAsync(Point clickPoint) {
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
                        Feature featurePhuongXa = iterator.next();
                        if (featurePhuongXa != null) {
                            String maHuyen = featurePhuongXa.getAttributes().get(Constant.HanhChinhFields.mahuyentp).toString();
                            String maXa = featurePhuongXa.getAttributes().get(Constant.HanhChinhFields.maxa).toString();
                            mFeatureEdit.setGeometry(clickPoint);
                            mFeatureEdit.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, maHuyen);
                            mFeatureEdit.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, maXa);
                            Calendar c = Calendar.getInstance();
                            mFeatureEdit.getAttributes().put(Constant.CSKDLayerFields.TGCAP_NHAT, c);
                            mFeatureEdit.getAttributes().put(Constant.CSKDLayerFields.NGUOI_CAP_NHAT, mApplication.getUser().getUserName());
                            Object maKinhDoanh = mFeatureEdit.getAttributes().get(Constant.CSKDLayerFields.MaKinhDoanh);
                            if(maKinhDoanh != null){
                                applyEditsAsync(mFeatureEdit);
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
                                            mFeatureEdit.getAttributes().put(Constant.CSKDLayerFields.DiaChi, address);
                                            applyEditsAsync(mFeatureEdit);
                                        } else {
                                            publishProgress(false);
                                        }
                                    } catch (InterruptedException e1) {
                                        publishProgress(e1.toString());
                                    } catch (Exception e1) {
                                        publishProgress(e1.toString());
                                    }
                                });
                            }
                        }
                        else {
                            publishProgress("Vị trí này không thuộc vùng quản lý!");
                        }
                    }
                    else {
                        publishProgress("Vị trí này không thuộc vùng quản lý!");
                    }
                } catch (InterruptedException e) {
                    publishProgress(e.toString());
                } catch (ExecutionException e) {
                    publishProgress(e.toString());
                }
            }
        });
    }
    private void applyEditsAsync(Feature feature) {
        ListenableFuture<Void> mapViewResult = sft_CSKDLayer.updateFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDLayer.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        updateCSKDTable();
                    } else {
                        publishProgress(false);
                    }
                } catch (InterruptedException e) {
                    publishProgress(e.toString());
                } catch (Exception e) {
                    publishProgress(e.toString());
                }

            });
        });

    }

    public void updateCSKDTable() {
        Feature selectedFeatureTBL = mApplication.getSelectedFeatureTBL();
        if (selectedFeatureTBL == null) return;
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.X, "Đã cập nhật");
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.Y, "Đã cập nhật");
        Calendar c = Calendar.getInstance();
        selectedFeatureTBL.getAttributes().put(Constant.CSKDTableFields.TGCAP_NHAT, c);
        ServiceFeatureTable sft_CSKDTable = (ServiceFeatureTable) mApplication.getTable_CoSoKinhDoanhChuaCapNhat().getFeatureTable();
        ListenableFuture<Void> mapViewResult = sft_CSKDTable.updateFeatureAsync(selectedFeatureTBL);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = sft_CSKDTable.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        mApplication.setSelectedFeatureTBL(null);
                        publishProgress(true);
                    } else {
                        publishProgress(false);
                    }
                } catch (InterruptedException e) {
                    publishProgress(e.toString());
                } catch (ExecutionException e) {
                    publishProgress(e.toString());
                }

            });
        });
    }
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        delegate.processFinish(values[0]);
    }


    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);

    }

}
