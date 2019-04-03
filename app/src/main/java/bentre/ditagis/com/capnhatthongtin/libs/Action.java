package bentre.ditagis.com.capnhatthongtin.libs;

public class Action {
    private boolean isView;
    private boolean isCreate;
    private boolean isEdit;
    private boolean isDelete;

    public Action(boolean isView, boolean isCreate, boolean isEdit, boolean isDelete) {
        this.isView = isView;
        this.isCreate = isCreate;
        this.isEdit = isEdit;
        this.isDelete = isDelete;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
