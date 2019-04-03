package bentre.ditagis.com.capnhatthongtin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
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
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.util.ListenableList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.adapter.DanhSachDiemDanhGiaAdapter;
import bentre.ditagis.com.capnhatthongtin.async.PreparingAsycn;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.LayerInfoDTG;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.ListObjectDB;
import bentre.ditagis.com.capnhatthongtin.libs.Action;
import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewAddDoneLoadingListener;
import bentre.ditagis.com.capnhatthongtin.utities.CheckConnectInternet;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.ImageFile;
import bentre.ditagis.com.capnhatthongtin.utities.LocationHelper;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewHandler;
import bentre.ditagis.com.capnhatthongtin.utities.MySnackBar;
import bentre.ditagis.com.capnhatthongtin.utities.Popup;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Uri mUri;
    private Popup popupInfos;
    private MapView mMapView;
    private ArcGISMap mMap;
    private Callout mCallout;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private MapViewHandler mMapViewHandler;
    private static double LATITUDE = 10.1809655;
    private static double LONGTITUDE = 106.4011284;
    private static int LEVEL_OF_DETAIL = 12;
    private SearchView mTxtSearch;
    private ListView mListViewSearch;
    private DanhSachDiemDanhGiaAdapter danhSachDiemDanhGiaAdapter;
    private ArcGISMapImageLayer hanhChinhImageLayers;
    private LinearLayout mLinnearDisplayLayerBaseMap;
    private FloatingActionButton mFloatButtonLayer, mFloatButtonLocation;
    private CheckBox cb_Layer_HanhChinh;
    private int states[][];
    private int colors[];

    private LocationDisplay mLocationDisplay;
    private int requestCode = 2;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 55;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationHelper mLocationHelper;
    private Location mLocation;
    private DApplication mApplication;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_chat_luong_nuoc);
        setLicense();
        mApplication = (DApplication) getApplication();
        setUp();
        initListViewSearch();

        initLayerListView();


        setOnClickListener();
        startGPS();
        startSignIn();
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
        startActivityForResult(intent, Constant.REQUEST_LOGIN);
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
    }

    private void initListViewSearch() {
        this.mListViewSearch = findViewById(R.id.lstview_search);
        //đưa listview search ra phía sau
        this.mListViewSearch.invalidate();
        List<DanhSachDiemDanhGiaAdapter.Item> items = new ArrayList<>();
        this.danhSachDiemDanhGiaAdapter = new DanhSachDiemDanhGiaAdapter(MainActivity.this, items);
        this.mListViewSearch.setAdapter(danhSachDiemDanhGiaAdapter);
        this.mListViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String objectID = ((DanhSachDiemDanhGiaAdapter.Item) parent.getItemAtPosition(position)).getObjectID();
                mMapViewHandler.queryByObjectID(objectID);
                danhSachDiemDanhGiaAdapter.clear();
                danhSachDiemDanhGiaAdapter.notifyDataSetChanged();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

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
                    if (mMapViewHandler != null)
                        mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mMapViewHandler != null) {
                    double[] location = mMapViewHandler.onScroll(e1, e2, distanceX, distanceY);
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
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                Point position = locationChangedEvent.getLocation().getPosition();
                edit_longtitude.setText(position.getX() + "");
                edit_latitude.setText(position.getY() + "");
                Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
                mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            }

        });

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
        cb_Layer_HanhChinh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < mLinnearDisplayLayerBaseMap.getChildCount(); i++) {
                    View view = mLinnearDisplayLayerBaseMap.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (isChecked) checkBox.setChecked(true);
                        else checkBox.setChecked(false);
                    }
                }
            }


        });
    }

    private void setFeatureService() {
        if (ListObjectDB.getInstance().getLstFeatureLayerDTG().size() == 0) return;
        mFeatureLayerDTGS = new ArrayList<>();
        for (final LayerInfoDTG layerInfoDTG : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (!layerInfoDTG.isView()) continue;
            String url = layerInfoDTG.getUrl();
            if (!layerInfoDTG.getUrl().startsWith("http"))
                url = "http:" + layerInfoDTG.getUrl();
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
                featureLayer.setPopupEnabled(true);
                mMapViewHandler = new MapViewHandler(featureLayerDTG, mMapView, MainActivity.this);
                mFeatureLayerDTGS.add(featureLayerDTG);
                mMap.getOperationalLayers().add(featureLayer);
                mApplication.setLayer_CoSoKinhDoanhDTG(featureLayerDTG);
            }
            if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(getString(R.string.table_cosokinhdoanh))) {
                mApplication.setTable_CoSoKinhDoanh(featureLayerDTG);
                mFeatureLayerDTGS.add(featureLayerDTG);
            }
            if (layerInfoDTG.getId().toUpperCase().equals(getString(R.string.IDLayer_Basemap))) {
                hanhChinhImageLayers = new ArcGISMapImageLayer(url);
                hanhChinhImageLayers.setId(layerInfoDTG.getId());
                mMapView.getMap().getOperationalLayers().add(hanhChinhImageLayers);
                hanhChinhImageLayers.addDoneLoadingListener(() -> {
                    if (hanhChinhImageLayers.getLoadStatus() == LoadStatus.LOADED) {
                        ListenableList<ArcGISSublayer> sublayerList = hanhChinhImageLayers.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            addCheckBox_SubLayer((ArcGISMapImageSublayer) sublayer, mLinnearDisplayLayerBaseMap);
                            String urlSubBaseMap = "http:" + layerInfoDTG.getUrl() + "/" + sublayer.getId();
                            if (sublayer.getId() == 6) {
                                new ServiceFeatureTable(urlSubBaseMap);
                            }
                        }
                    }
                });
                hanhChinhImageLayers.loadAsync();
            }
        }
        if (mFeatureLayerDTGS.size() == 0) {
            MySnackBar.make(mMapView, getString(R.string.no_access_permissions), true);
            return;
        }
        popupInfos = new Popup(MainActivity.this, mMapView, mCallout);

        mMapViewHandler.setPopupInfos(popupInfos);
        mMap.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                new MapViewAddDoneLoadingListener(MainActivity.this);
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
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        mTxtSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mTxtSearch.setQueryHint(getString(R.string.title_search));
        mTxtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mMapViewHandler.querySearch(query, mListViewSearch, danhSachDiemDanhGiaAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    danhSachDiemDanhGiaAdapter.clear();
                    danhSachDiemDanhGiaAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            MainActivity.this.mListViewSearch.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_tracuu) {
            final Intent intent = new Intent(this, TraCuuActivity.class);
            this.startActivityForResult(intent, requestCode);

        } else if (id == R.id.nav_logOut) {
            startSignIn();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean requestPermisson() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, REQUEST_ID_IMAGE_CAPTURE);
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
//                mMapViewHandler.capture();
                capture();
                break;
            case R.id.floatBtnAdd:
                ((LinearLayout) findViewById(R.id.linear_addfeature)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.img_map_pin)).setVisibility(View.VISIBLE);
                ((FloatingActionButton) findViewById(R.id.floatBtnAdd)).setVisibility(View.GONE);
                mMapViewHandler.setClickBtnAdd(true);
                break;
            case R.id.btn_add_feature_close:
                ((LinearLayout) findViewById(R.id.linear_addfeature)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.img_map_pin)).setVisibility(View.GONE);
                ((FloatingActionButton) findViewById(R.id.floatBtnAdd)).setVisibility(View.VISIBLE);
                mMapViewHandler.setClickBtnAdd(false);
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

    public void capture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

        File photo = ImageFile.getFile(this);
