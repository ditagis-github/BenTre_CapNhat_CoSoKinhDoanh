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
        ArrayList<HanhChinh> hanhChinhXaList = new ArrayList<>();
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
                HanhChinh hanhChinhXa = new HanhChinh(tenPhuongXa,maPhuongXa,maHuyenTP);
                hanhChinhXaList.add(hanhChinhXa);
            }
            mDApplication.setHanhChinhXaList(hanhChinhXaList);
        }).execute();
    }
    public static class HanhChinh {
        private String tenPhuongXa;
        private String maPhuongXa;
        private String maHuyenTP;
        private String tenHuyenTP;
        public HanhChinh() {
        }

        public HanhChinh(String tenPhuongXa, String maPhuongXa, String maHuyenTP) {
            this.tenPhuongXa = tenPhuongXa;
            this.maPhuongXa = maPhuongXa;
            this.maHuyenTP = maHuyenTP;
        }

        public String getTenPhuongXa() {
            return tenPhuongXa;
        }

        public void setTenPhuongXa(String tenPhuongXa) {
            this.tenPhuongXa = tenPhuongXa;
        }

        public String getMaPhuongXa() {
            return maPhuongXa;
        }

        public void setMaPhuongXa(String maPhuongXa) {
            this.maPhuongXa = maPhuongXa;
        }

        public String getMaHuyenTP() {
            return maHuyenTP;
        }

        public void setMaHuyenTP(String maHuyenTP) {
            this.maHuyenTP = maHuyenTP;
        }

        public String getTenHuyenTP() {
            return tenHuyenTP;
        }

        public void setTenHuyenTP(String tenHuyenTP) {
            this.tenHuyenTP = tenHuyenTP;
        }
    }
}
