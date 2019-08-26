
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

import com.esri.arcgisruntime.data.Feature;

import java.util.List;
import java.util.Map;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

public class TableCoSoKinhDoanhAdapter extends ArrayAdapter<Feature> {
    private Context context;
    private List<Feature> items;


    public TableCoSoKinhDoanhAdapter(Context context, List<Feature> items) {
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

    public List<Feature> getItems() {
        return items;
    }

    public void setItems(List<Feature> items) {
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
        Feature item = items.get(position);
        Map<String, Object> attributes = item.getAttributes();
        TextView txt_tracuu_tencongty = (TextView) convertView.findViewById(R.id.txt_tracuu_tencongty);
        TextView txt_tracuu_makinhdoanh = (TextView) convertView.findViewById(R.id.txt_tracuu_makinhdoanh);
        TextView txt_tracuu_diachi = (TextView) convertView.findViewById(R.id.txt_tracuu_diachi);
        Object tenDoanhNghiep = attributes.get(Constant.CSKDTableFields.TenDoanhNghiep);
        Object maKinhDoanh = attributes.get(Constant.CSKDTableFields.MaKinhDoanh);
        Object diaChi = attributes.get(Constant.CSKDTableFields.DiaChi);
        Object x = attributes.get(Constant.CSKDTableFields.X);
        Object y = attributes.get(Constant.CSKDTableFields.Y);
        if (tenDoanhNghiep != null)
            txt_tracuu_tencongty.setText(tenDoanhNghiep.toString());
        if (maKinhDoanh != null)
            txt_tracuu_makinhdoanh.setText(maKinhDoanh.toString());
        if (diaChi != null)
            txt_tracuu_diachi.setText(diaChi.toString());
        if((x == null || x.equals("")) && (y == null || y.equals(""))){
            txt_tracuu_tencongty.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            txt_tracuu_tencongty.setTypeface(Typeface.DEFAULT);
        }
        return convertView;
    }


}
