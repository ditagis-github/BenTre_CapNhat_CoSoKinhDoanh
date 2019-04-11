package bentre.ditagis.com.capnhatthongtin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.entities.DAddress;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class DiaChiAdapter extends ArrayAdapter<DAddress> {
    private Context context;
    private List<DAddress> items;
    public DiaChiAdapter(Context context, List<DAddress> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    public List<DAddress> getItems() {
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_diachi, null);
        }
        DAddress item = items.get(position);
        TextView txtDiaChi = convertView.findViewById(R.id.txt_diachi);
        if(item.getAdminArea() != null) {
            txtDiaChi.setText(item.getLocation());
        }
        return convertView;
    }
}
