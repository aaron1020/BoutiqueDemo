package jie.example.boutique;

import jie.example.utils.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 创建自定义的ViewGroup
 */
public class CascadeLayoutActivity extends BasicActivity {
	public static final String TAG = CascadeLayoutActivity.class
			.getSimpleName();
	public static final String TEST_RESULT_KEY = "test_result_key";
	private Intent mGetIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.cascade_layout);
		setContentView(R.layout.cascade_layout_aty);
		mGetIntent = getIntent();
		LogUtil.i(TAG, mGetIntent.getStringExtra("testKey") + "");
		mGetIntent.putExtra(TEST_RESULT_KEY, "resutlValue Back");// 按手机上的返回键返回的结果
		setResult(1, mGetIntent);
	}

	@Override
	public void initData() {
	}

	@Override
	public void loadingData() {
	}

	@Override
	public void setLeftBtnClick(View view) {

		// mGetIntent = new Intent();// 也可以直接这样实例化一个Intent
		mGetIntent.putExtra(TEST_RESULT_KEY, "resutlValue Finish");
		setResult(1, mGetIntent);// 按ActionBar的左键返回的结果
		super.setLeftBtnClick(view);// 直接Finish掉该Activity
	}
}
