package jie.example.boutique;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Vector;

import jie.example.boutique.R;
import jie.example.camera.BeepManager;
import jie.example.camera.CameraManager;
import jie.example.camera.CaptureActivityHandler;
import jie.example.camera.DecodeThread;
import jie.example.camera.FinishListener;
import jie.example.camera.InactivityTimer;
import jie.example.camera.ViewfinderView;
import jie.example.utils.LogUtil;

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
	private BeepManager beepManager;// 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
	public static String currentState = "qrcode";// 条形码二维码选择状态
	private String characterSet;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ViewfinderView viewfinderView;
	private TextView mActivityTitle;
	private TextView mTextQrcode;
	private TextView mTextBarcode;

	/**
	 * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;
	private CameraManager cameraManager;
	private Vector<BarcodeFormat> decodeFormats;// 编码格式
	private CaptureActivityHandler mHandler;// 解码线程

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_qrcode:
				currentState = "qrcode";
				mTextQrcode.setSelected(true);// 设置选中项的图片：方法1(前提：需要在图片选择器中的XML文件中添加state_selected状态为true)
				mTextBarcode.setSelected(false);
				// 设置选中项的图片：方法2
				// Drawable drawable = getResources().getDrawable(
				// R.drawable.scan_qrcode_click);
				// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				// drawable.getMinimumHeight());
				// mQrcode.setCompoundDrawables(null, drawable, null, null);
				qrcodeSetting();
				break;
			case R.id.item_onecode:
				currentState = "onecode";
				mTextQrcode.setSelected(false);
				mTextBarcode.setSelected(true);
				onecodeSetting();
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
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.capture_aty);
		initComponent();
		initView();

	}

	/**
	 * 初始化功能组件
	 */
	private void initComponent() {
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		cameraManager = new CameraManager(getApplication());
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mActivityTitle = (TextView) findViewById(R.id.actionbar_title);
		mSurfaceView = (SurfaceView) findViewById(R.id.capture_surface_view);
		mTextBarcode = (TextView) findViewById(R.id.item_onecode);
		mTextQrcode = (TextView) findViewById(R.id.item_qrcode);

		mActivityTitle.setText(R.string.scan_scan);
		mTextBarcode.setOnClickListener(mClickListener);
		mTextQrcode.setOnClickListener(mClickListener);
		mTextQrcode.setSelected(true);
		
		/**
		 * 主要对相机进行初始化工作
		 */
		inactivityTimer.onActivity();
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		mSurfaceHolder = mSurfaceView.getHolder();
		qrcodeSetting();
		if (hasSurface) {
			initCamera(mSurfaceHolder);
		} else {
			// 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
			mSurfaceHolder.addCallback(this);
		}
		// 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
		beepManager.updatePrefs();
		// 恢复活动监控器
		inactivityTimer.onResume();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		qrcodeSetting();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			LogUtil.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the mHandler starts the preview, which can also throw a
			// RuntimeException.
			if (mHandler == null) {
				mHandler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
		} catch (Exception e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			LogUtil.w(TAG,
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
		inactivityTimer.onPause();
		// 关闭摄像头
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_surface_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
	}

	/**
	 * 停止活动监控器,保存最后选中的扫描类型
	 */
	@Override
	protected void onDestroy() {
		// 停止活动监控器
		super.onDestroy();
		inactivityTimer.shutdown();
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
		inactivityTimer.onActivity();

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {

			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
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
		decodeFormats = new Vector<BarcodeFormat>(7);
		decodeFormats.clear();
		decodeFormats.addAll(DecodeThread.ONE_D_FORMATS);
		if (null != mHandler) {
			mHandler.setDecodeFormats(decodeFormats);
		}

		viewfinderView.refreshDrawableState();
		cameraManager.setManualFramingRect(360, 222);
		viewfinderView.refreshDrawableState();

	}

	private void qrcodeSetting() {
		decodeFormats = new Vector<BarcodeFormat>(2);
		decodeFormats.clear();
		decodeFormats.add(BarcodeFormat.QR_CODE);
		decodeFormats.add(BarcodeFormat.DATA_MATRIX);
		if (null != mHandler) {
			mHandler.setDecodeFormats(decodeFormats);
		}

		viewfinderView.refreshDrawableState();
		cameraManager.setManualFramingRect(300, 300);
		viewfinderView.refreshDrawableState();
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
		return viewfinderView;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
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
