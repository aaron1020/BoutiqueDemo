package jie.example.utils;

import jie.example.manager.BoutiqueApp;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtil {
	private static final String TAG = AppUtil.class.getSimpleName();

	/**
	 * 获取应用的名称
	 */
	public static String getAppName() {
		String appName = null;
		try {
			final Context context = BoutiqueApp.getContext();
			PackageManager pManager = context.getPackageManager();
			ApplicationInfo aInfo = pManager.getApplicationInfo(
					context.getPackageName(), 0);
			appName = (String) pManager.getApplicationLabel(aInfo);

			PackageInfo packageInfo = pManager.getPackageInfo(
					context.getPackageName(), 0);
			String versionName = packageInfo.versionName;
			LogUtil.i(TAG, TAG + "::versionName::" + versionName);
		} catch (Exception e) {
			LogUtil.e(TAG, TAG + "::getAppName()::" + e.toString());
			appName = TAG;
		}
		return appName;
	}

}
