package bentre.ditagis.com.capnhatthongtin.utities;

import java.text.SimpleDateFormat;


/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("ddMMyyyy");
    public static final String OBJECTID = "OBJECTID";
    public static final String TGCAP_NHAT = "TGCapNhat";
    private static final String SERVER_API = "http://qlkdbentre.ditagis.com/api";

    //    private static final String SERVER_API = "http://113.161.88.180:798/apiv1/api";
    public static class REQUEST {
        public static final int LOGIN = 0;
        public static final int QUERY = 1;
        public static final int PERMISS = 2;
        public static final int CAMERA = 3;
    }

    public static class HanhChinhFields {
        public static final String tenxa = "tenxa";
        public static final String maxa = "maxa";
        public static final String tenhuyen = "tenhuyen";
        public static final String mahuyen = "mahuyen";
    }

    //    public static class CSKDLayerFields {
//        public static final String MaKinhDoanh = "MaKinhDoanh";
//        public static final String MaPhuongXa = "MaPhuongXa";
//        public static final String MaHuyenTP = "MaHuyenTP";
//        public static final String TenDoanhNghiep = "TenDoanhNghiep";
//        public static final String DiaChi = "DiaChi";
//        public static final String DienThoai = "DienThoai";
//        public static final String GhiChu = "GhiChu";
//    }
    public static class CSKDLayerFields {
        public static final String MaKinhDoanh = "MaCSKD";
        public static final String MaPhuongXa = "MaPhuongXa";
        public static final String MaHuyenTP = "MaHuyenTP";
        public static final String TenDoanhNghiep = "TenCSKD";
        public static final String DiaChi = "DiaChi";
        public static final String DienThoai = "SoDienThoai";
        public static final String GhiChu = "GhiChu";
        public static final String NguoiTao = "NguoiTao";
    }

    public static class CSKDTableFields {
        public static final String MaKinhDoanh = "MaKinhDoanh";
        public static final String MaPhuongXa = "MaPhuongXa";
        public static final String MaHuyenTP = "MaHuyenTP";
        public static final String TenDoanhNghiep = "TenDoanhNghiep";
        public static final String DiaChi = "DiaChi";
        public static final String DienThoai = "DienThoai";
        public static final String X = "X";
        public static final String Y = "Y";
    }

    public static class API_URL {
        public static final String LOGIN = SERVER_API + "/Login";
        public static final String DISPLAY_NAME = SERVER_API + "/Account/Profile";
        public static final String LAYER_INFO = SERVER_API + "/Account/LayerInfo";
        public static final String IS_ACCESS = SERVER_API + "/Account/IsAccess/m_cnht";
    }

    public static class TYPE_SEARCH {
        public static final String DIACHI = "DIACHI";
        public static final String LAYER = "LAYER";
    }
}
