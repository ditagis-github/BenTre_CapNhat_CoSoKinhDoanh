
package bentre.ditagis.com.capnhatthongtin.adapter;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.Typeface;
        import android.support.annotation.NonNull;
        import android.view.LayoutInflater;
        import android.view.View;

        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

        import bentre.ditagis.com.capnhatthongtin.R;
public class TableCoSoKinhDoanhAdapter extends ArrayAdapter<TableCoSoKinhDoanhAdapter.Item> {
    private Context context;
    private List<Item> items;


    public TableCoSoKinhDoanhAdapter(Context context, List<TableCoSoKinhDoanhAdapter.Item> items) {
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
        TextView txt_tracuu_tencongty = (TextView) convertView.findViewById(R.id.txt_tracuu_tencongty);
        TextView txt_tracuu_makinhdoanh = (TextView) convertView.findViewById(R.id.txt_tracuu_makinhdoanh);
        TextView txt_tracuu_diachi = (TextView) convertView.findViewById(R.id.txt_tracuu_diachi);
        txt_tracuu_tencongty.setText(item.getTenDoanhNghiep().toUpperCase());
        txt_tracuu_makinhdoanh.setText(item.getMaKinhDoanh());
        txt_tracuu_diachi.setText(item.getDiaChi());
        if(item.getToaDoX().equals("") || item.getToaDoX().equals("")){
            txt_tracuu_tencongty.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            txt_tracuu_tencongty.setTypeface(Typeface.DEFAULT);
        }
        return convertView;
    }

    public static class Item{
        private String objectID;
        private String tenDoanhNghiep;
        private String maKinhDoanh;
        private String toaDoX;
        private String toaDoY;
        private String diaChi;

        public Item() {
        }
        public String getObjectID() {
            return objectID;
        }

        public void setObjectID(String objectID) {
            this.objectID = objectID;
        }

        public String getMaKinhDoanh() {
            return maKinhDoanh;
        }

        public void setMaKinhDoanh(String maKinhDoanh) {
            this.maKinhDoanh = maKinhDoanh;
        }

        public String getToaDoX() {
            return toaDoX;
        }

        public void setToaDoX(String toaDoX) {
            this.toaDoX = toaDoX;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            this.diaChi = diaChi;
        }

        public String getToaDoY() {
            return toaDoY;
        }

        public void setToaDoY(String toaDoY) {
            this.toaDoY = toaDoY;
        }

        public String getTenDoanhNghiep() {
            return tenDoanhNghiep;
        }

        public void setTenDoanhNghiep(String tenDoanhNghiep) {
            this.tenDoanhNghiep = tenDoanhNghiep;
        }
    }

}
