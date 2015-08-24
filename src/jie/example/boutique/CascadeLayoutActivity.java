package jie.example.boutique;

import jie.example.utils.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

/**
 * 创建自定义的ViewGroup
 */
public class CascadeLayoutActivity extends BasicActivity {
	public static final String TAG = CascadeLayoutActivity.class
			.getSimpleName();
	public static final String TEST_RESULT_KEY = "test_result_key";
	private Intent mGetIntent;
	private Button mBtnMove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.cascade_layout);
		setContentView(R.layout.cascade_aty);
		mGetIntent = getIntent();
		LogUtil.i(TAG, mGetIntent.getStringExtra("testKey") + "");
		mGetIntent.putExtra(TEST_RESULT_KEY, "resutlValue Back");// 按手机上的返回键返回的结果
		setResult(1, mGetIntent);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mBtnMove = (Button) findViewById(R.id.btn_move);
	}

	@Override
	public void loadingData() {
		mBtnMove.setX(200);
		mBtnMove.setY(200);
		mBtnMove.setOnTouchListener(mTouchListener);
	}

	@Override
	public void setLeftBtnClick(View view) {
		// mGetIntent = new Intent();// 也可以直接这样实例化一个Intent
		mGetIntent.putExtra(TEST_RESULT_KEY, "resutlValue Finish");
		setResult(1, mGetIntent);// 按ActionBar的左键返回的结果
		super.setLeftBtnClick(view);// 直接Finish掉该Activity
	}

	float downX, downY;
	float moveX, moveY;

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getRawX();
				downY = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				moveX = event.getRawX();
				moveY = event.getRawY();
				mBtnMove.setX(mBtnMove.getX() + (moveX - downX));
				mBtnMove.setY(mBtnMove.getY() + (moveY - downY));
				downX = moveX;
				downY = moveY;
				break;
			default:
				break;
			}
			return true;
		}
	};

}
