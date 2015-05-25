package jie.example.boutique;

import android.os.Bundle;

/**
 * 创建自定义的ViewGroup
 */
public class CascadeLayoutActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.cascade_layout);
		setContentView(R.layout.cascade_layout_aty);
	}

	@Override
	public void initData() {
	}

	@Override
	public void loadingData() {
	}

}
