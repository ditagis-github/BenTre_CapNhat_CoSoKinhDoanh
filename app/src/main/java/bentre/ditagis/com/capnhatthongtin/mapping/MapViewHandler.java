package bentre.ditagis.com.capnhatthongtin.mapping;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {

    private final FeatureLayer featureLayer;
    private MapView mMapView;

    private Popup popupInfos;
    private MainActivity mainActivity;
    private DApplication mDApplication;
    private ServiceFeatureTable sft_CSKDTable;
    private ServiceFeatureTable sft_CSKDLayer;

    public MapViewHandler(MapView mMapView, MainActivity mainActivity) {
        this.mMapView = mMapView;
        this.mainActivity = mainActivity;
        this.mDApplication = (DApplication) mainActivity.getApplication();
        this.featureLayer = mDApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer();
        this.sft_CSKDTable = (ServiceFeatureTable) mDApplication.getTable_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
        this.sft_CSKDLayer = (ServiceFeatureTable) mDApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
    }

    public void setPopupInfos(Popup popupInfos) {
        this.popupInfos = popupInfos;
    }


    public void addFeature() {
        SingleTapAdddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAdddFeatureAsync();
        Point add_point = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        singleTapAdddFeatureAsync.execute(add_point);
    }

    public void updateCSKDTable(double[] logLat) {
        Feature selectedFeatureTBL = mDApplication.getSelectedFeatureTBL();
        String toaDoX = "";
        String toaDoY = "";
        if(logLat != null){
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
                        mDApplication.setSelectedFeatureTBL(null);
                    }
                } catch (InterruptedException e) {
                    MySnackBar.make(mMapView, mainActivity.getString(R.string.data_cant_add), false);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    MySnackBar.make(mMapView, mainActivity.getString(R.string.data_cant_add), false);
                    e.printStackTrace();
                }

            });
        });
    }
    public double[] pointToLogLat(Point point) {
        Geometry project = GeometryEngine.project(point, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        android.graphics.Point mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync();
        singleTapMapViewAsync.execute(mClickPoint);
    }
    public void queryByMaKinhDoanh(String maKinhDoanh) {
        final QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        builder.append("MaKinhDoanh like N'%" + maKinhDoanh + "%'");
        queryParameters.setWhereClause(builder.toString());
        queryParameters.setReturnGeometry(true);
        queryFeaturesAsync(queryParameters);
    }

    public void queryByObjectID(String objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = sft_CSKDLayer.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    popupInfos.showPopup((ArcGISFeature) item);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
    private void queryFeaturesAsync(QueryParameters queryParameters){
        final ListenableFuture<FeatureQueryResult> feature = sft_CSKDLayer.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    popupInfos.showPopup((ArcGISFeature) item);
                    Geometry geometry = item.getGeometry();
                    if(geometry != null){
                        Point center = geometry.getExtent().getCenter();
                        double[] logLat = pointToLogLat(center);
                        updateCSKDTable(logLat);
                    }
                    mDApplication.getMainActivity().addFeatureClose();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
    public void querySearch(String searchStr, final TableCoSoKinhDoanhAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        for (Field field : sft_CSKDLayer.getFields()) {
            switch (field.getFieldType()) {
                case OID:
                case INTEGER:
                case SHORT:
                    try {
                        int search = Integer.parseInt(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {
                    }
                    break;
                case FLOAT:
                case DOUBLE:
                    try {
                        double search = Double.parseDouble(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {
                    }
                    break;
                case TEXT:
                    builder.append(field.getName() + " like N'%" + searchStr + "%'");
                    builder.append(" or ");
                    break;
            }
        }
        builder.append(" 1 = 2 ");
        queryParameters.setWhereClause(builder.toString());
        queryParameters.setMaxFeatures(100);
        queryAsync(queryParameters, adapter);

    }

    private void queryAsync(QueryParameters queryParameters, TableCoSoKinhDoanhAdapter adapter) {
        final ListenableFuture<FeatureQueryResult> feature = sft_CSKDLayer.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    Iterator iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        Map<String, Object> attributes = feature.getAttributes();
                        TableCoSoKinhDoanhAdapter.Item item = new TableCoSoKinhDoanhAdapter.Item();
                        item.setObjectID(attributes.get(Constant.OBJECTID).toString());
                        if (attributes.get(Constant.CSKDLayerFields.MaKinhDoanh) != null) {
                            item.setMaKinhDoanh(attributes.get(Constant.CSKDLayerFields.MaKinhDoanh).toString());
                        }
                        if (attributes.get(Constant.CSKDLayerFields.TenDoanhNghiep) != null) {
                            item.setTenDoanhNghiep(attributes.get(Constant.CSKDLayerFields.TenDoanhNghiep).toString());
                        }
                        if (attributes.get(Constant.CSKDLayerFields.DiaChi) != null) {
                            item.setDiaChi(attributes.get(Constant.CSKDLayerFields.DiaChi).toString());
                        }
                        adapter.add(item);
                        adapter.notifyDataSetChanged();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class SingleTapMapViewAsync extends AsyncTask<android.graphics.Point, Void, Void> {
        private ProgressDialog mDialog;

        public SingleTapMapViewAsync() {
            mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage(mainActivity.getString(R.string.processing));
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(android.graphics.Point... params) {
            final android.graphics.Point clickPoint = params[0];
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(featureLayer, clickPoint, 5, false, 1);
            identifyFuture.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        IdentifyLayerResult layerResult = identifyFuture.get();
                        List<GeoElement> resultGeoElements = layerResult.getElements();
                        if (resultGeoElements.size() > 0) {
                            if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                ArcGISFeature feature = (ArcGISFeature) resultGeoElements.get(0);
                                popupInfos.showPopup(feature);
                            }
                        } else {
                            popupInfos.dimissCallout();
                        }
                        publishProgress();
                    } catch (Exception e) {
                        Log.e(mainActivity.getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                    }
                }
            });
            return null;
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

    class SingleTapAdddFeatureAsync extends AsyncTask<Point, Void, Void> {
        private ProgressDialog mDialog;


        public SingleTapAdddFeatureAsync() {
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
            MySnackBar.make(mMapView, mainActivity.getString(R.string.data_cant_add), false);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }

        private void notifyError() {
            MySnackBar.make(mMapView, mainActivity.getString(R.string.error_occurred_notify), false);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }

        private void setAttributesLayer(Feature feature) {
            Feature selectedFeatureTBL = mDApplication.getSelectedFeatureTBL();
            Map<String, Object> attributes = selectedFeatureTBL.getAttributes();
            feature.getAttributes().put(Constant.CSKDLayerFields.MaKinhDoanh, attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, attributes.get(Constant.CSKDTableFields.MaPhuongXa).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, attributes.get(Constant.CSKDTableFields.MaHuyenTP).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.TenDoanhNghiep, attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.DiaChi, attributes.get(Constant.CSKDTableFields.DiaChi).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.DienThoai, attributes.get(Constant.CSKDTableFields.DienThoai).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.GhiChu, mDApplication.getUser().getUserName().toString());
        }

        private void addFeatureAsync(Point clickPoint) {
            double[] logLat = pointToLogLat(clickPoint);
            final Feature feature = sft_CSKDLayer.createFeature();
            feature.setGeometry(clickPoint);
            Calendar c = Calendar.getInstance();
            feature.getAttributes().put(Constant.TGCAP_NHAT, c);
            setAttributesLayer(feature);
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
                                    updateCSKDTable(logLat);
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



        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
}