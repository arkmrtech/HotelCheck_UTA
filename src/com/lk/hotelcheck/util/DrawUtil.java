package com.lk.hotelcheck.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * 绘制工具类
 * 
 * @author luopeihuan
 * 
 */
public class DrawUtil {
	public static final int NAVBAR_LOCATION_RIGHT = 1;
	public static final int NAVBAR_LOCATION_BOTTOM = 2;
	public static float sDensity = 1.0f;
	public static int sDensityDpi;
	public static int sWidthPixels;
	public static int sHeightPixels;
	public static float sFontDensity;
	public static int sTouchSlop = 15; // 点击的最大识别距离，超过即认为是移动
	public static int sNavBarLocation;

	private static int sRealWidthPixels;
	private static int sRealHeightPixels;
	private static int sNavBarWidth; // 虚拟键宽度
	private static int sNavBarHeight; // 虚拟键高度
	public static int sStatusHeight; // 平板中底边的状态栏高度
	private static int sStatusBarHeight = -1; // 手机的状态栏高度
	private static Class sClass = null;
	private static Method sMethodForWidth = null;
	private static Method sMethodForHeight = null;

	public static final float STANDARD_DENSITYDPI = 240f; //标准屏幕的densityDpi
	
	// 在某些机子上存在不同的density值，所以增加两个虚拟值
	public static float sVirtualDensity = -1;
	public static float sVirtualDensityDpi = -1;
	/**
	 * dip/dp转像素
	 * 
	 * @param dipValue
	 *            dip或 dp大小
	 * @return 像素值
	 */
	public static int dip2px(float dipVlue) {
		return (int) (dipVlue * sDensity + 0.5f);
	}

	/**
	 * 像素转dip/dp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return dip值
	 */
	public static int px2dip(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * sp 转 px
	 * 
	 * @param spValue
	 *            sp大小
	 * @return 像素值
	 */
	public static int sp2px(float spValue) {
		final float scale = sDensity;
		return (int) (scale * spValue);
	}

	/**
	 * px转sp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return sp值
	 */
	public static int px2sp(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale);
	}

