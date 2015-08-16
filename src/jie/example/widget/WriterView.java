package jie.example.widget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import jie.example.boutique.R;
import jie.example.utils.LogUtil;
import jie.example.utils.StringUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 写字板
 */
public class WriterView extends LinearLayout {
	private static final String TAG = WriterView.class.getSimpleName();
	private int mPanelWidth = 0, mPanelHeight = 0;
	private Resources mResources;
	private ImageView mIVPanel;
	private Bitmap mBitmapPanel;
	private Canvas mPaintCanvas;
	private Path mTextPath = new Path();
	private Paint mTextPaint;
	private StringBuilder mStrBuilder = new StringBuilder();

	public WriterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WriterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	@SuppressLint("ResourceAsColor")
	private void initView(Context context) {
		mResources = context.getResources();

		mIVPanel = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		mIVPanel.setLayoutParams(params);
		mIVPanel.setBackgroundColor(mResources
				.getColor(R.color.writer_panel_bg));// 设置画板颜色为白色

		addView(mIVPanel);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mPanelWidth == 0) {
			mPanelWidth = this.getWidth();
			mPanelHeight = this.getHeight();
			drawHistogramView();
		}
	}

	/**
	 * 保存手指运动轨迹
	 */
	private void saveTrack() {
		String track = mStrBuilder.toString();
		if (StringUtil.isNotEmpty(track.trim())) {
			LogUtil.i(TAG, "track = " + track);
			try {
				saveToSD("track_" + System.currentTimeMillis() + ".txt", track);
				mStrBuilder.delete(0, mStrBuilder.length());
			} catch (Exception e) {
				LogUtil.e(TAG, TAG + "::saveTrack(String)::" + e.toString());
			}
		} else {
			LogUtil.i(TAG, "track is null");
		}
	}

	public void saveToSD(String fileName, String content) throws Exception {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			OutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.close();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, TAG + "::onDetachedFromWindow()");
		saveTrack();
		releaseResource(mBitmapPanel, mPaintCanvas);
		mTextPath = null;
	}

	/**
	 * 回收Bitmap以及它对应的画布
	 */
	private void releaseResource(Bitmap bitmap, Canvas canvas) {
		if (bitmap != null && !bitmap.isRecycled()) {
			canvas = null;
			bitmap.recycle();
		}
	}

	private void drawHistogramView() {
		mBitmapPanel = Bitmap.createBitmap(mPanelWidth, mPanelHeight,
				Bitmap.Config.ARGB_8888);
		mIVPanel.setImageBitmap(mBitmapPanel);

		mPaintCanvas = new Canvas();
		mPaintCanvas.setBitmap(mBitmapPanel);

		mIVPanel.setOnTouchListener(mTouchListener);

		getDefaultPaint();
	}

	private Paint getDefaultPaint() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);// 抗(不显示)锯齿，让绘出来的物体更清晰
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setColor(mResources.getColor(R.color.red));// 设置画笔的颜色
		return mTextPaint;
	}

	private Paint getPaint(Paint paint, float scale) {
		Paint p = paint;
		p.setStrokeWidth(1 + 2.0f * scale);
		return p;
	}

	/**
	 * 清除写字板上的文字
	 */
	public void clearPanel() {
		if (mPaintCanvas != null) {
			mTextPath.reset();
			mPaintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			invalidate();
			mStrBuilder.delete(0, mStrBuilder.length());// 把轨迹的坐标信息也给清掉
		}
	}

	/**
	 * 以图片的形式保存写字板上的文字
	 */
	public void savePanelText() {
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		String savePath = null;
		File file = null;
		try {
			savePath = Environment.getExternalStorageDirectory()
					+ File.separator + System.currentTimeMillis()
					+ ".png".trim();
			file = new File(savePath);
			fos = new FileOutputStream(file);
			baos = new ByteArrayOutputStream();
			mBitmapPanel.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] data = baos.toByteArray();
			if (data != null) {
				fos.write(data);
			}

			saveTrack();
			clearPanel();
		} catch (Exception e) {
			LogUtil.e(TAG, TAG + "::savePanelText()::" + e.toString());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				LogUtil.e(TAG,
						TAG + "::savePanelText() finally::" + e.toString());
			}
		}
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LogUtil.i(TAG, "event.getPressure() = " + event.getPressure());
			float x = event.getX();
			float y = event.getY();

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mTextPath.moveTo(x, y);
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				mTextPath.quadTo(x, y, x, y); // 画线
				Paint paint = getPaint(mTextPaint, event.getPressure());
				mPaintCanvas.drawPath(mTextPath, paint);
				invalidate();
				mStrBuilder.append("x = " + x + ", y = " + y).append('|');
				paint = null;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mTextPath.reset();
			}
			return true;
		}
	};

}