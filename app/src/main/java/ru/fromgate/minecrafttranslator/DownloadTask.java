package ru.fromgate.minecrafttranslator;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.Set;


public class DownloadTask extends AsyncTask<Void, Integer, DownloadResult> {


    private static final int CREATE_NEW_SPIN = 0;
    private static final int CREATE_NEW_HORIZONTAL = 1;
    private static final int INC_BY_ONE = 2;
    private static final int SET_MAX = 3;
    private static final int SET_MSG = 4;


    private ProgressDialog progress;

    private boolean active;


    private boolean downloadFontPack;

    private Set<String> selectedLangs;


    public DownloadTask() {
        this.selectedLangs = null;
    }

    public DownloadTask(Set<String> selection) {
        this.selectedLangs = selection;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        active = true;
        MainActivity.getActivity().buttonStart.setEnabled(false);
        this.downloadFontPack = MainActivity.getActivity().checkBoxFontCreate.isChecked();
        this.progress = Dialoger.createProgressDialog(false, 100, R.string.progress_prepare, R.string.progress_prepare_download);

    }


    @Override
    protected DownloadResult doInBackground(Void... params) {
        if (!ResourceDownloader.downloadLangs(selectedLangs)) {
            return DownloadResult.DOWNLOAD_FAIL;
        }
        if (this.downloadFontPack) {
            publishProgress(CREATE_NEW_SPIN, R.string.progress_title, R.string.progress_saving_font, 100);
            if (!ResourceDownloader.downloadFont()) {
                return DownloadResult.FONT_FAIL;
            }
        }
        return DownloadResult.OK;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (values.length == 0) return;

        switch (values[0]) {
            case CREATE_NEW_SPIN:
                if (values.length >= 3) {
                    progress.dismiss();
                    progress = Dialoger.createProgressDialog(false, values.length > 3 ? values[3] : 100, values[1], values[2]);
                }
                break;
            case CREATE_NEW_HORIZONTAL:
                if (values.length == 4) {
                    progress.dismiss();
                    progress = Dialoger.createProgressDialog(true, values[3], values[1], values[2]);
                }
                break;
            case INC_BY_ONE:
                progress.incrementSecondaryProgressBy(1);
                break;
            case SET_MAX:
                if (values.length == 2) {
                    progress.setMax(values[1]);
                }
                progress.setProgress(0);
                break;
            case SET_MSG:
                if (values.length > 1) {
                    String message = Dialoger.getResource(values[1]);
                    if (values.length == 3) {
                        message = message.replace("%1%", String.valueOf(values[2]));
                    }
                    progress.setMessage(message);
                }
                break;
        }
    }

    @Override
    protected void onPostExecute(DownloadResult result) {
        super.onPostExecute(result);
        progress.dismiss();

        result.showDialog();

        active = false;
        MainActivity.getActivity().buttonsEnable(true);
    }


    public boolean isActive() {
        return this.active;
    }
}