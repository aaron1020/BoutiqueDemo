package jie.example.constant;

import jie.example.boutique.R;
import jie.example.utils.LogUtil;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

public class BoutiqueApp extends Application {

	private static Context mContext;// 全局的上下文对象

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		LogUtil.globalInfoLog("BoutiqueApp onCreate");
		getDevicePerferences();
	}

	private void getDevicePerferences() {

		// 实例化DisplayMetrics方法1；注：getWindowManager()为Activity中的方法
		// DisplayMetrics displayMetrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		// 实例化DisplayMetrics方法2
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		// 获取屏幕的宽和高，其值在横竖屏的情况下可能不一样
		Constant.screenHeight = displayMetrics.heightPixels;
		LogUtil.globalInfoLog("Screen Height-->" + Constant.screenHeight);
		Constant.screenWidth = displayMetrics.widthPixels;
		LogUtil.globalInfoLog("Screen Width-->" + Constant.screenWidth);

		// 获取屏幕Dpi
		int densityDpi = displayMetrics.densityDpi;
		LogUtil.globalInfoLog("Screen Dpi-->" + densityDpi);

		// 获取屏幕密度；注：每160个Dpi等于一个密度
		Constant.screenDensity = displayMetrics.density;
		LogUtil.globalInfoLog("Screen Density-->" + Constant.screenDensity);

		float scaledDensity = displayMetrics.scaledDensity;
		LogUtil.globalInfoLog("Screen ScaledDensity-->" + scaledDensity);

		/*
		 * 1.根据竖屏时的屏幕宽度除以屏幕密度的值建立对应的values-swXXXdp文件夹，则程序就会根据设备的分辨率的不同调用不同资源文件夹里面的资源
		 * ； 2.
		 * 假设某台设备的buildValuesFileRefer值为768，如果项目中没有建立values-sw768dp这个资源文件夹而有values
		 * -sw600dp这个资源文件夹，则程序会优先选择values-sw600dp资源文件夹；如果建立有values-sw768dp资源文件夹，
		 * 则程序会优先选择values
		 * -sw768dp资源文件夹；也就是说程序会根据buildValuesFileRefer选择比它小且和它相差最小的资源文件夹；
		 * 3.values -swXXXdp文件夹默认对应的是竖屏时应用的资源，而横屏时对应的资源文件夹为values-swXXXdp-land；
		 * 4.屏幕横屏时遵循的规则同上。
		 */
		int buildValuesFileRefer = (int) (Constant.screenWidth / Constant.screenDensity);
		LogUtil.globalInfoLog("buildValuesFileRefer-->" + buildValuesFileRefer);
		LogUtil.globalInfoLog("Should Preference Values File-->"
				+ getString(R.string.should_build_values_file,
						buildValuesFileRefer));

		LogUtil.globalInfoLog("Actually Preference Values File");
		LogUtil.globalInfoLog("Values-->" + getString(R.string.default_));
		LogUtil.globalInfoLog("Values-->" + getString(R.string.default_sw360));
		LogUtil.globalInfoLog("Values-->"
				+ getString(R.string.default_sw360_sw600));
		LogUtil.globalInfoLog("Values-->"
				+ getString(R.string.default_sw360_sw600_sw720));
		LogUtil.globalInfoLog("Values-->"
				+ getString(R.string.default_sw360_sw600_sw720_sw768));
		LogUtil.globalInfoLog("Values-->"
				+ getString(R.string.default_sw360_sw600_sw720_sw768_sw1024));

		try {
			LogUtil.globalInfoLog("Values Land-->"
					+ getString(R.string.values_land));
		} catch (Exception e) {
			// 如果竖屏时去引用横屏里竖屏并没有定义的资源，则程序会报错；同理，当横屏时......
			LogUtil.globalErrorLog("Wrongly preference values file belong to device's screen orientation is landscape when it's portrait");
		}

		// 把dp转化成具体的数值；转化规则：具体的数值= dp大小*屏幕密度
		float preferenceDimen = getResources().getDimension(
				R.dimen.preference_dimen);
		LogUtil.globalInfoLog("preferenceDimen-->" + preferenceDimen);
	}

	public static Context getContext() {
		return mContext;
	}

}
