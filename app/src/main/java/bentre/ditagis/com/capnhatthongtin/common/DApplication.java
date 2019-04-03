package bentre.ditagis.com.capnhatthongtin.common;

import android.app.Application;

import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;


public class DApplication extends Application {
    private FeatureLayerDTG table_CoSoKinhDoanhDTG;

    public FeatureLayerDTG getTable_CoSoKinhDoanhDTG() {
        return table_CoSoKinhDoanhDTG;
    }

    public void setTable_CoSoKinhDoanh(FeatureLayerDTG table_CoSoKinhDoanhDTG) {
        this.table_CoSoKinhDoanhDTG = table_CoSoKinhDoanhDTG;
    }
    private FeatureLayerDTG layer_CoSoKinhDoanhDTG;

    public FeatureLayerDTG getLayer_CoSoKinhDoanhDTG() {
        return layer_CoSoKinhDoanhDTG;
    }

    public void setLayer_CoSoKinhDoanhDTG(FeatureLayerDTG layer_CoSoKinhDoanhDTG) {
        this.layer_CoSoKinhDoanhDTG = layer_CoSoKinhDoanhDTG;
    }
}