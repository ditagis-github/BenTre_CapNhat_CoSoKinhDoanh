package bentre.ditagis.com.capnhatthongtin.common;

import android.app.Application;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.HashMap;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.User;
import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewAddDoneLoadingListener;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewHandler;


public class DApplication extends Application {
    //user name login
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //   table and layer co so kinh doanh
    private FeatureLayerDTG table_CoSoKinhDoanhDTG;

    public FeatureLayerDTG getTable_CoSoKinhDoanhDTG() {
        return table_CoSoKinhDoanhDTG;
    }

    public void setTable_CoSoKinhDoanhDTG(FeatureLayerDTG table_CoSoKinhDoanhDTG) {
        this.table_CoSoKinhDoanhDTG = table_CoSoKinhDoanhDTG;
    }

    private FeatureLayerDTG layer_CoSoKinhDoanhDTG;

    public FeatureLayerDTG getLayer_CoSoKinhDoanhDTG() {
        return layer_CoSoKinhDoanhDTG;
    }

    public void setLayer_CoSoKinhDoanhDTG(FeatureLayerDTG layer_CoSoKinhDoanhDTG) {
        this.layer_CoSoKinhDoanhDTG = layer_CoSoKinhDoanhDTG;
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

    //  features hanh chinh
    private HashMap<String, String> hashMapHuyenTP;
    private ArrayList<MapViewAddDoneLoadingListener.HanhChinhXa> hanhChinhXaList;

    public HashMap<String, String> getHashMapHuyenTP() {
        return hashMapHuyenTP;
    }

    public void setHashMapHuyenTP(HashMap<String, String> hashMapHuyenTP) {
        this.hashMapHuyenTP = hashMapHuyenTP;
    }

    public ArrayList<MapViewAddDoneLoadingListener.HanhChinhXa> getHanhChinhXaList() {
        return hanhChinhXaList;
    }

    public void setHanhChinhXaList(ArrayList<MapViewAddDoneLoadingListener.HanhChinhXa> hanhChinhXaList) {
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
}