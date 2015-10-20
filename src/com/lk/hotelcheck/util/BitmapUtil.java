package com.lk.hotelcheck.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

/**
 * 
 * 类描述:bitmap处理工具类
 * 功能详细描述:
 * 
 * @author  huyong
 * @date  [2012-8-25]
 */
public class BitmapUtil {
	private static final String TAG = "BitmapUtility";

	/**
	 * 功能简述:创建一张当前的view的bitmap截图
	 * 功能详细描述:根据指定的缩放比例，对当前view进行截图，并返回截图bitmap
	 * 注意:
	 * @param view：待画的view
	 * @param scale：缩放比例
	 * @return：view的截图，若当前view为null或宽高<=0，则返回null。
	 */
	public static final Bitmap createBitmap(View view, float scale) {
		Bitmap pRet = null;
		if (null == view) {
			Log.i(TAG, "create bitmap function param view is null");
			return pRet;
		}

		int scaleWidth = (int) (view.getWidth() * scale);
		int scaleHeight = (int) (view.getHeight() * scale);
		if (scaleWidth <= 0 || scaleHeight <= 0) {
			Log.i(TAG, "create bitmap function param view is not layout");
			return pRet;
		}

		boolean bViewDrawingCacheEnable = view.isDrawingCacheEnabled();
		if (!bViewDrawingCacheEnable) {
			view.setDrawingCacheEnabled(true);
		}
		try {
			Bitmap viewBmp = view.getDrawingCache(true);
			// 如果拿到的缓存为空
			if (viewBmp == null) {
				pRet = Bitmap.createBitmap(scaleWidth, scaleHeight, view.isOpaque()
						? Config.RGB_565
						: Config.ARGB_8888);
				Canvas canvas = new Canvas(pRet);
				canvas.scale(scale, scale);
				view.draw(canvas);
				canvas = null;
			} else {
				pRet = Bitmap.createScaledBitmap(viewBmp, scaleWidth, scaleHeight, true);
			}
			viewBmp = null;
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create bitmap exception");
		}
		if (!bViewDrawingCacheEnable) {
			view.setDrawingCacheEnabled(false);
		}

		return pRet;
	}

	/**
	 * 功能简述:创建一张已有bmp居中显示的指定宽高的新Bitmap
	 * 功能详细描述:需要传入已有bmp、新创建Bitmap的宽、高，三个条件，从而创建一张新的Bitmap，使得传入的bmp位于新Bitmap的居中显示。
	 * 注意:新创建的Bitmap的宽高，因不小于原有bmp的宽高。
	 * @param bmp：已有将要拿来居中显示的位图。
	 * @param desWidth：新创建位图的宽度
	 * @param desHeight：新创建位图的高度
	 * @return
	 */
	public static final Bitmap createBitmap(Bitmap bmp, int desWidth, int desHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create bitmap function param bmp is null");
			return pRet;
		}

		try {
			pRet = Bitmap.createBitmap(desWidth, desHeight, Config.ARGB_8888);
			Canvas canvas = new Canvas(pRet);
			int left = (desWidth - bmp.getWidth()) / 2;
			int top = (desHeight - bmp.getHeight()) / 2;
			canvas.drawBitmap(bmp, left, top, null);
			canvas = null;
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create bitmap exception");
		}

