package jie.example.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import jie.example.manager.BoutiqueApp;
import jie.example.utils.LogUtil;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * 视频录制控件
 */
public class RecordVideoView extends SurfaceView implements OnErrorListener,
		Callback {
	private static final String TAG = RecordVideoView.class.getSimpleName();
	private SurfaceHolder mSurfaceHolder;
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	private File mSaveRecordFile;// 文件
	private boolean mOpenCamera = true;// 是否一开始就打开摄像头

	public RecordVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("deprecation")
	public RecordVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * 初始化摄像头
	 */
	private void initCamera() throws Exception {
		freeCameraResource();

		mCamera = Camera.open();
		if (mCamera == null) {
			return;
		}

		setCameraParams();
		// mCamera.setDisplayOrientation(90);// 若是竖屏，相机要旋转90度
		mCamera.setPreviewDisplay(mSurfaceHolder);
		mCamera.startPreview();
		mCamera.unlock();
	}

	/**
	 * 设置摄像头为竖屏
	 */
	private void setCameraParams() {
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			params.set("orientation", "portrait");
			mCamera.setParameters(params);
		}
	}

	/**
	 * 释放资源
	 */
	private void releaseRecord() {
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**
	 * 释放摄像头资源
	 */
	private void freeCameraResource() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.lock();
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 初始化
	 */
	private void initRecord() throws Exception {
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.reset();
		if (mCamera != null) {
			mMediaRecorder.setCamera(mCamera);
			mMediaRecorder.setOnErrorListener(this);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
			mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
			mMediaRecorder.setOutputFormat(OutputFormat.THREE_GPP);// 视频输出格式
			mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);// 音频格式
			mMediaRecorder.setVideoSize(640, 480);// 设置分辨率：
			mMediaRecorder.setVideoEncodingBitRate(6 * 1024 * 1024);// 设置帧频率，调整清晰度
			// mMediaRecorder.setOrientationHint(90);// 若是竖屏，视频输出要旋转90度
			mMediaRecorder.setVideoEncoder(VideoEncoder.DEFAULT);// 视频录制格式

			File dir = new File(BoutiqueApp.getAppFolder(), "RecordVideo");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss")
					.format(new Date());
			mSaveRecordFile = new File(dir, fileName + ".3gp");// 视频格式与OutputFormat.THREE_GPP对应

			mMediaRecorder.setOutputFile(mSaveRecordFile.getAbsolutePath());
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		}
	}

	/**
	 * 开始录制视频
	 */
	public void record() throws Exception {
		if (!mOpenCamera) {// 如果未打开摄像头，则打开
			initCamera();
		}
		initRecord();
	}

	/**
	 * 停止拍摄
	 */
	public void stop() throws Exception {
		stopRecord();
		releaseRecord();
		freeCameraResource();
	}

	/**
	 * 停止录制
	 */
	public void stopRecord() throws Exception {
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);// 设置后不会崩
			mMediaRecorder.setPreviewDisplay(null);
			mMediaRecorder.stop();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mOpenCamera) {
			return;
		}
		try {
			initCamera();
		} catch (Exception e) {
			LogUtil.e(TAG, "initCamera()::" + e.toString());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (!mOpenCamera) {
			return;
		}
		freeCameraResource();
	}

	@Override
	public void onError(MediaRecorder mediaRecorder, int what, int extra) {
		if (mediaRecorder != null) {
			mediaRecorder.reset();
		}
	}

	public File getSaveRecordFile() {
		return mSaveRecordFile;
	}

}