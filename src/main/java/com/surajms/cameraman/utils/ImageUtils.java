package com.surajms.cameraman.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by surajshirvankar on 6/12/16.
 */
public class ImageUtils {
    public static File createImageFile(String directory) {
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + directory + "/");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS", Locale.getDefault()).format(new Date());
        if (!(mediaStorageDir != null && mediaStorageDir.exists())) {
            if (!(mediaStorageDir != null && mediaStorageDir.mkdirs())) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public static File createImageFile(File directory) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS", Locale.getDefault()).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public static File createTempImageFile(Context context) {
        File mediaStorageDir = context.getExternalCacheDir();
        if (!(mediaStorageDir != null && mediaStorageDir.exists())) {
            if (!(mediaStorageDir != null && mediaStorageDir.mkdirs())) {
                return null;
            }
        }
        return ImageUtils.createImageFile(mediaStorageDir);
    }

    public static boolean writeImageToFile(Bitmap bitmap, File file) {
        return ImageUtils.writeImageToFile(bitmap, file, Bitmap.CompressFormat.JPEG);
    }

    public static boolean writeImageToFile(Bitmap bitmap, File file, Bitmap.CompressFormat format) {
        byte[] bytes = ImageUtils.bitmapToBytes(bitmap, format);
        try {
            ImageUtils.writeBytesToFile(file, bytes);
            return true;
        } catch (Exception ev) {
            return false;
        }
    }

    public static Bitmap scaleImage(Bitmap bitmap, Integer width, Integer height, Integer quality) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, true);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, quality, blob);
        return resized;
    }

    public static Bitmap scaleImage(Bitmap bitmap, Integer width, Integer height) {
        return ImageUtils.scaleImage(bitmap, width, height, 100);
    }

    public static Bitmap scaleImage(Bitmap bitmap, Double percentage, Integer quality) {
        Double width = bitmap.getWidth() * percentage;
        Double height = (bitmap.getHeight() * percentage);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, width.intValue(), height.intValue(), true);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, quality, blob);
        return resized;
    }

    public static Bitmap scaleImage(Bitmap bitmap, Double percentage) {
        return ImageUtils.scaleImage(bitmap, percentage, 100);
    }

    public static Bitmap bytesToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, stream);
        return stream.toByteArray();
    }

    public static void broadcastFile(Context context, File file) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())));
    }


    public static boolean writeBytesToFile(@NonNull File file, byte[] bytes) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Bitmap fileToBitmap(String original) {
        return BitmapFactory.decodeFile(original);
    }
}