		return pRet;
	}
	
	/**
	 * 功能简述:创建一张已有bmp根据left,top值显示的指定宽高的新Bitmap
	 * @param bmp
	 * @param desWidth
	 * @param desHeight
	 * @param left
	 * @param top
	 * @return
	 */
	public static final Bitmap createBitmap(Bitmap bmp, int desWidth, int desHeight, int left, int top) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create bitmap function param bmp is null");
			return pRet;
		}

		try {
			pRet = Bitmap.createBitmap(desWidth, desHeight, Config.ARGB_8888);
			Canvas canvas = new Canvas(pRet);
			canvas.drawBitmap(bmp, left, top, null);
			canvas = null;
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create bitmap exception");
		}

		return pRet;
	}

	/**
	 * 功能简述:创建缩放处理后的新图，若缩放后大小与原图大小相同，则直接返回原图。
	 * 功能详细描述:
	 * 注意:若缩放目标尺寸与原图尺寸相等，则直接返回原图，不再创建新的bitmap
	 * @param bmp：待处理bmp
	 * @param scaleWidth：缩放目标宽
	 * @param scaleHeight：缩放目标高
	 * @return
	 */
	public static final Bitmap createScaledBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create scale bitmap function param bmp is null");
			return pRet;
		}
		// 这里有待改进，这里直接返回原图，有可能原图会在后面recycle，导致创建出来的都会被recycle
		if (scaleWidth == bmp.getWidth() && scaleHeight == bmp.getHeight()) {
			return bmp;
		}

		try {
			pRet = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap exception");
		}

		return pRet;
	}

	/**
	 * 功能简述:将位图保存为指定文件名的文件。
	 * 功能详细描述:
	 * 注意:若已存在同名文件，则首先删除原有文件，若删除失败，则直接退出，保存失败
	 * @param bmp：待保存位图
	 * @param bmpName：保存位图内容的目标文件路径
	 * @return true for 保存成功，false for 保存失败。
	 */
	public static final boolean saveBitmap(Bitmap bmp, String bmpName) {
		return saveBitmap(bmp, bmpName, Bitmap.CompressFormat.PNG);
	}

	public static final boolean saveBitmap(Bitmap bmp, String bmpName, CompressFormat format) {
		if (null == bmp) {
			Log.i(TAG, "save bitmap to file bmp is null");
			return false;
		}
		FileOutputStream stream = null;
		try {
			File file = new File(bmpName);
			if (file.exists()) {
				boolean bDel = file.delete();
				if (!bDel) {
					Log.i(TAG, "delete src file fail");
					return false;
				}
			} else {
				File parent = file.getParentFile();
				if (null == parent) {
					Log.i(TAG, "get bmpName parent file fail");
					return false;
				}
				if (!parent.exists()) {
					boolean bDir = parent.mkdirs();
					if (!bDir) {
						Log.i(TAG, "make dir fail");
						return false;
					}
				}
			}
			boolean bCreate = file.createNewFile();
			if (!bCreate) {
				Log.i(TAG, "create file fail");
				return false;
			}
			stream = new FileOutputStream(file);
			boolean bOk = bmp.compress(format, 100, stream);

			if (!bOk) {
				Log.i(TAG, "bitmap compress file fail");
				return false;
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			return false;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (Exception e2) {
					Log.i(TAG, "close stream " + e2.toString());
				}
			}
		}
		return true;
	}

	/**
	 * 功能简述:根据指定的图片文件的uri，创建图片。
	 * 功能详细描述:
	 * 注意:
	 * @param context
	 * @param uri：目标图片文件的uri
	 * @return
	 */
	public static Bitmap loadBitmap(Context context, Uri uri, int simpleSize) {
		Bitmap pRet = null;
		if (null == context) {
			Log.i(TAG, "load bitmap context is null");
			return pRet;
		}
		if (null == uri) {
			Log.i(TAG, "load bitmap uri is null");
			return pRet;
		}

		InputStream is = null;
		int sampleSize = simpleSize;
		Options opt = new Options();

		boolean bool = true;
		while (bool) {
			try {
				is = context.getContentResolver().openInputStream(uri);
				opt.inSampleSize = sampleSize;
				pRet = null;
				pRet = BitmapFactory.decodeStream(is, null, opt);
				bool = false;
			} catch (OutOfMemoryError e) {
				sampleSize *= 2;
				if (sampleSize > (1 << 10)) {
					bool = false;
				}
			} catch (Throwable e) {
				bool = false;
				Log.i(TAG, e.getMessage());
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (Exception e2) {
					Log.i(TAG, e2.getMessage());
					Log.i(TAG, "load bitmap close uri stream exception");
				}
			}
		}

		return pRet;
	}

	/**
	 * 功能简述:
	 * 功能详细描述:
	 * 注意:
	 * @param drawable
	 * @param w
	 * @param h
	 * @param res
	 * @return
	 */
	public static BitmapDrawable clipDrawable(BitmapDrawable drawable, int w, int h, Resources res) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			if (width < w) {
				w = width;
			}
			if (height < h) {
				h = height;
			}
			int x = (width - w) >> 1;
			int y = (height - h) >> 1;
			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象

			//建立新的bitmap，其内容是对原bitmap的缩放后的图
			Bitmap newbmp = Bitmap.createBitmap(drawable.getBitmap(), x, y, w, h, matrix, true);
			matrix = null;
			// 把 bitmap 转换成 drawable 并返回
			return new BitmapDrawable(res, newbmp);
		}
		return null;
	}

	/**
	 * 重叠合并两张图片，合并后的大小等同于作为底图的图片大小
	 * 
	 * @param background：下层图，即底图
	 * @param foreground：上层图，即前置图
	 * @return 合并后的Bitmap
	 */
	public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground, Paint paint) {
		if (null == background) {
			return null;
		}

		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		// int fgWidth = foreground.getWidth();
		// int fgHeight = foreground.getHeight();
		// create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
		Bitmap newbmp = null;
		try {
			newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			// OOM,return null
			return null;
		}
		Canvas cv = new Canvas(newbmp);
		// draw bg into
		cv.drawBitmap(background, 0, 0, paint); // 在 0，0坐标开始画入bg
		// draw fg into
		if (null != foreground) {
			cv.drawBitmap(foreground, 0, 0, paint); // 在 0，0坐标开始画入fg ，可以从任意位置画入
		}
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG); // 保存
		// store
		cv.restore(); // 存储
		return newbmp;
	}

	/**
	 * 对图标进行灰色处理
	 * 
	 * @param srcDrawable
	 *            源图
	 * @return 非彩色的图片
	 */
	public static Drawable getNeutralDrawable(Drawable srcDrawable) {
		if (srcDrawable != null) {
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.setSaturation(0f);
			// 设为黑白
			srcDrawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			// 设为阴影
			// srcDrawable.setColorFilter(new
			// PorterDuffColorFilter(0x87000000,PorterDuff.Mode.SRC_ATOP));
			return srcDrawable;
		}
		return null;
	}

	/**
	 * 还原图标
	 * 
	 * @param 灰阶处理过得图标
	 * @return 原来的图标
	 */
	public static Drawable getOriginalDrawable(Drawable neturalDrawable) {
		if (neturalDrawable != null) {
			neturalDrawable.setColorFilter(null);
			return neturalDrawable;
		}
		return null;
	}

	public static Drawable composeDrawableTextExpend(Context context, Drawable src, String text,
			int textSize, int padding) {
		if (src == null) {
			return null;
		}
		if (text == null) {
			return src;
		}
		try {
			if (!(src instanceof BitmapDrawable)) {
				Paint paint = new Paint();
				paint.setTextSize(textSize);
				paint.setStyle(Style.FILL_AND_STROKE);
				paint.setColor(Color.WHITE);
				paint.setAntiAlias(true); // 抗锯齿
				paint.setTextAlign(Paint.Align.CENTER);
				int length = (int) paint.measureText(text);
				
				int width = src.getIntrinsicWidth();
				int height = src.getIntrinsicHeight();

				if (width < length + padding * 2) {
					width = length + padding * 2;
				}
				src.setBounds(0, 0, width, height);
				
				Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(temp);
				src.draw(canvas);
				Drawable drawable = new BitmapDrawable(context.getResources(), temp);
				return composeDrawableText(context, drawable, text, textSize);
			} else {
				return composeDrawableText(context, src, text, textSize);
			}
			

		} catch (Exception e) {
		}
		return null;
	}
	
	public static Drawable composeDrawableText(Context context, Drawable src, String text,
			int textSize) {
		if (src == null) {
			return null;
		}
		if (text == null) {
			return src;
		}
		try {
			Bitmap srcBitmap = null;
			if (src instanceof BitmapDrawable) {
				srcBitmap = ((BitmapDrawable) src).getBitmap();
			} else {
				srcBitmap = createBitmapFromDrawable(src);
			}
			if (srcBitmap == null) {
				return null;
			}
			int width = srcBitmap.getWidth();
			int height = srcBitmap.getHeight();
			Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(temp);
			canvas.drawBitmap(srcBitmap, 0, 0, null);

			Paint paint = new Paint();
			paint.setTextSize(textSize);
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true); // 抗锯齿
			paint.setTextAlign(Paint.Align.CENTER);
			int size = text.length();
			int length = (int) paint.measureText(text);
			int center = length / size / 2;
			int offX = width / 2;
			int offY = height / 2 + center + 1;
			canvas.drawText(text, offX, offY, paint);

			return new BitmapDrawable(context.getResources(), temp);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static Bitmap createBitmapFromDrawable(final Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		Bitmap bitmap = null;
		final int intrinsicWidth = drawable.getIntrinsicWidth();
		final int intrinsicHeight = drawable.getIntrinsicHeight();

		try {
			Config config = drawable.getOpacity() != PixelFormat.OPAQUE
					? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config);
		} catch (OutOfMemoryError e) {
			return null;
		}

		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
		drawable.draw(canvas);
		canvas = null;
		return bitmap;
	}
	
	public static Bitmap createBitmapFromDrawable(final Drawable drawable, int intrinsicWidth,
			int intrinsicHeight) {

		if (drawable == null) {
			return null;
		}

		Bitmap bitmap = null;
		if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
			return null;
		}

		try {
			Config config = drawable.getOpacity() != PixelFormat.OPAQUE
					? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config);
		} catch (OutOfMemoryError e) {
			return null;
		}
		if (bitmap == null) {
			return null;
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
		drawable.draw(canvas);
		canvas = null;
		return bitmap;
	}
	
	/**
	 * 将图片设为72*72
	 * 
	 * @param drawable
	 * @return
	 */
	public static BitmapDrawable convertLePhoneIcon(Context context, BitmapDrawable drawable) {
		int width = drawable.getBitmap().getWidth();
		int height = drawable.getBitmap().getHeight();
		int newWidth = 72;
		int newHeight = 72;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// create the new Bitmap object
		Bitmap resizedBitmap = Bitmap.createBitmap(drawable.getBitmap(), 0, 0, width, height,
				matrix, true);
		BitmapDrawable bmd = new BitmapDrawable(context.getResources(), resizedBitmap);
		return bmd;
	}
	
	
	/**
	 * 功能简述:
	 * 功能详细描述:
	 * 注意:
	 * @param bitmap
	 * @param wScale
	 * @param hScale
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float wScale, float hScale) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			matrix.postScale(wScale, hScale); // 设置缩放比例

			//建立新的bitmap，其内容是对原bitmap的缩放后的图
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
			matrix = null;

			return newbmp;
		}
		return null;
	}
	
	/**
	 * 功能简述:对指定drawable进行指定的高宽缩放后，创建一张新的BitmapDrawable。
	 * 功能详细描述:
	 * 注意:
	 * @param context
	 * @param drawable：待处理的drawable
	 * @param w:期望缩放后的BitmapDrawable的宽
	 * @param h：期望缩放后的BitmapDrawable的高
	 * @return 经缩放处理后的新的BitmapDrawable
	 */
	public static BitmapDrawable zoomDrawable(Context context, Drawable drawable, int w, int h) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = null;
			// drawable 转换成 bitmap
			if (drawable instanceof BitmapDrawable) {
				// 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
				oldbmp = ((BitmapDrawable) drawable).getBitmap();
			} else {
				oldbmp = createBitmapFromDrawable(drawable);
			}

			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			float scaleWidth = (float) w / width; // 计算缩放比例
			float scaleHeight = (float) h / height;
			matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例

			//建立新的bitmap，其内容是对原bitmap的缩放后的图
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
			matrix = null;

			//把bitmap转换成drawable并返回
			return new BitmapDrawable(context.getResources(), newbmp);
		}
		return null;
	}
	
	public static BitmapDrawable createBitmapDrawableFromDrawable(final Drawable drawable,
			Context context) {
		Bitmap bitmap = createBitmapFromDrawable(drawable);
		if (bitmap == null) {
			return null;
		}

		BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
		return bitmapDrawable;
	}

	/**
	 * 功能简述:
	 * 功能详细描述:
	 * 注意:
	 * @param drawable
	 * @param wScale
	 * @param hScale
	 * @param res
	 * @return
	 */
	public static BitmapDrawable zoomDrawable(Drawable drawable, float wScale, float hScale,
			Resources res) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = null;
			// drawable 转换成 bitmap
			if (drawable instanceof BitmapDrawable) {
				// 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
				oldbmp = ((BitmapDrawable) drawable).getBitmap();
			} else {
				oldbmp = createBitmapFromDrawable(drawable);
			}

			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			matrix.postScale(wScale, hScale); // 设置缩放比例

			//建立新的bitmap，其内容是对原bitmap的缩放后的图
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
			matrix = null;

			// 把 bitmap 转换成 drawable 并返回
			return new BitmapDrawable(res, newbmp);
		}
		return null;
	}
	
	/**
	 * 将bitmap转成drawable
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Context context, Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 裁剪图片为指定的宽高尺寸
	 * @param srcBitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap fitBitmap(Bitmap srcBitmap, int width, int height) {
        if (srcBitmap == null || srcBitmap.isRecycled()) {
            return null;
        }
        
        if (srcBitmap.getWidth() == width &&
        		srcBitmap.getHeight() == height) {
        	return srcBitmap;
        }

        try {
        	Config config = srcBitmap.getConfig();
        	if (config == null) {
        		config = Config.RGB_565;
        	}
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            Canvas canvasTemp = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();

            float scaleWidth = ((float) width) / srcWidth;
            float scaleHeight = ((float) height) / srcHeight;

			final float scale = Math.max(scaleWidth, scaleHeight);
			final float tx = (width - srcWidth * scale) * 0.5f;
			final float ty = (height - srcHeight * scale) * 0.5f;
			canvasTemp.translate(tx, ty);
            canvasTemp.scale(scale, scale);
            canvasTemp.drawBitmap(srcBitmap, 0, 0, paint);
            return bitmap;
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return srcBitmap;
    }


	/**
	 * 计算获取图片合适的sampleSize
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap, CompressFormat compressFormat) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(compressFormat, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 对Bitmap进行匹配机型的缩放，若不能缩放，则直接返回原图.
	 * 
	 * @param context
	 * @param sourceBitmap
	 *            源Bitmap
	 * @return
	 */
	public static Bitmap scaleBitmapToMachine(Context context, Bitmap sourceBitmap) {
		Bitmap desBitmap = sourceBitmap;
		if (context != null && sourceBitmap != null) {
			int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
			if (densityDpi != DrawUtil.STANDARD_DENSITYDPI) {
				float scale = densityDpi / DrawUtil.STANDARD_DENSITYDPI;
				try {
					desBitmap = Bitmap.createScaledBitmap(sourceBitmap,
							(int) (sourceBitmap.getWidth() * scale),
							(int) (sourceBitmap.getHeight() * scale), true);
				} catch (Error e) {
					// TODO: handle exception
					// Log.i("ThemeStoreUtil",
					// "scaleBitmapToMachine throw exception= " +
					// e.getMessage());
				}
			}
		}
		return desBitmap;
	}
	
	/**
	 * 把Bitmap进行指定大小的缩放，若不能缩放，则直接返回原图。
	 * 
	 * @param sourceBitmap
	 *            源Bitmap
	 * @param mWidth
	 *            目标宽度
	 * @param mHeight
	 *            目标高度
	 * @return
	 */
	public static Bitmap scaleBitmapToDisplay(Context context, Bitmap sourceBitmap,
			int displayWidth, int displayHeight) {
		Bitmap desBitmap = sourceBitmap;
		if (context != null && sourceBitmap != null && displayWidth > 0 && displayHeight > 0) {
			if (sourceBitmap.getWidth() == displayWidth
					&& sourceBitmap.getHeight() == displayHeight) {
				// 目标尺寸与现有尺寸相同，则直接返回。
				return desBitmap;
			}
			float originalWidth = sourceBitmap.getWidth();
			float originalHeight = sourceBitmap.getHeight();
			float scale = 1.0f;
			if (originalWidth > 0.0 && originalHeight > 0.0) {
				float scaleWidth = displayWidth / originalWidth;
				float scaleHeight = displayHeight / originalHeight;
				scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
			}
			try {
				// 等比缩放
				Bitmap bitmap = Bitmap.createScaledBitmap(sourceBitmap,
						(int) (sourceBitmap.getWidth() * scale),
						(int) (sourceBitmap.getHeight() * scale), true);
				if (bitmap != null) {
					int bmpWidth = bitmap.getWidth();
					int bmpHeight = bitmap.getHeight();
					if (bmpWidth == displayWidth && bmpHeight == displayHeight) {
						desBitmap = bitmap;
					} else {
						// 显示缩放
						int startWidth = (bitmap.getWidth() - displayWidth) / 2;
						int startHeight = (bitmap.getHeight() - displayHeight) / 2;
						startWidth = startWidth < 0 ? 0 : startWidth;
						startHeight = startHeight < 0 ? 0 : startHeight;
						desBitmap = Bitmap.createBitmap(bitmap, startWidth, startHeight,
								displayWidth, displayHeight);
						if (!bitmap.equals(desBitmap)) {
							String hashCode = Integer.valueOf(bitmap.hashCode()).toString();
							// 回收中间的缩放bitmap
							bitmap.recycle();
						}
					}
					bitmap = null;
				}

			} catch (Throwable e) {
				// TODO: handle exception
				// Log.i("GoStore",
				// "ThemeStoreUtil.scaleBitmapToDisplay throw error for " +
				// e.getMessage());
			}
		}
		return desBitmap;
	}
	
