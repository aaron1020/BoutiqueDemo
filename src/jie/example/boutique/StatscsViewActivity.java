package jie.example.boutique;

import java.util.ArrayList;

import jie.example.widget.Chartview;
import jie.example.widget.ImageData;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 柱状图表Activity
 */
public class StatscsViewActivity extends BasicActivity {

	private LinearLayout mStatscsViewContainer;

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
		mStatscsViewContainer = (LinearLayout) findViewById(R.id.statscs_view_container);
	}

	@Override
	public void loadingData() {
		mStatscsViewContainer.removeAllViews();
		Chartview childView = new Chartview(this);
		ArrayList<ImageData> list = new ArrayList<ImageData>();
		for (int i = 0; i < 20; i++) {
			ImageData data = new ImageData();

			// private String t1;
			// private String t2;
			// private String t3;
			// private String name;
			// private String province;
			// private float y;
			// private float business_volum;
			// private float consumption;
			// private float process;
			// private float load_pressure;
			// private String server_style;
			// private String time;
			// private long endTime;
			// private long startTime;
			// private String color;
			data.setT1("BOSS系统建设质量");
			data.setT2("NGBOSS1.0*2.0规范符合度测试");
			data.setT3("省公司维度");
			data.setName("江西" + i);
			data.setProvince("");
			data.setY(84.28f);
			data.setColor("#FF0000");
		}
		childView.setList(list);
		mStatscsViewContainer.addView(childView);
	}

	public void setOnClick(View view) {
	}

}
