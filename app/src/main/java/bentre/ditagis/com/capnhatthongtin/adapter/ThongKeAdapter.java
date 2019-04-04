package bentre.ditagis.com.capnhatthongtin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;

/**
 * Created by ThanLe on 04/10/2017.
 */
public class ThongKeAdapter extends ArrayAdapter<ThongKeAdapter.Item> {
    private Context context;
    private List<Item> items;

    public ThongKeAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_thoigian_thongke, null);
        }
        Item item = items.get(position);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout_tracuu);
        TextView txt_thongke_mota = (TextView) convertView.findViewById(R.id.txt_tracuu_trangthai);
        //todo
        txt_thongke_mota.setText(item.getMota());
        TextView txt_thongke_thoigian = (TextView) convertView.findViewById(R.id.txt_tracuu_diadiem);
        //todo
        if(item.getThoigianhienthi() != null) {
            txt_thongke_thoigian.setText(item.getThoigianhienthi());
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_selectTime);
        if (item.isChecked()) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public List<Item> getItems() {
        return items;
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


    public static class Item {
        int id;
        String mota;
        String thoigianbatdau;
        String thoigianketthuc;
        String thoigianhienthi;
        private boolean isChecked = false;

        public Item() {
        }

        public Item(int id, String mota, String thoigianbatdau, String thoigianketthuc,String thoigianhienthi) {
            this.id = id;
            this.mota = mota;
            this.thoigianbatdau = thoigianbatdau;
            this.thoigianketthuc = thoigianketthuc;
            this.thoigianhienthi = thoigianhienthi;
        }

        public String getThoigianhienthi() {
            return thoigianhienthi;
        }

        public void setThoigianhienthi(String thoigianhienthi) {
            this.thoigianhienthi = thoigianhienthi;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMota() {
            return mota;
        }

        public void setMota(String mota) {
            this.mota = mota;
        }

        public String getThoigianbatdau() {
            return thoigianbatdau;
        }

        public void setThoigianbatdau(String thoigianbatdau) {
            this.thoigianbatdau = thoigianbatdau;
        }

        public String getThoigianketthuc() {
            return thoigianketthuc;
        }

        public void setThoigianketthuc(String thoigianketthuc) {
            this.thoigianketthuc = thoigianketthuc;
        }
    }
}
