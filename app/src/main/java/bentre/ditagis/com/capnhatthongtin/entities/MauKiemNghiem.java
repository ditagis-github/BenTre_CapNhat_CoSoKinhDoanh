package bentre.ditagis.com.capnhatthongtin.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fuhi
 */
public class MauKiemNghiem {
    private HashMap<String, String> string_attributes;
    private Map<String, Object> object_attributes;
    private String objectID;
    private String idDiemDanhGia;
    private String dienTich;
    private String tinhTrangNuoc;
    private String muiNuoc;
    private String mauNuoc;
    private String ngayCapNhat;

    public MauKiemNghiem() {
    }

    public HashMap<String, String> getString_attributes() {
        return string_attributes;
    }

    public void setString_attributes(HashMap<String, String> string_attributes) {
        this.string_attributes = string_attributes;
        setObjectID(string_attributes.get("OBJECTID"));
        setIdDiemDanhGia(string_attributes.get("IDDiemDanhGia"));
        setDienTich(string_attributes.get("DienTich"));
        setTinhTrangNuoc(string_attributes.get("TinhTrangNuoc"));
        setMuiNuoc(string_attributes.get("MuiNuoc"));
        setMauNuoc(string_attributes.get("MauNuoc"));
        setNgayCapNhat(string_attributes.get("NgayCapNhat"));
    }

    public Map<String, Object> getObject_attributes() {
        return object_attributes;
    }

    public void setObject_attributes(Map<String, Object> object_attributes, long objectId) {
        this.object_attributes = object_attributes;
        setObjectID(getValueObjectAttributes("OBJECTID"));
        setIdDiemDanhGia(getValueObjectAttributes("IDDiemDanhGia"));
        setDienTich(object_attributes.get("DienTich").toString());
        setTinhTrangNuoc(getValueObjectAttributes("TinhTrangNuoc"));
        setMuiNuoc(getValueObjectAttributes("MuiNuoc"));
        setMauNuoc(getValueObjectAttributes("MauNuoc"));
        setNgayCapNhat(getValueObjectAttributes("NgayCapNhat"));
    }
    private String getValueObjectAttributes(String string){
        Object object = this.object_attributes.get(string);
        if(object != null)
            return object.toString();
        return null;
    }

    public void setAttributes(HashMap<String, Object> attributes) {

    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getIdDiemDanhGia() {
        return idDiemDanhGia;
    }

    public void setIdDiemDanhGia(String idDiemDanhGia) {
        this.idDiemDanhGia = idDiemDanhGia;
    }

    public String getDienTich() {
        return dienTich;
    }

    public void setDienTich(String dienTich) {
        this.dienTich = dienTich;
    }

    public String getTinhTrangNuoc() {
        return tinhTrangNuoc;
    }

    public void setTinhTrangNuoc(String tinhTrangNuoc) {
        this.tinhTrangNuoc = tinhTrangNuoc;
    }

    public String getMuiNuoc() {
        return muiNuoc;
    }

    public void setMuiNuoc(String muiNuoc) {
        this.muiNuoc = muiNuoc;
    }

    public String getMauNuoc() {
        return mauNuoc;
    }

    public void setMauNuoc(String mauNuoc) {
        this.mauNuoc = mauNuoc;
    }

    public String getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(String ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}
