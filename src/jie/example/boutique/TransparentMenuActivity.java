package jie.example.boutique;

import jie.example.constant.Constant;
import jie.example.entity.PersonParcel;
import jie.example.entity.PersonSerialize;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 菜单Activity：具有一定透明度且直接呈现在某个Activity上方。
 */
public class TransparentMenuActivity extends BasicActivity {

	private static final int MENU_ITEN_NUM = 5;
	private static final int MENU_ANIM_DURATION = 150;
	private static final int MENU_ANIM_START_OFFSET = 100;// 显示时间 默认100毫秒
	private TransparentMenuActivity mActivity;
	private TextView mMenuItem[] = new TextView[MENU_ITEN_NUM];
	private ImageView mBtnMenuBottom;
	private boolean mBtnMenuBottomCanClick;
	private int mMenuItemIndex = 0;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.HANDLER_MENU_OPEN:
				mMenuItem[mMenuItemIndex].setVisibility(View.VISIBLE);
				mMenuItem[mMenuItemIndex].setAnimation(menuOpenAnimation());
				mMenuItemIndex++;
				if (mMenuItemIndex == MENU_ITEN_NUM) {
					mBtnMenuBottomCanClick = true;
					mBtnMenuBottom.setClickable(mBtnMenuBottomCanClick);
					return;
				}
				if (mHandler != null) {
					mHandler.sendEmptyMessageDelayed(
							Constant.HANDLER_MENU_OPEN, MENU_ANIM_DURATION);
				}
				break;
			case Constant.HANDLER_MENU_CLOSE:
				mMenuItemIndex--;
				mMenuItem[mMenuItemIndex].setAnimation(menuCloseAnimation());
				mMenuItem[mMenuItemIndex].setVisibility(View.GONE);
				if (mMenuItemIndex == 0) {
					mBtnMenuBottom.setVisibility(View.GONE);
					mHandler.sendEmptyMessageDelayed(
							Constant.HANDLER_MENU_CLOSE_ACTIVITY,
							MENU_ANIM_DURATION + 200);
					return;
				}
				if (mHandler != null) {
					mHandler.sendEmptyMessageDelayed(
							Constant.HANDLER_MENU_CLOSE, MENU_ANIM_DURATION);
				}
				break;
			case Constant.HANDLER_MENU_CLOSE_ACTIVITY:
				closeMenuActivity();
				break;

			case Constant.HANDLER_MENU_START_ACTIVITY:
				if (msg.arg1 == R.id.transparent_menu_item5) {
					startActivity(new Intent(mActivity,
							ChineseMapViewActivity.class));
				} else if (msg.arg1 == R.id.transparent_menu_item4) {
					startActivity(new Intent(mActivity,
							HistogramViewActivity.class));
				} else if (msg.arg1 == R.id.transparent_menu_item3) {
					startActivity(new Intent(mActivity,
							PaintDemoActivity.class));
				} else if (msg.arg1 == R.id.transparent_menu_item2) {
					startActivity(new Intent(mActivity, CaptureActivity.class));
				} else {
					Intent intent = new Intent(mActivity,
							DynamicAddViewActivity.class);

					// 两种传输对象的方式
					intent.putExtra("PersonParcel", new PersonParcel("wbj", 26,
							"Man"));
					intent.putExtra("PersonSerialize", new PersonSerialize(
							"wbj", 26, "Man"));

					startActivity(intent);
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.hideActionBar();
		setContentView(R.layout.transparent_menu_aty);
		initData();
	}

	@Override
	public void initData() {
		mActivity = TransparentMenuActivity.this;
		mBtnMenuBottom = (ImageView) findViewById(R.id.transparent_menu_bottom);
		for (int index = 0; index < MENU_ITEN_NUM; index++) {
			mMenuItem[index] = (TextView) findViewById(R.id.transparent_menu_item1
					- index);
		}
		mBtnMenuBottom.setClickable(mBtnMenuBottomCanClick);
		mHandler.sendEmptyMessage(Constant.HANDLER_MENU_OPEN);
	}

	private Animation menuCloseAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation animAlpha = new AlphaAnimation(1, 0);
		ScaleAnimation animScale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animationSet.addAnimation(animScale);
		animationSet.addAnimation(animAlpha);
		animationSet.setDuration(MENU_ANIM_DURATION);
		animationSet.setStartOffset(MENU_ANIM_START_OFFSET);
		animationSet.setFillAfter(true);
		return animationSet;
	}

	private Animation menuOpenAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
		ScaleAnimation animScale = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animationSet.addAnimation(animScale);
		animationSet.addAnimation(animAlpha);
		animationSet.setDuration(MENU_ANIM_DURATION);
		animationSet.setStartOffset(MENU_ANIM_START_OFFSET);
		animationSet.setFillAfter(true);
		return animationSet;
	}

	@Override
	public void loadingData() {
	}

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.transparent_menu_bottom:
			mBtnMenuBottomCanClick = false;
			mBtnMenuBottom.setClickable(mBtnMenuBottomCanClick);
			mHandler.sendEmptyMessage(Constant.HANDLER_MENU_CLOSE);
			break;

		default:
			closeMenuActivity();
			Message msg = mHandler.obtainMessage();
			msg.what = Constant.HANDLER_MENU_START_ACTIVITY;
			msg.arg1 = view.getId();
			mHandler.sendMessageDelayed(msg, MENU_ANIM_DURATION);
			break;
		}
	}

	private void closeMenuActivity() {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		if (mBtnMenuBottomCanClick) {
			mHandler.sendEmptyMessage(Constant.HANDLER_MENU_CLOSE);
			mBtnMenuBottomCanClick = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler = null;
	}

}
