package jie.example.boutique;

import java.io.IOException;

import jie.example.boutique.R;
import jie.example.utils.LogUtil;
import jie.example.zxing.decode.CaptureActivityHandler;
import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ScanningViewActivity extends Activity implements
		SurfaceHolder.Callback {
	private static final String TAG = ScanningViewActivity.class
			.getSimpleName();
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private CameraManager mCameraManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
		/*
		 * 若不设置FEATURE_NO_TITLE或沒有在功能清单文件中设置Activity的主题样式为Theme.NoTitleBar，
		 * 则扫描效果会大打折扣，甚至会扫描不出。
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scanning_view_aty);
		initView();
		loadingData();
	}

	private void initView() {
		TextView textTitle = (TextView) findViewById(R.id.actionbar_title);
		textTitle.setText(R.string.scanning_view_aty);
		mSurfaceView = (SurfaceView) findViewById(R.id.scanning_surface_view);
		mSurfaceHolder = mSurfaceView.getHolder();
		mCameraManager = new CameraManager(this);
	}

	private void loadingData() {
		mSurfaceHolder.addCallback(this);
	}
	private CaptureActivityHandler mHandler;// 解码线程
	private void initCamera(SurfaceHolder holder) {
		if (mCameraManager.isOpen()) {
			LogUtil.w(TAG, "back's camera() is already open");
			return;
		}
		try {
			mCameraManager.openDriver(holder);
			// Creating the mHandler starts the preview
			if (mHandler == null) {
				// mHandler = new CaptureActivityHandler(this, mDecodeFormats,
				// null, mCameraManager);
			}
		} catch (IOException e) {
			LogUtil.e(TAG, "Open Back's Camera Exception" + e.toString());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtil.i(TAG, "surfaceCreated");
		if (holder != null) {
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		LogUtil.i(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtil.i(TAG, "surfaceDestroyed");
	}

	public void setLeftBtnClick(View view) {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCameraManager.closeDriver();
	}

}
