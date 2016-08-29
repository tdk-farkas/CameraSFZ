package farkas.tdk.app;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * author：Administrator
 * time：2016/8/26.9:39
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {
    protected String TAG = "";
    protected BaseActivity context;

    protected final int BASE_TRANSLUCENT = -666110; //全屏
    protected final int BASE_FULLSCREEN = -666111;//沉浸式
    protected final int BASE_SCREEN_ON = -666112;//常亮
    protected final int BASE_LANDSCAPE = -666113;//横屏
    protected final int BASE_PORTRAIT = -666114;//竖屏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().toString();
        context = this;

        initStyle(BASE_TRANSLUCENT);
        initStyle(BASE_SCREEN_ON);
        initStyle(BASE_PORTRAIT);

        setContentView(initLayout());
        
        initViews();
        initValues();
        initListener();
    }

    /**
     * 窗口显示样式
     * @param  style BaseActivity.this or WindowManager.LayoutParams  
     */
    protected void initStyle(int style) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            switch (style) {
                case BASE_TRANSLUCENT:
                    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    break;
                case BASE_FULLSCREEN:
                    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case BASE_SCREEN_ON:
                    window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case BASE_LANDSCAPE:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case BASE_PORTRAIT:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                default:
                    window.setFlags(style, style);
                    break;
            }
        }
    }

    /**
     * 返回上下文视图资源id
     * @return 资源id
     */
    protected abstract int initLayout();
    
    /**
     * 初始化视图
     */
    protected abstract void initViews();

    /**
     * 初始化视图属性
     */
    protected abstract void initValues();

    /**
     * 监听视图事件
     */
    protected abstract void initListener();

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "缓存");
        saveState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "重绘");
        restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 数据保存
     * @param saveState 将数据缓存到这个对象里
     */
    protected abstract void saveState(Bundle saveState);

    /**
     * 恢复数据
     * @param restoreState 之前缓存的数据对象
     */
    protected abstract void restoreState(Bundle restoreState);
}
