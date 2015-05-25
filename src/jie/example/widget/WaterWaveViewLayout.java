package jie.example.widget;

import jie.example.boutique.R;
import jie.example.utils.ToastUtil;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 一定要在生命周期中调用,
 * 
 * <pre>
 * 例如：lifeMethod 是 PunshCardBg 对象。
 *  @Override
 * 	protected void onCreate() {
 * 		super.onCreate();
 * 		if (null != lifeMethod) {
 * 			lifeMethod.onCreate();
 * 		}
 * 	}
 *    @Override
 * 	protected void onResume() {
 * 		super.onResume();
 * 		if (null != lifeMethod) {
 * 			lifeMethod.onResume();
 * 		}
 * 	}
 * 
 * 	@Override
 * 	protected void onPause() {
 * 		super.onPause();
 * 		if (null != lifeMethod) {
 * 			lifeMethod.onPuase();
 * 		}
 * 	}
 * 
 * 	@Override
 * 	protected void onDestroy() {
 * 		super.onDestroy();
 * 		if (null != lifeMethod) {
 * 			lifeMethod.onDestory();
 * 		}
 * 	}
 * </pre>
 * 
 * 如果需要自定义视图的状态回调，实现接口 OnPunshCardListener
 * 
 * @author pWX184170
 * 
 */
public class WaterWaveViewLayout extends FrameLayout {

	private static final String TAG = "PunshCardView";
	private static final int ID_CIRCLE_NORMAL = 1002;
	private static final int ID_CIRCLE_TRANSLATE = 1003;
	public static int marginTop = 90;// 距离Top大小，需要传递给 punchBgView 的centerY Add

	// status
	public enum Status {
		WAIT(), ING(), SUCCESS(), FAIL(), EXCEPTION();
		Status() {
		}
	}

	private Status mStatus = Status.WAIT;

	private Context context;

	private LayoutInflater layoutInflater;
	private int circleHeight;
	private int circleWidth;

	// View
	private WaterWaveView punchBgView;
	private ImageView circleNormal;
	private ProgressBar waitProgressBar;
	private LinearLayout centerView;

	// Listener
	private OnPunshCardListener punshCardListener;

	// private Typeface numberface;

	// 摇一摇动画
	private RotateAnimation sharkTranslate;

	public WaterWaveViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WaterWaveViewLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaterWaveViewLayout(Context context) {
		this(context, null, 0);
	}

	/**
	 * @param context
	 */
	private void init(Context context) {
		this.context = context;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// numberface = Typeface.createFromAsset(context.getAssets(),
		// Constants.NUMBER_FONT_PATH);
		// get Width and height.
		circleHeight = getResources().getDisplayMetrics().heightPixels * 25 / 100;
		circleWidth = circleHeight;// (int) (options.outWidth * scaleDensity);

		// 添加背景
		marginTop = (int) (getResources().getDisplayMetrics().heightPixels * 7.5 / 100);
		punchBgView = new WaterWaveView(context, marginTop);
		addView(punchBgView, gerenCenterParamsDefault());

		// 添加圆圈
		circleNormal = buildImageView(ID_CIRCLE_NORMAL,
				R.drawable.circle_normal);
		circleNormal.setOnClickListener(onPunshCardClick);

		waitProgressBar = buildProgressBar(ID_CIRCLE_TRANSLATE);
		waitProgressBar.setVisibility(INVISIBLE);

		centerView = new LinearLayout(context);
		centerView.setLayoutParams(gerenCenterParams());
		loadInitView(centerView);
		addView(centerView, gerenCenterParams());

		// 初始化摇一摇动画。
		sharkTranslate = new RotateAnimation(0, 15, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sharkTranslate.setDuration(500L);
		sharkTranslate.setRepeatCount(2);
		sharkTranslate.setRepeatMode(Animation.REVERSE);
		sharkTranslate.setAnimationListener(sharkListener);

	}

	private LayoutParams gerenCenterParams() {
		LayoutParams params = new LayoutParams(circleWidth, circleHeight);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = marginTop;
		return params;
	}

	private LayoutParams gerenCenterParamsDefault() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		return params;
	}

	private ImageView buildImageView(int id, int resourceId) {
		ImageView imageview = new ImageView(context);
		imageview.setId(id);
		imageview.setImageResource(resourceId);
		addView(imageview, gerenCenterParams());
		return imageview;
	}

