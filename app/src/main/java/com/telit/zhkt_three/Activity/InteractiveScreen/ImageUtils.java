package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;

/**
 * author: qzx
 * Date: 2019/7/9 15:36
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageUtils {
    /**
     * ANR
     */
    public static Bitmap image_ARGB8888_2_bitmap(DisplayMetrics metrics, Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int width = image.getWidth();
//        Log.d("WOW", "image w = " + width);
        int height = image.getHeight();
//        Log.d("WOW", "image h = " + height);

        int pixelStride = planes[0].getPixelStride();
//        Log.d("WOW", "pixelStride is " + pixelStride);
        int rowStride = planes[0].getRowStride();
//        Log.d("WOW", "row Stride is " + rowStride);
        int rowPadding = rowStride - pixelStride * width;
//        Log.d("WOW", "rowPadding is " + rowPadding);

        int offset = 0;
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(metrics, width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int pixel = 0;
                pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                pixel |= (buffer.get(offset + 2) & 0xff);       // B
                pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                bitmap.setPixel(j, i, pixel);
                offset += pixelStride;
            }
            offset += rowPadding;
        }
        return bitmap;
    }

    /**
     * 这个方法可以转换，但是得到的图片右边多了一列，比如上面方法得到1080x2160，这个方法得到1088x2160
     *
     * @param image
     * @param config
     * @return
     */
    public static Bitmap image_2_bitmap(Image image, Bitmap.Config config) {

        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap bitmap;

        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                config);
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {

        // 获取 drawable 长宽
        int width = drawable.getIntrinsicWidth();
        int heigh = drawable.getIntrinsicHeight();

        drawable.setBounds(0, 0, width, heigh);

        // 获取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 创建bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, heigh, config);
        // 创建bitmap画布
        Canvas canvas = new Canvas(bitmap);
        // 将drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
