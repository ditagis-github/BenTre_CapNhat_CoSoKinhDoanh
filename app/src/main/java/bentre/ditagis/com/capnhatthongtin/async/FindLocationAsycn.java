package bentre.ditagis.com.capnhatthongtin.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bentre.ditagis.com.capnhatthongtin.R;
import bentre.ditagis.com.capnhatthongtin.entities.DAddress;

public class FindLocationAsycn extends AsyncTask<String, Void, List<DAddress>> {
    private Geocoder mGeocoder;
    private boolean mIsFromLocationName;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private AsyncResponse mDelegate;
    private double mLongtitude, mLatitude;

    public interface AsyncResponse {
        void processFinish(List<DAddress> output);
    }

    public void setmLongtitude(double mLongtitude) {
        this.mLongtitude = mLongtitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public FindLocationAsycn(Context context, boolean isFromLocationName,
                             AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mContext = context;
        this.mIsFromLocationName = isFromLocationName;
        this.mGeocoder = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<DAddress> doInBackground(String... params) {
        if (!Geocoder.isPresent())
            return null;
        final List<DAddress> lstLocation = new ArrayList<>();
        if (mIsFromLocationName) {
            final String text = params[0];
            try {
                List<Address> addressList = mGeocoder.getFromLocationName(text, 5);

                for (Address address : addressList)
                        lstLocation.add(new DAddress(address.getLongitude(), address.getLatitude(),
                                address.getSubAdminArea(), address.getAdminArea(), address.getAddressLine(0)));
            } catch (IOException ignored) {
                //todo grpc failed
                Log.e("error", ignored.toString());
            }

        } else {
            try {
                List<Address> addressList = mGeocoder.getFromLocation(mLatitude, mLongtitude, 1);
                for (Address address : addressList)
                        lstLocation.add(new DAddress(address.getLongitude(), address.getLatitude(),
                                address.getSubAdminArea(), address.getAdminArea(), address.getAddressLine(0)));
            } catch (IOException ignored) {
                Log.e("error", ignored.toString());
            }
        }


        return lstLocation;
    }

    @Override
    protected void onPostExecute(List<DAddress> addressList) {
        super.onPostExecute(addressList);
        if (addressList == null)
            Toast.makeText(mContext, R.string.message_no_geocoder_available, Toast.LENGTH_LONG).show();
        assert addressList != null;
        this.mDelegate.processFinish(addressList);
    }
}
