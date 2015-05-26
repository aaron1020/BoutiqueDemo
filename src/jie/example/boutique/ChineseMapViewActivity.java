package jie.example.boutique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jie.example.constant.Constant;
import jie.example.entity.ChineseMapViewEntity;
import jie.example.widget.ChineseMapView;
import jie.example.widget.GaugeView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

public class ChineseMapViewActivity extends BasicActivity {

	private RelativeLayout mRelativeLayout;
	private ChineseMapView mChineseMapView;
	private GaugeView mGaugeView;// 仪表盘
	private List<ChineseMapViewEntity> mProvinceInfoList;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.l_menu_buslate);
		setContentView(R.layout.chinese_map_view_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mGaugeView = (GaugeView) findViewById(R.id.gauge_view);
		mProvinceInfoList = new ArrayList<ChineseMapViewEntity>();
		mRelativeLayout = (RelativeLayout) findViewById(R.id.chinese_map_view_container);
	}

	@Override
	public void loadingData() {
		mGaugeView.setTargetValue(68.5f);// 设置仪表盘的指针位置，如果不调用这句代码则指针不显示
		loadingChineseMapView();
	}

	public void setOnClick(View view) {
		mProvinceInfoList.clear();
		// 改变集合中的数据以刷新地图
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			int ran = random.nextInt(33);
			mProvinceInfoList.add(new ChineseMapViewEntity(ran));
		}
		mChineseMapView.refreshChineseMapView();

		int scale = random.nextInt(101);
		mGaugeView.setTargetValue(scale);
	}

	private void loadingChineseMapView() {
		mProvinceInfoList.clear();
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			int ran = random.nextInt(33);
			mProvinceInfoList.add(new ChineseMapViewEntity(ran));
		}
		mChineseMapView = new ChineseMapView(this, mHandler, mRelativeLayout,
				mProvinceInfoList, Constant.screenWidth);
	}

}
