package jie.example.boutique;

import jie.example.constant.Constant;
import jie.example.utils.LogUtil;
import jie.example.widget.WaterWaveViewLayout;
import jie.example.widget.WaterWaveViewLayout.OnPunshCardListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * 精品首页
 */
public class BoutiqueActivity extends BasicActivity {

	private static final String TAG = "BoutiqueActivity";
	private BoutiqueActivity mActivity;
	private TextSwitcher mTextSwitcherOval;
	private TextSwitcher mTextSwitcherCircle;
	private TextSwitcher mTextSwitcherRectangle;
	private WaterWaveViewLayout punchView;
	private String[] mTextLong = { "111111", "222222", "333333", "444444" };
	private String[] mTextShort = { "1", "2", "3", "4" };
	private int mTextOvalId = 0;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.aty_boutique);
		super.setLeftBtnImage(R.drawable.actionbar_left_ibtn_menu);
		setContentView(R.layout.boutique_aty);
		LogUtil.globalInfoLog("BoutiqueActivity onCreate");

		initData();
		loadingData();

		if (savedInstanceState != null) {
			LogUtil.globalInfoLog(savedInstanceState
					.getString(Constant.SAVE_INSTANCE_STATE));
		} else {
			LogUtil.globalInfoLog("(BoutiqueActivity) savedInstanceState is null");
		}

	}

	@Override
	public void initData() {
		mActivity = BoutiqueActivity.this;
		mTextSwitcherOval = (TextSwitcher) findViewById(R.id.text_switcher_oval);
		mTextSwitcherCircle = (TextSwitcher) findViewById(R.id.text_switcher_circle);
		mTextSwitcherRectangle = (TextSwitcher) findViewById(R.id.text_switcher_rectangle);
		punchView = (WaterWaveViewLayout) findViewById(R.id.punchCardView);
		punchView.setPunshCardListener(punshListener);
		punchView.onCreate();
	}

	/**
	 * 打卡控件监听
	 */
	private OnPunshCardListener punshListener = new OnPunshCardListener() {

		@Override
		public void onPunshSuccess() {

		}

		@Override
		public void onPunshFail() {

		}

		@Override
		public void onPunshException() {

		}

		@Override
		public void onPunshCardWait() {
		}

		@Override
		public void onPunshCardIng() {
			// 初始化定位数据

			// 点击打卡首先去定位，定位成功后再去调用打开接口
			// infos.setText(getResources().getString(R.string.location_get_ing));
			// 显示定位转圈图片
			// infos.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
			// null);
			// pbLocationGeting.setVisibility(View.VISIBLE);
			// InitLocation();
		}

		@Override
		public boolean canPunsh() {
			// 如果是免打卡员工
			// if (attendMainActivity.isFreeCard()) {
			// openDialogBox(3);
			// return false;
			// }
			// root禁止打卡
			// if(PublicUtil.isRooted()){
			// openDialogBox(1);
			// return false;
			// }
			// 模拟器禁止打卡
			// if(PublicUtil.isEmulator()){
			// openDialogBox(2);
			// return false;
			// }
			return true;
		}

		@Override
		public void resumeTips() {
			// 还原打卡提示信息，成功状态下不还原
		}

	};

	@Override
	public void loadingData() {
		mTextSwitcherOval.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				TextView textView = new TextView(mActivity);
				textView.setLayoutParams(params);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18.0f);
				textView.setText(mTextLong[mTextOvalId]);
				LogUtil.i(TAG, "mTextSwitcherOval-->makeView");
				return textView;
			}
		});
		mTextSwitcherOval.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.textswitcher_text_in));
		mTextSwitcherOval.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.textswitcher_text_out));

		mTextSwitcherCircle.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				TextView textView = new TextView(mActivity);
				textView.setLayoutParams(params);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18.0f);
				textView.setText(mTextShort[mTextOvalId]);
				return textView;
			}
		});
		mTextSwitcherCircle.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.textswitcher_text_in));
		mTextSwitcherCircle.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.textswitcher_text_out));

		mTextSwitcherRectangle.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				TextView textView = new TextView(mActivity);
				textView.setLayoutParams(params);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(18.0f);
				textView.setText(mTextLong[mTextOvalId]);
				return textView;
			}
		});
		mTextSwitcherRectangle.setInAnimation(AnimationUtils.loadAnimation(
				this, R.anim.textswitcher_text_in));
		mTextSwitcherRectangle.setOutAnimation(AnimationUtils.loadAnimation(
				this, R.anim.textswitcher_text_out));
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.globalInfoLog("BoutiqueActivity onStart");
	}

	@Override
	public void setLeftBtnClick(View view) {
		startActivity(new Intent(this, TreeListViewActivity.class));
	}

	@SuppressLint("NewApi")
	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.cascade_layout:
			startActivity(new Intent(this, CascadeLayoutActivity.class));
			break;

		case R.id.transparent_menu:
			startActivity(new Intent(this, TransparentMenuActivity.class));
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;

		case R.id.textswitcher_text_pervious:
			mTextOvalId--;
			if (mTextOvalId < 0) {
				mTextOvalId = mTextLong.length - 1;
			}
			mTextSwitcherOval.setText(mTextLong[mTextOvalId]);
			mTextSwitcherCircle.setText(mTextShort[mTextOvalId]);
			mTextSwitcherRectangle.setText(mTextLong[mTextOvalId]);
			break;

		case R.id.textswitcher_text_next:
			mTextOvalId++;
			if (mTextOvalId == mTextLong.length) {
				mTextOvalId = 0;
			}
			mTextSwitcherOval.setText(mTextLong[mTextOvalId]);
			mTextSwitcherCircle.setText(mTextShort[mTextOvalId]);
			mTextSwitcherRectangle.setText(mTextLong[mTextOvalId]);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		LogUtil.globalInfoLog("(BoutiqueActivity) onSaveInstanceState(Bundle)");
		outState.putString(Constant.SAVE_INSTANCE_STATE,
				"(BoutiqueActivity) restore values that saved in onSaveInstanceState(Bundle)");
	}

	@SuppressLint("NewApi")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String outStateValue = savedInstanceState.getString(
				Constant.SAVE_INSTANCE_STATE, "defaultValue");
		LogUtil.globalInfoLog("(BoutiqueActivity) onRestoreInstanceState(Bundle)-->"
				+ outStateValue);
	}

}
