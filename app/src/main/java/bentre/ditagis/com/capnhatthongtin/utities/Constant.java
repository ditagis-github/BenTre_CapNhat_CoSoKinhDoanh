package bentre.ditagis.com.capnhatthongtin.utities;

import java.text.SimpleDateFormat;

import bentre.ditagis.com.capnhatthongtin.adapter.SettingsAdapter;

/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("ddMMyyyy");
    public static final String OBJECTID = "OBJECTID";
    public static final String IDDIEM_DANH_GIA = "IDDiemDanhGia";
    public static final String IDMAUKIEMNGHIEM = "IDMauKiemNghiem";
    public static final String DIACHI = "DiaChi";
    public static final String NGAY_CAP_NHAT = "NgayCapNhat";
    public static final int REQUEST_LOGIN = 0;
    private static final String SERVER_API = "http://qlkdbentre.ditagis.com/api";
//    private static final String SERVER_API = "http://113.161.88.180:798/apiv1/api";

    public static class API_URL {
        public static final String LOGIN = SERVER_API + "/Login";
        public static final String DISPLAY_NAME = SERVER_API + "/Account/Profile";
        public static final String LAYER_INFO = SERVER_API + "/Account/LayerInfo";
        public static final String IS_ACCESS = SERVER_API + "/Account/IsAccess/m_cnht";
    }

    private static Constant mInstance = null;

    public static Constant getInstance() {
        if (mInstance == null)
            mInstance = new Constant();
        return mInstance;
    }

    private Constant() {
    }


    public static class FIELD_DIEM_DANH_GIA {
        public static final String CANH_BAO_VUOT_NGUONG = "CanhBaoVuotNguong";
    }

    public static class VALUE_CANH_BAO_VUOT_NGUONG {
        public static final short VUOT = 1;
        public static final short KHONG_VUOT = 2;
    }
}
