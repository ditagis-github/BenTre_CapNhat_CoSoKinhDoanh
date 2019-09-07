package bentre.ditagis.com.capnhatthongtin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.util.ListenableList;

import java.util.ArrayList;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.adapter.DiaChiAdapter;
import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.async.PreparingAsycn;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.entities.DAddress;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.LayerInfoDTG;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.ListObjectDB;
import bentre.ditagis.com.capnhatthongtin.libs.Action;
import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewAddDoneLoadingListener;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewHandler;
import bentre.ditagis.com.capnhatthongtin.utities.CheckConnectInternet;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.DAlertDialog;
import bentre.ditagis.com.capnhatthongtin.utities.DProgressDialog;
import bentre.ditagis.com.capnhatthongtin.utities.LocationHelper;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Uri mUri;
    private Popup mPopupInfos;
    private MapView mMapView;
    private ArcGISMap mMap;
    private Callout mCallout;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private MapViewHandler mMapViewHandler;
    private static double LATITUDE = 10.1809655;//10.3952832;
    private static double LONGTITUDE = 106.4011284;//106.380246;
    private static int LEVEL_OF_DETAIL = 12;
    private SearchView mSearchView;
    private TableCoSoKinhDoanhAdapter coSoKinhDoanhAdapter;
    private DiaChiAdapter diaChiAdapter;
    private ArcGISMapImageLayer hanhChinhImageLayers;
    private LinearLayout mLinnearDisplayLayerBaseMap;
    private FloatingActionButton mFloatButtonLayer, mFloatButtonLocation;
    private CheckBox cb_Layer_HanhChinh;
    private int states[][];
    private int colors[];
    private LocationDisplay mLocationDisplay;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationHelper mLocationHelper;
    private Location mLocation;
    private DApplication mApplication;
    private GraphicsOverlay graphicsOverlay;
    private ViewGroup mRootView;

    public ViewGroup getmRootView() {
        return mRootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capnhat_cskd);
        mRootView = findViewById(R.id.main_container);
        setLicense();
        mApplication = (DApplication) getApplication();
        setUp();
        initListViewSearch();

        initLayerListView();
        mApplication.setProgressDialog(new DProgressDialog());
        mApplication.getProgressDialog().show(this, mRootView, "Đang khởi tạo ứng dụng");
        setOnClickListener();
        startGPS();
        startSignIn();
        mApplication.setMainActivity(this);
    }

    private void startGPS() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationHelper = new LocationHelper(this, (longtitude, latitude) -> {

        });
        if (!mLocationHelper.checkPlayServices()) {
            mLocationHelper.buildGoogleApiClient();
        }
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
                if (!mLocationHelper.checkPlayServices()) {
                    mLocationHelper.buildGoogleApiClient();
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert locationManager != null;
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    private void startSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constant.REQUEST.LOGIN);
    }

    private void setOnClickListener() {
        findViewById(R.id.layout_layer_open_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_topo).setOnClickListener(this);
        findViewById(R.id.floatBtnLayer).setOnClickListener(this);
        findViewById(R.id.floatBtnAdd).setOnClickListener(this);
        findViewById(R.id.btn_add_feature_close).setOnClickListener(this);
        findViewById(R.id.btn_layer_close).setOnClickListener(this);
        findViewById(R.id.img_layvitri).setOnClickListener(this);
        findViewById(R.id.floatBtnLocation).setOnClickListener(this);
        findViewById(R.id.floatBtnHome).setOnClickListener(this);
        findViewById(R.id.layout_tim_cskd).setOnClickListener(this);
        findViewById(R.id.layout_tim_dia_chi).setOnClickListener(this);
    }

    private void initListViewSearch() {
        ListView listViewSearchLayer = findViewById(R.id.lstview_search_layer);
        //đưa listview search ra phía sau
        listViewSearchLayer.invalidate();
        List<Feature> items = new ArrayList<>();
        this.coSoKinhDoanhAdapter = new TableCoSoKinhDoanhAdapter(MainActivity.this, items);
        listViewSearchLayer.setAdapter(coSoKinhDoanhAdapter);
        listViewSearchLayer.setOnItemClickListener((parent, view, position, id) -> {
            Feature feature = ((Feature) parent.getItemAtPosition(position));
            mApplication.setSelectedFeatureLYR(feature);
            //TODO lấy featureTBL cho feature
            getSelectedFeature(feature);


        });


        ListView listViewSearchDiaChi = findViewById(R.id.lstview_search_diachi);
        //đưa listview search ra phía sau
        listViewSearchDiaChi.invalidate();
        List<DAddress> addressItems = new ArrayList<>();
        this.diaChiAdapter = new DiaChiAdapter(MainActivity.this, addressItems);
        listViewSearchDiaChi.setAdapter(diaChiAdapter);
        listViewSearchDiaChi.setOnItemClickListener((parent, view, position, id) -> {
            Point point = ((DAddress) parent.getItemAtPosition(position)).getPoint();
            mMapView.setViewpointCenterAsync(point, 100);
            SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.BLUE, 10);
            Graphic graphic = new Graphic(point, symbol);
            graphicsOverlay.getGraphics().clear();
            graphicsOverlay.getGraphics().add(graphic);
        });
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
//                    mPopupInfos.showPopup((ArcGISFeature) mApplication.getSelectedFeatureLYR());
//                    mMapViewHandler.queryByMaKinhDoanh(featureTBL);
                    mPopupInfos.showPopup((ArcGISFeature) item);
                    coSoKinhDoanhAdapter.clear();
                    coSoKinhDoanhAdapter.notifyDataSetChanged();
                } else {
                    new DAlertDialog().show(MainActivity.this, "Không tìm thấy CSKD_TABLE");
                }
            } catch (InterruptedException e) {
                new DAlertDialog().show(MainActivity.this, "Có lỗi xảy ra", e.toString());
            } catch (Exception e) {
                new DAlertDialog().show(MainActivity.this, "Có lỗi xảy ra", e.toString());
            }
        });

    }
    private void setUp() {
        states = new int[][]{{android.R.attr.state_checked}, {}};
        colors = new int[]{R.color.colorTextColor_1, R.color.colorTextColor_1};
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPermisson();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMapView() {
        mLinnearDisplayLayerBaseMap = findViewById(R.id.linnearDisplayLayerBaseMap);
        mMapView = findViewById(R.id.mapView);
        mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, LATITUDE, LONGTITUDE, LEVEL_OF_DETAIL);
        mMapView.setMap(mMap);
        mCallout = mMapView.getCallout();
        final PreparingAsycn preparingAsycn = new PreparingAsycn(this, output -> {
            setFeatureService();
        });
        if (CheckConnectInternet.isOnline(this))
            preparingAsycn.execute();
        final EditText edit_latitude = ((EditText) findViewById(R.id.edit_latitude));
        final EditText edit_longtitude = ((EditText) findViewById(R.id.edit_longtitude));
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                try {
                    android.graphics.Point point = new android.graphics.Point((int) e.getX(), (int) e.getY());

                    // create a map point from a point
                    Point mapPoint = mMapView.screenToLocation(point);
                    if (mMapViewHandler != null)
                        mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }


            @SuppressLint("SetTextI18n")
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mMapViewHandler != null) {
                    Point center = ((MapView) mMapView).getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
                    double[] location = mMapViewHandler.pointToLogLat(center);
                    edit_longtitude.setText(location[0] + "");
                    edit_latitude.setText(location[1] + "");
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
        changeStatusOfLocationDataSource();
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                Point position = locationChangedEvent.getLocation().getPosition();
                edit_longtitude.setText(position.getX() + "");
                edit_latitude.setText(position.getY() + "");
                Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
                mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            }

        });
        graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    private void initLayerListView() {
        findViewById(R.id.layout_layer_open_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_topo).setOnClickListener(this);
        mFloatButtonLayer = findViewById(R.id.floatBtnLayer);
        mFloatButtonLayer.setOnClickListener(this);
        findViewById(R.id.btn_layer_close).setOnClickListener(this);
        mFloatButtonLocation = findViewById(R.id.floatBtnLocation);
        mFloatButtonLocation.setOnClickListener(this);

        cb_Layer_HanhChinh = findViewById(R.id.cb_Layer_HanhChinh);
        cb_Layer_HanhChinh.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < mLinnearDisplayLayerBaseMap.getChildCount(); i++) {
                View view = mLinnearDisplayLayerBaseMap.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (isChecked) checkBox.setChecked(true);
                    else checkBox.setChecked(false);
                }
            }
        });
        SeekBar skbr_basemap = findViewById(R.id.skbr_basemap);
        skbr_basemap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hanhChinhImageLayers.setOpacity((float) i / 100);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setFeatureService() {
        if (ListObjectDB.getInstance().getLstFeatureLayerDTG().size() == 0) return;
        mFeatureLayerDTGS = new ArrayList<>();
        mApplication.setCountElementMustLoad(ListObjectDB.getInstance().getLstFeatureLayerDTG().size() + 2
                // thêm 2 phần tử trong mapviewadddonelistener
        );
        MapViewAddDoneLoadingListener mapViewAddDoneLoadingListener = new MapViewAddDoneLoadingListener(MainActivity.this);
        for (final LayerInfoDTG layerInfoDTG : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (!layerInfoDTG.isView()) {
                mApplication.setCountElementMustLoad(mApplication.getCountElementMustLoad() - 1);
                continue;
            }
            String url = layerInfoDTG.getUrl();
            if (url.equals("https://ditagis.com/arcgis/rest/services/BenTre_QLKD/ChuyenDe/FeatureServer/0"))
                url = "https://ditagis.com/arcgis/rest/services/BenTre_QLKD/ChuyenDeTest/FeatureServer/0";
            else if (url.equals("https://ditagis.com/arcgis/rest/services/BenTre_QLKD/CSKD/FeatureServer/1"))
                url = "https://ditagis.com/arcgis/rest/services/BenTre_QLKD/ChuyenDeTest/FeatureServer/1";
            ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
            FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
            featureLayer.setName(layerInfoDTG.getTitleLayer());
            featureLayer.setMaxScale(0);
            featureLayer.setMinScale(1000000);
            featureLayer.setId(layerInfoDTG.getId());
            Action action = new Action(layerInfoDTG.isView(), layerInfoDTG.isCreate(), layerInfoDTG.isEdit(), layerInfoDTG.isDelete());
            FeatureLayerDTG featureLayerDTG = new FeatureLayerDTG(featureLayer, layerInfoDTG.getTitleLayer(), action);
            featureLayerDTG.setOutFields(getFieldsDTG(layerInfoDTG.getOutField()));
            featureLayerDTG.setQueryFields(getFieldsDTG(layerInfoDTG.getOutField()));
            featureLayerDTG.setUpdateFields(getFieldsDTG(layerInfoDTG.getOutField()));
            if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(getString(R.string.layer_cosokinhdoanh))) {
                featureLayerDTG.setTitleLayer(getString(R.string.title_layer));
                featureLayer.setPopupEnabled(true);
                featureLayer.loadAsync();
                mFeatureLayerDTGS.add(featureLayerDTG);
                mMap.getOperationalLayers().add(featureLayer);
                mApplication.setLayer_CoSoKinhDoanhDTG(featureLayer);
                featureLayer.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        mApplication.setCountElementMustLoad(mApplication.getCountElementMustLoad() - 1);
                        mMapViewHandler = new MapViewHandler(mMapView, MainActivity.this);
                        mApplication.setMapViewHandler(mMapViewHandler);
                        mMapViewHandler.setPopupInfos(mPopupInfos);
                    }
                });
            } else if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(getString(R.string.table_cosokinhdoanh))) {
                mApplication.setCountElementMustLoad(mApplication.getCountElementMustLoad() - 1);
                mApplication.setTable_CoSoKinhDoanhChuaCapNhat(featureLayer);
                mFeatureLayerDTGS.add(featureLayerDTG);
            } else if (layerInfoDTG.getId().toUpperCase().equals(getString(R.string.IDLayer_Basemap))) {
                hanhChinhImageLayers = new ArcGISMapImageLayer(url);
                hanhChinhImageLayers.setId(layerInfoDTG.getId());
                mMapView.getMap().getOperationalLayers().add(hanhChinhImageLayers);
                hanhChinhImageLayers.addDoneLoadingListener(() -> {
                    if (hanhChinhImageLayers.getLoadStatus() == LoadStatus.LOADED) {
                        ListenableList<ArcGISSublayer> sublayerList = hanhChinhImageLayers.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            addCheckBox_SubLayer((ArcGISMapImageSublayer) sublayer, mLinnearDisplayLayerBaseMap);
                            String urlSubBaseMap = layerInfoDTG.getUrl() + "/" + sublayer.getId();
                            if (sublayer.getId() == 3) {
                                mApplication.setSft_HanhChinhXa(new ServiceFeatureTable(urlSubBaseMap));
                            }
                            if (sublayer.getId() == 7) {
                                mApplication.setSft_HanhChinhHuyen(new ServiceFeatureTable(urlSubBaseMap));
                            }
                        }
                        mapViewAddDoneLoadingListener.getHanhChinh();
                    }
                    mApplication.setCountElementMustLoad(mApplication.getCountElementMustLoad() - 1);
                });

                hanhChinhImageLayers.loadAsync();
            } else {
                mApplication.setCountElementMustLoad(mApplication.getCountElementMustLoad() - 1);
            }
        }
        if (mFeatureLayerDTGS.size() == 0) {
            MySnackBar.make(mMapView, getString(R.string.no_access_permissions), true);
            return;
        }
        mPopupInfos = new Popup(MainActivity.this, mMapView, mCallout);
        mMap.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LinearLayout linnearDisplayLayer = (LinearLayout) findViewById(R.id.linnearDisplayLayer);
                int states[][] = {{android.R.attr.state_checked}, {}};
                int colors[] = {R.color.colorTextColor_1, R.color.colorTextColor_1};
                for (final FeatureLayerDTG layer : mFeatureLayerDTGS) {
                    if (layer.getFeatureLayer().getId() != null && layer.getFeatureLayer().getId().equals(getString(R.string.layer_cosokinhdoanh))) {
                        CheckBox checkBox = new CheckBox(linnearDisplayLayer.getContext());
                        checkBox.setText(layer.getTitleLayer());
                        checkBox.setChecked(true);
                        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
                        linnearDisplayLayer.addView(checkBox);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (buttonView.isChecked()) {
                                    layer.getFeatureLayer().setVisible(true);
                                } else {
                                    layer.getFeatureLayer().setVisible(false);
                                }

                            }
                        });
                    }
                }
                for (int i = 0; i < linnearDisplayLayer.getChildCount(); i++) {
                    View v = linnearDisplayLayer.getChildAt(i);
                    if (v instanceof CheckBox) {
                        if (((CheckBox) v).getText().equals(getString(R.string.title_layer)))
                            ((CheckBox) v).setChecked(true);
                        else ((CheckBox) v).setChecked(false);
                    }
                }
            }
        });
    }

    private void addCheckBox_SubLayer(final ArcGISMapImageSublayer layer, LinearLayout linearLayout) {
        final CheckBox checkBox = new CheckBox(linearLayout.getContext());
        checkBox.setText(layer.getName());
        checkBox.setChecked(false);
        layer.setVisible(false);
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (checkBox.isChecked()) {
                    if (buttonView.getText().equals(layer.getName()))
                        layer.setVisible(true);


                } else {
                    if (checkBox.getText().equals(layer.getName()))
                        layer.setVisible(false);
                }
            }
        });
        linearLayout.addView(checkBox);
    }

    private String[] getFieldsDTG(String stringFields) {
        String[] returnFields = null;
        if (stringFields != null) {
            if (stringFields == "*") {
                returnFields = new String[]{"*"};
            } else {
                returnFields = stringFields.split(",");
            }

        }
        return returnFields;
    }

    private void setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license));
    }

    private void changeStatusOfLocationDataSource() {
        mLocationDisplay = mMapView.getLocationDisplay();
//        changeStatusOfLocationDataSource();
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted()) return;

                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null) return;

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, Constant.REQUEST.PERMISS);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
//                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
//                            .getSource().getLocationDataSource().getError().getMessage());
//                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quan_ly_su_co, menu);
        MenuItem menuSearch = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menuSearch.getActionView();
        mSearchView.setQueryHint(getString(R.string.title_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                if (mApplication.getTypeSearch().equals(Constant.TYPE_SEARCH.LAYER)) {
                    mMapViewHandler.querySearchLayer(search, coSoKinhDoanhAdapter);
                } else {
                    mMapViewHandler.querySearchDiaChi(search, diaChiAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    coSoKinhDoanhAdapter.clear();
                    coSoKinhDoanhAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        LinearLayout mLayoutTimKiem = findViewById(R.id.layout_tim_kiem);
        menuSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                mLayoutTimKiem.setVisibility(View.VISIBLE);
                visibleFloatActionButton();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mLayoutTimKiem.setVisibility(View.INVISIBLE);
                visibleFloatActionButton();
                diaChiAdapter.clear();
                diaChiAdapter.notifyDataSetChanged();
                graphicsOverlay.getGraphics().clear();
                return true;
            }
        });
        return true;
    }

    private void visibleFloatActionButton() {
        if (mFloatButtonLayer.getVisibility() == View.VISIBLE) {
            mFloatButtonLayer.setVisibility(View.INVISIBLE);
            mFloatButtonLocation.setVisibility(View.INVISIBLE);
        } else {
            mFloatButtonLayer.setVisibility(View.VISIBLE);
            mFloatButtonLocation.setVisibility(View.VISIBLE);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        addFeatureClose();
        switch (id) {
            case R.id.nav_tracuu:
            final Intent intent = new Intent(this, TraCuuActivity.class);
            this.startActivityForResult(intent, Constant.REQUEST.QUERY);
                break;
            case R.id.nav_refresh:
                initMapView();
            case R.id.nav_logOut:
            startSignIn();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.main_container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean requestPermisson() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, Constant.REQUEST.CAMERA);
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else return true;
    }

    private void goHome() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_tim_cskd:
                mApplication.setTypeSearch(Constant.TYPE_SEARCH.LAYER);
                findViewById(R.id.layout_tim_cskd).setBackgroundResource(R.drawable.layout_border_bottom);
                findViewById(R.id.layout_tim_dia_chi).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                break;
            case R.id.layout_tim_dia_chi:
                mApplication.setTypeSearch(Constant.TYPE_SEARCH.DIACHI);
                findViewById(R.id.layout_tim_dia_chi).setBackgroundResource(R.drawable.layout_border_bottom);
                findViewById(R.id.layout_tim_cskd).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                break;
            case R.id.floatBtnLayer:
                v.setVisibility(View.INVISIBLE);
                ((LinearLayout) findViewById(R.id.layout_layer)).setVisibility(View.VISIBLE);
                break;
            case R.id.layout_layer_open_street_map:
                mMapView.getMap().setMaxScale(1128.497175);
                mMapView.getMap().setBasemap(Basemap.createOpenStreetMap());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_open_street_map);
                break;
            case R.id.layout_layer_street_map:
                mMapView.getMap().setMaxScale(1128.497176);
                mMapView.getMap().setBasemap(Basemap.createStreets());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_street_map);
                break;
            case R.id.layout_layer_topo:
                mMapView.getMap().setMaxScale(5);
                mMapView.getMap().setBasemap(Basemap.createImageryWithLabels());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_topo);

                break;
            case R.id.btn_layer_close:
                ((LinearLayout) findViewById(R.id.layout_layer)).setVisibility(View.INVISIBLE);
                ((FloatingActionButton) findViewById(R.id.floatBtnLayer)).setVisibility(View.VISIBLE);
                break;
            case R.id.img_layvitri:
                addFeatureClose();
                Feature featureLYR = mApplication.getSelectedFeatureLYR();
                if (featureLYR != null) {
                    mMapViewHandler.editFeature();
                } else {
                    Feature featureTBL = mApplication.getSelectedFeatureTBL();
                    if (featureTBL != null) {
                        mMapViewHandler.addFeature();
                    } else {
                        //TODO tại sao lại zoo đây??
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle("Thêm CSKD")
                                .setMessage("Thêm cơ sở kinh doanh phát sinh")
                                .setIcon(R.drawable.add)
                                .setPositiveButton("Thêm", (dialog, whichButton) -> {
                                    mMapViewHandler.addFeature();
                                    dialog.dismiss();
                                })
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .create();
                        alertDialog.show();
                    }

                }

                break;
            case R.id.floatBtnAdd:
                addFeature();
                break;
            case R.id.btn_add_feature_close:
                addFeatureClose();
                break;
            case R.id.floatBtnLocation:
                if (!mLocationDisplay.isStarted()) mLocationDisplay.startAsync();
                else mLocationDisplay.stop();
                break;
            case R.id.floatBtnHome:
                goHome();
                break;
        }
    }

    public void addFeatureClose() {
        findViewById(R.id.linear_addfeature).setVisibility(View.GONE);
        findViewById(R.id.img_map_pin).setVisibility(View.GONE);
        findViewById(R.id.floatBtnAdd).setVisibility(View.VISIBLE);
        mApplication.setSelectedFeatureLYR(null);
    }

    public void addFeature() {
        findViewById(R.id.linear_addfeature).setVisibility(View.VISIBLE);
        findViewById(R.id.img_map_pin).setVisibility(View.VISIBLE);
        findViewById(R.id.floatBtnAdd).setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    private void handlingColorBackgroundLayerSelected(int id) {
        switch (id) {
            case R.id.layout_layer_open_street_map:
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_street_map:
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_topo:
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case Constant.REQUEST.LOGIN:
                    if (Activity.RESULT_OK != resultCode) {
                        finish();
                        return;
                    } else {
                        initMapView();
                    }
                    break;
                case Constant.REQUEST.QUERY:
                    if (resultCode == Activity.RESULT_OK) {
//                    mPopupInfos.showPopup((ArcGISFeature) mApplication.getSelectedFeatureLYR());
                        mMapViewHandler.queryByMaKinhDoanh(mApplication.getSelectedFeatureTBL());
//                    addFeature();
                    }

                    break;
            }
        } catch (Exception e) {
            new DAlertDialog().show(MainActivity.this, "Có lỗi xảy ra", e.toString());
        }
    }
}