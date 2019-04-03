package bentre.ditagis.com.capnhatthongtin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Field;

import java.util.Calendar;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;


public class ChiTietMauKiemNghiemAdapter extends ArrayAdapter<ChiTietMauKiemNghiemAdapter.Item> {
    private Context context;
    private List<ChiTietMauKiemNghiemAdapter.Item> items;


    public ChiTietMauKiemNghiemAdapter(Context context, List<ChiTietMauKiemNghiemAdapter.Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    public List<ChiTietMauKiemNghiemAdapter.Item> getItems() {
        return items;
    }

    public void setItems(List<ChiTietMauKiemNghiemAdapter.Item> items) {
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
            convertView = inflater.inflate(R.layout.item_text_text_image, null);
        }
        ChiTietMauKiemNghiemAdapter.Item item = items.get(position);
        TextView textViewItem1 = (TextView) convertView.findViewById(R.id.txtItem1);
        TextView textViewItem2 = (TextView) convertView.findViewById(R.id.txtItem2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_Item);
        textViewItem1.setText(item.getAlias());
        textViewItem2.setText(item.getValue());
        if (item.isEdit) {
            imageView.setVisibility(View.VISIBLE);
        } else imageView.setVisibility(View.GONE);

        return convertView;
    }


    public static class Item {
        private String alias;
        private String value;
        private boolean isEdit;
        private String fieldName;
        private Field.Type fieldType;
        private Calendar calendar;


        public Item(String alias, String value) {
            this.alias = alias;
            this.value = value;
            this.isEdit = false;
        }

        public Item() {
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }

        public boolean isEdit() {
            return isEdit;
        }

        public void setEdit(boolean edit) {
            isEdit = edit;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldType(Field.Type fieldType) {
            this.fieldType = fieldType;
        }

        public Field.Type getFieldType() {
            return fieldType;
        }
    }
}
