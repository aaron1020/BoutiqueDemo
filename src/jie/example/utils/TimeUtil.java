package jie.example.utils;

import android.content.Context;
import jie.example.boutique.R;
import jie.example.entity.TimeEntity;
import jie.example.manager.BoutiqueApp;

public class TimeUtil {

	/**
	 * 根据传入的秒数计算时间差
	 */
	public static TimeEntity getTimeEntity(long seconds) {
		long day = seconds / (24 * 3600);
		long hour = seconds % (24 * 3600) / 3600;
		long minute = seconds % 3600 / 60;
		long second = seconds % 60;
		return new TimeEntity(seconds, day, hour, minute, second);
	}

	public static String calculateTemainTime(int remainTime) {
		Context context = BoutiqueApp.getContext();
		StringBuilder sb = new StringBuilder();
		TimeEntity timeEntity = getTimeEntity(remainTime);
		if (timeEntity.getDay() > 0) {
			sb.append(timeEntity.getDay()).append(
					context.getString(R.string.day));
			sb.append(timeEntity.getHour()).append(
					context.getString(R.string.hour));
			sb.append(timeEntity.getMinute()).append(
					context.getString(R.string.minute));
			sb.append(timeEntity.getSecond()).append(
					context.getString(R.string.second));
		} else if (timeEntity.getDay() <= 0 && timeEntity.getHour() > 0) {
			sb.append(timeEntity.getHour()).append(
					context.getString(R.string.hour));
			sb.append(timeEntity.getMinute()).append(
					context.getString(R.string.minute));
			sb.append(timeEntity.getSecond()).append(
					context.getString(R.string.second));
		} else if (timeEntity.getDay() <= 0 && timeEntity.getHour() <= 0
				&& timeEntity.getMinute() > 0) {
			sb.append(timeEntity.getMinute()).append(
					context.getString(R.string.minute));
			sb.append(timeEntity.getSecond()).append(
					context.getString(R.string.second));
		} else {
			sb.append(timeEntity.getSecond()).append(
					context.getString(R.string.second));
		}
		return sb.toString();
	}

}
