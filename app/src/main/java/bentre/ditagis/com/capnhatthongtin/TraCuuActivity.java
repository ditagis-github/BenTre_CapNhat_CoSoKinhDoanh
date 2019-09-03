package bentre.ditagis.com.capnhatthongtin;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Collections;
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
    private ServiceFeatureTable mServiceFeatureTable;
    private DApplication mDApplication;
    TextView txtDiaDiemHuyen, txtDiaDiemXa, txtTrangThai;
    private ParameterQuery mParameterQuery;
    private Dialog mDialogQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_cuu);
        mDApplication = (DApplication) getApplication();
        mServiceFeatureTable = (ServiceFeatureTable) mDApplication.getTable_CoSoKinhDoanhChuaCapNhatDTG().getFeatureLayer().getFeatureTable();
        this.txtTongItem = this.findViewById(R.id.txtTongItem);
        TraCuuActivity.this.findViewById(R.id.layout_tracuu).setOnClickListener(v -> showQueryDialog());
        txtDiaDiemHuyen = TraCuuActivity.this.findViewById(R.id.txt_tracuu_diadiem_huyen);
        txtDiaDiemXa = TraCuuActivity.this.findViewById(R.id.txt_tracuu_diadiem_xa);
        txtTrangThai = TraCuuActivity.this.findViewById(R.id.txt_tracuu_trangthai);
        if (mDApplication.getParameterQuery() != null) {
            mParameterQuery = mDApplication.getParameterQuery();
        } else {
            mParameterQuery = new ParameterQuery();
            mParameterQuery.setTrangThai(getString(R.string.TT_ChuaXuLi));
        }
        queryFeatures();
    }

    private void showQueryDialog() {
        if (mDialogQuery != null)
            mDialogQuery.show();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
            View layout = getLayoutInflater().inflate(R.layout.layout_tracuu_theothuoctinh, null);
            builder.setView(layout);
            Spinner spinnerTrangThai = layout.findViewById(R.id.spin_edit_trangthai);
            Spinner spinnerHuyenTP = layout.findViewById(R.id.spin_edit_huyentp);
            Spinner spinnerPhuongXa = layout.findViewById(R.id.spin_edit_phuongxa);
            EditText editTop = layout.findViewById(R.id.edit__tra_cuu__top);
            Button btnAcceptQuery = layout.findViewById(R.id.btn_AcceptQuery);
            ArrayList<String> phuongXaCodes = new ArrayList<>();
            ArrayList<String> huyenTPCodes = new ArrayList<>();
            HashMap<String, String> hashMapHuyenTP = mDApplication.getHashMapHuyenTP();
            huyenTPCodes.add(getString(R.string.whole_province));
            huyenTPCodes.add(getString(R.string.value_is_null));
            List<String> tenHuyenTPs = new ArrayList<>();
            for (Map.Entry<String, String> entry : hashMapHuyenTP.entrySet()) {
                String value = entry.getValue();
                tenHuyenTPs.add(value);

            }
            Collections.sort(tenHuyenTPs);
            huyenTPCodes.addAll(tenHuyenTPs);
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
                    for (Map.Entry<String, String> entry : hashMapHuyenTP.entrySet()) {
                        String value = entry.getValue();
                        String code = entry.getKey();
                        if (value.equals(tenHuyenTP)) {
                            adapterPhuongXa.add(getString(R.string.all_district));
                            adapterPhuongXa.add(getString(R.string.value_is_null));
                            ArrayList<MapViewAddDoneLoadingListener.HanhChinh> hanhChinhXaList = mDApplication.getHanhChinhXaList();
                            List<String> tenPhuongXas = new ArrayList<>();
                            if (hanhChinhXaList != null) {
                                for (MapViewAddDoneLoadingListener.HanhChinh hanhChinhXa : hanhChinhXaList) {
                                    if (code.equals(hanhChinhXa.getMaHuyenTP())) {
                                        tenPhuongXas.add(hanhChinhXa.getTenPhuongXa());
                                    }
                                }
                                Collections.sort(tenPhuongXas);
                                adapterPhuongXa.addAll(tenPhuongXas);
                            }
                            break;
                        }
                    }
                    if (mParameterQuery.getHanhChinhXa() != null) {
                        String selectValuePhuongXa = mParameterQuery.getHanhChinhXa().getMaPhuongXa();
                        if (selectValuePhuongXa != null) {
                            spinnerPhuongXa.setSelection(adapterPhuongXa.getPosition(selectValuePhuongXa));
                        }
                    }
                    adapterPhuongXa.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });
            mDialogQuery = builder.create();
            mDialogQuery.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogQuery.show();
            btnAcceptQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object huyenTPSelectedItem = spinnerHuyenTP.getSelectedItem();
                    Object phuongXaSelectedItem = spinnerPhuongXa.getSelectedItem();
                    Object trangThaiSelectedItem = spinnerTrangThai.getSelectedItem();
                    MapViewAddDoneLoadingListener.HanhChinh hanhChinh = new MapViewAddDoneLoadingListener.HanhChinh();
                    if (huyenTPSelectedItem != null) {
                        String maHuyenTP = getMaHuyenTP(huyenTPSelectedItem.toString());
                        if (maHuyenTP != null) {
                            hanhChinh.setMaHuyenTP(maHuyenTP);
                        }
                        hanhChinh.setTenHuyenTP(huyenTPSelectedItem.toString());
                        if (phuongXaSelectedItem != null) {
                            String maPhuongXa = getMaPhuongXa(phuongXaSelectedItem.toString(), maHuyenTP);
                            if (maPhuongXa != null) {
                                hanhChinh.setMaPhuongXa(maPhuongXa);
                            }
                            hanhChinh.setTenPhuongXa(phuongXaSelectedItem.toString());
                        }
                    }


                    mParameterQuery.setTop(Integer.parseInt(editTop.getText().toString()));
                    mParameterQuery.setHanhChinhXa(hanhChinh);
                    if (trangThaiSelectedItem != null) {
                        mParameterQuery.setTrangThai(trangThaiSelectedItem.toString());
                    }
                    if (mDialogQuery.isShowing()) {
                        queryFeatures();
                    }
                    mDialogQuery.dismiss();


                }
            });
            if (mParameterQuery != null) {
                String trangThai = mParameterQuery.getTrangThai();
                if (trangThai != null) {
                    SpinnerAdapter adapter = spinnerTrangThai.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).toString().equals(trangThai)) {
                            spinnerTrangThai.setSelection(i);
                            break;
                        }
                    }
                }
                if (mParameterQuery.getHanhChinhXa() != null) {
                    String selectValueHuyenTP = mParameterQuery.getHanhChinhXa().getMaHuyenTP();
                    if (selectValueHuyenTP != null) {
                        spinnerHuyenTP.setSelection(huyenTPCodes.indexOf(selectValueHuyenTP));
                    }
                }
            }
        }
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

    private String getMaPhuongXa(String tenPhuongXa, String maHuyenTP) {
        ArrayList<MapViewAddDoneLoadingListener.HanhChinh> hanhChinhXaList = mDApplication.getHanhChinhXaList();
        for (MapViewAddDoneLoadingListener.HanhChinh hanhChinhXa : hanhChinhXaList) {
            if (hanhChinhXa.getMaHuyenTP().equals(maHuyenTP) && hanhChinhXa.getTenPhuongXa().equals(tenPhuongXa)) {
                return hanhChinhXa.getMaPhuongXa();
            }
        }
        return null;
    }

    private void queryFeatures() {
        mDApplication.setParameterQuery(mParameterQuery);
        StringBuilder builder = new StringBuilder();
        QueryParameters queryParameters = new QueryParameters();
        if (mParameterQuery != null) {
            MapViewAddDoneLoadingListener.HanhChinh hanhChinh = mParameterQuery.getHanhChinhXa();
            if (hanhChinh != null) {
                txtDiaDiemHuyen.setText(hanhChinh.getTenHuyenTP());
                if (hanhChinh.getTenHuyenTP().equals(TraCuuActivity.this.getString(R.string.value_is_null))) {
                    builder.append(String.format("%s is null", Constant.CSKDTableFields.MaHuyenTP));
                    builder.append(" and ");
                } else if (hanhChinh.getMaHuyenTP() != null) {
                    builder.append(String.format("%s = %s", Constant.CSKDTableFields.MaHuyenTP, hanhChinh.getMaHuyenTP()));
                    builder.append(" and ");
                }
                if (hanhChinh.getMaPhuongXa() != null) {
                    txtDiaDiemXa.setText(hanhChinh.getTenPhuongXa());
                    if (hanhChinh.getMaPhuongXa() .equals(TraCuuActivity.this.getString(R.string.value_is_null))) {
                        builder.append(String.format("%s is null", Constant.CSKDTableFields.MaPhuongXa));
                        builder.append(" and ");
                    } else if (hanhChinh.getMaPhuongXa() != null) {
                        builder.append(String.format("%s = %s", Constant.CSKDTableFields.MaPhuongXa, hanhChinh.getMaPhuongXa()));
                        builder.append(" and ");
                    }
                }
            }
            if (mParameterQuery.getTrangThai().equals(getString(R.string.TT_ChuaXuLi))) {
                builder.append("(X = '' or Y = '' or X is null or Y is null)");
                builder.append(" and ");
            } else if (mParameterQuery.getTrangThai().equals("Đã xử lý")) {
                builder.append(String.format("%s <> ''", Constant.CSKDTableFields.X));
                builder.append(" and ");
                builder.append(String.format("%s <> ''", Constant.CSKDTableFields.Y));
                builder.append(" and ");
            }
            txtTrangThai.setText(mParameterQuery.getTrangThai());
        }
        builder.append(" 1 = 1 ");
        queryParameters.setWhereClause(builder.toString());
        ListView listView = findViewById(R.id.listview);
        final List<Feature> items = new ArrayList<>();
        final TableCoSoKinhDoanhAdapter adapter = new TableCoSoKinhDoanhAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Feature item = adapter.getItems().get(position);
            getSelectedFeature(item);
            mDApplication.getMapViewHandler().queryByMaKinhDoanh(item);
            finish();
        });
        new QueryTableCoSoKinhDoanhAsync(this, mServiceFeatureTable, txtTongItem, adapter, features -> {
        }, mParameterQuery.top).execute(builder.toString());

    }

    public void getSelectedFeature(Feature item) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + item.getAttributes().get(Constant.OBJECTID);
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature next = result.iterator().next();
                    mDApplication.setSelectedFeatureTBL(next);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    public class ParameterQuery {
        private MapViewAddDoneLoadingListener.HanhChinh hanhChinhXa;
        private String trangThai;
        private int top;

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public ParameterQuery() {
        }

        public MapViewAddDoneLoadingListener.HanhChinh getHanhChinhXa() {
            return hanhChinhXa;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public void setHanhChinhXa(MapViewAddDoneLoadingListener.HanhChinh hanhChinhXa) {
            this.hanhChinhXa = hanhChinhXa;
        }

        public void setTrangThai(String trangThai) {
            this.trangThai = trangThai;
        }
    }


}
