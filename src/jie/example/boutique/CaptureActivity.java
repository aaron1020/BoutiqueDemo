package jie.example.boutique;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Vector;

import jie.example.boutique.R;
import jie.example.utils.LogUtil;
import jie.example.utils.ToastUtil;
import jie.example.widget.ViewfinderView;
import jie.example.zxing.camera.BeepManager;
import jie.example.zxing.camera.CameraManager;
import jie.example.zxing.decode.CaptureActivityHandler;
import jie.example.zxing.decode.DecodeThread;
import jie.example.zxing.decode.FinishListener;
import jie.example.zxing.decode.InactivityTimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

/**
 * 条码二维码扫描功能实现
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = CaptureActivity.class.getSimpleName();
	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
			.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);
	private boolean hasSurface;
	private BeepManager mBeepManager;// 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
	public static String currentState = "qrcode";// 条形码二维码选择状态

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ViewfinderView mViewfinderView;
	private TextView mActivityTitle;
	private TextView mTextQrcode, mTextBarcode, mTextHelp;
	private InactivityTimer mInactivityTimer;
	private CameraManager mCameraManager;
	private Vector<BarcodeFormat> mDecodeFormats;// 编码格式
	private CaptureActivityHandler mHandler;// 解码线程

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_qrcode:
				currentState = "qrcode";
				mTextQrcode.setSelected(true);// 设置选中项的选中图片：方法1(前提：需要在图片选择器中的XML文件中添加state_selected状态为true)
				// 设置选中项的图片：方法2
				// Drawable drawable = getResources().getDrawable(
				// R.drawable.scan_qrcode_click);
				// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				// drawable.getMinimumHeight());
				// mQrcode.setCompoundDrawables(null, drawable, null, null);
				mTextBarcode.setSelected(false);
				mActivityTitle.setText(mTextQrcode.getText().toString());
				qrcodeSetting();
				break;
			case R.id.item_onecode:
				currentState = "onecode";
				mTextQrcode.setSelected(false);
				mTextBarcode.setSelected(true);
				mActivityTitle.setText(mTextBarcode.getText().toString());
				onecodeSetting();
				break;
			case R.id.item_help:
				ToastUtil.showToast(mTextHelp.getText().toString());
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
		/*
		 * 若不设置FEATURE_NO_TITLE或沒有在功能清单文件中设置Activity的主题样式为Theme.NoTitleBar，
		 * 则扫描效果会大打折扣，甚至会扫描不出。
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_code_aty);
		initView();
		loadingData();
	}

	private void initView() {
		mActivityTitle = (TextView) findViewById(R.id.actionbar_title);
		mSurfaceView = (SurfaceView) findViewById(R.id.capture_surface_view);
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mTextBarcode = (TextView) findViewById(R.id.item_onecode);
		mTextQrcode = (TextView) findViewById(R.id.item_qrcode);
		mTextHelp = (TextView) findViewById(R.id.item_help);
		mInactivityTimer = new InactivityTimer(this);
		mBeepManager = new BeepManager(this);
		mCameraManager = new CameraManager(this);
		mSurfaceHolder = mSurfaceView.getHolder();
	}

	private void loadingData() {
		mTextQrcode.setOnClickListener(mClickListener);
		mTextBarcode.setOnClickListener(mClickListener);
		mTextHelp.setOnClickListener(mClickListener);
		mActivityTitle.setText(R.string.scan_qrcode);
		mTextQrcode.setSelected(true);

		mInactivityTimer.onActivity();
		mViewfinderView.setCameraManager(mCameraManager);
		mSurfaceHolder.addCallback(this);
		// 恢复活动监控器
		mInactivityTimer.onResume();

	}

	@Override
	protected void onResume() {
		super.onResume();
		qrcodeSetting();
	}

	public void drawViewfinder() {
		mViewfinderView.drawViewfinder();
	}

	/**
	 * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (mCameraManager.isOpen()) {
			LogUtil.i(TAG, "back's camera() is already open");
			return;
		}
		try {
			mCameraManager.openDriver(surfaceHolder);
			// Creating the mHandler starts the preview
			if (mHandler == null) {
				mHandler = new CaptureActivityHandler(this, mDecodeFormats,
						null, mCameraManager);
			}
		} catch (Exception e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			LogUtil.e(TAG,
					"Unexpected error initializing camera" + e.toString());
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 若摄像头被占用或者摄像头有问题则跳出提示对话框
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.launcher_icon);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	/**
	 * 暂停活动监控器,关闭摄像头
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (mHandler != null) {
			mHandler.quitSynchronously();
			mHandler = null;
		}
		// 暂停活动监控器
		mInactivityTimer.onPause();
		// 关闭摄像头
		mCameraManager.closeDriver();
		if (!hasSurface) {
			mSurfaceHolder.removeCallback(this);
		}
	}

	/**
	 * 停止活动监控器,保存最后选中的扫描类型
	 */
	@Override
	protected void onDestroy() {
		// 停止活动监控器
		super.onDestroy();
		mInactivityTimer.shutdown();
	}

	public void setLeftBtnClick(View view) {
		finish();
	}

	/**
	 * 获取扫描结果
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		mInactivityTimer.onActivity();

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {

			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			mBeepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, scaleFactor, rawResult);
		}
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);
		Map<ResultMetadataType, Object> metadata = rawResult
				.getResultMetadata();
		StringBuilder metadataText = new StringBuilder(20);
		if (metadata != null) {
			for (Map.Entry<ResultMetadataType, Object> entry : metadata
					.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
			}
		}
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable("bitmap", barcode);
		bundle.putString("barcodeFormat", rawResult.getBarcodeFormat()
				.toString());
		bundle.putString("decodeDate",
				formatter.format(new Date(rawResult.getTimestamp())));
		bundle.putCharSequence("metadataText", metadataText);
		bundle.putString("resultString", rawResult.getText());
		intent.setClass(CaptureActivity.this, ResultActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 在扫描图片结果中绘制绿色的点
	 * 
	 * @param barcode
	 * @param scaleFactor
	 * @param rawResult
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor,
			Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					if (point != null) {
						canvas.drawPoint(scaleFactor * point.getX(),
								scaleFactor * point.getY(), paint);
					}
				}
			}
		}
	}

	/**
	 * 在扫描图片结果中绘制绿色的线
	 * 
	 * @param canvas
	 * @param paint
	 * @param a
	 * @param b
	 * @param scaleFactor
	 */
	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
					scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	private void onecodeSetting() {
		mDecodeFormats = new Vector<BarcodeFormat>(7);
		mDecodeFormats.clear();
		mDecodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
		if (null != mHandler) {
			mHandler.setDecodeFormats(mDecodeFormats);
		}

		mViewfinderView.refreshDrawableState();
		mCameraManager.setManualFramingRect(360, 222);
		mViewfinderView.refreshDrawableState();

	}

	private void qrcodeSetting() {
		mDecodeFormats = new Vector<BarcodeFormat>(2);
		mDecodeFormats.clear();
		mDecodeFormats.add(BarcodeFormat.QR_CODE);
		mDecodeFormats.add(BarcodeFormat.DATA_MATRIX);
		if (null != mHandler) {
			mHandler.setDecodeFormats(mDecodeFormats);
		}

		mViewfinderView.refreshDrawableState();
		mCameraManager.setManualFramingRect(560, 560);
		mViewfinderView.refreshDrawableState();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			LogUtil.e(TAG, "surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	public ViewfinderView getViewfinderView() {
		return mViewfinderView;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public CameraManager getCameraManager() {
		return mCameraManager;
	}

	/**
	 * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
	 * 
	 * @param delayMS
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

}
