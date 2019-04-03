
package bentre.ditagis.com.capnhatthongtin.adapter;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.support.annotation.NonNull;
        import android.view.LayoutInflater;
        import android.view.View;

        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

        import bentre.ditagis.com.capnhatthongtin.R;
public class DanhSachDiemDanhGiaAdapter extends ArrayAdapter<DanhSachDiemDanhGiaAdapter.Item> {
    private Context context;
    private List<Item> items;


    public DanhSachDiemDanhGiaAdapter(Context context, List<DanhSachDiemDanhGiaAdapter.Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tracuu, null);
        }
        Item item = items.get(position);
        TextView txt_tracuu_id = (TextView) convertView.findViewById(R.id.txt_tracuu_id);
        TextView txt_tracuu_ngaycapnhat = (TextView) convertView.findViewById(R.id.txt_tracuu_ngaycapnhat);
        TextView txt_tracuu_diachi = (TextView) convertView.findViewById(R.id.txt_tracuu_diachi);
        txt_tracuu_id.setText(item.getiDDiemDanhGia());
        txt_tracuu_ngaycapnhat.setText(item.getNgayCapNhat());
        txt_tracuu_diachi.setText(item.getDiaChi());
        return convertView;
    }

    public static class Item{
        private String objectID;
        private String iDDiemDanhGia;
        private String ngayCapNhat;
        private String diaChi;

        public Item() {
        }

        public Item(String objectID, String iDDiemDanhGia, String ngayCapNhat) {
            this.objectID = objectID;
            this.iDDiemDanhGia = iDDiemDanhGia;
            this.ngayCapNhat = ngayCapNhat;
        }

        public String getObjectID() {
            return objectID;
        }

        public void setObjectID(String objectID) {
            this.objectID = objectID;
        }

        public String getiDDiemDanhGia() {
            return iDDiemDanhGia;
        }

        public void setiDDiemDanhGia(String iDDiemDanhGia) {
            this.iDDiemDanhGia = iDDiemDanhGia;
        }

        public String getNgayCapNhat() {
            return ngayCapNhat;
        }

        public void setNgayCapNhat(String ngayCapNhat) {
            this.ngayCapNhat = ngayCapNhat;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            this.diaChi = diaChi;
        }
    }

}