//	/**
//	 * 应用游戏中心，为图标加上遮罩
//	 * 
//	 * @param bitmap
//	 *            原始bitmap
//	 * @return 处理后的bitmap
//	 */
//	public static synchronized Bitmap createMaskBitmap(Context context, Bitmap bitmap) {
//		if (context == null || bitmap == null || bitmap.isRecycled()) {
//			return null;
//		}
//		Canvas mCanvas = new Canvas();
//		mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
//				| Paint.FILTER_BITMAP_FLAG));
//		Paint mPaint = new Paint();
//		mPaint.setAntiAlias(true);
//		Matrix mMatrix = new Matrix();
//		PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
//
//		BitmapDrawable drawable = new BitmapDrawable(bitmap);
//		// 取罩子和底座和mask蒙版
//		Bitmap base = ((BitmapDrawable) context.getResources().getDrawable(
//				R.drawable.gomarket_category_icon_base)).getBitmap();
//		base = base.copy(Bitmap.Config.ARGB_8888, true);
//		BitmapDrawable cover = null;
//		BitmapDrawable mask = (BitmapDrawable) context.getResources().getDrawable(
//				R.drawable.gomarket_appgame_mask);
//		try {
//			mMatrix.reset();
//			mPaint.reset();
//			drawable = composeIcon(context, base, cover, drawable, mask, mCanvas, mMatrix, mPaint,
//					mXfermode, 1.0f);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (OutOfMemoryError error) {
//			error.printStackTrace();
//		}
//		base = null;
//		cover = null;
//		if (drawable == null) {
//			return bitmap;
//		} else {
//			return drawable.getBitmap();
//		}
//	}
//	
	/**
	 * 合成图片
	 * 
	 * @author huyong
	 * @param base
	 *            ：合成图片底图
	 * @param cover
	 *            ：合成图片罩子
	 * @param drawable
	 *            ： 待合成的源图
	 * @param drawable
	 *            ： 合成图片蒙版
	 * @param canvas
	 *            ：画布
	 * @param matrix
	 *            ：缩放matrix
	 * @param paint
	 *            ：画笔
	 * @param scale
	 *            ：缩放比率
	 * @return
	 */
	public static BitmapDrawable composeIcon(Context context, Bitmap base, BitmapDrawable cover,
			BitmapDrawable drawable, BitmapDrawable mask, Canvas canvas, Matrix matrix,
			Paint paint, PorterDuffXfermode xfermode, float scale) {
		if (context == null || canvas == null || matrix == null || paint == null
				|| drawable == null || drawable.getBitmap() == null) {
			return drawable;
		}
		// 有底图或罩子
		if (base == null) {
			if (cover != null && cover.getBitmap() != null) {
				final Bitmap.Config config = cover.getOpacity() != PixelFormat.OPAQUE
						? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				base = Bitmap.createBitmap(cover.getBitmap().getWidth(), cover.getBitmap()
						.getHeight(), config);
			}
			if (base == null) {
				return drawable;
			}
		}
		int width = base.getWidth();
		int height = base.getHeight();

		float scaleWidth = scale * width; // 缩放后的宽大小
		float scaleHeight = scale * height; // 缩放后的高大小
		Bitmap midBitmap = drawable.getBitmap();
		float scaleFactorW = 0f; // 缩放后较原图的宽的比例
		float scaleFactorH = 0f; // 缩放后较原图的高的比例
		if (midBitmap != null) {
			int realWidth = midBitmap.getWidth();
			int realHeight = midBitmap.getHeight();
			scaleFactorW = scaleWidth / realWidth;
			scaleFactorH = scaleHeight / realHeight;
		}
		canvas.setBitmap(base);
		int saveId = canvas.save();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		matrix.setScale(scaleFactorW, scaleFactorH);
		matrix.postTranslate((width - scaleWidth) / 2f, (height - scaleHeight) / 2f);
		canvas.drawBitmap(drawable.getBitmap(), matrix, paint);
		canvas.restoreToCount(saveId);
		if (cover != null) {
			canvas.drawBitmap(cover.getBitmap(), 0, 0, paint);
		}
		// 加上mask蒙版
		if (mask != null) {
			paint.setXfermode(xfermode);
			canvas.drawBitmap(mask.getBitmap(), 0, 0, paint);
		}
		return new BitmapDrawable(base);
	}

	public static  Bitmap drawTextToBitmap(Bitmap bitmap, String gText) { 
		if (bitmap == null) {
			return null;
		}
			  android.graphics.Bitmap.Config bitmapConfig =  
			      bitmap.getConfig();  
			  
			  
			  
//			  // set default bitmap config if none  
			  if(bitmapConfig == null) {  
			    bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;  
			  }  
//			  // resource bitmaps are imutable,   
//			  // so we need to convert it to mutable one  
			Bitmap  textBitmap = bitmap.copy(bitmapConfig, true);  
			   
			  Canvas canvas = new Canvas(textBitmap);  
			  // new antialised Paint  
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
			  // text color - #3D3D3D  
			  paint.setColor(Color.WHITE);    
			  paint.setTextSize(DrawUtil.sp2px(30));		  
		       paint.setDither(true); //获取跟清晰的图像采样  
		       paint.setFilterBitmap(true);//过滤一些  
			  Rect bounds = new Rect();  
			  paint.getTextBounds(gText, 0, gText.length(), bounds);  			  
			  int x = 30;  
			  int y = 30;  			   
			  canvas.drawText(gText, 20, textBitmap.getHeight()-20, paint);  
			  bitmap = null;
			  return textBitmap;  
			} 

}
