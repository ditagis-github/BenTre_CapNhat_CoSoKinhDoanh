package bentre.ditagis.com.capnhatthongtin.utities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bentre.ditagis.com.capnhatthongtin.MainActivity;
import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.adapter.FeatureViewMoreInfoAdapter;
import bentre.ditagis.com.capnhatthongtin.async.EditAsync;
import bentre.ditagis.com.capnhatthongtin.async.NotifyDataSetChangeAsync;
import bentre.ditagis.com.capnhatthongtin.common.DApplication;
import bentre.ditagis.com.capnhatthongtin.libs.FeatureLayerDTG;

public class Popup extends AppCompatActivity {
    private MainActivity mainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable layer_CoSoKinhDoanh;
    private Callout mCallout;
    private FeatureLayerDTG featureLayerDTG;
    private List<String> lstFeatureType;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private static double DELTA_MOVE_Y = 0;//7000;
    private DApplication mDApplication;

    public Popup(MainActivity mainActivity, MapView mMapView, Callout callout) {
        this.mainActivity = mainActivity;
        this.mDApplication = (DApplication) this.mainActivity.getApplication();
        this.layer_CoSoKinhDoanh = (ServiceFeatureTable) this.mDApplication.getLayer_CoSoKinhDoanhDTG().getFeatureLayer().getFeatureTable();
        this.mCallout = callout;
        this.mMapView = mMapView;
        featureLayerDTG = mDApplication.getLayer_CoSoKinhDoanhDTG();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void refreshPopup() {
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attributes.get(field.getName());
            switch (field.getName()) {
                case Constant.CSKDLayerFields.TenDoanhNghiep:
                    if (value != null)
                        ((TextView) linearLayout.findViewById(R.id.txt_ten_cskd)).setText(value.toString());
                    break;
                case Constant.CSKDLayerFields.DiaChi:
                    if (value != null)
                        ((TextView) linearLayout.findViewById(R.id.txt_vitri_cskd)).setText(value.toString());
                    break;
            }
        }
    }

    public void dimissCallout() {
        FeatureLayer featureLayer = featureLayerDTG.getFeatureLayer();
        featureLayer.clearSelection();
        if (mCallout != null && mCallout.isShowing()) {
            mCallout.dismiss();
        }
    }

