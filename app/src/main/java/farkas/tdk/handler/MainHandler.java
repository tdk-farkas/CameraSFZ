package farkas.tdk.handler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import farksa.tdk.ocr.MainActivity;

/**
 * author：tangdk
 * time：2016/8/16.14:55
 * 接收MainActivity的ui更新消息
 */
public class MainHandler extends BaseHandler implements BaseHandler.StandardMsg{
    private static String TAG ;
    
    public final int SAVESTATE = 0;
    public final int RESTORESTATE = 1;
    public final int AUTOFOCUS = 2;
    public final int TAKEPICTURE = 3;

    public MainHandler(MainActivity activity) throws Exception {
        if(instance(activity,this) == null){
            throw new Exception("初始化出错");
        }
        TAG = MainHandler.class.toString();
    }
    
//    public MainHandler getHandler(MainActivity activity) {
//        if(mainHandler == null) {
//            mainHandler = new MainHandler();
//             if(instance(activity,mainHandler) == null){
//                 mainHandler = null;
//             }
//            TAG = MainHandler.class.toString();
//        }
//        
//        return mainHandler;
//    }

    @Override
    public void handleStandardMessage(Activity activity, int what, Bundle bundle) {
        MainActivity theActivity = (MainActivity) activity;
        switch (what){
            case SAVESTATE:
                theActivity.saveState(bundle);
                break;
            case RESTORESTATE:
                theActivity.restoreState(bundle);
                break;
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
        MainActivity theActivity = (MainActivity) activity;
        switch (what){
            case 5:
                break;
        }
    }
    
    @Override
    public void error(String e){
        Log.e(TAG,e);
    }
} 