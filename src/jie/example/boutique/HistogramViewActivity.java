package jie.example.boutique;

import java.util.ArrayList;

import jie.example.widget.HistogramView;
import jie.example.widget.HistogramEntity;
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
		super.setActionBarTitle(R.string.statscs_view_aty);
		setContentView(R.layout.statscs_view_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mHistogramViewContainer = (RelativeLayout) findViewById(R.id.statscs_view_container);
	}

	@Override
	public void loadingData() {
		mHistogramViewContainer.removeAllViews();
		ArrayList<HistogramEntity> histogramEntityList = new ArrayList<HistogramEntity>();
		for (int i = 0; i < 28; i++) {
			HistogramEntity histogramEntity = new HistogramEntity();
			histogramEntity.setName("江西" + i);
			histogramEntity.setY(64.28f + i);
			histogramEntity.setColor("#00FF00");
			histogramEntityList.add(histogramEntity);
		}
		mHistogramViewContainer.addView(new HistogramView(this,
				histogramEntityList, 66.3f));
	}

}