	public static void resetDensity(Context context) {
		if (context != null && null != context.getResources()) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			sDensity = metrics.density;
			sFontDensity = metrics.scaledDensity;
			sWidthPixels = metrics.widthPixels;
			sHeightPixels = metrics.heightPixels;
			sDensityDpi = metrics.densityDpi;
			if (Machine.isTablet(context)) {
				sStatusHeight = getTabletScreenHeight(context) - sHeightPixels;
			}
			try {
				final ViewConfiguration configuration = ViewConfiguration.get(context);
				if (null != configuration) {
					sTouchSlop = configuration.getScaledTouchSlop();
				}
			} catch (Throwable e) {
				Log.i("DrawUtils", "resetDensity has error" + e.getMessage());
			}
			resetNavBarHeight(context);
		}
	}

	private static void resetNavBarHeight(Context context) {
		if (context != null) {
			try {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				Point realSize = new Point();
				Method method = sClass.getMethod("getRealSize", Point.class);
				method.invoke(display, realSize);
				sRealWidthPixels = realSize.x;
				sRealHeightPixels = realSize.y;
				sNavBarWidth = realSize.x - sWidthPixels;
				sNavBarHeight = realSize.y - sHeightPixels;
			} catch (Throwable e) {
				e.printStackTrace();
				sRealWidthPixels = sWidthPixels;
				sRealHeightPixels = sHeightPixels;
				sNavBarHeight = 0;
			}
		}
		sNavBarLocation = getNavBarLocation();
	}
	
	public static int getTabletScreenWidth(Context context) {
		int width = 0;
		if (context != null) {
			try {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				if (sMethodForWidth == null) {
					sMethodForWidth = sClass.getMethod("getRealWidth");
				}
				width = (Integer) sMethodForWidth.invoke(display);
			} catch (Exception e) {
			}
		}

		// Rect rect= new Rect();
		// ((Activity)
		// context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		// int statusbarHeight = height - rect.bottom;
		if (width == 0) {
			width = sWidthPixels;
		}

		return width;
	}

	public static int getTabletScreenHeight(Context context) {
		int height = 0;
		if (context != null) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			try {
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				if (sMethodForHeight == null) {
					sMethodForHeight = sClass.getMethod("getRealHeight");
				}
				height = (Integer) sMethodForHeight.invoke(display);
			} catch (Exception e) {
			}
		}

		// Rect rect= new Rect();
		// ((Activity)
		// context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		// int statusbarHeight = height - rect.bottom;
		if (height == 0) {
			height = sHeightPixels;
		}

		return height;
	}
	
	public static void setVirtualDensity(float density) {
		sVirtualDensity = density;
	}
	
	public static void setVirtualDensityDpi(float densityDpi) {
		sVirtualDensityDpi = densityDpi;
	}
	
	public static int getRealWidth() {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			return sRealWidthPixels;
		}
		return sWidthPixels;
	}
	
	public static int getRealHeight() {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			return sRealHeightPixels;
		}
		return sHeightPixels;
	}
	

	/**
	 * 虚拟键在下面时
	 * @return
	 */
	public static int getNavBarHeight() {
		if (Machine.IS_SDK_ABOVE_KITKAT && Machine.canHideNavBar()) {
			return sNavBarHeight;
		}
		return 0;
	}
	
	/**
	 * 横屏，虚拟键在右边时
	 * @return
	 */
	public static int getNavBarWidth() {
		if (Machine.IS_SDK_ABOVE_KITKAT && Machine.canHideNavBar()) {
			return sNavBarWidth;
		}
		return 0;
	}
	
	public static int getNavBarLocation() {
		if (sRealWidthPixels > sWidthPixels) {
			return NAVBAR_LOCATION_RIGHT;
		}
		return NAVBAR_LOCATION_BOTTOM;
	}
	
	public static boolean isPortait(Context context) {
		if (getScreenHeight(context) > getScreenWidth(context)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 屏幕高度(px)
	 * 
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		if (Machine.isTablet(context)) {
			return DrawUtil.getTabletScreenHeight(context);
		}
		return DrawUtil.sHeightPixels;
	}

	/**
	 * 获取GOLauncher实际显示高度(px) 跟当前横竖屏状态有关
	 * 
	 * @return
	 */
	public static int getDisplayHeight(Context context) {
		int height = getScreenHeight(context) - getStatusBarHeight(context);
		return height;
	}
	
	/**
	 * 屏幕宽度(px)
	 * 
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		if (Machine.isTablet(context)) {
			return DrawUtil.getTabletScreenWidth(context);
		}
		return DrawUtil.sWidthPixels;
	}
	
	/**
	 * 判断虚拟导航键是否存在
	 * 需要注意存在用户用root的方式隐藏了虚拟导航键，然后用手势返回或呼出home的情况
	 * 这时候sRealHeightPixels = sHeightPixels，sRealWidthPixels = sWidthPixels
	 * @return
	 */
	public static boolean isNavBarAvailable() {
		if (sRealHeightPixels > sHeightPixels || sRealWidthPixels > sWidthPixels) {
			return true;
		}
		return false;
	}
	
	public static boolean isPortrait() {
		return sWidthPixels < sHeightPixels;
	}
	
	/**
	 * 
	 * @param view 重设宽高的view
	 * @param heigh 原来的高
	 * @param width 原来的宽
	 * @param lineNum 每行的个数
	 */
	public static void resetViewSizeFull(View view, int heigh, int width,
			int lineNum) {
		android.view.ViewGroup.LayoutParams para = view.getLayoutParams();
		int[] hw = DrawUtil.getHeightWidth(lineNum, 6, heigh, width, true);
		para.height = hw[0];
		para.width = hw[1];
		view.setLayoutParams(para);
	}
	
	public static void resetViewSize(View view, int heigh, int width,
			int lineNum, int space) {
		android.view.ViewGroup.LayoutParams para = view.getLayoutParams();
		int[] hw = DrawUtil.getHeightWidth(lineNum, space, heigh, width, false);
		para.height = hw[0];
		para.width = hw[1];
		view.setLayoutParams(para);
	}
	
	/**
	 * 
	 * @param lineNum 每行的个数
	 * @param space 元素相隔
	 * @param heigh 原来的高
	 * @param width 原来的宽
	 * @param isFull 是否满屏
	 * @return 高宽
	 */
	public static int[] getHeightWidth(int lineNum, int space, int heigh,
			int width, boolean isFull) {
		int[] hw = new int[2];
		int newWidth = width;
		if (isFull) {
			newWidth = sWidthPixels / lineNum;
		} else {
			newWidth = (sWidthPixels - (lineNum + 1) * space) / lineNum;
		}
		int newHeigh = heigh * newWidth / width;
		hw[0] = newHeigh;
		hw[1] = newWidth;
		return hw;
	}
	
	/**
	 * 裁剪成圆形图片
	 *  @param bitmap 原图
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap moutBitmap = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas mcanvas = new Canvas(moutBitmap);
			final int mcolor = 0xff424242;
			final Paint mpaint = new Paint();
			final Rect mrect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF mrectF = new RectF(mrect);
			final float mroundPX = bitmap.getWidth() / 2;
			mpaint.setAntiAlias(true);
			mcanvas.drawARGB(0, 0, 0, 0);
			mpaint.setColor(mcolor);
			mcanvas.drawRoundRect(mrectF, mroundPX, mroundPX, mpaint);
			mpaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			mcanvas.drawBitmap(bitmap, mrect, mrect, mpaint);
			return moutBitmap;
		} else {
			return null;
		}

	}
	
	/**
	 * <br>功能简述: 通过反射获取状态栏的真正高度，这个函数无视是否隐藏状态栏，直接拉取系统的状态栏的高度
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		if (sStatusBarHeight >= 0) {
			return sStatusBarHeight;
		}
		
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			if (Machine.isMeizu()) {
				try {
					field = c.getField("status_bar_height_large");
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			if (field == null) {
				field = c.getField("status_bar_height");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (field != null && obj != null) {
			try {
				int id = Integer.parseInt(field.get(obj).toString());
				sStatusBarHeight = context.getResources().getDimensionPixelSize(id);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		int normalHeight = dip2px(25);
		if (Machine.isTablet(context) && sStatusBarHeight > normalHeight) {
			sStatusBarHeight = 0;
		} else {
			if (sStatusBarHeight <= 0 || normalHeight > normalHeight * 2) {
				if (sVirtualDensity != -1) {
					sStatusBarHeight = (int) (25 * sVirtualDensity + 0.5f); 
				} else {
					sStatusBarHeight = normalHeight;
				}
			}
		}
		return sStatusBarHeight;
	}
	
	public static String getDisplay(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		return width + "*" + height;
	}
}