//        this.mUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", photo);
        this.mUri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.mUri);
//        this.mUri = Uri.fromFile(photo);
        startActivityForResult(cameraIntent, REQUEST_ID_IMAGE_CAPTURE);
    }

    @Nullable
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
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
            String returnedResult = data.getExtras().get(getString(R.string.ket_qua_objectid)).toString();
            if (resultCode == Activity.RESULT_OK) {
                mMapViewHandler.queryByObjectID(returnedResult);
            }
        } catch (Exception e) {
        }

        switch (requestCode) {
            case REQUEST_ID_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    if (this.mUri != null) {
//                    Uri selectedImage = this.mUri;
//                    getContentResolver().notifyChange(selectedImage, null);
                        Bitmap bitmap = getBitmap(mUri.getPath());
                        try {
                            if (bitmap != null) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                byte[] image = outputStream.toByteArray();
                                Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
                                mMapViewHandler.addFeature(image);
                                //Todo xóa ảnh
                            }
                        } catch (Exception e) {
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    MySnackBar.make(mMapView, "Hủy chụp ảnh", false);
                } else {
                    MySnackBar.make(mMapView, "Lỗi khi chụp ảnh", false);
                }
                break;
            case Constant.REQUEST_LOGIN:
                if (Activity.RESULT_OK != resultCode) {
                    finish();
                    return;
                } else {
                    initMapView();
                }
                break;
        }
    }
}