    public LinearLayout showPopup(final ArcGISFeature arcGISFeature) {
        dimissCallout();
        this.mSelectedArcGISFeature = arcGISFeature;
        FeatureLayer featureLayer = featureLayerDTG.getFeatureLayer();
        featureLayer.selectFeature(arcGISFeature);
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < arcGISFeature.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(arcGISFeature.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.popup, null);
        linearLayout.findViewById(R.id.img_popup_close)
                .setOnClickListener(view -> {
                    if (mCallout != null && mCallout.isShowing()) mCallout.dismiss();
                    featureLayer.clearSelection();
                });
        refreshPopup();
        Object nguoiTao = arcGISFeature.getAttributes().get(Constant.CSKDLayerFields.NguoiTao);
        boolean isUserCreate = nguoiTao != null && nguoiTao.toString().equals(mDApplication.getUser().getUserName());
        if (featureLayerDTG.getAction().isEdit()) {
            if (isUserCreate) {
                ImageButton imgBtn_ViewMoreInfo = linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo);
            imgBtn_ViewMoreInfo.setVisibility(View.VISIBLE);
                imgBtn_ViewMoreInfo.setOnClickListener(v -> viewMoreInfo());
            }
            ImageButton imgBtn_changeLocation = linearLayout.findViewById(R.id.imgBtn_changeLocation);
            imgBtn_changeLocation.setVisibility(View.VISIBLE);
            imgBtn_changeLocation.setOnClickListener(v -> changeLocation());
        }
        if (featureLayerDTG.getAction().isDelete() && isUserCreate) {
            ImageButton imgBtn_delete = linearLayout.findViewById(R.id.imgBtn_delete);
            imgBtn_delete.setVisibility(View.VISIBLE);
            imgBtn_delete.setOnClickListener(v -> {
                arcGISFeature.getFeatureTable().getFeatureLayer().clearSelection();
                deleteFeature();
            });
        }
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = arcGISFeature.getGeometry().getExtent();
        mMapView.setViewpointGeometryAsync(envelope, 0);
        // show CallOut
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallout.refresh();
                mCallout.show();
            }
        });
        return linearLayout;
    }

    private void changeLocation(){
        dimissCallout();
        mDApplication.getMainActivity().addFeature();
    }
    private void viewMoreInfo() {
        Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        final FeatureViewMoreInfoAdapter adapter = new FeatureViewMoreInfoAdapter(mainActivity, new ArrayList<FeatureViewMoreInfoAdapter.Item>());
        final ListView lstView_ViewMoreInfo = layout.findViewById(R.id.lstView_alertdialog_info);
        lstView_ViewMoreInfo.setAdapter(adapter);
        lstView_ViewMoreInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit(parent, view, position, id);
            }
        });
        String[] updateFields = featureLayerDTG.getUpdateFields();
        String[] uneditFields = mainActivity.getResources().getStringArray(R.array.uneditFields);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attr.get(field.getName());
            FeatureViewMoreInfoAdapter.Item item = new FeatureViewMoreInfoAdapter.Item();
            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            if (value != null) {
                if (field.getName().equals(Constant.CSKDLayerFields.TenDoanhNghiep)) {
                    ((TextView) layout.findViewById(R.id.txt_ten_cskd)).setText(value.toString());
                }
                if (item.getFieldName().equals(typeIdField)) {
                    List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                    String valueFeatureType = getValueFeatureType(featureTypes, value.toString()).toString();
                    if (valueFeatureType != null) item.setValue(valueFeatureType);
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                    Object valueDomain = getValueDomain(codedValues, value.toString());
                    if (valueDomain != null) item.setValue(valueDomain.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
                        item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                    case OID:
                    case TEXT:
                        item.setValue(value.toString());
                        break;
                    case SHORT:
                        item.setValue(value.toString());
                        break;
                }
            }
            item.setEdit(false);
            if (updateFields.length > 0) {
                if (updateFields[0].equals("*") || updateFields[0].equals("")) {
                    item.setEdit(true);
                } else {
                    for (String updateField : updateFields) {
                        if (item.getFieldName().equals(updateField)) {
                            item.setEdit(true);
                            break;
                        }
                    }
                }
                for (String uneditField : uneditFields) {
                    if (item.getFieldName().equals(uneditField)) {
                        item.setEdit(false);
                        break;
                    }
                }
            }
            item.setFieldType(field.getFieldType());
            adapter.add(item);
            adapter.notifyDataSetChanged();
        }
        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton(mainActivity.getString(R.string.btn_Accept), null);
        builder.setNeutralButton(mainActivity.getString(R.string.btn_Esc), null);
        final AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setPositiveButton(android.R.string.ok, null);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAsync editAsync = new EditAsync(mainActivity, layer_CoSoKinhDoanh, mSelectedArcGISFeature);
                try {
                    editAsync.execute(adapter).get();
                    refreshPopup();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private Object getValueDomain(List<CodedValue> codedValues, String code) {
        Object value = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getCode().toString().equals(code)) {
                value = codedValue.getName();
                break;
            }

        }
        return value;
    }

    private Object getValueFeatureType(List<FeatureType> featureTypes, String code) {
        Object value = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getId().toString().equals(code)) {
                value = featureType.getName();
                break;
            }
        }
        return value;
    }

    private void edit(final AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) instanceof FeatureViewMoreInfoAdapter.Item) {
            final FeatureViewMoreInfoAdapter.Item item = (FeatureViewMoreInfoAdapter.Item) parent.getItemAtPosition(position);
            if (item.isEdit()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Cập nhật thuộc tính");
                builder.setMessage(item.getAlias());
                builder.setCancelable(false).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().
                        inflate(R.layout.layout_dialog_update_feature_listview, null);

                final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
                final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
                ImageView img_selectTime = (ImageView) layout.findViewById(R.id.img_selectTime);
                final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
                final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
                final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
                final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

                final Domain domain = mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
                if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField())) {
                    layoutSpin.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, lstFeatureType);
                    spin.setAdapter(adapter);
                    if (item.getValue() != null)
                        spin.setSelection(lstFeatureType.indexOf(item.getValue()));
                } else if (domain != null) {
                    layoutSpin.setVisibility(View.VISIBLE);
                    List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                    if (codedValues != null) {
                        List<String> codes = new ArrayList<>();
                        for (CodedValue codedValue : codedValues)
                            codes.add(codedValue.getName());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                        spin.setAdapter(adapter);
                        if (item.getValue() != null)
                            spin.setSelection(codes.indexOf(item.getValue()));

                    }
                } else switch (item.getFieldType()) {
                    case DATE:
                        layoutTextView.setVisibility(View.VISIBLE);
                        textView.setText(item.getValue());
                        img_selectTime.setOnClickListener(v -> {
                            final View dialogView = View.inflate(mainActivity, R.layout.date_time_picker, null);
                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mainActivity).create();
                            dialogView.findViewById(R.id.date_time_set).setOnClickListener(view1 -> {
                                DatePicker datePicker =  dialogView.findViewById(R.id.date_picker);
                                String s = String.format("%02d/%02d/%d", datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
                                textView.setText(s);
                                alertDialog.dismiss();
                            });
                            alertDialog.setView(dialogView);
                            alertDialog.show();
                        });
                        break;
                    case TEXT:
                        layoutEditText.setVisibility(View.VISIBLE);
                        editText.setText(item.getValue());
                        break;
                    case SHORT:
                        layoutEditText.setVisibility(View.VISIBLE);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setText(item.getValue());


                        break;
                    case DOUBLE:
                        layoutEditText.setVisibility(View.VISIBLE);
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(item.getValue());
                        break;
                }
                builder.setPositiveButton("Cập nhật", (dialog, which) -> {
                    if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField()) || (domain != null)) {
                        item.setValue(spin.getSelectedItem().toString());
                    } else {
                        switch (item.getFieldType()) {
                            case DATE:
                                item.setValue(textView.getText().toString());
                                break;
                            case DOUBLE:
                                try {
                                    double x = Double.parseDouble(editText.getText().toString());
                                    item.setValue(editText.getText().toString());
                                } catch (Exception e) {
                                    Toast.makeText(mainActivity, R.string.input_format_incorrect, Toast.LENGTH_LONG).show();
                                }
                                break;
                            case TEXT:
                                item.setValue(editText.getText().toString());
                                break;
                            case SHORT:
                                try {
                                    short x = Short.parseShort(editText.getText().toString());
                                    item.setValue(editText.getText().toString());
                                } catch (Exception e) {
                                    Toast.makeText(mainActivity, R.string.input_format_incorrect, Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                    dialog.dismiss();
                    FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
                    new NotifyDataSetChangeAsync(mainActivity).execute(adapter);
                });
                builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();

            }
        }

    }

    public void getSelectedFeatureAndUpdateCSKDTable(String maKinhDoanh) {
        mDApplication.setSelectedFeatureTBL(null);
        final QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s = '%s'", Constant.CSKDTableFields.MaKinhDoanh, maKinhDoanh));
        queryParameters.setWhereClause(builder.toString());
        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) mDApplication.getTable_CoSoKinhDoanhChuaCapNhatDTG().getFeatureLayer().getFeatureTable();
        final ListenableFuture<FeatureQueryResult> feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    mDApplication.setSelectedFeatureTBL(item);
                    //TODO update CSKDTable null
