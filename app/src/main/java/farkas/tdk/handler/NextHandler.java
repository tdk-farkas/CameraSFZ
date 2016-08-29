package farkas.tdk.handler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import farkas.tdk.handler.base.BaseHandler;
import farksa.tdk.ocr.NextActivity;

/**
 * author：Administrator
 * time：2016/8/26.17:05
 */
public class NextHandler extends BaseHandler {

    public final int AUTOFOCUS = 2;
    public final int TAKEPICTURE = 3;
    
    public NextHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleStandardMessage(Activity activity, int what, Bundle bundle) {
        NextActivity theActivity = (NextActivity) activity;
        switch (what){
            case AUTOFOCUS:
                theActivity.autoFocus();
                break;
            case TAKEPICTURE:
                theActivity.takePicture();
                break;
        }
    }

    @Override
    public void handleStandardMessage(Activity activity, int what, Object obj) {

    }

    @Override
    public void error(String e) {
        Log.e(TAG,e);
    }
}