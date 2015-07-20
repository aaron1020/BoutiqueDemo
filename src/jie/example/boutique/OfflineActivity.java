package jie.example.boutique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OfflineActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.force_offline);
		setContentView(R.layout.offline_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
	}

	@Override
	public void loadingData() {
	}

	public void setOnClick(View view) {
		Intent intent = new Intent("ying.jie.action.FORCE_OFFLINE");
		sendBroadcast(intent);
	}

}
