package farkas.tdk.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

/**
 * author：Administrator
 * time：2016/8/26.16:12
 */
public class ProgressHandler extends Handler {
    private static ProgressDialog pd;
    
    public static final int PROGRESS_SHOW = -666001;
    public static final int PROGRESS_DISMISS = -666002;
    
    public ProgressHandler(Context context){
        if(pd == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                pd = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                pd = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
            } else {
                pd = new ProgressDialog(context);
            }
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
        }
    }
    
    public void setContent(String title,String msg){
        pd.setTitle(title);
        pd.setMessage(msg);
    }
    
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PROGRESS_SHOW:
                pd.show();
                break;
            case PROGRESS_DISMISS:
                setContent("","");
                pd.dismiss();
                break;
        }
    }
}
