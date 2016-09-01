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
    
    /*
        身份证 完整图片(单位:px)  w 323  h  204
        姓名： x 53(16.4)  y 23(11)    w 140(43.3) h 25(12.3)
        性别:  x 53(16.4)  y 50(24.5)  w 40(12.4)  h 25(12.3)
        名族： x 122(37.7) y 50(24.5)  w 73(22.6)  h 25(12.3)
        年：   x 53(16.4)  y 77(37.7)  w 40(12.4)  h 25(12.3)
        月：   x 104(32.1) y 77(37.7)  w 22(6.8)   h 25(12.3)
        日：   x 134(41.4) y 77(37.7)  w 22(6.8)   h 25(12.3)
        住址： x 53(16.4)  y 104(50.9) w 140(43.3) h 55(27)
        号码： x 106(32.8) y 166(81.3) w 190(43.9) h 25(12.3)
        
        身份证 完整图片(单位:px(%))  w 645 h 408
        姓名： x 123(19)   y 59(22)    w 268(41.6) h 34(8.3)
        性别： x 123(19)   y 112(27.5) w 64(10)  h 34(8.3)
        名族： x 252(39)   y 112(27.5) w 139(21.6) h 34(8.3)
        年：   x 123(19)   y 160(39.2) w 64(10)  h 34(8.3)
        月：   x 219(33.9) y 160(39.2) w 32(5)  h 34(8.3)
        日：   x 280(43.4) y 160(39.2) w 32(5)  h 34(8.3)
        住址： x 123(19)   y 216(52.9) w 268(41.6) h 100(24.5)
        号码： x 205(31.7) y 330(80.8) w 350(54.3) h 34(8.3)
     */

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

//        fileName = fileName + ".jpg";
        File file = createDirAndFile(dirName, fileName + ".jpg");
        File file_xm = createDirAndFile(dirName, fileName + "_xingming.jpg");
        File file_xb = createDirAndFile(dirName, fileName + "_xingbie.jpg");
        File file_mz = createDirAndFile(dirName, fileName + "_mingzu.jpg");
        File file_n = createDirAndFile(dirName, fileName + "_nian.jpg");
        File file_y = createDirAndFile(dirName, fileName + "_yue.jpg");
        File file_r = createDirAndFile(dirName, fileName + "_ri.jpg");
        File file_dz = createDirAndFile(dirName, fileName + "_dizhi.jpg");
        File file_hm = createDirAndFile(dirName, fileName + "_haoma.jpg");

        String msg = "成功保存到：" + file.getAbsolutePath() + "文件";
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        int[] dh = getWidthAndHeight();
        int margin = getAppContext().getResources().getDimensionPixelSize(R.dimen.dp48);
        int height = dh[0] - margin * 2;
        int width = (int) (height * 1.583 + 0.5f);

        if (bitmap.getHeight() > dh[0]) {
            bitmap = createScaleBitmap(bitmap, dh[1], dh[0]);
        }

        Bitmap bit = Bitmap.createBitmap(bitmap, margin, margin, width, height);
        bitmap.recycle();

        ///TODO 姓名截取 start ： x 123(19)   y 59(144)    w 268(41.6) h 36(8.8)
        int x = (int) (width * 0.19);
        int y = (int) (height * 0.144);
        int w = (int) (width * 0.416 + 0.5f);
        int h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_xm = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_xm, bit_xm);
        /// 姓名截取 end

        ///TODO 性别截取 start  ： x 123(19)   y 112(27.5) w 64(10)  h 36(8.8)
        x = (int) (width * 0.19);
        y = (int) (height * 0.275);
        w = (int) (width * 0.10 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_xb = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_xb, bit_xb);
        /// 性别截取 end

        ///TODO 名族截取 start： x 250(38.7)   y 112(27.5) w 139(21.6) h 36(8.8)
        x = (int) (width * 0.387);
        y = (int) (height * 0.275);
        w = (int) (width * 0.216 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_mz = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_mz, bit_mz);
        /// 名族截取 end

        ///TODO 年截取 start：   x 123(19)   y 160(39.2) w 64(10)  h 36(8.8)
        x = (int) (width * 0.19);
        y = (int) (height * 0.392);
        w = (int) (width * 0.10 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_n = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_n, bit_n);
        /// 年截取 end

        ///TODO  月截取 start：   x 219(33.9) y 160(39.2) w 32(5)  h 36(8.8)
        x = (int) (width * 0.339);
        y = (int) (height * 0.392);
        w = (int) (width * 0.05 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_y = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_y, bit_y);
        /// 月截取 end

        ///TODO  日截取 start：   x 280(43.4) y 160(39.2) w 32(5)  h 36(8.8)
        x = (int) (width * 0.434);
        y = (int) (height * 0.392);
        w = (int) (width * 0.05 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_r = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_r, bit_r);
        /// 日截取 end

        ///TODO 地址截取 start： x 123(19)   y 216(52.9) w 268(41.6) h 100(24.5)
        x = (int) (width * 0.19);
        y = (int) (height * 0.529);
        w = (int) (width * 0.416 + 0.5f);
        h = (int) (height * 0.245 + 0.5f);
        Bitmap bit_dz = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_dz, bit_dz);
        /// 地直截取 end

        ///TODO 身份证号码截取 start： x 221(34.2) y 341(83.5) w 350(54.3) h 36(8.8)
        x = (int) (width * 0.342);
        y = (int) (height * 0.835);
        w = (int) (width * 0.543 + 0.5f);
        h = (int) (height * 0.088 + 0.5f);
        Bitmap bit_hm = Bitmap.createBitmap(bit, x, y, w, h);
        saveFile(file_hm, bit_hm);
        /// 身份证号码截取 end

        ///TODO 保存完整身份证图片 start
        saveFile(file, bit);
        /// 保存完整身份证图片 end
        json.put("msg", msg);
        json.put("state", true);

        return json;
    }

    private static void saveFile(File file, Bitmap bitmap) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.flush();
        stream.close();
        bitmap.recycle();
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