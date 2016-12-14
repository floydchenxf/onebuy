package com.yyg365.interestbar.biz.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import com.yyg365.interestbar.biz.constants.EnvConstants;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Created by floyd on 15-11-29.
 */
public class ThumbnailUtils {

    private static final String TAG = "ThumbnailUtils";

    public static final String GIF = "GIF";

    public static final String PNG = "PNG";

    public static final String JPG = "JPG";

    private static final float FACTOR = 0.4f;

    private static final int UNCONSTRAINED = -1;

    public static final int COMPRESS_RATE = 60;

    public static Bitmap compressFileAndRotateToBitmapThumb(String filePath, int width, int height, int degree, String savePath) {
        Bitmap bm = compressFileToBitmapThumb(filePath, width, height, savePath);
        if (bm == null)
            return null;
        Bitmap b2 = rotateBitmap(bm, degree);
        if (b2 != null) {
            FileTools.writeBitmap(savePath, b2, 90);
            if (bm != b2) {
                bm.recycle();
                bm = null;
            }
            return b2;
        }
        return bm;
    }

    public static int[] getResizedDimension(int actualWidth, int actualHeight) {
        int[] resizedResults = new int[4];
        int DEFAULT_WIDTH = 800;
        int DEFAULT_HEIGHT = 800;
        int mMaxHeight = 1000;
        int mMinWidth = 600;

        if (actualWidth <= 0 || actualHeight <= 0) {
            resizedResults[0] = DEFAULT_WIDTH;
            resizedResults[1] = DEFAULT_HEIGHT;
            resizedResults[2] = DEFAULT_WIDTH;
            resizedResults[3] = DEFAULT_HEIGHT;
            return resizedResults;
        }
        if (actualWidth <= actualHeight) {
            if (actualHeight > mMaxHeight) {
                double ratio = (double) actualHeight / (double) mMaxHeight;
                int tmpWidth = (int) (actualWidth / ratio);
                if (tmpWidth > mMinWidth) {
                    resizedResults[0] = tmpWidth; // 最终尺寸的宽度
                    resizedResults[1] = mMaxHeight;// 最终尺寸的高度
                    resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                    resizedResults[3] = actualHeight; // 裁剪尺寸的高度
                } else {
                    ratio = (double) mMinWidth / (double) actualWidth;
                    int tmpHeight = (int) (actualHeight * ratio);
                    if (tmpHeight > mMaxHeight) {
                        resizedResults[0] = mMinWidth; // 最终尺寸的宽度
                        resizedResults[1] = mMaxHeight;// 最终尺寸的高度
                        resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                        resizedResults[3] = (int) ((double) actualWidth
                                * mMaxHeight / (double) mMinWidth);
                    } else {
                        resizedResults[0] = mMinWidth; // 最终尺寸的宽度
                        resizedResults[1] = tmpHeight;// 最终尺寸的高度
                        resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                        resizedResults[3] = actualHeight; // 裁剪尺寸的高度
                    }
                }
            } else {
                if (actualWidth < mMinWidth) {
                    double ratio = (double) mMinWidth / (double) actualWidth;
                    int tmpHeight = (int) (actualHeight * ratio);
                    if (tmpHeight > mMaxHeight) {
                        resizedResults[0] = mMinWidth; // 最终尺寸的宽度
                        resizedResults[1] = mMaxHeight;// 最终尺寸的高度
                        resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                        resizedResults[3] = (int) ((double) actualWidth
                                * mMaxHeight / (double) mMinWidth);
                    } else {
                        resizedResults[0] = mMinWidth; // 最终尺寸的宽度
                        resizedResults[1] = tmpHeight;// 最终尺寸的高度
                        resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                        resizedResults[3] = actualHeight; // 裁剪尺寸的高度
                    }
                } else {
                    resizedResults[0] = actualWidth; // 最终尺寸的宽度
                    resizedResults[1] = actualHeight;// 最终尺寸的高度
                    resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
                    resizedResults[3] = actualHeight; // 裁剪尺寸的高度
                }
            }
        } else {
            int[] results = getResizedDimension(actualHeight, actualWidth);
            resizedResults[0] = results[1];
            resizedResults[1] = results[0];
            resizedResults[2] = results[3];
            resizedResults[3] = results[2];
        }
        return resizedResults;
    }


