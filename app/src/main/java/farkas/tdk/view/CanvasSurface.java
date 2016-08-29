package farkas.tdk.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.InputStream;
import java.util.Date;

import farkas.tdk.util.MyUtil;
import farksa.tdk.ocr.R;

/**
 * author：Administrator
 * time：2016/8/25.11:43
 */
public class CanvasSurface extends SurfaceView implements SurfaceHolder.Callback {
    private MyThread myThread;
    private String TAG = "";
    private Context context;

    public CanvasSurface(Context context) {
        super(context);
        this.context = context;
        this.TAG = this.getClass().toString();
        this.getHolder().addCallback(this);
    }

    public CanvasSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.TAG = this.getClass().toString();
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "start");
        if (myThread == null) {
            myThread = new MyThread(surfaceHolder);
            Log.e(TAG, "new");
        }
        try {
            myThread.start();
        } catch (Exception e) {
            Log.e(TAG, "错误:" + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "change");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "finish");
        myThread.finish();
    }

    public void setCount(int count) {
        myThread = new MyThread(this.getHolder());
        myThread.setCount(count);
    }

    public int getCount() {
        return myThread.getCount();
    }

    class MyThread implements Runnable {
        private SurfaceHolder holder;
        private boolean isRun;
        private int count = 0;

        public MyThread(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            Log.e(TAG, "线程开始了");
            Resources resources = context.getResources();
//            int colorPrimary = resources.getColor(R.color.colorPrimary);
//            int colorPrimaryDark = resources.getColor(R.color.colorPrimaryDark);
            int colorAccent = resources.getColor(R.color.colorAccent);

            int[] dh = MyUtil.getWidthAndHeight();
            int margin =  resources.getDimensionPixelSize(R.dimen.dp48);
            int width = dh[0] - margin * 2;
            int height = dh[1] - margin;

            Bitmap bitmap = MyUtil.decodeSampledBitmapFromResource(resources, R.mipmap.sfz_x_90, width, height);

//            Log.e(TAG,"字体颜色："+colorAccent);
//            Log.e(TAG,"深颜色："+colorPrimaryDark);
//            Log.e(TAG,"背景颜色："+colorPrimary);

            Paint paint = new Paint(); //创建画笔
            paint.setColor(colorAccent);
            paint.setTextSize(32);
            while (isRun) {
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
//                    long time = new Date().getTime();
                    if (canvas != null) {
                        canvas.drawBitmap(bitmap, margin, margin, paint);
                        
//                        canvas.drawText("这是第" + (count++) + "帧", 100, 100, paint);

//                        time = new Date().getTime() - time;
                        
//                        canvas.drawText("这一帧用时："+time+"毫秒",100,200,paint); 
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void start() {
            this.isRun = true;
            new Thread(this).start();
        }

        public void finish() {
            this.isRun = false;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }
}
