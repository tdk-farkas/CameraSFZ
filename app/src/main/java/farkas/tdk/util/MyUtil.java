package farkas.tdk.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import farkas.tdk.app.MyApp;
import farksa.tdk.ocr.R;

/**
 * author：Administrator
 * time：2016/8/24.17:55
 */
public class MyUtil {
    public static Context getAppContext() {
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

//    /**
//     * 获取底部虚拟按钮高度
//     *
//     * @return int
//     */
//    public static int getNavigationBar() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        int h1 = getWidthAndHeight()[1];
//        int h2 = getScreen()[1];
//        return h2 - h1;
//    }

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

    /**
     * 保持图片比例读取图片
     *
     * @param res       资源对象
     * @param resId     资源id
     * @param reqWidth  预计宽度
     * @param reqHeight 预计高度
     * @return bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        int[] size = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight); // 计算inSampleSize 
        options.inSampleSize = size[0];
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, size[1], size[2]); // 进一步得到目标大小的缩略图
    }

    //确定 Bitmap 的缩放比, 并返回 修正了比例的 宽高
    private static int[] calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        float whb = Float.intBitsToFloat(width) / Float.intBitsToFloat(height);

        if (width / reqWidth >= height / reqHeight) {
            reqHeight = (int) (reqWidth / whb + 0.5f);
        } else {
            reqWidth = (int) (reqHeight / whb + 0.5f);
        }

        if (width > reqWidth) {
            int halfWidth = width / 2;
            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return new int[]{inSampleSize, reqWidth, reqHeight};
    }

    //按照指定 宽高加载图片
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 裁剪身份证图像并保持至本地
     *
     * @param data     图片字节数组
     * @param dirName  根目录为相册目录，在此之下的目录名
     * @param fileName 文件名
     * @return 保存结果
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject saveImageByByte(byte[] data, String dirName, String fileName) throws JSONException, IOException {
        JSONObject json = new JSONObject();

        fileName = fileName + ".jpg";
        File file = createDirAndFile(dirName, fileName);

        String msg = "成功保存到："+file.getAbsolutePath()+"文件";
        FileOutputStream outputStream = new FileOutputStream(file);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        int[] dh = getWidthAndHeight();
        int margin = getAppContext().getResources().getDimensionPixelSize(R.dimen.dp48);
        int height = dh[0] - margin*2;
        int width = (int)(height * 1.583+0.5f);
        
        if(bitmap.getHeight() > dh[0]) {
            bitmap = createScaleBitmap(bitmap, dh[1], dh[0]);
        }
        
        Bitmap bit = Bitmap.createBitmap(bitmap, margin, margin, width, height);
        bitmap.recycle();

        bit.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();

        bit.recycle();

        json.put("msg", msg);
        json.put("state", true);

        return json;
    }

    /**
     * 在相册目录下创建相关目录和文件
     *
     * @param dirName  目录名
     * @param fileName 文件名
     * @return 创建好的文件对象
     * @throws IOException io异常
     */
    public static File createDirAndFile(String dirName, String fileName) throws IOException {
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + dirName;
            File directory = new File(dir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("没有读写sd卡权限");
                }
            }
            return new File(directory, fileName);
        } else {
            throw new IOException("没有找到sd卡");
        }
    }
}