package bentre.ditagis.com.capnhatthongtin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.adapter.TableCoSoKinhDoanhAdapter;
import bentre.ditagis.com.capnhatthongtin.async.QueryTableCoSoKinhDoanhAsync;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.mapping.MapViewAddDoneLoadingListener;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

public class TraCuuActivity extends AppCompatActivity {
    private TextView txtTongItem;
    private ServiceFeatureTable serviceFeatureTable;
    private DApplication mDApplication;
    TextView diaDiem, trangThai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_cuu);
        mDApplication = (DApplication) getApplication();
        serviceFeatureTable = (ServiceFeatureTable) mDApplication.getTable_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
        this.txtTongItem = this.findViewById(R.id.txtTongItem);
        ((LinearLayout) TraCuuActivity.this.findViewById(R.id.layout_tracuu)).setOnClickListener(v -> showQueryDialog());
        diaDiem = TraCuuActivity.this.findViewById(R.id.txt_tracuu_diadiem);
        trangThai = TraCuuActivity.this.findViewById(R.id.txt_tracuu_trangthai);
        ParameterQuery parameterQuery = new ParameterQuery();
        parameterQuery.setTrangThai(getString(R.string.TT_ChuaXuLi));
        queryFeatures(parameterQuery);
    }

    private void showQueryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        View layout = getLayoutInflater().inflate(R.layout.layout_tracuu_theothuoctinh, null);
        builder.setView(layout);
        Spinner spinnerTrangThai = layout.findViewById(R.id.spin_edit_trangthai);
        Spinner spinnerHuyenTP = layout.findViewById(R.id.spin_edit_huyentp);
        Spinner spinnerPhuongXa = layout.findViewById(R.id.spin_edit_phuongxa);
        Button btnAcceptQuery = layout.findViewById(R.id.btn_AcceptQuery);
        ArrayList<String> phuongXaCodes = new ArrayList<>();
        ArrayList<String> huyenTPCodes = new ArrayList<>();
        HashMap<String, String> hashMapHuyenTP = mDApplication.getHashMapHuyenTP();
        ArrayList<MapViewAddDoneLoadingListener.HanhChinhXa> hanhChinhXaList = mDApplication.getHanhChinhXaList();
        huyenTPCodes.add("");
        for (Map.Entry<String, String> entry : hashMapHuyenTP.entrySet()) {
            String value = entry.getValue();
            huyenTPCodes.add(value);
        }
        ArrayAdapter<String> adapterHuyenTP = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, huyenTPCodes);
        spinnerHuyenTP.setAdapter(adapterHuyenTP);
        ArrayAdapter<String> adapterPhuongXa = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, phuongXaCodes);
        spinnerPhuongXa.setAdapter(adapterPhuongXa);
        spinnerHuyenTP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Object itemAtPosition = spinnerHuyenTP.getItemAtPosition(position);
                String tenHuyenTP = itemAtPosition.toString();
                adapterPhuongXa.clear();
                adapterPhuongXa.add("");
                for (Map.Entry<String, String> entry : hashMapHuyenTP.entrySet()) {
                    String value = entry.getValue();
                    String code = entry.getKey();
                    if (value.equals(tenHuyenTP)) {
                        for (MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa : hanhChinhXaList) {
                            if (code.equals(hanhChinhXa.getMaHuyenTP())) {
                                adapterPhuongXa.add(hanhChinhXa.getTenPhuongXa());
                            }
                        }
                        break;
                    }
                }
                adapterPhuongXa.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        final AlertDialog selectQueryDialog = builder.create();
        selectQueryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectQueryDialog.show();
        btnAcceptQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object huyenTPSelectedItem = spinnerHuyenTP.getSelectedItem();
                Object phuongXaSelectedItem = spinnerPhuongXa.getSelectedItem();
                Object trangThaiSelectedItem = spinnerTrangThai.getSelectedItem();
                ParameterQuery parameterQuery = new ParameterQuery();
                MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa = new MapViewAddDoneLoadingListener.HanhChinhXa();
                if (phuongXaSelectedItem.toString() != "" && phuongXaSelectedItem != null) {
                    hanhChinhXa = getMaHanhChinh(phuongXaSelectedItem.toString());
                    diaDiem.setText(phuongXaSelectedItem.toString() + " - " + huyenTPSelectedItem.toString());
                } else if (huyenTPSelectedItem != "" && huyenTPSelectedItem != null) {
                    String maHuyenTP = getMaHuyenTP(huyenTPSelectedItem.toString());
                    hanhChinhXa.setMaHuyenTP(maHuyenTP);
                    diaDiem.setText(" * - " + huyenTPSelectedItem.toString());
                } else {
                    diaDiem.setText("Toàn tỉnh Bến Tre");
                }
                parameterQuery.setHanhChinhXa(hanhChinhXa);
                if (trangThaiSelectedItem.toString() != "" && trangThaiSelectedItem != null) {
                    parameterQuery.setTrangThai(trangThaiSelectedItem.toString());
                    trangThai.setText(trangThaiSelectedItem.toString());
                } else {
                    trangThai.setText("Tất cả");
                }
                if (selectQueryDialog.isShowing()) {
                    queryFeatures(parameterQuery);
                }
                selectQueryDialog.dismiss();


            }
        });


    }

    private String getMaHuyenTP(String tenHuyenTP) {
        HashMap<String, String> hashMapHuyenTP = mDApplication.getHashMapHuyenTP();
        for (Map.Entry<String, String> entry : hashMapHuyenTP.entrySet()) {
            String value = entry.getValue();
            String code = entry.getKey();
            if (value.equals(tenHuyenTP)) {
                return code;
            }
        }
        return null;
    }

    private MapViewAddDoneLoadingListener.HanhChinhXa getMaHanhChinh(String tenPhuongXa) {
        ArrayList<MapViewAddDoneLoadingListener.HanhChinhXa> hanhChinhXaList = mDApplication.getHanhChinhXaList();
        for (MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa : hanhChinhXaList) {
            if (tenPhuongXa.equals(hanhChinhXa.getTenPhuongXa())) {
                return hanhChinhXa;
            }
        }
        return null;
    }

    private void queryFeatures(ParameterQuery parameterQuery) {
        StringBuilder builder = new StringBuilder();
        QueryParameters queryParameters = new QueryParameters();
        if (parameterQuery != null) {
            MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa = parameterQuery.getHanhChinhXa();
            if (hanhChinhXa != null) {
                if (hanhChinhXa.getMaPhuongXa() != null) {
                    builder.append(String.format("%s = %s", getString(R.string.MaPhuongXa), hanhChinhXa.getMaPhuongXa()));
                    builder.append(" and ");
                } else if (hanhChinhXa.getMaHuyenTP() != null) {
                    builder.append(String.format("%s = %s", getString(R.string.MaHuyenTP), hanhChinhXa.getMaHuyenTP()));
                    builder.append(" and ");
                }
            }
            if (parameterQuery.getTrangThai().equals(getString(R.string.TT_ChuaXuLi))) {
                builder.append(String.format("%s = ''", getString(R.string.TOADOX)));
                builder.append(" or ");
                builder.append(String.format("%s = ''", getString(R.string.TOADOY)));
                builder.append(" and ");
            } else if (parameterQuery.getTrangThai().equals("Đã xử lý")) {
                builder.append(String.format("%s <> ''", getString(R.string.TOADOX)));
                builder.append(" and ");
                builder.append(String.format("%s <> ''", getString(R.string.TOADOY)));
                builder.append(" and ");
            }
        }
        builder.append(" 1 = 1 ");
        queryParameters.setWhereClause(builder.toString());
        ListView listView = (ListView) findViewById(R.id.listview);
        final List<TableCoSoKinhDoanhAdapter.Item> items = new ArrayList<>();
        final TableCoSoKinhDoanhAdapter adapter = new TableCoSoKinhDoanhAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                Intent intent = new Intent();
                TableCoSoKinhDoanhAdapter.Item item = adapter.getItems().get(position);
                getSelectedFeature(item.getObjectID());
                if (item.getToaDoX().equals("") || item.getToaDoX().equals("")) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });
        new QueryTableCoSoKinhDoanhAsync(this, serviceFeatureTable, txtTongItem, adapter, features -> {
        }).execute(builder.toString());

    }

    public void getSelectedFeature(String objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    if (result.iterator().hasNext()) {
                        Feature item = result.iterator().next();
                        mDApplication.setSelectedFeatureTBL(item);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public class ParameterQuery {
        private MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa;
        private String trangThai;

        public ParameterQuery() {
        }

        public MapViewAddDoneLoadingListener.HanhChinhXa getHanhChinhXa() {
            return hanhChinhXa;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public void setHanhChinhXa(MapViewAddDoneLoadingListener.HanhChinhXa hanhChinhXa) {
            this.hanhChinhXa = hanhChinhXa;
        }

        public void setTrangThai(String trangThai) {
            this.trangThai = trangThai;
        }
    }


}
