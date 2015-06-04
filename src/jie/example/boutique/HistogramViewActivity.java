package jie.example.boutique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jie.example.widget.HistogramView;
import jie.example.widget.HistogramEntity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RelativeLayout;

/**
 * 柱状图表Activity
 */
public class HistogramViewActivity extends BasicActivity {

	private RelativeLayout mHistogramViewContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.histogram_view_aty);
		setContentView(R.layout.histogram_view_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mHistogramViewContainer = (RelativeLayout) findViewById(R.id.histogram_view_container);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void loadingData() {
		mHistogramViewContainer.removeAllViews();

		String[] provinceArray = getResources().getStringArray(
				R.array.provinces_list);
		ArrayList<HistogramEntity> histogramEntityList = new ArrayList<HistogramEntity>();

		int tempCount = 0;
		float digitSum = 0;
		Random ran = new Random();
		while (tempCount < provinceArray.length) {
			float digit = ran.nextFloat() * 101;
			if (digit <= 100 && digit >= 50.0f) {
				HistogramEntity histogramEntity = new HistogramEntity(
						provinceArray[tempCount], digit);
				if (digit <= 100 && digit >= 90) {
					histogramEntity.setHistogramColor(R.color.eagle_one);
				} else if (digit < 90 && digit >= 80) {
					histogramEntity.setHistogramColor(R.color.eagle_two);
				} else if (digit < 80 && digit >= 70) {
					histogramEntity.setHistogramColor(R.color.eagle_three);
				} else if (digit < 70 && digit >= 60) {
					histogramEntity.setHistogramColor(R.color.view_divide_line);
				}
				histogramEntityList.add(histogramEntity);
				digitSum += digit;
				tempCount++;
			}
		}

		Collections.sort(histogramEntityList);// 对对象进行排序操作

		HistogramView histogramView = new HistogramView(this,
				getString(R.string.hv_top_main_title), histogramEntityList,
				true, true, true, false);
		histogramView.setTopSubTitleValue(getString(R.string.hv_top_sub_title));
		histogramView.setLeftTitleValue(getString(R.string.hv_left_title));
		String averageValue = String.format("%.2f", digitSum / tempCount);// 四舍五入，保留两位小数
		histogramView.setAverageValue(averageValue);
		mHistogramViewContainer.addView(histogramView);
	}

}
