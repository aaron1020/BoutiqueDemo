package jie.example.boutique;

import jie.example.manager.ActivityCollector;
import jie.example.utils.LogUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * BasicActivity用于置Activity共有的属性和功能，为了编码的规范，继承BasicActivity时必须覆写initData()
 * 和loadingData()方法
 */
@SuppressLint("NewApi")
public abstract class BasicActivity extends Activity {

	private ActionBar mActionBar;
	private View mActionBarView;
	private TextView mActionBarTitle;
	private ImageButton mLeftIBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.globalInfoLog("BasicActivity onCreate");
		LogUtil.globalInfoLog("onCreate Activity-->" + this);
		ActivityCollector.addActivity(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 全局竖屏显示
		initActionBar();
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.globalInfoLog("BasicActivity onStart");
	}

	/**
	 * 设置全局ActionBar
	 */
	private void initActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);// 若不加此处代码，则自定义布局显示
		mActionBarView = getLayoutInflater().inflate(R.layout.actionbar_view,
				null);// 用inflate的方式加载ActionBar要显示的布局
		mActionBar.setCustomView(mActionBarView);// 为ActionBar设置自定义的布局

		// 找到ActionBar包含的控件
		mActionBarTitle = (TextView) mActionBarView
				.findViewById(R.id.actionbar_title);
		mLeftIBtn = (ImageButton) mActionBarView
				.findViewById(R.id.actionbar_back);
	}

	public void setLeftBtnClick(View view) {
		finish();
	}

	protected void setLeftBtnImage(int resId) {
		mLeftIBtn.setImageDrawable(getResources().getDrawable(resId));
	}

	/**
	 * 隐藏ActionBar
	 */
	protected void hideActionBar() {
		mActionBar.hide();
	}

	/**
	 * 设置ActionBar标题
	 */
	protected void setActionBarTitle(String title) {
		if (mActionBarTitle != null) {
			mActionBarTitle.setText(title);
		}
	}

	/**
	 * 设置ActionBar标题
	 */
	protected void setActionBarTitle(int resId) {
		setActionBarTitle(getString(resId));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.globalInfoLog("onDestroy Activity-->" + this);
		ActivityCollector.removeActivity(this);
	}

	/**
	 * initData()方法用于初始化变量
	 */
	public abstract void initData();

	/**
	 * loadingData()方法用于加载数据
	 */
	public abstract void loadingData();

}
