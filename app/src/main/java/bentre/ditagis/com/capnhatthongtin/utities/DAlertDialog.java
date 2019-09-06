package bentre.ditagis.com.capnhatthongtin.utities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import bentre.ditagis.com.capnhatthongtin.R;

public class DAlertDialog {
    private Dialog mDialog;

    public void show(Activity activity, String title, String... message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DDialogBuilder);
        builder.setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false);
        if (message != null && message.length > 0)
            builder.setMessage(message[0]);
        mDialog = builder.create();
        mDialog.show();
    }
}
