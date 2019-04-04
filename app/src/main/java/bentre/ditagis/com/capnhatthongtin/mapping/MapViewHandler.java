package bentre.ditagis.com.capnhatthongtin.mapping;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {

    private final ArcGISMap mMap;
    private final FeatureLayer suCoTanHoaLayer;
    LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private FeatureLayerDTG mFeatureLayerDTG;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup popupInfos;
    private MainActivity mainActivity;
    private DApplication mDApplication;

    public MapViewHandler(FeatureLayerDTG featureLayerDTG, MapView mMapView, MainActivity mainActivity) {
        this.mFeatureLayerDTG = featureLayerDTG;
        this.mMapView = mMapView;
        this.mServiceFeatureTable = (ServiceFeatureTable) featureLayerDTG.getFeatureLayer().getFeatureTable();
        this.mainActivity = mainActivity;
        this.mMap = mMapView.getMap();
        this.suCoTanHoaLayer = featureLayerDTG.getFeatureLayer();
        this.mDApplication = (DApplication) mainActivity.getApplication();
    }

    public void setPopupInfos(Popup popupInfos) {
        this.popupInfos = popupInfos;
    }

    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }

    public void addFeature() {
        SingleTapAdddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAdddFeatureAsync();
        Point add_point = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        singleTapAdddFeatureAsync.execute(add_point);
    }


    public double[] onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Point center = ((MapView) mMapView).getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
//        Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        if (isClickBtnAdd) {
            mMapView.setViewpointCenterAsync(clickPoint, 10);
        } else {
            mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
            mSelectedArcGISFeature = null;
            // get the point that was clicked and convert it to a point in map coordinates
            int tolerance = 10;
            double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
            // create objects required to do a selection with a query
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mMap.getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            // add done loading listener to fire when the selection returns

            SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync();
            singleTapMapViewAsync.execute(clickPoint);
        }
    }

    public void queryByObjectID(String objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    if (result.iterator().hasNext()) {
                        Feature item = result.iterator().next();
                        showPopup(item);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showPopup(Feature selectedFeature) {
        if (selectedFeature != null) {
            popupInfos.setFeatureLayerDTG(mFeatureLayerDTG);
            popupInfos.showPopup((ArcGISFeature) selectedFeature);
        }
    }

    public void querySearch(String searchStr, ListView listView, final TableCoSoKinhDoanhAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        for (Field field : mServiceFeatureTable.getFields()) {
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
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    Iterator iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Feature item = (Feature) iterator.next();
                        Map<String, Object> attributes = item.getAttributes();
                        String format_date = "";
                        String[] split = attributes.get(Constant.CSKDLayerFields.TenCSKD).toString().split("_");
                        try {
                            format_date = Constant.DATE_FORMAT.format((new GregorianCalendar(Integer.parseInt(split[3]), Integer.parseInt(split[2]), Integer.parseInt(split[1])).getTime()));
                        } catch (Exception e) {

                        }
                        String diachi = "";
                        try {
                            diachi = attributes.get(Constant.CSKDLayerFields.DiaChi).toString();
                        } catch (Exception e) {
                        }
                        TableCoSoKinhDoanhAdapter.Item diemdanhgia = new TableCoSoKinhDoanhAdapter.Item();
                        diemdanhgia.setObjectID(attributes.get(Constant.OBJECTID).toString());
                        diemdanhgia.setMaKinhDoanh(attributes.get(Constant.CSKDLayerFields.TenCSKD).toString());
                        diemdanhgia.setToaDoX(format_date);
                        diemdanhgia.setDiaChi(diachi);
                        adapter.add(diemdanhgia);
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

    class SingleTapMapViewAsync extends AsyncTask<Point, Void, Void> {
        private ProgressDialog mDialog;

        public SingleTapMapViewAsync() {
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
            final Point clickPoint = params[0];
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(suCoTanHoaLayer, mClickPoint, 5, false, 1);
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
                                mSelectedArcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                            }
                        } else {
                            mSelectedArcGISFeature = null;
                        }
                        publishProgress();
                    } catch (Exception e) {
                        Log.e(mainActivity.getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            popupInfos.setFeatureLayerDTG(mFeatureLayerDTG);
            if (mSelectedArcGISFeature != null) popupInfos.showPopup(mSelectedArcGISFeature);
            else popupInfos.dimissCallout();
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
            MySnackBar.make(mMapView, "Không thêm được dữ liệu", false);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }

        private void notifyError() {
            MySnackBar.make(mMapView, "Đã xảy ra lỗi", false);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }

        private void setAttributesLayer(Feature feature) {
            Feature selectedFeatureTBL = mDApplication.getSelectedFeatureTBL();
            Map<String, Object> attributes = selectedFeatureTBL.getAttributes();
            feature.getAttributes().put(Constant.CSKDLayerFields.MaCSKD, attributes.get(Constant.CSKDTableFields.MaKinhDoanh).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.MaPhuongXa, attributes.get(Constant.CSKDTableFields.MaPhuongXa).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.MaHuyenTP, attributes.get(Constant.CSKDTableFields.MaHuyenTP).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.TenCSKD, attributes.get(Constant.CSKDTableFields.TenDoanhNghiep).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.DiaChi, attributes.get(Constant.CSKDTableFields.DiaChi).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.ChuSoHuu, attributes.get(Constant.CSKDTableFields.ChuSoHuu).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.NguoiDaiDien, attributes.get(Constant.CSKDTableFields.NguoiDaiDien).toString());
            feature.getAttributes().put(Constant.CSKDLayerFields.SoDienThoai, attributes.get(Constant.CSKDTableFields.DienThoai).toString());
        }

        private void addFeatureAsync(Point clickPoint) {
            final Feature feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(clickPoint);
            Calendar c = Calendar.getInstance();
            feature.getAttributes().put(Constant.TGCAP_NHAT, c);
            setAttributesLayer(feature);
            ListenableFuture<Void> mapViewResult = mServiceFeatureTable.addFeatureAsync(feature);
            mapViewResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mServiceFeatureTable.applyEditsAsync();
                    listListenableEditAsync.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                                if (featureEditResults.size() > 0) {
                                    long objectId = featureEditResults.get(0).getObjectId();
                                    final QueryParameters queryParameters = new QueryParameters();
                                    final String query = "OBJECTID = " + objectId;
                                    queryParameters.setWhereClause(query);
                                    final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
                                    feature.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mDialog != null && mDialog.isShowing()) {
                                                mDialog.dismiss();
                                            }
                                        }
                                    });
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