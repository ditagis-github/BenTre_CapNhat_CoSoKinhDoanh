package bentre.ditagis.com.capnhatthongtin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import bentre.ditagis.com.capnhatthongtin.adapter.DanhSachDiemDanhGiaAdapter;
import bentre.ditagis.com.capnhatthongtin.adapter.ThongKeAdapter;
import bentre.ditagis.com.capnhatthongtin.async.QueryDiemDanhGiaAsync;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.LayerInfoDTG;
import bentre.ditagis.com.capnhatthongtin.entities.entitiesDB.ListObjectDB;

public class TraCuuActivity extends AppCompatActivity {
    private TextView txtTongItem;
    private ServiceFeatureTable serviceFeatureTable;
    private ThongKeAdapter thongKeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        for (final LayerInfoDTG layerInfoDTG : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (layerInfoDTG.getId() != null && layerInfoDTG.getId().equals(getString(R.string.layer_cosokinhdoanh))) {
                String url = layerInfoDTG.getUrl();
                if (!layerInfoDTG.getUrl().startsWith("http"))
                    url = "http:" + layerInfoDTG.getUrl();
                serviceFeatureTable = new ServiceFeatureTable(url);
            }
        }
        this.txtTongItem = this.findViewById(R.id.txtTongItem);
        ((LinearLayout) TraCuuActivity.this.findViewById(R.id.layout_thongke_thoigian)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQueryDialog();
            }
        });
    }
    private void showQueryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        View layout = getLayoutInflater().inflate(R.layout.layout_tracuu_theothuoctinh, null);
        builder.setView(layout);
        layout.findViewById(R.id.spin_edit_trangthai);

        final AlertDialog selectQueryDialog = builder.create();
        selectQueryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectQueryDialog.show();

    }
    private void query(ThongKeAdapter.Item item) {
        ((TextView) TraCuuActivity.this.findViewById(R.id.txt_thongke_mota)).setText(item.getMota());
        TextView txtThoiGian = TraCuuActivity.this.findViewById(R.id.txt_thongke_thoigian);
        if (item.getThoigianhienthi() == null) txtThoiGian.setVisibility(View.GONE);
        else {
            txtThoiGian.setText(item.getThoigianhienthi());
            txtThoiGian.setVisibility(View.VISIBLE);
        }
        String whereClause = "1 = 1";
        if (item.getThoigianbatdau() == null || item.getThoigianketthuc() == null) {
            whereClause = "1 = 1";
        } else
            whereClause = "NgayCapNhat" + " >= date '" + item.getThoigianbatdau() + "' and " + "NgayCapNhat" + " <= date '" + item.getThoigianketthuc() + "'";
        getQueryDiemDanhGiaAsync(whereClause);


    }

    private void getQueryDiemDanhGiaAsync(String whereClause) {
        ListView listView = (ListView) findViewById(R.id.listview);
        final List<DanhSachDiemDanhGiaAdapter.Item> items = new ArrayList<>();
        final DanhSachDiemDanhGiaAdapter adapter = new DanhSachDiemDanhGiaAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.ket_qua_objectid), adapter.getItems().get(position).getObjectID());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        if (serviceFeatureTable != null)
            new QueryDiemDanhGiaAsync(this, serviceFeatureTable, txtTongItem, adapter, new QueryDiemDanhGiaAsync.AsyncResponse() {
                public void processFinish(List<Feature> features) {
                }
            }).execute(whereClause);
    }



}