    public static String getType(byte[] data) {
        String type = null;
        if (data == null || data.length <= 9) {
            return null;
        }
        try {
            // Png test:
            if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
                type = PNG;
                return type;
            }
            // Gif test:
            if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
                type = GIF;
                return type;
            }
            // JPG test:
            if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
                    && data[9] == 'F') {
                type = JPG;
                return type;
            }
        } catch (Exception e) {
            // EXCEPTION_TODO: handle exception
        }
        return type;
    }

    public static Bitmap rotateBitmap(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, b.getWidth() / 2, b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                return b2;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap getImageThumbnail(File file, int width, int height,
                                           String saveName, boolean needRotate) {
        if (file != null && file.exists()) {
            int sampleSize = 1;
            while (true) {
                try {
                    long length = file.length();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = sampleSize;
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(
                            fis.getFD(), null, options);
                    fis.close();
                    fis = null;
                    int outWidth = options.outWidth;
                    int outHeight = options.outHeight;
                    if (outWidth > outHeight && needRotate) {
                        int temp = outWidth;
                        outWidth = outHeight;
                        outHeight = temp;
                    }
                    float tw = (float) outWidth / (float) width;
                    float t = tw;
                    if (t <= (1.0 + FACTOR)) {
                        String savePath = EnvConstants.imageRootPath + File.separator + saveName;
                        if (bitmap != null && length > 0) {
                            if (bitmap.getWidth() > bitmap.getHeight()
                                    && needRotate) {
                                Bitmap tempBitmap = bitmap;
                                Matrix matrix = new Matrix();
                                matrix.setRotate((float) 90.0);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                        bitmap.getWidth(), bitmap.getHeight(),
                                        matrix, true);
                                if (tempBitmap != bitmap) {
                                    tempBitmap.recycle();
                                    tempBitmap = null;
                                }
                            }
                            FileOutputStream fos = null;
                            File imageRoot = new File(EnvConstants.imageRootPath);
                            if (!imageRoot.exists()) {
                                imageRoot.mkdirs();
                            }
                            File savefile = new File(savePath);
                            if (!savefile.exists()) {
                                try {
                                    savefile.createNewFile();
                                } catch (IOException e) {
                                    // AUTO_TODO Auto-generated catch block
                                    Log.w(TAG, e);
                                }
                            }
                            try {
                                fos = new FileOutputStream(savefile);
                            } catch (FileNotFoundException e) {
                                // AUTO_TODO Auto-generated catch block
                                Log.w(TAG, e);
                            }
                            if (fos != null) {
                                if (bitmap.compress(Bitmap.CompressFormat.JPEG,
                                        60, fos)) {
                                    try {
                                        fos.flush();
                                        fos.close();
                                    } catch (IOException e) {
                                        // AUTO_TODO Auto-generated catch block
                                        Log.w(TAG, e);
                                    }
                                    bitmap.recycle();
                                    bitmap = null;
                                    bitmap = FileTools.readBitmap(savePath);
                                }
                            }
                        } else {
                            break;
                        }
                        return bitmap;
                    } else {
                        if (bitmap != null) {
                            bitmap.recycle();
                        }
                        float f = sampleSize * t;
                        int i = (int) (sampleSize * t);
                        if ((f - FACTOR) > i) {
                            sampleSize = (i + 1);
                        } else {
                            sampleSize = i;
                        }
                    }
                } catch (OutOfMemoryError oe) {
                    sampleSize++;
                } catch (FileNotFoundException e) {
                    Log.w(TAG, e);
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
            }
        }
        return null;
    }

    public static Bitmap getImageThumbnailFromAlbum(Context context, Uri uri,
                                                    int width, int height, String saveName, int orientation) {
        return getImageThumbnailFromAlbum(context, uri, width, height,
                saveName, orientation, false);
    }

    public static Bitmap getImageThumbnailFromAlbum(Context context, Uri uri,
                                                    int width, int height, String saveName, int orientation,
                                                    boolean shouldCut) {
        InputStream in = null;
        int sampleSize = 1;
        String type = JPG;

        int length = 0;
        try {
            in = context.getContentResolver().openInputStream(uri);
            length = in.available();
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            sampleSize = findBestSampleSize(actualWidth, actualHeight, width,
                    height);
            if (sampleSize == 1
                    && (actualWidth > width || actualHeight > height)) {
                sampleSize = 2;
            }

            while (true) {
                try {
                    try {
                        in = context.getContentResolver().openInputStream(uri);
                        length = in.available();
                    } catch (FileNotFoundException e) {
                        // AUTO_TODO Auto-generated catch block
                        Log.w(TAG, e);
                    } catch (IOException e) {
                        // AUTO_TODO Auto-generated catch block
                        Log.w(TAG, e);
                    } catch (OutOfMemoryError e) {
                        Log.w(TAG, e);
                    }

                    if (in != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = sampleSize;
                        Bitmap bitmap = BitmapFactory.decodeStream(in, null,
                                options);
                        int outWidth = options.outWidth;
                        int outHeight = options.outHeight;
                        if (orientation == 90 || orientation == 270) {
                            int temp = outWidth;
                            outWidth = outHeight;
                            outHeight = temp;
                        }
                        float tw = (float) outWidth / (float) width;
                        float th = (float) outHeight / (float) height;
                        float t = tw < th ? tw : th;
                        if (shouldCut) {
                            if (tw < 1 || th < 1) {
                                t = 1;
                            }
                        }
                        String savePath = EnvConstants.imageRootPath
                                + File.separator + saveName;
                        if (bitmap != null && length > 0) {
                            Matrix matrix = new Matrix();
                            Bitmap tempBitmap = bitmap;
                            if (orientation > 0) {
                                matrix.setRotate(orientation);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                        bitmap.getWidth(), bitmap.getHeight(),
                                        matrix, true);

                                if (tempBitmap != bitmap) {
                                    tempBitmap.recycle();
                                    tempBitmap = null;
                                }
                            }

                            if (shouldCut && t != 1) {
                                matrix = new Matrix();
                                matrix.setScale(1f / t, 1f / t);
                                tempBitmap = bitmap;
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                        bitmap.getWidth(), bitmap.getHeight(),
                                        matrix, true);

                                if (tempBitmap != bitmap) {
                                    tempBitmap.recycle();
                                    tempBitmap = null;
                                }
                            }

                            FileOutputStream fos = null;
                            File imageRoot = new File(
                                    EnvConstants.imageRootPath);
                            if (!imageRoot.exists()) {
                                imageRoot.mkdirs();
                            }
                            File file = new File(savePath);
                            if (!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    // AUTO_TODO Auto-generated catch block
                                    Log.w(TAG, e);
                                }
                            } else {
                                file.delete();
                            }
                            try {
                                fos = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                // AUTO_TODO Auto-generated catch block
                                Log.w(TAG, e);
                            }
                            if (fos != null) {
                                Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                                if (PNG.equals(type)) {
                                    format = Bitmap.CompressFormat.PNG;
                                }
                                if (bitmap.compress(format, 60, fos)) {
                                    try {
                                        fos.flush();
                                        fos.close();
                                    } catch (IOException e) {
                                        // AUTO_TODO Auto-generated catch block
                                        Log.w(TAG, e);
                                    }
                                    bitmap.recycle();
                                    bitmap = null;
                                    bitmap = FileTools.readBitmap(savePath);
                                }
                            }
                        } else {
                            break;
                        }
                        return bitmap;
                    } else {
                        break;
                    }
                } catch (OutOfMemoryError oe) {
                    // EXCEPTION_TODO: handle exception
                    sampleSize++;
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        // AUTO_TODO Auto-generated catch block
                        Log.w(TAG, e);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
        } catch (IOException e) {
            Log.w(TAG, e);
        } catch (OutOfMemoryError e) {
            Log.w(TAG, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap compressFileToBitmapThumb(String filePath, int width,
                                                   int height, String savePath) {
        if (null == filePath) {
            return null;
        }
        int targetSize = Math.min(width, height);
        int maxPixels = width * height;
        // String tmpFilePath = filePath + ".tmp";
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileTools.deleteFile(savePath);

        FileInputStream fis = null;
        FileDescriptor fd = null;
        try {
            fis = new FileInputStream(file);
            fd = fis.getFD();
        } catch (Exception e) {
            Log.w(TAG, e);
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (Exception e) {
            Log.w(TAG, e);
            try {
                fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                fis.close();
            } catch (IOException e) {
                Log.w(TAG, e);
            }
            return null;
        }
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.w(TAG, e);
            }
            return null;
        }
        int sampleSize = computeSampleSize(options, targetSize, maxPixels);
        int maxSample = Math.max(sampleSize, 20);
        options.inJustDecodeBounds = false;

        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        for (int index = sampleSize; index <= maxSample; index++) {
            try {
                options.inSampleSize = index;
                Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null,
                        options);
                if (null != bm) {
                    fis.close();
                    FileTools.writeBitmap(savePath, bm, COMPRESS_RATE);
                    bm.recycle();
                    bm = null;
                    // File tmpFile = new File(savePath);
                    // tmpFile.renameTo(file);
                    return FileTools.readBitmap(savePath);
                }
            } catch (OutOfMemoryError e) {
                Log.w(TAG, e);
            } catch (Exception e) {
                Log.w(TAG, e);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        try {
            fis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.w(TAG, e);
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap getCropAndScaledBitmap(Bitmap oriBitmap,
                                                int desiredWidth, int desiredHeight, int resizedWidth,
                                                int resizedHeight, boolean recycle) {
        Bitmap cropBitmap = null;
        int width = oriBitmap.getWidth();
        int height = oriBitmap.getHeight();
        resizedWidth = Math.min(resizedWidth, width);
        resizedHeight = Math.min(resizedHeight, height);
        if (width == resizedWidth && height == resizedHeight) {
            cropBitmap = oriBitmap;
        } else {
            int x = Math.max((width - resizedWidth) / 2, 0);
            int y = Math.max((height - resizedHeight) / 2, 0);
            cropBitmap = Bitmap.createBitmap(oriBitmap, x, y, resizedWidth,
                    resizedHeight);
            if (cropBitmap != oriBitmap && recycle) {
                oriBitmap.recycle();
            }
        }
        Bitmap scalebBitmap = null;
        // If necessary, scale down to the maximal acceptable size.
        if (cropBitmap != null
                && (cropBitmap.getWidth() != desiredWidth || cropBitmap
                .getHeight() != desiredHeight)) {
            scalebBitmap = Bitmap.createScaledBitmap(cropBitmap, desiredWidth,
                    desiredHeight, true);
            if (cropBitmap != scalebBitmap && recycle) {
                cropBitmap.recycle();
            }
        } else {
            scalebBitmap = cropBitmap;
        }
        return scalebBitmap;
    }

    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2KOrM(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "K");
    }
}