	private ProgressBar buildProgressBar(int id) {
		ProgressBar progressBar = (ProgressBar) layoutInflater.inflate(
				R.layout.hrattend_home_i_punchpb, null).findViewById(
				R.id.waitProgressbar);
		progressBar.setId(id);
		addView(progressBar, gerenCenterParams());
		return progressBar;
	}

	/**
	 * 加载不需要打卡初始化界面的方法
	 * 
	 * @param rootView
	 */
	private void loadInitView(ViewGroup rootView) {
		centerView.removeAllViews();
		layoutInflater.inflate(R.layout.hrattend_home_i_punchwait, rootView);
		// ((TextView) rootView.findViewById(R.id.digitalTime))
		// .setTypeface(numberface);
	}

	/**
	 * 打卡初始化界面
	 * 
	 * @param rootView
	 */
	private void loadLoadingView(ViewGroup rootView) {
		centerView.removeAllViews();
		layoutInflater.inflate(R.layout.hrattend_home_i_punching, rootView);
	}

	/**
	 * 打卡成功界面
	 * 
	 * @param rootView
	 */
	private void loadSuccessView(ViewGroup rootView) {
		centerView.removeAllViews();
		layoutInflater.inflate(R.layout.hrattend_home_i_punchsuccess, rootView);
	}

	/**
	 * 打卡失败界面
	 * 
	 * @param rootView
	 */
	private void loadFailView(ViewGroup rootView) {
		centerView.removeAllViews();
		layoutInflater.inflate(R.layout.hrattend_home_i_punchfail, rootView);
	}

	/**
	 * 打卡摇一摇界面
	 * 
	 * @param rootView
	 */
	private void loadSharkView(ViewGroup rootView) {
		centerView.removeAllViews();
		layoutInflater.inflate(R.layout.hrattend_home_i_punchshark, rootView);
		ImageView sharkView = (ImageView) rootView
				.findViewById(R.id.imgSharkIcon);
		sharkView.setAnimation(sharkTranslate);
		sharkTranslate.start();
	}

