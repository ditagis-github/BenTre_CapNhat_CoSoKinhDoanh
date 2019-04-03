package bentre.ditagis.com.capnhatthongtin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class CustomAdapter extends ArrayAdapter<CustomAdapter.Item> {
    private Context context;
    private List<Item> items;
    public CustomAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
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


//    public Item getItem(String mlt) {
//        for (Item item : this.items)
//            if (item.getTieuThu().equals(mlt))
//                return item;
//        return null;
//    }
//
//    public boolean removeItem(String mlt) {
//        for (Item item : this.items)
//            if (item.getTieuThu().equals(mlt)) {
//                this.items.remove(item);
//                return true;
//            }
//        return false;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_search, null);
        }
        Item item = items.get(position);


        TextView txtDanhBo = (TextView) convertView.findViewById(R.id.txt_tracuu_id);
        //todo
        txtDanhBo.setText(item.getNam() + "_" + item.getKy() + "_" + item.getDot() + "_" + item.getMay());




        return convertView;
    }

    public static class Item {
        String ky;
        String dot;
        String nam;
        String soLuong;
        String may;
        int flag;

        public Item(String ky, String dot, String nam, String soLuong, String may, int flag) {
            this.ky = ky;
            this.dot = dot;
            this.nam = nam;
            this.soLuong = soLuong;
            this.may = may;
            this.flag = flag;
        }

        public String getMay() {
            return may;
        }

        public String getKy() {
            return ky;
        }

        public String getDot() {
            return dot;
        }

        public String getNam() {
            return nam;
        }

        public String getSoLuong() {
            return soLuong;
        }

        public int getFlag() {
            return flag;
        }
    }
}
