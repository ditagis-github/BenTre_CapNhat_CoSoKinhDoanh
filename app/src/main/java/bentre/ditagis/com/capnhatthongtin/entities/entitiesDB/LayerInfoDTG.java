package bentre.ditagis.com.capnhatthongtin.entities.entitiesDB;


/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class LayerInfoDTG {


    private String id;
    private String titleLayer;
    private String url;
    private boolean isCreate;
    private boolean isDelete;
    private boolean isEdit;
    private boolean isView;
    private String outField;
    private String definition;

    public LayerInfoDTG(String id, String titleLayer, String url, boolean isCreate, boolean isDelete, boolean isEdit, boolean isView, String outField, String definition) {
        this.id = id;
        this.titleLayer = titleLayer;
        this.url = url;
        this.isCreate = isCreate;
        this.isDelete = isDelete;
        this.isEdit = isEdit;
        this.isView = isView;
        this.outField = outField;
        this.definition = definition;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleLayer() {
        return titleLayer;
    }

    public void setTitleLayer(String titleLayer) {
        this.titleLayer = titleLayer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public String getOutField() {
        return outField;
    }

    public void setOutField(String outField) {
        this.outField = outField;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }


}
