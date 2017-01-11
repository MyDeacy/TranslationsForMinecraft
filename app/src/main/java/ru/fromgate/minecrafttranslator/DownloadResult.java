package ru.fromgate.minecrafttranslator;


public enum DownloadResult {

    OK(R.string.msg_langpack_create_ok, R.string.msg_langpack_create_ok_text),
    // MCPE_NOT_FOUND(R.string.msg_mcpe_not_found_title, R.string.msg_mcpe_not_found),
    DOWNLOAD_FAIL(R.string.msg_mcpc_fail_title, R.string.msg_mcpc_fail_text),
    // SAVE_FAIL(R.string.msg_save_fail_title, R.string.msg_save_fail_text),
    FONT_FAIL(R.string.msg_save_fail_title, R.string.msg_font_save_fail_text);

    private int titleId;
    private int messageId;

    DownloadResult(int titleId, int messageId) {
        this.titleId = titleId;
        this.messageId = messageId;
    }

    public void showDialog() {
        Dialoger.showOkDialog(this.titleId, this.messageId);
    }

}
