package jie.example.boutique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jie.example.entity.ChineseMapViewEntity;
import jie.example.widget.ChineseMapView;
import jie.example.widget.GaugeView;
import android.os.Bundle;
import android.view.View;

public class ChineseMapViewActivity extends BasicActivity {

	private ChineseMapView mChineseMapView;
	private GaugeView mGaugeView;// 仪表盘
	private List<ChineseMapViewEntity> mProvinceInfoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.menu_chinese_map);
		setContentView(R.layout.chinese_map_view_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mGaugeView = (GaugeView) findViewById(R.id.gauge_view);
		mChineseMapView = (ChineseMapView) findViewById(R.id.chinese_map_view);
		mProvinceInfoList = new ArrayList<ChineseMapViewEntity>();
	}

	@Override
	public void loadingData() {
		mGaugeView.setTargetValue(68.5f);// 设置仪表盘的指针位置，如果不调用这句代码则指针不显示
		loadingChineseMapView();
	}

	public void setOnClick(View view) {

		// 改变集合中的数据以刷新地图
		mProvinceInfoList.clear();
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			int ran = random.nextInt(33);
			mProvinceInfoList.add(new ChineseMapViewEntity(ran));
		}
		mChineseMapView.refreshChineseMapView();

		// 刷新刷新仪表盘指针指向
		int scale = random.nextInt(101);
		mGaugeView.setTargetValue(scale);
	}

	private void loadingChineseMapView() {
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			int ran = random.nextInt(33);
			mProvinceInfoList.add(new ChineseMapViewEntity(ran));
		}

		mChineseMapView.setProvinceInfoList(mProvinceInfoList);
		mChineseMapView.refreshChineseMapView();
	}

}
