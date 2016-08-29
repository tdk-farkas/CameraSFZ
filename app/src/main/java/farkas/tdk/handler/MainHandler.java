package farkas.tdk.handler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import farkas.tdk.handler.base.BaseHandler;
import farksa.tdk.ocr.MainActivity;

/**
 * author：tangdk
 * time：2016/8/16.14:55
 * 接收MainActivity的ui更新消息
 */
public class MainHandler extends BaseHandler {

//    public final int SAVESTATE = 0;
//    public final int RESTORESTATE = 1;
    public final int AUTOFOCUS = 2;
    public final int NEXTBTM = 3;

    public MainHandler(MainActivity activity) throws Exception {
        super(activity);
    }

    @Override
    public void handleStandardMessage(Activity activity, int what, Bundle bundle) {
        MainActivity theActivity = (MainActivity) activity;
        switch (what) {
//            case SAVESTATE:
//                theActivity.saveState(bundle);
//                break;
//            case RESTORESTATE:
//                theActivity.restoreState(bundle);
//                break;
            case AUTOFOCUS:
                theActivity.autoFocus();
                break;
            case NEXTBTM:
                theActivity.nextActivity();
                break;
        }
    }

    @Override
    public void handleStandardMessage(Activity activity, int what, Object obj) {
        MainActivity theActivity = (MainActivity) activity;
        switch (what) {
            case 5:
                break;
        }
    }

    @Override
    public void error(String e) {
        Log.e(TAG, e);
    }
} 