package farkas.tdk.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import farkas.tdk.app.MyApp;

/**
 * author：Administrator
 * time：2016/8/16.15:18
 */
public abstract class BaseHandler<E> {
    private static TheHandler handler;
    private static ProgressHandler ph;
//// TODO: 2016/8/26  
//    abstract BaseHandler(Activity a);
    
    protected static Handler instance(Activity activity, StandardMsg standardMsg) {
        if (handler == null) {
            try {
                handler = new TheHandler(activity, standardMsg);
                ph = new ProgressHandler(activity);
            } catch (Exception e) {
                return null;
            }
        }

        return handler;
    }

    public Message obtainMessage() {
        return handler.obtainMessage();
    }

    public Message obtainMessage(int what, Object obj) {
        Message msg = obtainMessage();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public Message obtainMessage(int what, Bundle bundle) {
        Message msg = obtainMessage();
        msg.what = what;
        msg.setData(bundle);
        return msg;
    }

    public void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public void sendMessage(int what) {
        handler.sendEmptyMessage(what);
    }

    public void showProgress(String title, String content) {
        ph.setContent(title, content);
        ph.sendEmptyMessage(ProgressHandler.PROGRESS_SHOW);
    }

    public void showProgress() {
        ph.setContent("", "");
        ph.sendEmptyMessage(ProgressHandler.PROGRESS_SHOW);
    }

    public void hideProgress() {
        ph.sendEmptyMessage(ProgressHandler.PROGRESS_DISMISS);
    }

    /**
     * 子类需要实现处理消息的函数
     */
    public interface StandardMsg {
        void handleStandardMessage(Activity activity, int what, Bundle bundle);

        void handleStandardMessage(Activity activity, int what, Object obj);

        void error(String e);
    }

    /**
     * 处理消息
     */
    private static class TheHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private StandardMsg useMsg;

        public TheHandler(Activity activity, StandardMsg useMsg) throws Exception {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw new Exception("只允许在主线程初始化该函数");
            } else {
                this.mActivity = new WeakReference<Activity>(activity);
                this.useMsg = useMsg;
            }
        }

        @Override
        public void handleMessage(Message msg) {
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