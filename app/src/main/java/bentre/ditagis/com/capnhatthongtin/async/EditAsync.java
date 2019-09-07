package bentre.ditagis.com.capnhatthongtin.async;

import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bentre.ditagis.com.capnhatthongtin.adapter.FeatureViewMoreInfoAdapter;
import bentre.ditagis.com.capnhatthongtin.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, Void, Void> {
    private Context mContext;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;

    public EditAsync(Context context, ServiceFeatureTable serviceFeatureTable, ArcGISFeature selectedArcGISFeature, AsyncResponse delegate) {
        mContext = context;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        this.mDelegate = delegate;
    }

    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Object output);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        FeatureViewMoreInfoAdapter adapter = params[0];
        for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
            if (item.getValue() == null) continue;
            Domain domain = mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
            Object codeDomain = null;
            if (domain != null) {
                List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                codeDomain = getCodeDomain(codedValues, item.getValue());
            }
            if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField())) {
                List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                Object idFeatureTypes = getIdFeatureTypes(featureTypes, item.getValue());
                mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(idFeatureTypes.toString()));

            } else switch (item.getFieldType()) {
                case DATE:
                    Date date = null;
                    try {
                        date = Constant.DATE_FORMAT.parse(item.getValue());
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), c);
                    } catch (ParseException e) {
                    }
                    break;

                case TEXT:
                    if (codeDomain != null) {
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), codeDomain.toString());
                    } else
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), item.getValue());
                    break;
                case SHORT:
                    if (codeDomain != null) {
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                    } else
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                    break;
            }
        }
        Calendar currentTime = Calendar.getInstance();
        mSelectedArcGISFeature.getAttributes().put(Constant.CSKDTableFields.TGCAP_NHAT, currentTime);
        mServiceFeatureTable.loadAsync();
        mServiceFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // update feature in the feature table
                    mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            mServiceFeatureTable.applyEditsAsync().addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    publishProgress();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    publishProgress();
                }
            }
        });
        return null;
    }

    private Object getIdFeatureTypes(List<FeatureType> featureTypes, String value) {
        Object code = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getName().equals(value)) {
                code = featureType.getId();
                break;
            }
        }
        return code;
    }

    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().equals(value)) {
                code = codedValue.getCode();
                break;
            }

        }
        return code;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        mDelegate.processFinish(null);
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

