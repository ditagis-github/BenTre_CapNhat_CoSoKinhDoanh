package bentre.ditagis.com.capnhatthongtin.entities;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

public class DAddress {
    private double longtitude;
    private double latitude;
    private String subAdminArea;
    private String adminArea;
    private String location;

    public DAddress(double longtitude, double latitude, String subAdminArea, String adminArea, String location) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.subAdminArea = subAdminArea;
        this.adminArea = adminArea;
        this.location = location;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public String getLocation() {
        return location;
    }
    public Point getPoint(){
        Point pointLongLat = new Point(this.getLongtitude(), this.getLatitude());
        Geometry geometryWg = GeometryEngine.project(pointLongLat, SpatialReferences.getWgs84());
        Geometry geometryWM = GeometryEngine.project(geometryWg, SpatialReferences.getWebMercator());
        Point point = geometryWM.getExtent().getCenter();
        return point;
    }
}