	private AnimationListener sharkListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 动画结束后开始打卡
			punshCardIng();
		}
	};

	/**
	 * 开始打卡
	 */
	public void startShark() {
		// 如果已经开始打卡模式，不能进入到打卡默认了提示即可
		if (isShouldPunch()) {
			loadSharkView(centerView);
		} else {
			ToastUtil.showToast("正在打卡，请稍后再摇一摇");
		}
	}

	public boolean isShouldPunch() {
		return mStatus == Status.WAIT;
	}

	// Activity life start
	public void onCreate() {
		if (null != punchBgView) {
			punchBgView.startDrawThread();
		}
		if (null != punshCardListener) {
			// 开始等待动画
			punshCardListener.onPunshCardWait();
			mStatus = Status.WAIT;
		}
	}

	public void onResume() {
		resumeBgAnimation();
	}

	public void onPuase() {
		puaseBgAnimations();
	}

	public void onDestory() {
		if (null != punchBgView) {
			punchBgView.stopDrawThread();
		}
	}

	// Activity life end
	// need handler punsh card.stop the punchBgView .
	private void puaseBgAnimations() {
		if (null != punchBgView && mStatus != Status.ING) {
			punchBgView.pauseDrawThread();
		}
	}

	/**
	 * 恢复动画的时候需要判断专题，只有在wait状态才会去显示水波背景，打卡失败后，恢复状态到wait
	 */
	private void resumeBgAnimation() {
		if (null != punchBgView && mStatus.ordinal() < Status.ING.ordinal()) {
			punchBgView.resumeDrawThread();
		}
	}

	/**
	 * 点击打卡的时候首先需要停止背景动画，然后启动转圈动画，转圈动画需要隐蔽背景
	 */
	private View.OnClickListener onPunshCardClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!punshCardListener.canPunsh()) {
				return;
			}
			if (mStatus == Status.WAIT) {
				punshCardIng();
			}
		}
	};

	public void onClick() {
		if (!punshCardListener.canPunsh()) {
			return;
		}
		if (mStatus == Status.WAIT) {
			punshCardIng();
		}
	}

	/**
	 * 点击打卡后处理。回调到页面
	 */
	private void punshCardIng() {
		puaseBgAnimations();
		toggleWaitProgress(true);
		// onPunshCardIng 是一个耗时操作。需要在子线程,然后通过handler转到主线程
		mStatus = Status.ING;
		punshCardListener.onPunshCardIng();
	}

	/**
	 * 将打卡结果通知回来
	 * 
	 * @param resultCode
	 */
	public void notifyPunshCardResult(int resultCode) {
		proccessPunshCardResult(resultCode);
	}

	private void proccessPunshCardResult(int resultCode) {
		if (null != punshCardListener) {
			Log.i(TAG, "punshCardIng end resultCode:" + resultCode);
			toggleWaitProgress(false);
			if (resultCode == OnPunshCardListener.STATUS_SUCCESS) {
				punshCardSuccess();
			} else if (resultCode == OnPunshCardListener.STATUS_FAIL) {
				punshCardFail();

			} else if (resultCode == OnPunshCardListener.STATUS_EXCEPTION) {
				punshCardException();
			}
			punshCardRefresh();
		}
	}

	/**
	 * 打卡后 页面自动重置到打卡状态
	 */
	private void punshCardRefresh() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				punshCardReOpen();
			}
		}, 3900);
	}

	/**
	 * 打卡成功，界面显示，
	 */
	private void punshCardSuccess() {
		punshCardListener.onPunshSuccess();
		mStatus = Status.SUCCESS;
		// FIXME 打卡成功后是否需要回到 开始打卡状态。界面显示是成功，然后开始背景动画
		// resumeBgAnimation();
		// update 2015-3-5 打开成功后，不显示水波背景
		loadSuccessView(centerView);
	}

	/**
	 * 打卡失败，界面显示，
	 */
	private void punshCardFail() {
		punshCardListener.onPunshFail();
		mStatus = Status.FAIL;
		loadFailView(centerView);
	}

	/**
	 * 打卡异常，界面显示，
	 */
	private void punshCardException() {
		punshCardListener.onPunshException();
		mStatus = Status.EXCEPTION;
	}

	/**
	 * 跳转到重新打卡页面
	 */
	private void punshCardReOpen() {
		mStatus = Status.WAIT;
		loadInitView(centerView);
		resumeBgAnimation();
		punshCardListener.resumeTips();
	}

	private void toggleWaitProgress(boolean isWaitShow) {
		circleNormal.setVisibility(isWaitShow ? View.INVISIBLE : View.VISIBLE);
		waitProgressBar.setVisibility(isWaitShow ? View.VISIBLE
				: View.INVISIBLE);
		loadLoadingView(centerView);
	}

	public OnPunshCardListener getPunshCardListener() {
		return punshCardListener;
	}

	public void setPunshCardListener(OnPunshCardListener punshCardListener) {
		this.punshCardListener = punshCardListener;
	}

	/**
	 * 视图回调到Activity的事件。
	 * 
	 * @author pWX184170
	 * 
	 */
	public interface OnPunshCardListener {
		int STATUS_SUCCESS = 1;
		int STATUS_FAIL = 2;
		int STATUS_EXCEPTION = 3;

		/**
		 * 视图默认状态，等待打开
		 */
		void onPunshCardWait();

		/**
		 * 用户点击打卡后，显示等待框，这个时候页面应该去请求接口。视图会等待这个页面的返回。不管请求成功或者失败都要返回。 <br>
		 * <b>Status Code:</b>
		 * <p>
		 * {@link OnPunshCardListener#STATUS_SUCCESS}
		 * </p>
		 * <p>
		 * {@link OnPunshCardListener#STATUS_FAIL}
		 * </p>
		 * <p>
		 * {@link OnPunshCardListener#STATUS_EXCEPTION}
		 * </p>
		 * 
		 * @return Status Code;
		 */
		void onPunshCardIng();

		/**
		 * 打卡成功
		 */
		void onPunshSuccess();

		/**
		 * 打卡失败
		 */
		void onPunshFail();

		/**
		 * 打卡异常
		 */
		void onPunshException();

		/**
		 * 校验是否可以打卡
		 */
		boolean canPunsh();

		/**
		 * 还原打卡提示信息
		 */
		void resumeTips();

	}

}
