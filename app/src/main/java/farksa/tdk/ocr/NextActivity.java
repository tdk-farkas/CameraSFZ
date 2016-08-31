package farksa.tdk.ocr;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import farkas.tdk.app.BaseActivity;
import farkas.tdk.handler.NextHandler;
import farkas.tdk.util.MyUtil;
import farkas.tdk.view.CameraSurface;
import farkas.tdk.view.CanvasSurface;

/**
 * author：Administrator
 * time：2016/8/26.17:00
 */
public class NextActivity extends BaseActivity {
    private CameraSurface cameraSurface;
    private CanvasSurface canvasSurface;
    private RelativeLayout parentLayout;
    private Button takePicture;
    private NextHandler handler;

    /**
     * 返回上下文视图资源id
     *
     * @return 资源id
     */
    @Override
    protected int initLayout() {
        return R.layout.activity_next;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initViews() {
        parentLayout = (RelativeLayout)findViewById(R.id.parentLayout);
        takePicture = (Button) findViewById(R.id.takePicture);
        cameraSurface = (CameraSurface) findViewById(R.id.cameraSurface);
        canvasSurface = (CanvasSurface) findViewById(R.id.canvasSurface);
    }

    /**
     * 初始化视图属性
     */
    @Override
    protected void initValues() {
        handler = new NextHandler(context);
    }

    /**
     * 监听视图事件
     */
    @Override
    protected void initListener() {
        parentLayout.setOnClickListener(this);
        canvasSurface.setOnClickListener(this);
        takePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int what = view.getId();
        switch (what) {
            case R.id.canvasSurface:
                what = handler.AUTOFOCUS;
                break;
            case R.id.takePicture:
                what = handler.TAKEPICTURE;
                break;
        }
        
        handler.sendMessage(what);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void saveState(Bundle saveState) {
        releaseCamera();
        saveState.putInt("count", getCount());
    }
    
    @Override
    protected void restoreState(Bundle restoreState) {
        drawableImage();
        setCount(restoreState.getInt("count"));
    }
    
    /// TODO 业务函数 start
    public void takePicture() {
        CameraSurface.TakePictureCallback tpc = cameraSurface.getCameraCallback();
        cameraSurface.takePicture(tpc);
    }

    public void autoFocus() {
//        handler.showProgress(context);
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    handler.hideProgress();
//                }
//            }
//        }.start();
        cameraSurface.autoFocus();
    }
    /// 业务函数 end
    
    /// TODO 私有函数 start
    private void drawableImage() {
        Resources res = getResources();
        FrameLayout.LayoutParams buttonLayoutParams = (FrameLayout.LayoutParams) takePicture.getLayoutParams();

        if (MyUtil.isLandscape()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int right = res.getDimensionPixelSize(R.dimen.dp48);
                buttonLayoutParams.setMargins(0, 0, right, 0);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                int down = res.getDimensionPixelSize(R.dimen.dp48);
                buttonLayoutParams.setMargins(0, 0, 0, down);
            }
        }
        
        takePicture.setLayoutParams(buttonLayoutParams);
    }  
    
    private void setCount(int count) {
        canvasSurface.setCount(count);
    }

    private int getCount() {
        return canvasSurface.getCount();
    }
    
    private void releaseCamera() {
        cameraSurface.releaseCamera();
    }

    /// 私有函数 end
}
