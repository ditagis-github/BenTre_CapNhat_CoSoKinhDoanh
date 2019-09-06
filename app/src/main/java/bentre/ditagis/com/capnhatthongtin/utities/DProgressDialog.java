package bentre.ditagis.com.capnhatthongtin.utities;

import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialog;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import bentre.ditagis.com.capnhatthongtin.R;

public class DProgressDialog {
    private Dialog mDialog;
    private LinearLayout mLayout;

    public void show(Activity activity, ViewGroup view, String title) {
        mDialog = new BottomSheetDialog(activity);
        mLayout =
                (LinearLayout) activity.getLayoutInflater().inflate(
                        R.layout.layout_progress_dialog,
                        view,
                        false
                );
        ((TextView) mLayout.findViewById(R.id.txt_progress_dialog_title)).setText(title);

        mDialog.setCancelable(false);
        mDialog.setContentView(mLayout);


        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        mDialog.show();
    }

    public void changeTitle(Activity activity, ViewGroup view, String title) {
        if (mDialog != null && mDialog.isShowing()) {
            ((TextView) mLayout.findViewById(R.id.txt_progress_dialog_title)).setText(title);
        } else {
            show(activity, view, title);
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
}