//                    mDApplication.getMapViewHandler().updateCSKDTable(null);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    private void deleteFeature() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("Xác nhận");
        builder.setMessage(R.string.question_delete_point);
        builder.setPositiveButton("Có", (dialog, which) -> {
            dialog.dismiss();
            mSelectedArcGISFeature.loadAsync();
            Object maKinhDoanh = mSelectedArcGISFeature.getAttributes().get(Constant.CSKDLayerFields.MaKinhDoanh);
            if (maKinhDoanh != null) {
                getSelectedFeatureAndUpdateCSKDTable(maKinhDoanh.toString());
            }
            // update the selected feature
            mSelectedArcGISFeature.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (mSelectedArcGISFeature.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                        Log.d(getResources().getString(R.string.app_name), "Error while loading feature");
                    }
                    try {
                        // update feature in the feature table
                        ListenableFuture<Void> mapViewResult = layer_CoSoKinhDoanh.deleteFeatureAsync(mSelectedArcGISFeature);
                        mapViewResult.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                // apply change to the server
                                final ListenableFuture<List<FeatureEditResult>> serverResult = layer_CoSoKinhDoanh.applyEditsAsync();
                                serverResult.addDoneListener(() -> {
                                    List<FeatureEditResult> edits = null;
                                    try {
                                        edits = serverResult.get();
                                        if (edits.size() > 0) {
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                });
                            }
                        });

                    } catch (Exception e) {
                        Log.e(getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
                    }
                }
            });
            if (mCallout != null) mCallout.dismiss();
        }).setNegativeButton("Không", (dialog, which) -> dialog.dismiss()).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }
}
