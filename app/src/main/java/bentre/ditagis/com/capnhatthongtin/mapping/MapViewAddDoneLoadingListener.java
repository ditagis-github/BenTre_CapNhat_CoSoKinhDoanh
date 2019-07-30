package bentre.ditagis.com.capnhatthongtin.mapping;

import com.esri.arcgisruntime.data.Feature;

import java.util.ArrayList;
import java.util.HashMap;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.async.QueryHanhChinhsAsync;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

public class MapViewAddDoneLoadingListener {
    private DApplication mDApplication;
    private MainActivity mMainActivity;
    public MapViewAddDoneLoadingListener(MainActivity mainActivity) {
        this.mDApplication = (DApplication) mainActivity.getApplication();
        this.mMainActivity = mainActivity;
    }

    public void getHanhChinh() {
        HashMap<String, String> hashMapHuyenTP = new HashMap<>();
        ArrayList<HanhChinhXa> hanhChinhXaList = new ArrayList<>();
        new QueryHanhChinhsAsync(mMainActivity, this.mDApplication.getSft_HanhChinhHuyen(), output -> {
            for (Feature feature : output) {
                String tenHuyenTP = feature.getAttributes().get(Constant.HanhChinhFields.tenhuyen).toString();
                String maHuyenTP = feature.getAttributes().get(Constant.HanhChinhFields.maquanhuyen).toString();
                hashMapHuyenTP.put(maHuyenTP,tenHuyenTP);
            }
            mDApplication.setHashMapHuyenTP(hashMapHuyenTP);
        }).execute();
        new QueryHanhChinhsAsync(mMainActivity, this.mDApplication.getSft_HanhChinhXa(), output ->{
            for (Feature feature : output) {
                String tenPhuongXa = feature.getAttributes().get(Constant.HanhChinhFields.tenxa).toString();
                String maPhuongXa = feature.getAttributes().get(Constant.HanhChinhFields.maxa).toString();
                String maHuyenTP = feature.getAttributes().get(Constant.HanhChinhFields.mahuyentp).toString();
                HanhChinhXa hanhChinhXa = new HanhChinhXa(tenPhuongXa,maPhuongXa,maHuyenTP);
                hanhChinhXaList.add(hanhChinhXa);
            }
            mDApplication.setHanhChinhXaList(hanhChinhXaList);
        }).execute();
    }
    public static class HanhChinhXa{
        private String tenPhuongXa;
        private String maPhuongXa;
        private String maHuyenTP;
        private String selectValuePhuongXa;
        private String selectValueHuyenTP;
        public HanhChinhXa() {
        }
        public HanhChinhXa(String tenPhuongXa, String maPhuongXa, String maHuyenTP) {
            this.tenPhuongXa = tenPhuongXa;
            this.maPhuongXa = maPhuongXa;
            this.maHuyenTP = maHuyenTP;
        }

        public String getTenPhuongXa() {
            return tenPhuongXa;
        }

        public String getMaPhuongXa() {
            return maPhuongXa;
        }

        public String getMaHuyenTP() {
            return maHuyenTP;
        }

        public void setMaHuyenTP(String maHuyenTP) {
            this.maHuyenTP = maHuyenTP;
        }

        public void setTenPhuongXa(String tenPhuongXa) {
            this.tenPhuongXa = tenPhuongXa;
        }

        public void setMaPhuongXa(String maPhuongXa) {
            this.maPhuongXa = maPhuongXa;
        }

        public String getSelectValuePhuongXa() {
            return selectValuePhuongXa;
        }

        public void setSelectValuePhuongXa(String selectValuePhuongXa) {
            this.selectValuePhuongXa = selectValuePhuongXa;
        }

        public String getSelectValueHuyenTP() {
            return selectValueHuyenTP;
        }

        public void setSelectValueHuyenTP(String selectValueHuyenTP) {
            this.selectValueHuyenTP = selectValueHuyenTP;
        }
    }
}
