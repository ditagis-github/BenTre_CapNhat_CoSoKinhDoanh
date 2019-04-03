
package bentre.ditagis.com.capnhatthongtin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bentre.ditagis.com.capnhatthongtin.R;

public class MauKiemNghiemApdapter extends ArrayAdapter<MauKiemNghiemApdapter.MauKiemNghiem> {
    private Context context;
    private List<MauKiemNghiemApdapter.MauKiemNghiem> mauKiemNghiems;

    public MauKiemNghiemApdapter(Context context, List<MauKiemNghiemApdapter.MauKiemNghiem> mauKiemNghiems) {
        super(context, 0, mauKiemNghiems);
        this.context = context;
        this.mauKiemNghiems = mauKiemNghiems;
    }

    public List<MauKiemNghiemApdapter.MauKiemNghiem> getMauKiemNghiems() {
        return mauKiemNghiems;
    }

    public void setMauKiemNghiems(List<MauKiemNghiemApdapter.MauKiemNghiem> mauKiemNghiems) {
        this.mauKiemNghiems = mauKiemNghiems;
    }

    public void clear() {
        mauKiemNghiems.clear();
    }

    @Override
    public int getCount() {
        return mauKiemNghiems.size();
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
        MauKiemNghiem mauKiemNghiem = mauKiemNghiems.get(position);
        TextView textViewItem1 = (TextView) convertView.findViewById(R.id.txtItem1);
        TextView textViewItem2 = (TextView) convertView.findViewById(R.id.txtItem2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_Item);
        textViewItem1.setText(mauKiemNghiem.getIdMauKiemNghiem());
        textViewItem2.setText(mauKiemNghiem.getTenMau());
        if (mauKiemNghiem.isView) {
            imageView.setVisibility(View.VISIBLE);
        } else imageView.setVisibility(View.GONE);
        return convertView;
    }

    public static class MauKiemNghiem {
        private String OBJECTID;
        private String idMauKiemNghiem;
        private String tenMau;
        private Boolean isView;

        public MauKiemNghiem() {
        }

        public Boolean isView() {
            return isView;
        }

        public void setView(Boolean isView) {
            this.isView = isView;
        }

        public String getOBJECTID() {
            return OBJECTID;
        }

        public void setOBJECTID(String OBJECTID) {
            this.OBJECTID = OBJECTID;
        }

        public String getIdMauKiemNghiem() {
            return idMauKiemNghiem;
        }

        public void setIdMauKiemNghiem(String idMauKiemNghiem) {
            this.idMauKiemNghiem = idMauKiemNghiem;
        }

        public String getTenMau() {
            return tenMau;
        }

        public void setTenMau(String tenMau) {
            this.tenMau = tenMau;
        }
    }

}
