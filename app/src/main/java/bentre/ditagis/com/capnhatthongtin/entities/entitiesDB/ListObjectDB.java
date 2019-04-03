package bentre.ditagis.com.capnhatthongtin.entities.entitiesDB;

import java.util.List;


public class ListObjectDB {

    private static ListObjectDB instance = null;
    private List<String> dmas;
    private List<LayerInfoDTG> lstFeatureLayerDTG;

    private ListObjectDB() {
    }

    public static ListObjectDB getInstance() {
        if (instance == null)
            instance = new ListObjectDB();
        return instance;
    }
    public List<String> getDmas() {
        return dmas;
    }

    public void setDmas(List<String> dmas) {
        this.dmas = dmas;
    }

    public List<LayerInfoDTG> getLstFeatureLayerDTG() {
        return lstFeatureLayerDTG;
    }

    public void setLstFeatureLayerDTG(List<LayerInfoDTG> lstFeatureLayerDTG) {
        this.lstFeatureLayerDTG = lstFeatureLayerDTG;
    }
}
