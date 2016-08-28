package farkas.tdk.handler.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import farkas.tdk.handler.util.ProgressHandler;

/**
 * author：Administrator
 * time：2016/8/16.15:18
 */
public abstract class BaseHandler implements StandardMsg {
    private TheHandler handler;
    private static ProgressHandler ph;
    protected static String TAG;

    public BaseHandler(Activity activity) {
        TAG = activity.getClass().toString();
        try {
            handler = new TheHandler(activity);
            ph = ProgressHandler.getProgress();
            setStandardMsg(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void setStandardMsg(StandardMsg standardMsg) {
        handler.setUseMsg(standardMsg);
    }

    public Message obtainMessage(int what) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        return msg;
    }

    public Message obtainMessage(int what, Object obj) {
        Message msg = obtainMessage(what);
        msg.obj = obj;
        return msg;
    }

    public Message obtainMessage(int what, Bundle bundle) {
        Message msg = obtainMessage(what);
        msg.setData(bundle);
        return msg;
    }

    public void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public void sendMessage(int what) {
        handler.sendEmptyMessage(what);
    }

    public void showProgress(Activity activity, String title, String content) {
        ph.setContent(title, content);
        ph.sendMessage(obtainMessage(ProgressHandler.PROGRESS_SHOW, activity));
    }

    public void showProgress(Activity activity) {
        showProgress(activity, "", "");
    }

    public void hideProgress() {
        ph.sendEmptyMessage(ProgressHandler.PROGRESS_DISMISS);
    }

    /**
     * 处理消息
     */
    private class TheHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private StandardMsg useMsg;

        public TheHandler(Activity activity) throws Exception {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw new Exception("只允许在主线程初始化该函数");
            } else {
                this.mActivity = new WeakReference<Activity>(activity);
            }
        }

        public void setUseMsg(StandardMsg useMsg) {
            this.useMsg = useMsg;
        }

        @Override
        public void handleMessage(Message msg) {
            if (useMsg == null) {
                return;
            }
            try {
                Activity a = mActivity.get();
                if (a != null) {
                    if (msg.obj != null) {
                        useMsg.handleStandardMessage(a, msg.what, msg.obj);
                    } else {
                        useMsg.handleStandardMessage(a, msg.what, msg.getData());
                    }
                } else {
                    useMsg.error("当前handler对应的activity已经不存在");
                }
            } catch (Exception e) {
                useMsg.error(e.getMessage());
            } finally {
                msg.obj = null;
            }
        }
    }
}