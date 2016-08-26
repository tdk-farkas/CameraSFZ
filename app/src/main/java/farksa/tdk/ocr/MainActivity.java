package farksa.tdk.ocr;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import farkas.tdk.app.BaseActivity;
import farkas.tdk.handler.MainHandler;
import farkas.tdk.util.MyUtil;
import farkas.tdk.view.CameraSurface;
import farkas.tdk.view.CanvasSurface;

public class MainActivity extends BaseActivity{
    private CameraSurface cameraSurface;
    private CanvasSurface canvasSurface;
    private ImageView imageView;
    private RelativeLayout parentLayout;
    private Button takePicture;
    private MainHandler handler;
    private String TAG = "";
    
    @Override
    protected int initLayout(){
        return R.layout.activity_main;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initViews() {
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        cameraSurface = (CameraSurface) findViewById(R.id.cameraSurface);
        canvasSurface = (CanvasSurface) findViewById(R.id.canvasSurface);
        imageView = (ImageView) findViewById(R.id.imageView);
        takePicture = (Button) findViewById(R.id.takePicture);
    }

    /**
     * 初始化视图属性
     */
    @Override
    protected void initValues() {
        try {
            handler = new MainHandler(this);
        } catch (Exception e) {
            Toast.makeText(this,"初始化失败",Toast.LENGTH_LONG).show();
            finish();
        }

        drawableImage();
    }

    /**
     * 监听视图事件
     */
    @Override
    protected void initListener() {
        parentLayout.setOnClickListener(this);
        takePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int what = view.getId();
        switch (what){
            case R.id.parentLayout:
                what = handler.AUTOFOCUS;
                break;
            case R.id.takePicture:
                what = handler.TAKEPICTURE;
                break;
        }
        handler.sendMessage(what);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "缓存");
        
        handler.sendMessage(handler.obtainMessage(handler.SAVESTATE, outState));
        
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

        handler.sendMessage(handler.obtainMessage(handler.RESTORESTATE, savedInstanceState));

        super.onRestoreInstanceState(savedInstanceState);
    }
    
    ///todo 私有函数 start
    private void drawableImage() {
        Resources res = getResources();
        Drawable drawable;
        RelativeLayout.LayoutParams buttonLayoutParams = (RelativeLayout.LayoutParams) takePicture.getLayoutParams();
        RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();

        if (MyUtil.isLandscape()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                buttonLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                buttonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                buttonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                int right = res.getDimensionPixelSize(R.dimen.dp48);
                buttonLayoutParams.setMargins(0, 0, right, 0);
            }
            imageLayoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            imageLayoutParams.height = GridLayout.LayoutParams.MATCH_PARENT;

            int left = res.getDimensionPixelSize(R.dimen.dp48);
            int top = res.getDimensionPixelSize(R.dimen.dp48);
            int down = res.getDimensionPixelSize(R.dimen.dp48);
            imageLayoutParams.setMargins(left, top, 0, down);

            drawable = res.getDrawable(R.mipmap.sfz_mb);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                buttonLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                buttonLayoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
                buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                buttonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                int down = res.getDimensionPixelSize(R.dimen.dp48);
                buttonLayoutParams.setMargins(0, 0, 0, down);
            }
            imageLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
            imageLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;

            int left = res.getDimensionPixelSize(R.dimen.dp48);
            int right = res.getDimensionPixelSize(R.dimen.dp48);
            int top = res.getDimensionPixelSize(R.dimen.dp48);
            imageLayoutParams.setMargins(left, top, right, 0);

            drawable = res.getDrawable(R.mipmap.sfz_mb_90);
        }
        imageView.setImageDrawable(drawable);
        imageView.setLayoutParams(imageLayoutParams);
        takePicture.setLayoutParams(buttonLayoutParams);
    }
    
    private void releaseCamera() {
        cameraSurface.releaseCamera();
    }
    
    private void setCount(int count) {
        canvasSurface.setCount(count);
    }

    private int getCount() {
        return canvasSurface.getCount();
    }
    /// 私有函数 end
    
    ///todo 业务函数 start
        public void takePicture() {
            CameraSurface.TakePictureCallback tpc = cameraSurface.getCameraCallback();
            cameraSurface.takePicture(tpc);
        }
    
        public void autoFocus() {
            handler.showProgress();
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        handler.hideProgress();
                    }
                }
            }.start();
            cameraSurface.autoFocus();
        }
    
        public void saveState(Bundle saveState) {
            releaseCamera();
    
            saveState.putInt("count", getCount());
        }
    
        public void restoreState(Bundle saveState) {
            drawableImage();
    
            setCount(saveState.getInt("count"));
        }
    /// 业务函数 end
}