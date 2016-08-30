package farksa.tdk.ocr;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
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

public class MainActivity extends BaseActivity {
    private CameraSurface cameraSurface;
    private ImageView imageView;
    private RelativeLayout parentLayout;
    private Button nextBtn;
    private MainHandler handler;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initViews() {
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        cameraSurface = (CameraSurface) findViewById(R.id.cameraSurface);
        imageView = (ImageView) findViewById(R.id.imageView);
        nextBtn = (Button) findViewById(R.id.nextBtn);
    }

    /**
     * 初始化视图属性
     */
    @Override
    protected void initValues() {
        try {
            handler = new MainHandler(this);
        } catch (Exception e) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_LONG).show();
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
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int what = view.getId();
        switch (what) {
            case R.id.parentLayout:
                what = handler.AUTOFOCUS;
                break;
            case R.id.nextBtn:
                what = handler.NEXTBTM;
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

    @Override
    protected void saveState(Bundle saveState) {
        releaseCamera();
    }

    @Override
    protected void restoreState(Bundle saveState) {
        drawableImage();
    }
    
    
    ///todo 私有函数 start
    private void drawableImage() {
        Resources res = getResources();
//        Drawable drawable;
        RelativeLayout.LayoutParams buttonLayoutParams = (RelativeLayout.LayoutParams) nextBtn.getLayoutParams();
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

//            drawable = res.getDrawable(R.mipmap.sfz_mb);
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

            
//            drawable = res.getDrawable(R.mipmap.sfz_x_90);
        }
        int[] dh = MyUtil.getWidthAndHeight();
        int margin =  res.getDimensionPixelSize(R.dimen.dp48);
        int width = dh[0] - margin * 2;
        int height = dh[1] - margin;

        Bitmap bitmap = MyUtil.decodeSampledBitmapFromResource(res, R.mipmap.sfz_x_90, width, height);
        imageView.setImageBitmap(bitmap);
//        imageView.setImageDrawable(drawable);
        imageView.setLayoutParams(imageLayoutParams);
        nextBtn.setLayoutParams(buttonLayoutParams);
    }

    private void releaseCamera() {
        cameraSurface.releaseCamera();
    }
    /// 私有函数 end

    ///todo 业务函数 start
    public void nextActivity() {
//        Intent intent = new Intent(context,NextActivity.class);
//        startActivity(intent);
        cameraSurface.takePicture(cameraSurface.getCameraCallback());
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
}