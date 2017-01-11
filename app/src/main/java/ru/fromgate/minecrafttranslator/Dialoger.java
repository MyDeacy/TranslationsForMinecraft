package ru.fromgate.minecrafttranslator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;



public class Dialoger {

    static ProgressDialog createProgressDialog(boolean horizontal, int max, int titleId, int textId) {
        ProgressDialog progress = new ProgressDialog(MainActivity.getActivity());
        progress.setProgressStyle(horizontal ? ProgressDialog.STYLE_HORIZONTAL : ProgressDialog.STYLE_SPINNER);
        progress.setTitle(titleId);
        progress.setMessage(getResource(textId));
        if (textId != 0) progress.setMessage(getResource(textId));
        progress.setMax(max);
        progress.setCanceledOnTouchOutside(false);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);
        if (!MainActivity.getActivity().isFinishing()) {
            progress.show();
            saveDialog(progress);
        }
        return progress;
    }

    static void showOkDialog(int titleMessageId, int textMessageId) {
        MainActivity activity = MainActivity.getActivity();
        if (activity == null || activity.isFinishing()) return;
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(getResource(titleMessageId))
                .setMessage(getResource(textMessageId))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        saveDialog(dialog);
    }

    static String getResource(int id) {
        return MainActivity.getActivity().getResources().getString(id);
    }

    private static void saveDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}
