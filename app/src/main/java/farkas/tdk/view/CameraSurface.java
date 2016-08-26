package farkas.tdk.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import farkas.tdk.util.MyUtil;

/**
 * author：Administrator
 * time：2016/8/24.9:27
 */
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "";
    private Context context;
    Camera camera;
    boolean isPreview = false;

    public CameraSurface(Context context) {
        super(context);
        this.context = context;
        this.TAG = this.getClass().toString();
        this.getHolder().addCallback(this);
    }

    public CameraSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.TAG = this.getClass().toString();
        this.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
            camera.setPreviewDisplay(getHolder());//通过SurfaceView显示取景画面
            int[] wh = MyUtil.getWidthAndHeight();
            initParameters(wh[1], wh[0]);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rotateCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    /**
     * 初始化相机属性
     */
    public void initParameters(int width, int height) {
        Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
        int PreviewWidth = width, PreviewHeight = height;
        boolean isSize = true;

        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (sizeList.size() > 1) {
            for (Camera.Size cur : sizeList) {
                if (cur.width >= PreviewWidth
                        && cur.height >= PreviewHeight) {
                    PreviewWidth = cur.width;
                    PreviewHeight = cur.height;
                    break;
                }
            }
        } else if (sizeList.size() > 0) {
            Camera.Size cur = sizeList.get(0);
            PreviewWidth = cur.width;
            PreviewHeight = cur.height;
        } else {
            isSize = false;
        }

        if (isSize) {
            parameters.setPreviewSize(PreviewWidth, PreviewHeight);//设置预览照片的大小
            parameters.setPictureFormat(ImageFormat.JPEG);//设置照片的格式
            parameters.setJpegQuality(100);//设置照片的质量
            parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置照片的大小，默认是和     屏幕一样大
            camera.setParameters(parameters);
        }
    }

    /**
     * 开始预览
     *
     * @return 预览状态
     */
    public boolean startPreview() {
        if (camera != null) {
            camera.startPreview();//开始预览
            isPreview = true;//设置是否预览参数为真
        }

        return isPreview();
    }

    public boolean isPreview() {
        return isPreview;
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (camera != null) {
            if (isPreview) {
                camera.stopPreview();
                camera.release();
                camera = null;
                isPreview = false;
            }
        }
    }

    /**
     * 旋转镜头成像
     */
    public void rotateCamera() {
        camera.stopPreview();
        int rotation = MyUtil.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 90;
                break;
            case Surface.ROTATION_90:
                degrees = 0;
                break;
            case Surface.ROTATION_180:
                degrees = -90;
                break;
            case Surface.ROTATION_270:
                degrees = 180;
                break;
        }

        camera.setDisplayOrientation(degrees);
        camera.startPreview();
        autoFocus();
    }

    /**
     * 自动对焦
     */
    public void autoFocus() {
        camera.autoFocus(null);
    }

    /**
     * 照相
     */
    public void takePicture(Camera.PictureCallback callback) {
        camera.takePicture(null, null, callback);
    }

    /**
     *  返回照相回掉函数实例
     * @return TakePictureCallback
     */
    public TakePictureCallback getCameraCallback() {
        return new TakePictureCallback();
    }

    /**
     * 照相完成的回调，处理获得的照片数据
     */
    public class TakePictureCallback implements Camera.PictureCallback {
        String TAG = "TakePictureCallback";

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (pd == null) {
                saveBitmap(data);
            }
        }

        /**
         * 新建线程保存图片
         * @param data 二进制图片数据
         */
        private void saveBitmap(final byte[] data){
            new Thread() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                    boolean isSave = false;
                    String dir = "", fileName = "";
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        String sdStatus = Environment.getExternalStorageState();
                        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + dirName();
                            File directory = new File(dir);
                            if (!directory.exists()) {
                                if (!directory.mkdirs()) {
                                    String msg = "创建图片临时目录失败，请允许该应用读写sd卡";
                                    showMsg(msg);
                                    return;
                                }
                            }
                            fileName = fileName() + "." + fileType();
                            File file = new File(directory, fileName);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();
                            isSave = true;
                        } else {
                            String msg = "没有找到sd卡无法保存照片";
                            showMsg(msg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    } finally {
                        handler.sendEmptyMessage(2);
                        if (isSave) {
                            String msg = "照片保存在：" + dir + "/" + fileName + "文件";
                            showMsg(msg);
                        }
                    }
                }
            }.start();
        }
        
        /**
         * 返回一个 文件夹 名称  默认为 OCR
         */
        public String dirName() {
            return "OCR";
        }

        /**
         * 返回 文件 名,不带后缀
         */
        public String fileName() {
            return dirName() + "_" + System.currentTimeMillis();
        }
        
        /**
         * 文件后缀名
         */
        private String fileType() {
            return "jpg";
        }
        
        /**
         * 显示消息
         */
        public void showMsg(String msg) {
            Message message = handler.obtainMessage();
            message.obj = msg;
            message.what = 3;
            handler.sendMessage(message);
        }

        private ProgressDialog pd;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if(pd == null)
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            pd = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                        }else{
                            pd = new ProgressDialog(context);
                        }
                        pd.setTitle("正在处理图片");
                        pd.setMessage("请稍后...");
                        pd.setCancelable(false);
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        if (camera != null) {
                            camera.stopPreview();
                        }
                        break;
                    case 2:
                        pd.dismiss();
                        if (camera != null) {
                            camera.startPreview();
                        }
                        break;
                    case 3:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }
}