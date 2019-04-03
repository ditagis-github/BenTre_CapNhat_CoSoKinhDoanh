package bentre.ditagis.com.capnhatthongtin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import bentre.ditagis.com.capnhatthongtin.R;

/**
 * Created by ThanLe on 04/10/2017.
 */

public class SettingsAdapter extends ArrayAdapter<SettingsAdapter.Item> {
    private Context context;
    private Item[] items;

    public SettingsAdapter(Context context, Item[] items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItemSubtitle(int position, String subTitle) {
        items[position].setSubTitle(subTitle);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_search, null);
        }
        Item item = items[position];


        TextView txtTitle = (TextView) convertView.findViewById(R.id.txt_settings_title);
        //todo
        txtTitle.setText(item.getTitle());

        TextView txtSubTitle = (TextView) convertView.findViewById(R.id.txt_settings_subtitle);
        //todo
        txtSubTitle.setText(item.getSubTitle());


        return convertView;
    }

    public static class Item {
        private String title;
        private String subTitle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public Item(String title, String subTitle) {
            this.title = title;
            this.subTitle = subTitle;
        }
    }
}
