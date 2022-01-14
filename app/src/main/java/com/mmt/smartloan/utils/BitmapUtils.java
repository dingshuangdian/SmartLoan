package com.mmt.smartloan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class BitmapUtils {


    private static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    /**
     * 缩放图片
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        if (dstHeight <= 0 || dstWidth <= 0) return null;
        /*如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false*/
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 计算图片压缩比例（采样率）
     * 通常如果图片是针对某种分辨率设计的，直接decode图片不会有什么问题。
     * 但是，如果图片不是特定设计，且比较大的话，容易造成OOM。
     * 假设，一个ImageView大小为512*384，有一张2048*1536的图片需要显示，
     * 那么如果加载整张图片，那就造成了浪费，这时候就需要采用不同的采样率对原图片进行一定的压缩。
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        /*源图片的高度和宽度*/
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            /*计算出实际宽高和目标宽高的比率*/
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //质量压缩
    public static File compressImage(String pathName, int reqWidth, int reqHeight) {
        Bitmap bitmap = decodeSampledBitmapFromFile(pathName, reqWidth, reqHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 1024) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        String fileName = "IMG_CPMPRESS" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        File dirFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File file = new File(dirFile.getPath(), System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }

    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }
}
