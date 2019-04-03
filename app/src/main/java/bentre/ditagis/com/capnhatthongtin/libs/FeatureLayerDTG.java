package bentre.ditagis.com.capnhatthongtin.libs;


import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class FeatureLayerDTG {
    private FeatureLayer featureLayer;
    private String[] addFields;
    private String[] updateFields;
    private String[] queryFields;
    private String[] outFields;
    private String titleLayer;
    private Action action;

    public String[] getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(String[] updateFields) {
        this.updateFields = updateFields;
    }
    public FeatureLayerDTG(FeatureLayer featureLayer, String titleLayer, Action action) {
        this.featureLayer = featureLayer;
        this.titleLayer = titleLayer;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    public FeatureLayer getFeatureLayer() {
        return featureLayer;
    }

    public String[] getAddFields() {
        return addFields;
    }

    public void setAddFields(String[] addFields) {
        this.addFields = addFields;
    }

    public String[] getOutFields() {
        return outFields;
    }

    public void setOutFields(String[] outFields) {
        this.outFields = outFields;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String[] queryFields) {
        this.queryFields = queryFields;
    }

    public String getTitleLayer() {
        return titleLayer;
    }

    public void setTitleLayer(String titleLayer) {
        this.titleLayer = titleLayer;
    }
}
