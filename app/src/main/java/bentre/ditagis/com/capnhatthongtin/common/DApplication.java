package bentre.ditagis.com.capnhatthongtin.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.ArrayList;
import java.util.HashMap;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.TraCuuActivity;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.User;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewAddDoneLoadingListener;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewHandler;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;
import bentre.ditagis.com.capnhatthongtin.utities.DProgressDialog;


public class DApplication extends Application {
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private DProgressDialog progressDialog;
    private int countElementMustLoad = 0;

    public int getCountElementMustLoad() {
        return countElementMustLoad;
    }

    public void setCountElementMustLoad(int countElementMustLoad) {
        this.countElementMustLoad = countElementMustLoad;
        if (this.countElementMustLoad == 0)
            progressDialog.dismiss();
    }

    public DProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(DProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    //user name login
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //   table and layer co so kinh doanh
    private FeatureLayer serviceFeatureTableCSKD, serviceFeatureTableCSKD_ChuaCapNhat;

    public FeatureLayer getTable_CoSoKinhDoanhChuaCapNhat() {
        return serviceFeatureTableCSKD_ChuaCapNhat;
    }

    public void setTable_CoSoKinhDoanhChuaCapNhat(FeatureLayer table_CoSoKinhDoanhChuaCapNhatDTG) {
        this.serviceFeatureTableCSKD_ChuaCapNhat = table_CoSoKinhDoanhChuaCapNhatDTG;
    }


    public FeatureLayer getLayer_CoSoKinhDoanhDTG() {
        return serviceFeatureTableCSKD;
    }

    public void setLayer_CoSoKinhDoanhDTG(FeatureLayer layer_CoSoKinhDoanhDTG) {
        this.serviceFeatureTableCSKD = layer_CoSoKinhDoanhDTG;
    }

    // sft hanh chinh

    private ServiceFeatureTable sft_HanhChinhXa;
    private ServiceFeatureTable sft_HanhChinhHuyen;

    public ServiceFeatureTable getSft_HanhChinhXa() {
        return sft_HanhChinhXa;
    }

    public void setSft_HanhChinhXa(ServiceFeatureTable sft_HanhChinhXa) {
        this.sft_HanhChinhXa = sft_HanhChinhXa;
    }

    public ServiceFeatureTable getSft_HanhChinhHuyen() {
        return sft_HanhChinhHuyen;
    }

    public void setSft_HanhChinhHuyen(ServiceFeatureTable sft_HanhChinhHuyen) {
        this.sft_HanhChinhHuyen = sft_HanhChinhHuyen;
    }

    //  feature hanh chinh
    private HashMap<String, String> hashMapHuyenTP;
    private ArrayList<MapViewAddDoneLoadingListener.HanhChinh> hanhChinhXaList;

    public HashMap<String, String> getHashMapHuyenTP() {
        return hashMapHuyenTP;
    }

    public void setHashMapHuyenTP(HashMap<String, String> hashMapHuyenTP) {
        this.hashMapHuyenTP = hashMapHuyenTP;
    }

    public ArrayList<MapViewAddDoneLoadingListener.HanhChinh> getHanhChinhXaList() {
        return hanhChinhXaList;
    }

    public void setHanhChinhXaList(ArrayList<MapViewAddDoneLoadingListener.HanhChinh> hanhChinhXaList) {
        this.hanhChinhXaList = hanhChinhXaList;
    }

    // selected feature CSKD table
    private Feature selectedFeatureTBL;

    public Feature getSelectedFeatureTBL() {
        return selectedFeatureTBL;
    }

    public void setSelectedFeatureTBL(Feature selectedFeatureTBL) {
        this.selectedFeatureTBL = selectedFeatureTBL;
    }
    // selected feature CSKD layer
    private Feature selectedFeatureLYR;

    public Feature getSelectedFeatureLYR() {
        return selectedFeatureLYR;
    }

    public void setSelectedFeatureLYR(Feature selectedFeatureLYR) {
        this.selectedFeatureLYR = selectedFeatureLYR;
    }
    // set Mapview handle
    private MapViewHandler mapViewHandler;

    public MapViewHandler getMapViewHandler() {
        return mapViewHandler;
    }

    public void setMapViewHandler(MapViewHandler mapViewHandler) {
        this.mapViewHandler = mapViewHandler;
    }

    private MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // save last query
    private TraCuuActivity.ParameterQuery parameterQuery;

    public TraCuuActivity.ParameterQuery getParameterQuery() {
        return parameterQuery;
    }

    public void setParameterQuery(TraCuuActivity.ParameterQuery parameterQuery) {
        this.parameterQuery = parameterQuery;
    }
    // TYPE SEARCH
    private String typeSearch = Constant.TYPE_SEARCH.LAYER;

    public String getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(String typeSearch) {
        this.typeSearch = typeSearch;
    }

}