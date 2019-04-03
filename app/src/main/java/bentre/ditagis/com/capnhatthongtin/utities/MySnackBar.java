package bentre.ditagis.com.capnhatthongtin.utities;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by ThanLe on 26/10/2017.
 */

public class MySnackBar {
    public static void make(View view, String text, boolean isLong) {
        int time = isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar.make(view, text, time)
                .setAction("Action", null).show();
    }

    public static void make(View view, int id, boolean isLong) {
        int time = isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar.make(view, id, time)
                .setAction("Action", null).show();
    }

    public static void make(View view, int id, int time) {
        Snackbar.make(view, id, time)
                .setAction("Action", null).show();
    }
}
