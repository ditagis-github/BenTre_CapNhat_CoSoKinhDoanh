package bentre.ditagis.com.capnhatthongtin.utities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ThanLe on 4/11/2018.
 */

public class Preference {
    private Context mContext;
    private static Preference mInstance = null;

    public static Preference getInstance() {
        if (mInstance == null)
            mInstance = new Preference();
        return mInstance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private Preference() {

    }

    public SharedPreferences getPreferences() {
        return mContext.getSharedPreferences("LOGGED_IN", MODE_PRIVATE);
    }

    /**
     * Method used to save Preferences
     */
    public void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferences(String key, Set<String> values) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, values);
        editor.commit();
    }

    public boolean deletePreferences(String key) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(key).commit();
        return false;
    }

    public boolean deletePreferences() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.clear().commit();
        return false;
    }

    /**
     * Method used to load Preferences
     */
    public String loadPreference(String key) {
        try {
            SharedPreferences sharedPreferences = getPreferences();
            String strSavedMemo = sharedPreferences.getString(key, "");
            return strSavedMemo;
        } catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    public Set<String> loadPreferences(String key) {
        try {
            SharedPreferences sharedPreferences = getPreferences();
            Set<String> strSavedMemo = sharedPreferences.getStringSet(key, null);
            return strSavedMemo;
        } catch (NullPointerException nullPointerException) {
            return null;
        }
    }
}
