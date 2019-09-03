package bentre.ditagis.com.capnhatthongtin.mapping;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.MotionEvent;
import android.widget.Toast;

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
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.DiaChiAdapter;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.async.AddFeatureAsync;
import bentre.ditagis.com.capnhatthongtin.async.EditFeatureAsync;
import bentre.ditagis.com.capnhatthongtin.async.FindLocationAsycn;
import bentre.ditagis.com.capnhatthongtin.async.SingleTapMapViewAsync;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.entities.DAddress;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {

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
        this.sft_CSKDTable = (ServiceFeatureTable) mDApplication.getTable_CoSoKinhDoanhChuaCapNhatDTG().getFeatureLayer().getFeatureTable();
        this.sft_CSKDLayer = (ServiceFeatureTable) mDApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
    }

    public void setPopupInfos(Popup popupInfos) {
        this.popupInfos = popupInfos;
    }

    public void editFeature() {
        Point editPoint = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        new EditFeatureAsync(mainActivity, mMapView, () -> {
        }).execute(editPoint);
    }

    public void addFeature() {
        Point add_point;
        AddFeatureAsync addFeatureAsync = new AddFeatureAsync(mainActivity, mMapView);
        add_point = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        addFeatureAsync.execute(add_point);
    }

    public void updateCSKDTable(double[] logLat) {
        Feature selectedFeatureTBL = mDApplication.getSelectedFeatureTBL();
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
        SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mainActivity, mMapView, popupInfos);
        singleTapMapViewAsync.execute(mClickPoint);
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

    public void queryByMaKinhDoanh(Feature item) {
        Object maKinhDoanh = item.getAttributes().get(Constant.CSKDTableFields.MaKinhDoanh);
        if (maKinhDoanh != null) {
            final QueryParameters queryParameters = new QueryParameters();
            StringBuilder builder = new StringBuilder();
            builder.append(Constant.CSKDLayerFields.MaKinhDoanh + " like N'%" + maKinhDoanh.toString() + "%'");
            queryParameters.setWhereClause(builder.toString());
            queryParameters.setReturnGeometry(true);
            queryFeaturesAsync(queryParameters, item);
        } else {
            Toast.makeText(mainActivity, "Không có mã kinh doanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryFeaturesAsync(QueryParameters queryParameters, Feature item) {
        final ListenableFuture<FeatureQueryResult> feature = sft_CSKDLayer.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                Object x = item.getAttributes().get(Constant.CSKDTableFields.X);
                Object y = item.getAttributes().get(Constant.CSKDTableFields.Y);
                if (result.iterator().hasNext()) {
                    Feature next = result.iterator().next();
                    popupInfos.showPopup((ArcGISFeature) next);
                    if (x == null || y == null || x.equals("") || y.equals("")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity)
                                .setTitle("Thông báo")
                                .setMessage("CSKD đã có!")
                                .setIcon(R.drawable.add)
                                .setPositiveButton("Cập nhật vị trí kinh doanh", (dialog, whichButton) -> {
                                    Geometry geometry = next.getGeometry();
                                    if (geometry != null) {
                                        Point center = geometry.getExtent().getCenter();
                                        double[] logLat = pointToLogLat(center);
                                        updateCSKDTable(logLat);
                                    }
                                    dialog.dismiss();
                                }).setCancelable(false)
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .create();
                        alertDialog.show();
                    }

                }
                else {
                    if (x == null || y == null || x.equals("") || y.equals("")) {
                        this.mainActivity.addFeature();
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity)
                                .setTitle("Thông báo")
                                .setMessage("Không tìm được CSKD đã thêm!")
                                .setIcon(R.drawable.add)
                                .setPositiveButton("Thêm lại", (dialog, whichButton) -> {
                                    mainActivity.addFeature();
                                    dialog.dismiss();
                                }).setCancelable(false)
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .create();
                        alertDialog.show();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void querySearchLayer(String searchStr, final TableCoSoKinhDoanhAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        String[] searchFields = mainActivity.getResources().getStringArray(R.array.searchFields);
        for (String searchField: searchFields) {
            for (Field field : sft_CSKDLayer.getFields()) {
                if(searchField.equals(field.getName())) {
                    if (field.getName().equals(Constant.CSKDLayerFields.TenDoanhNghiep)) {
                        builder.append("upper(").append(field.getName()).append(") like N'%")
                                .append(searchStr.toUpperCase()).append("%' or ");
                    } else {
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
                                builder.append(field.getName()).append(" LIKE N'%")
                                        .append(searchStr).append("%' or ");
                                break;
                        }
                    }
                    break;
                }
            }
        }
        // Query the feature table
        builder.append(" 1 = 2");
        queryParameters.setWhereClause(builder.toString());
        queryParameters.setMaxFeatures(100);
        queryAsync(queryParameters, adapter);

    }

    private void queryAsync(QueryParameters queryParameters, TableCoSoKinhDoanhAdapter adapter) {
        final ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture
                = sft_CSKDLayer.queryFeaturesAsync(queryParameters);
        featureQueryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = featureQueryResultListenableFuture.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature feature = (Feature) iterator.next();
                    adapter.add(feature);
                }
                if (adapter.getCount() > 0) {
                    adapter.notifyDataSetChanged();
                } else {
                    MySnackBar.make(mMapView, mainActivity.getString(R.string.data_not_found), false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void querySearchDiaChi(String search, DiaChiAdapter diaChiAdapter) {
        diaChiAdapter.clear();
        diaChiAdapter.notifyDataSetChanged();
        FindLocationAsycn findLocationAsycn = new FindLocationAsycn(mainActivity,
                true, output -> {
            if (output != null) {
                if (output.size() > 0) {
                    for (DAddress address : output) {
                        diaChiAdapter.add(address);
                        diaChiAdapter.notifyDataSetChanged();
                    }
                } else {
                    MySnackBar.make(mMapView, mainActivity.getString(R.string.data_not_found), false);
                }
            }

        });
        findLocationAsycn.execute(search);
    }

}