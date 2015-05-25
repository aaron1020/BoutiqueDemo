package jie.example.boutique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jie.example.constant.Constant;
import jie.example.entity.ChineseMapViewEntity;
import jie.example.widget.ChineseMapView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

public class ChineseMapViewActivity extends BasicActivity {

	private RelativeLayout mRelativeLayout;
	private ChineseMapView mChineseMapView;
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
		mProvinceInfoList = new ArrayList<ChineseMapViewEntity>();
		mRelativeLayout = (RelativeLayout) findViewById(R.id.chinese_map_view_container);
	}

	@Override
	public void loadingData() {
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
