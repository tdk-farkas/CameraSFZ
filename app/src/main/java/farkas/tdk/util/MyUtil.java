package farkas.tdk.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

import farkas.tdk.app.MyApp;

/**
 * author：Administrator
 * time：2016/8/24.17:55
 */
public class MyUtil {
    public static Context getAppContext(){
        return MyApp.getInstance();
    }
    
    /**
     * 获取屏幕宽高
     *
     * @return int[]  , width=int[0]  , height=int[1]
     */
    public static int[] getWidthAndHeight() {
        WindowManager manager = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);

        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 获取屏幕真实宽高
     *
     * @return height
     */
    public static int[] getScreen() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int heightPixels;
        WindowManager manager = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // since SDK_INT = 1;  
        heightPixels = metrics.heightPixels;

        // includes window decorations (statusbar bar/navigation bar)  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.graphics.Point realSize = new android.graphics.Point();
            Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(display, realSize);
            heightPixels = realSize.y;
        }
        
        return new int[]{metrics.widthPixels, heightPixels};
    }

    /**
     * 获取底部虚拟按钮高度
     * 
     * @return int
     */
    public static int getNavigationBar() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int  h1 = getWidthAndHeight()[1];
        int  h2 = getScreen()[1];
        return  h2-h1;
    }

    /**
     * 获取镜头的方向
     *
     * @return 方向
     */
    public static int getRotation() {
        WindowManager manager = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getRotation();
    }

    /**
     * 是否横屏
     *
     * @return true 是， false 不是
     */
    public static boolean isLandscape() {
        Configuration mOrientation = getAppContext().getResources().getConfiguration();
        return mOrientation.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 获取状态栏高度
     *
     * @return int
     */
    public static int getStatusBarHeight() {
        int resourceId = getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? MyApp.getInstance().getResources().getDimensionPixelSize(resourceId) : 0;
    }
}
