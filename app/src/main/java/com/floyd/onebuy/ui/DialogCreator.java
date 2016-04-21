package com.floyd.onebuy.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ProgressBar;

import com.floyd.onebuy.ui.R;

/**
 * Created by floyd on 15-12-23.
 */
public class DialogCreator {

    public static Dialog createDataLoadingDialog(Context context) {
        Dialog dataLoadDialog = new Dialog(context, R.style.data_load_dialog);
        ProgressBar bar = new ProgressBar(context);
        dataLoadDialog.setContentView(bar);
        dataLoadDialog.setCanceledOnTouchOutside(true);

        dataLoadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                arg0.dismiss();
            }
        });

        return dataLoadDialog;
    }
}
