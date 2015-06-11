package jie.example.boutique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jie.example.entity.HistogramEntity;
import jie.example.utils.ToastUtil;
import jie.example.widget.HistogramView;
import jie.example.widget.HistogramView.HistogramViewClick;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 柱状图表Activity
 */
public class HistogramViewActivity extends BasicActivity {

	private int mTempCount = 0;
	private float mDigitSum = 0;
	private String[] mProvinceArray;
	private RelativeLayout mHistogramViewContainer;
	private HistogramView mHistogramViewChild;
	private ArrayList<HistogramEntity> mHistogramEntityList;

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
		mProvinceArray = getResources().getStringArray(R.array.provinces_list);
		mHistogramEntityList = new ArrayList<HistogramEntity>();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void loadingData() {
		makeData();
		mHistogramViewContainer.removeAllViews();

		mHistogramViewChild = new HistogramView(this,
				getString(R.string.hv_top_main_title), mHistogramEntityList,
				true, true, true, false);
		mHistogramViewChild
				.setTopSubTitleValue(getString(R.string.hv_top_sub_title));
		mHistogramViewChild
				.setLeftTitleValue(getString(R.string.hv_left_title));
		String averageValue = String.format("%.2f", mDigitSum / mTempCount);// 四舍五入，保留两位小数
		mHistogramViewChild.setAverageValue(averageValue);
		mHistogramViewChild.setHistogramViewClick(new HistogramViewClick() {

			@Override
			public void setHistogramViewListener(int histogramId,
					HistogramEntity histogramEntity) {
				ToastUtil.showToast(histogramEntity.getHistogramName());
			}
		});
		mHistogramViewContainer.addView(mHistogramViewChild);

	}

	@SuppressLint("ResourceAsColor")
	public void setOnClick(View view) {
		makeData();
		mHistogramViewChild.setAverageValue(String.format("%.2f", mDigitSum
				/ mTempCount));

		mHistogramViewChild.refreshHistogramView();
	}

	@SuppressLint("ResourceAsColor")
	private void makeData() {

		mDigitSum = 0;
		mTempCount = 0;
		mHistogramEntityList.clear();

		Random ran = new Random();
		while (mTempCount < mProvinceArray.length) {
			float digit = ran.nextFloat() * 101;
			if (digit <= 100 && digit >= 50.0f) {
				HistogramEntity histogramEntity = new HistogramEntity(
						mProvinceArray[mTempCount], digit);
				if (digit <= 100 && digit >= 90) {
					histogramEntity.setHistogramColor(R.color.eagle_one);
				} else if (digit < 90 && digit >= 80) {
					histogramEntity.setHistogramColor(R.color.eagle_two);
				} else if (digit < 80 && digit >= 70) {
					histogramEntity.setHistogramColor(R.color.eagle_three);
				} else if (digit < 70 && digit >= 60) {
					histogramEntity.setHistogramColor(R.color.view_divide_line);
				}
				mHistogramEntityList.add(histogramEntity);
				mDigitSum += digit;
				mTempCount++;
			}
		}
		Collections.sort(mHistogramEntityList);// 对对象进行排序操作
	}

}
