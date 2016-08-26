package farkas.tdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import farkas.tdk.util.MyUtil;
import farksa.tdk.ocr.R;

/**
 * author：Administrator
 * time：2016/8/25.11:43
 */
public class CanvasSurface extends SurfaceView implements SurfaceHolder.Callback{
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
        if(myThread==null) {
            myThread  = new MyThread(surfaceHolder);
            Log.e(TAG,"new");
        }
        try {
            myThread.start();
        } catch (Exception e) {
            Log.e(TAG, "错误:" + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG,"change");
       
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG,"finish");
        myThread.finish();
    }
    
    public void setCount(int count){
        myThread = new MyThread(this.getHolder());
        myThread.setCount(count);
    }

    public int getCount(){
        return myThread.getCount();
    }
    
    class MyThread implements  Runnable{
        private SurfaceHolder holder;
        private boolean isRun;
        private int count = 0;
        public MyThread(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            Log.e(TAG,"线程开始了");
            int colorPrimary = context.getResources().getColor(R.color.colorPrimary);
            int colorPrimaryDark = context.getResources().getColor(R.color.colorPrimaryDark);
            int colorAccent = context.getResources().getColor(R.color.colorAccent);

            int[] dh = MyUtil.getWidthAndHeight();
            int width=dh[0];
            int height=dh[1];
            
            Log.e(TAG,"字体颜色："+colorAccent);
            Log.e(TAG,"深颜色："+colorPrimaryDark);
            Log.e(TAG,"背景颜色："+colorPrimary);
            
            Paint paint = new Paint(); //创建画笔
            while (isRun) {
                Canvas canvas = null;
                try {
                        canvas = holder.lockCanvas();
                        if(canvas!=null) {
                            if (width > height) {
                                canvas.drawColor(colorPrimaryDark);//设置画布背景颜色
                            } else {
                                canvas.drawColor(colorPrimary);//设置画布背景颜色
                            }
    
                            paint.setColor(colorAccent);
                            paint.setTextSize(32);
    
                            canvas.drawText("这是第" + (count++) + "帧", 100, 100, paint);
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

        public void setCount(int count){
            this.count = count;
        }

        public int getCount(){
            return this.count;
        }
    }
}
