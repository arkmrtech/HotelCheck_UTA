package com.lk.hotelcheck.util;

/**
 * 
 */

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;




/**
 * 图片工具类 封装一些对MImage的操作以方便使用
 * 
 * @author dengweiming
 * 
 */
public class ImageUtil {
	public final static int TILEMODE = 0;
	public final static int STRETCHMODE = 1;
	public final static int CENTERMODE = 2;

	/**
	 * 绘制平铺图片 指定一个矩形和图片，这个图片根据自己大小填充矩形 当图片大小大于矩形框时，默认拉伸图片与矩形大小一样
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */

	public static void drawTileImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		if (bitmap.getWidth() > (right - left) || bitmap.getHeight() > (bottom - top)) {
			// 图片比矩形大时，让图片自动拉伸成矩形大小
			drawStretchImage(canvas, bitmap, left, top, right, bottom, paint);
		} else {
			Rect rect = new Rect(0, 0, right - left, bottom - top);
			BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT,
					Shader.TileMode.REPEAT);
			Shader shaderBak = paint.getShader();
			paint.setShader(shader);
			canvas.save();
			canvas.translate(left, top);
			canvas.drawRect(rect, paint);
			paint.setShader(shaderBak);
			canvas.restore();
		}
	}

	/**
	 * 绘制图片于矩形中间 指定一个矩形和图片，这个图片绘制在矩形中间 如果图片大于矩形时，图片将以fit to widow的方式显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawCenterImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		int offsetx = 0;
		int offsety = 0;
		int imageW;
		int imageH;
		int newWidth;
		int newHeight;

		imageW = bitmap.getWidth();
		imageH = bitmap.getHeight();
		newWidth = right - left;
		newHeight = bottom - top;

		canvas.save();
		if (imageW > newWidth || imageH > newHeight) // 图片大于矩形时
		{
			float factor;
			if (newWidth * imageH > newHeight * imageW) {
				// 以宽度为主进行缩放
				factor = (float) newHeight / imageH;
				offsetx = (newWidth - (int) (factor * imageW)) / 2;
				offsety = (newHeight - (int) (factor * imageH)) / 2;
				canvas.translate(left + offsetx, top + offsety);
				canvas.scale(factor, factor);

			} else {
				// 以高度为主进行缩放
				factor = (float) newWidth / imageW;
				offsetx = (newWidth - (int) (factor * imageW)) / 2;
				offsety = (newHeight - (int) (factor * imageH)) / 2;
				canvas.translate(left + offsetx, top + offsety);
				canvas.scale(factor, factor);
			}
		} else {
			offsetx = (newWidth - imageW) / 2;
			offsety = (newHeight - imageH) / 2;
			canvas.translate(left + offsetx, top + offsety);
		}
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}

	/**
	 * 绘制拉伸图片 将图片拉伸至矩形大小显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawStretchImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		final float scaleFactorW = (right - left) / (float) bitmap.getWidth();
		final float scaleFactorH = (bottom - top) / (float) bitmap.getHeight();

		canvas.save();
		canvas.translate(left, top);
		canvas.scale(scaleFactorW, scaleFactorH);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}

	public static void computeStretchMatrix(Matrix matrix, int w, int h, int left, int top,
			int right, int bottom) {
		matrix.setTranslate(left, top);
		// if (Machine.isM9()){
		matrix.preScale((float) (1.01 * (right - left) / w), (float) (1.01 * (bottom - top) / h));
		// }else{
		// matrix.preScale((right - left) / (float)w, (bottom - top) /
		// (float)h);
		// }
	}

	/**
	 * 绘制图片 根据mode的不同绘制不同方式的图片
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param mode
	 *            0 ：平铺； 1 ：拉伸； 2：居中
	 * @param left
	 *            渐变区域的左边界
	 * @param top
	 *            渐变区域的上边界
	 * @param right
	 *            渐变区域的右边界
	 * @param bottom
	 *            渐变区域的下边界
	 * @param paint
	 *            画笔，当平铺时paint不能为null
	 */

	public static void drawImage(Canvas canvas, Drawable pic, int mode, int left, int top,
			int right, int bottom, Paint paint) {
		if (pic instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) pic).getBitmap();
			if ((bitmap != null) && (!bitmap.isRecycled())) {
				switch (mode) {
					case TILEMODE :
						drawTileImage(canvas, bitmap, left, top, right, bottom, paint);
						break;

					case STRETCHMODE :
						drawStretchImage(canvas, bitmap, left, top, right, bottom, paint);
						break;

					case CENTERMODE :
						drawCenterImage(canvas, bitmap, left, top, right, bottom, paint);
						break;
				}
			}
		} else if (pic instanceof NinePatchDrawable) {
			pic.setBounds(left, top, right, bottom);
			pic.draw(canvas);
		}
	}

	/**
	 * 绘制图片于矩形中间 指定一个矩形和图片，这个图片绘制在矩形中间 如果图片大于矩形时，图片将以fit to widow的方式显示
	 * 
	 * @param canvas
	 *            画布
	 * @param bitmap
	 *            图片
	 * @param left
	 *            左边界
	 * @param top
	 *            上边界
	 * @param right
	 *            右边界
	 * @param bottom
	 *            下边界
	 * @param paint
	 *            画笔，不能为null
	 */
	public static void drawFitImage(Canvas canvas, Bitmap bitmap, int left, int top, int right,
			int bottom, Paint paint) {
		if (bitmap == null) {
			return;
		}
		int offsetx = 0;
		int offsety = 0;
		int imageW;
		int imageH;
		int newWidth;
		int newHeight;

		imageW = bitmap.getWidth();
		imageH = bitmap.getHeight();
		newWidth = right - left;
		newHeight = bottom - top;

		canvas.save();
		canvas.clipRect(left, top, right, bottom);
		float factor;
		if (newWidth * imageH < newHeight * imageW) {
			// 以宽度为主进行缩放
			factor = (float) newHeight / imageH;
			offsetx = (newWidth - (int) (factor * imageW)) / 2;
			offsety = (newHeight - (int) (factor * imageH)) / 2;
			canvas.translate(left + offsetx, top + offsety);
			canvas.scale(factor, factor);

		} else {
			// 以高度为主进行缩放
			factor = (float) newWidth / imageW;
			offsetx = (newWidth - (int) (factor * imageW)) / 2;
			offsety = (newHeight - (int) (factor * imageH)) / 2;
			canvas.translate(left + offsetx, top + offsety);
			canvas.scale(factor, factor);
		}
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.restore();
	}
	/**
	 * 将tag画到原图的右上角
	 * @param context
	 * @param drawable
	 * @param tag
	 * @return
	 */
	public static Drawable drawRightTopTag(Context context, Drawable drawable, Drawable tag) {
		int drawableHeight = drawable.getIntrinsicHeight();
		int drawableWidth = drawable.getIntrinsicWidth();
		Bitmap bmp = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		int h = tag.getIntrinsicHeight();
		int w = tag.getIntrinsicWidth();
		drawable.setBounds(0, 0, drawableWidth, drawableHeight);
		drawable.draw(canvas);
		canvas.save();
		canvas.translate(drawableWidth - w - DrawUtil.dip2px(5), DrawUtil.dip2px(5));
		tag.setBounds(0, 0, w, h);
		tag.draw(canvas);
		canvas.restore();
		BitmapDrawable bmd = new BitmapDrawable(bmp);
		bmd.setTargetDensity(context.getResources().getDisplayMetrics());
		return bmd;
	}
	/**
	 * 两张图对齐画
	 * @param context
	 * @param drawable
	 * @param tag
	 * @return
	 */
	public static Drawable drawCoverImage(Context context, Drawable drawable, Drawable tag) {
		int drawableHeight = drawable.getIntrinsicHeight();
		int drawableWidth = drawable.getIntrinsicWidth();
		Bitmap bmp = Bitmap.createBitmap(drawableWidth, drawableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		int h = tag.getIntrinsicHeight();
		int w = tag.getIntrinsicWidth();
		drawable.setBounds(0, 0, drawableWidth, drawableHeight);
		drawable.draw(canvas);
		tag.setBounds(0, 0, w, h);
		tag.draw(canvas);
		BitmapDrawable bmd = new BitmapDrawable(bmp);
		bmd.setTargetDensity(context.getResources().getDisplayMetrics());
		return bmd;
	}
	
	/**
	 * 获得圆角图片的方法
	 * @param bitmap
	 * @param roundPx
	 * @return , int newWidth, int newHeight
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	
	/**
	 * 按照指定宽高缩放图片后，并获得圆角图片的方法
	 * @param bitmap
	 * @param roundPx
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx , int newWidth, int newHeight, float scale) {
		if (bitmap == null) {
			return bitmap;
		}
		//获取Bitmap宽度
		int width = bitmap.getWidth();
		//获取Bitmap高度
		int height = bitmap.getHeight();
		Rect src = new Rect(0, 0, width, height);
		if (width == newWidth && height == newHeight) {
//			output = bitmap;
		} else {
			if (width * scale - newWidth > 0 || height * scale - newHeight > 0) {
				// 缩放并截取图片
//				output = Bitmap.createBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true), width * scale - newWidth > 0 ? (int) (width * scale - newWidth) / 2 : 0, height * scale - newHeight > 0 ? (int) (height * scale - newHeight) / 2 : 0, newWidth, newHeight);
				int x = width - newWidth / scale > 0 ? (int) (width - newWidth / scale) / 2 : 0;
				int y = height - newHeight / scale > 0 ? (int) (height - newHeight / scale) / 2 : 0;
				src.set(x, y, width - x, height - y);
			}
		}
		Bitmap newBitmap = null;
		try {
			newBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
		}
		if (newBitmap != null) {
			Canvas canvas = new Canvas(newBitmap);
			final int color = 0xff424242;
			final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			final Rect rect = new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
			final RectF rectF = new RectF(rect);
//			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, src, rect, paint);
			return newBitmap;
		}
		return bitmap;
	}
	
	/**
	 * 获得圆角图片的方法
	 * @param bitmap
	 * @param roundPx
	 * @return , int newWidth, int newHeight
	 */
	/**
	 * 按照指定宽高缩放图片后，并获得圆角图片的方法
	 * @param bitmap
	 * @param roundPx
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx , int newWidth, int newHeight) {
		if (bitmap == null) {
			return bitmap;
		}
//		Bitmap output;
		//获取Bitmap宽度
		int width = bitmap.getWidth();
		//获取Bitmap高度
		int height = bitmap.getHeight();
		Rect src = new Rect(0, 0, width, height);
		if (width == newWidth && height == newHeight) {
//			output = bitmap;
		} else {
//			Matrix matrix = new Matrix();
			//参考Bitmap高度获取缩放比例(高度)
			float scale = newHeight / (float) height;
			//如果缩放出来的宽度少于需要的宽度,则参照宽度比例缩放Bitmap.
			if (width * scale < newWidth) {
				scale = newWidth / (float) width;
			}
			//设置缩放比例
//			matrix.postScale(scale, scale);
			if (width * scale - newWidth > 0 || height * scale - newHeight > 0) {
				// 缩放并截取图片
//				output = Bitmap.createBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true), width * scale - newWidth > 0 ? (int) (width * scale - newWidth) / 2 : 0, height * scale - newHeight > 0 ? (int) (height * scale - newHeight) / 2 : 0, newWidth, newHeight);
				int x = width - newWidth / scale > 0 ? (int) (width - newWidth / scale) / 2 : 0;
				int y = height - newHeight / scale > 0 ? (int) (height - newHeight / scale) / 2 : 0;
				src.set(x, y, width - x, height - y);
			}
		}
		Bitmap newBitmap = null;
		try {
			newBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
		}
		if (newBitmap != null) {
			Canvas canvas = new Canvas(newBitmap);
			final int color = 0xff424242;
			final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			final Rect rect = new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight());
			final RectF rectF = new RectF(rect);
//			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, src, rect, paint);
			return newBitmap;
		}
		return bitmap;
	}
	
	/**
	 * 通过传入的大小返回对应的图片(缩放/剪切等操作)
	 * @param bitmap 要缩放的图片
	 * @param width 要缩放的宽度
	 * @param height 要缩放的高度
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		if (bitmap == null) {
			return bitmap;
		}
		//获取Bitmap宽度
		int width = bitmap.getWidth();
		//获取Bitmap高度
		int height = bitmap.getHeight();
		if (width == newWidth && height == newHeight) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		//参考Bitmap高度获取缩放比例(高度)
		float scale = newHeight / (float) height;
		//如果缩放出来的宽度少于需要的宽度,则参照宽度比例缩放Bitmap.
		if (width * scale < newWidth) {
			scale = newWidth / (float) width;
		}
		//设置缩放比例
		matrix.postScale(scale, scale);
		if (width * scale - newWidth > 0 || height * scale - newHeight > 0) {
			// 缩放并截取图片
			return Bitmap.createBitmap(Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true), width * scale - newWidth > 0 ? (int) (width * scale - newWidth) / 2 : 0, height * scale - newHeight > 0 ? (int) (height * scale - newHeight) / 2 : 0, newWidth, newHeight);
		}
		//缩放图片
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}	
	
//	/**
//	 * <br>功能简述:为view设置毛玻璃效果的背景图
//	 * <br>功能详细描述:
//	 * <br>注意: Android API17以上
//	 * @param context
//	 * @param bkg
//	 * @param view
//	 * @param radius
//	 */
//	public static Bitmap blur(Context context, Bitmap bkg, float radius) {
//		Bitmap bitmap = bkg.copy(bkg.getConfig(), true);
//		final RenderScript rs = RenderScript.create(context);
//		final Allocation input = Allocation.createFromBitmap(rs, bkg,
//				Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
//		final Allocation output = Allocation.createTyped(rs, input.getType());
//		final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//		script.setRadius(radius);
//		script.setInput(input);
//		script.forEach(output);
//		output.copyTo(bitmap);
//		rs.destroy();
//		return bitmap;
//	}

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:Android API17以下使用，有效果问题，不推荐使用
	 * This method was copied from http://stackoverflow.com/a/10028267/694378.
	 * The only modifications I've made are to remove a couple of Log
	 * statements which could slow things down slightly.
	 * 
	 * // Stack Blur v1.0 from
	    // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
	    //
	    // Java Author: Mario Klingemann <mario at quasimondo.com>
	    // http://incubator.quasimondo.com
	    // created Feburary 29, 2004
	    // Android port : Yahel Bouaziz <yahel at kayenko.com>
	    // http://www.kayenko.com
	    // ported april 5th, 2012

	    // This is a compromise between Gaussian Blur and Box blur
	    // It creates much better looking blurs than Box Blur, but is
	    // 7x faster than my Gaussian Blur implementation.
	    //
	    // I called it Stack Blur because this describes best how this
	    // filter works internally: it creates a kind of moving stack
	    // of colors whilst scanning through the image. Thereby it
	    // just has to add one new block of color to the right side
	    // of the stack and remove the leftmost color. The remaining
	    // colors on the topmost layer of the stack are either added on
	    // or reduced by one, depending on if they are on the right or
	    // on the left side of the stack.
	    //
	    // If you are using this algorithm in your code please add
	    // the following line:
	    //
	    // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		if (radius < 1) {
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = i / divsum;
		}
		yw = yi = 0;
		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;
		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = p & 0x0000ff;
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;
			for (x = 0; x < w; x++) {
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = p & 0x0000ff;
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer % div];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				sir = stack[i + radius];
				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				rbs = r1 - Math.abs(i);
				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];
				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return bitmap;
	}
	
	
	/**
     * Utility method for downsampling images.
     *
     * @param path
     *            the file path
     * @param data
     *            if file path is null, provide the image data directly
     * @param target
     *            the target dimension
     * @param isWidth
     *            use width as target, otherwise use the higher value of height
     *            or width
     * @param round
     *            corner radius
     * @return the resized image
     */
    public static Bitmap getResizedImage(String path, byte[] data, int target,
            boolean isWidth, int round) {
 
        Options options = null;
 
        if (target > 0) {
 
            Options info = new Options();
            info.inJustDecodeBounds = true;
            //设置这两个属性可以减少内存损耗
            info.inInputShareable = true;
            info.inPurgeable = true;
 
            decode(path, data, info);
 
            int dim = info.outWidth;
            if (!isWidth)
                dim = Math.max(dim, info.outHeight);
            int ssize = sampleSize(dim, target);
 
            options = new Options();
            options.inSampleSize = ssize;
 
        }
 
        Bitmap bm = null;
        try {
            bm = decode(path, data, options);
        } catch (OutOfMemoryError e) {
//            L.red(e.toString());
            e.printStackTrace();
        }
 
        if (round > 0) {
            bm = getRoundedCornerBitmap(bm, round);
        }
 
        return bm;
 
    }
 
    private static Bitmap decode(String path, byte[] data,
            BitmapFactory.Options options) {
 
        Bitmap result = null;
 
        if (path != null) {
 
            result = decodeFile(path, options);
 
        } else if (data != null) {
 
            // AQUtility.debug("decoding byte[]");
 
            result = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);
 
        }
 
        if (result == null && options != null && !options.inJustDecodeBounds) {
            Log.d("decode image failed", path);
        }
 
        return result;
    }
 
    private static Bitmap decodeFile(String path, BitmapFactory.Options options) {
 
        Bitmap result = null;
 
        if (options == null) {
            options = new Options();
        }
 
        options.inInputShareable = true;
        options.inPurgeable = true;
 
        FileInputStream fis = null;
 
        try {
 
            fis = new FileInputStream(path);
 
            FileDescriptor fd = fis.getFD();
 
            // AQUtility.debug("decoding file");
            // AQUtility.time("decode file");
 
            result = BitmapFactory.decodeFileDescriptor(fd, null, options);
 
            // AQUtility.timeEnd("decode file", 0);
        } catch (IOException e) {
            Log.e("TAG",e.toString());
        } finally {
            try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
 
        return result;
 
    }
 
    private static int sampleSize(int width, int target) {
 
        int result = 1;
 
        for (int i = 0; i < 10; i++) {
 
            if (width < target * 2) {
                break;
            }
 
            width = width / 2;
            result = result * 2;
 
        }
 
        return result;
    }
 
    /**
     * 获取圆角的bitmap
     * @param bitmap
     * @param pixels
     * @return
     */
    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
 
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
 
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
 
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
 
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
 
        return output;
    }
 
    /**
     * auto fix the imageOrientation
     * @param bm source
     * @param iv imageView  if set invloke it's setImageBitmap() otherwise do nothing
     * @param uri image Uri if null user path
     * @param path image path if null use uri
     */
    public static Bitmap autoFixOrientation(Bitmap bm, ImageView iv, Uri uri,String path) {
        int deg = 0;
        try {
            ExifInterface exif = null;
            if (uri == null) {
                exif = new ExifInterface(path);
            }
            else if (path == null) {
                exif = new ExifInterface(uri.getPath());
            }
 
            if (exif == null) {
                Log.e("TAG","exif is null check your uri or path");
                return bm;
            }
 
            String rotate = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int rotateValue = Integer.parseInt(rotate);
            System.out.println("orientetion : " + rotateValue);
            switch (rotateValue) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                deg = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                deg = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                deg = 270;
                break;
            default:
                deg = 0;
                break;
            }
        } catch (Exception ee) {
            Log.d("catch img error", "return");
            if(iv != null)
            iv.setImageBitmap(bm);
            return bm;
        }
        Matrix m = new Matrix();
        m.preRotate(deg);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
 
        //bm = Compress(bm, 75);
        if(iv != null)
            iv.setImageBitmap(bm);
        return bm;
    }

	public static boolean isWidthPic(String localImagePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(localImagePath);
		if (bitmap != null) {
			return bitmap.getWidth() > bitmap.getHeight();
		} 
		return false;
	}
}
