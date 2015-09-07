package jie.example.utils;

import jie.example.constant.Constant;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

/**
 * 获取屏幕信息工具类
 */
public class ScreenUtil {
	/**
	 * 获取当前屏幕截图(包含状态栏)
	 */
	public static Bitmap getScreenShot(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap source = view.getDrawingCache();
		int width = Constant.screenWidth;
		int height = Constant.screenHeight;
		// 根据屏幕的宽和高截取屏幕图像
		Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, width, height);
		view.destroyDrawingCache();
		return bitmap;
	}

}
