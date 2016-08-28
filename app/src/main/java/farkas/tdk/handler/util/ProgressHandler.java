package farkas.tdk.handler.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

/**
 * author：Administrator
 * time：2016/8/26.16:12
 */
public class ProgressHandler extends Handler {
    private ProgressDialog pd;
    private static ProgressHandler ph;
    public static final int PROGRESS_SHOW = -666001;
    public static final int PROGRESS_DISMISS = -666002;
    private String title, body;

    private ProgressHandler() {
        ph = this;
    }

    public static ProgressHandler getProgress() {
        return ph == null ? new ProgressHandler() : ph;
    }

    public void setContent(String title, String msg) {
        this.title = title;
        this.body = msg;
    }

    private void initProgress(Activity activity) {
        if (pd == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                pd = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                pd = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_DARK);
            } else {
                pd = new ProgressDialog(activity);
            }
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setTitle(title);
            pd.setMessage(body);
        }
    }

    private void showProgress(Activity activity) {
        initProgress(activity);
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    private void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PROGRESS_SHOW:
                showProgress((Activity) msg.obj);
                break;
            case PROGRESS_DISMISS:
                hideProgress();
                break;
        }
        msg.obj = null;
    }
}
