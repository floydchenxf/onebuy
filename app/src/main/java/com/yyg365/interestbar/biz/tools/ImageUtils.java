package com.yyg365.interestbar.biz.tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yyg365.interestbar.IMChannel;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.utils.WXUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {

	private static final String TAG = ImageUtils.class.getSimpleName();

	private static RenderScript rs = null;
	/**
	 * 图片去色,返回灰度图片
	 *
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		if (bmpOriginal == null){
			return null;
		}
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = null;
		try {
			bmpGrayscale = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			return null;
		}
		if(bmpGrayscale != null){
			Canvas c = new Canvas(bmpGrayscale);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
			c.drawBitmap(bmpOriginal, 0, 0, paint);
		}

		return bmpGrayscale;
	}

	public static Bitmap fastBlur(Bitmap sentBitmap, int radius) {
		if (Build.VERSION.SDK_INT > 16) {
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
			if (rs == null) {
				rs = RenderScript.create(IMChannel.getApplication());
			}
			final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
					Allocation.USAGE_SCRIPT);
			final Allocation output = Allocation.createTyped(rs, input.getType());
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			script.setRadius(radius);
			script.setInput(input);
			script.forEach(output);
			output.copyTo(bitmap);
			return bitmap;
		}

		return sentBitmap;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Drawable toDrawable(Resources res, String imageStr) {
		if(imageStr == null){
			return null;
		}
		try{
			byte[] b = Base64.decode(imageStr, Base64.DEFAULT);
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			Bitmap bm = BitmapFactory.decodeStream(in);
			return new BitmapDrawable(res, bm);
		}catch (IllegalArgumentException e){
			return null;
		}
	}

	/**
	 * 直接从sd卡获取图片
	 * @param url
	 * @return
	 */
	public static Bitmap getImageFromSD(String url) {
		return FileTools.decodeBitmap(getImageSDPath(url));
	}

    /**
     * 获取图片sd卡路径
     * @param url
     * @return
     */
    public static String getImageSDPath(String url) {
        String localName = WXUtil.getFileName(url);
        File file = new File(EnvConstants.imageRootPath, localName);
        if (!file.exists()){
            localName = WXUtil.getMD5FileName(url);
        }
        return EnvConstants.imageRootPath + File.separator + localName;
    }

	/**
	 * 获取圆角图片
	 * @param bitmap	 源图片
	 * @param width	生成的宽度
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap, int width) {
		if (bitmap == null){
			return null;
		}
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			// EXCEPTION_TODO: handle exception
			return null;
		}
		float radius = (float)(width*0.17544);	// 2???ios±èày
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.TRANSPARENT);
		Rect dstRect = new Rect(0, 0, width, width);
		Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(dstRect);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return output;
	}

	/**
	 * 获取圆角图片
	 * @param bitmap	 源图片
	 * @param width	生成的宽度
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap, int width, float radius) {
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, e.getMessage(),e);
			return null;
		}
		if(output!=null){
			Canvas canvas = new Canvas(output);
			canvas.drawColor(Color.TRANSPARENT);
			Rect dstRect = new Rect(0, 0, width, width);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            Rect srcRect = null;
            if (bitmapWidth > bitmapHeight) {
                int diff = bitmapWidth - bitmapHeight;
                int left = (int) (diff / 2);
                srcRect = new Rect(left, 0, bitmapHeight, bitmapHeight);
            } else {
                int diff = bitmapHeight - bitmapWidth;
                int top = (int) (diff / 2);
                srcRect = new Rect(0, top, bitmapWidth, bitmapWidth);
            }

			RectF rectF = new RectF(dstRect);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			canvas.drawRoundRect(rectF, radius, radius, paint);
			paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		}

		return output;
	}

	public static Bitmap getOriginRoundBitmap(Bitmap bitmap, float radius) {
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, e.getMessage(),e);
			return null;
		}
		if(output!=null){
			Canvas canvas = new Canvas(output);
			canvas.drawColor(Color.TRANSPARENT);
			Rect dstRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());;
			RectF rectF = new RectF(dstRect);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			canvas.drawRoundRect(rectF, radius, radius, paint);
			paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		}

		return output;
	}

	public static Bitmap getDefaultHeadBitmap(Context context){
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tupian);
		Bitmap bitmapHead = ImageUtils.getCircleBitmap(bitmap, bitmap.getWidth() / 2);
		bitmap.recycle();
		return bitmapHead;
	}

	public static Bitmap getCircleBitmap(Context context, int resource){

		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
		} catch (Exception e) {
			Log.w(TAG, e);
			return null;
		}
		Bitmap bitmapHead = ImageUtils.getCircleBitmap(bitmap, bitmap.getWidth() / 2);
		bitmap.recycle();
		return bitmapHead;
	}

	/**
	 * 获取圆形图片
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap, float radius) {
		return getCircleBitmap(bitmap, radius, 0);
	}


	/**
	 * 获取圆形图片c
	 * @param bitmap
	 * @param radius
	 * @param padding  源图片四周需要忽略的宽度
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap, float radius, int padding) {
		if (bitmap == null) {
			return null;
		}
		int width = (int)radius*2;
		if (padding < 0) {
			padding = 0;
		}
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		} catch (Exception e) {
			Log.w(TAG, e);
			return null;
		} catch (OutOfMemoryError e){
			Log.w(TAG, e);
			return null;
		}
		if(output == null){
			return null;
		}
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.TRANSPARENT);
		Rect dstRect = new Rect(0, 0, width, width);
		Rect srcRect = new Rect(padding, padding, bitmap.getWidth()-padding, bitmap.getHeight()-padding);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setFilterBitmap(true);
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return output;
	}


	/**
	 * 获取指定大小的图片
	 * @param bitmap
	 * @param width    生成图片的宽度
	 * @param height	 生成图片的高度
	 * @return
	 */
	public static Bitmap getSmallBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			return null;
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		} catch (Exception e) {
			// EXCEPTION_TODO: handle exception
			return null;
		}catch (OutOfMemoryError e) {
			// EXCEPTION_TODO: handle exception
			return null;
		}
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.TRANSPARENT);
		Rect dstRect = new Rect(0, 0, width, height);
		Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return output;
	}



	/**
	 * 根据提供的视图的大小，裁剪出相应的图片
	 * @param bitmap
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap getCropBitmap(Bitmap bitmap, int viewWidth, int viewHeight) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = null;
		int width = 0;
		int height = 0;
		Rect srcRect = new Rect();
		int imageW = bitmap.getWidth();
		int imageH = bitmap.getHeight();
		if (imageW <= viewWidth && imageH <= viewWidth) {
			return bitmap;
		} else if (imageW > viewWidth && imageH <= viewHeight) {
			width = viewWidth;
			height = imageH;
			int left = (imageW - viewWidth) / 2;
			srcRect.set(left, 0, left + viewWidth, imageH);
		} else if (imageW <= viewWidth && imageH > viewHeight) {
			width = imageW;
			height = viewHeight;
			int top = (imageH - viewHeight) / 2;
			srcRect.set(0, top, imageW, top + viewHeight);
		} else {
			width = viewWidth;
			height = viewHeight;
			double wRate = imageW * 1.0 / viewWidth;
			double hRate = imageH * 1.0 / viewHeight;
			if (wRate >= hRate) { // 优先以高等比缩放
				int left = (int)((imageW - viewWidth * hRate) / 2);
				int right = (int)(left + viewWidth * hRate);
				srcRect.set(left, 0, right, imageH);
			} else {	// 优先以宽等比缩放
				int top = (int)((imageH - viewHeight * wRate) / 2);
				int bottom = (int)(top + viewHeight * wRate);
				srcRect.set(0, top, imageW, bottom);
			}
		}
		try {
			output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		}
		Canvas canvas = new Canvas(output);
		canvas.drawColor(Color.WHITE);
		Rect dstRect = new Rect(0, 0, width, height);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return output;
	}

	/**
     * 获取照片的角度 两种方式:1.根据绝对路径或根据Uri
     *
     * @param imagePath
     *            照片的路径
     * @param context
     * @param photoUri
     *
     * */
	public static int getOrientation(String imagePath, Context context,
            Uri photoUri) {
        int nOrientation = 0;
        if (!TextUtils.isEmpty(imagePath)) {
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                nOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 1);
                switch (nOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                }

            } catch (IOException e) {
                Log.w(TAG, e);
            }
		} else if (context != null && photoUri != null) {
			Cursor cursor = null;
			try {
				cursor = DataBaseUtils.doContentResolverQueryWrapper(context,photoUri,
								new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
								null, null, null);

				if (cursor == null || cursor.getCount() != 1) {
					return 0;
				}
				cursor.moveToFirst();

				return cursor.getInt(0);
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}

		}

        return 0;
    }

	public static Bitmap rotateBitmap(Bitmap b, int degrees) {
	      if (degrees != 0 && b != null) {
	         Matrix m = new Matrix();
	         m.setRotate(degrees, b.getWidth() / 2, b.getHeight() / 2);
	         try {
	            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
	            b.recycle();
	            b = null;
	            return b2;
	         } catch (Exception e) {
	            Log.w(TAG, e);
	         } catch (Throwable ex) {
	            ex.printStackTrace();
	         }
	      }
	      return null;
	   }



	/**
	 * 将图片保存到相册
	 * @param context
	 * @param directory
	 * @param filename
	 * @param source
	 * @return
	 */
	public static boolean saveImageToAlbum(Context context, String directory, String filename, Bitmap source) {
		if (!TextUtils.equals(Environment.getExternalStorageState(),
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, R.string.insert_sdcard, Toast.LENGTH_SHORT).show();
			return false;
		}
		boolean success = false;
		OutputStream outputStream = null;
		try {
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(directory, filename);
			if (file.createNewFile()) {
				outputStream = new FileOutputStream(file);
				if (source != null) {
					source.compress(CompressFormat.JPEG, 75, outputStream);
					success = true;
				}
			} else {
				success = true;
			}
		} catch (FileNotFoundException ex) {
			Log.i(TAG, ex.getMessage());
		} catch (IOException ex) {
			Log.i(TAG, ex.getMessage());
		} catch (OutOfMemoryError e){
			Log.i(TAG, e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
					Log.i(TAG, t.getMessage());
				}
			}
		}
		String filePath = directory + "/" + filename;
		ContentValues values = new ContentValues(7);
		values.put(Images.Media.TITLE, filename);
		values.put(Images.Media.DISPLAY_NAME, filename);
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(Images.Media.DATA, filePath);
		try {
			 context.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		} catch (IllegalArgumentException e) {
			Log.i(TAG, e.getMessage());
			success = false;
		} catch (IllegalStateException e){
			Log.i(TAG, e.getMessage());
			success = false;
		}
		return success;
	}

	/**
	 * 将图片和imageView解绑
	 * @param vg
	 */
	public static void recycleImageView(ViewGroup vg){
	      for(int i=0;i<vg.getChildCount();i++){
	            View childView=vg.getChildAt(i);
	            if(childView instanceof ViewGroup){
	               recycleImageView((ViewGroup)childView);
	            }else if(childView instanceof ImageView){
	                ImageView iv=(ImageView)childView;
	                iv.setImageBitmap(null);
	            }
	      }
	   }
}
