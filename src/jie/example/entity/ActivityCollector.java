package jie.example.entity;

import java.util.ArrayList;
import java.util.List;

import jie.example.utils.LogUtil;

import android.app.Activity;

/**
 * 活动管理器
 */
public class ActivityCollector {

	private static List<Activity> mActivityList = new ArrayList<Activity>();

	public static void addActivity(Activity activity) {
		mActivityList.add(activity);
	}

	public static void removeActivity(Activity activity) {
		mActivityList.remove(activity);
	}

	/**
	 * 清除所有未关闭的Activity，退出应用
	 */
	public static void finishAllActivity() {
		for (Activity activity : mActivityList) {
			if (!activity.isFinishing()) {
				LogUtil.globalInfoLog("finish activity-->" + activity);
				activity.finish();
			}
		}
	}

}
