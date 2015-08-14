package jie.example.widget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import jie.example.boutique.R;
import jie.example.utils.LogUtil;
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
	private Context mContext;
	private Resources mResources;
	private ImageView mIVPanel;
	private Bitmap mBitmapPanel;
	private Canvas mPaintCanvas;
	private Path mTextPath = new Path();;
	private Paint mTextPaint;
	private StringBuilder mStrBuilder = new StringBuilder();

	public WriterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WriterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		mResources = context.getResources();

		mIVPanel = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		mIVPanel.setLayoutParams(params);
		mIVPanel.setBackgroundColor(Color.WHITE);// 设置画板颜色为白色

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
	private void saveTrack(String track) {
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, TAG + "::onDetachedFromWindow()");
		saveTrack(mStrBuilder.toString());
	}

	private void drawHistogramView() {
		mBitmapPanel = Bitmap.createBitmap(mPanelWidth, mPanelHeight,
				Bitmap.Config.ARGB_8888);
		mIVPanel.setImageBitmap(mBitmapPanel);

		mPaintCanvas = new Canvas();
		mPaintCanvas.setBitmap(mBitmapPanel);

		mIVPanel.setOnTouchListener(mTouchListener);

		getPaint();
	}

	private Paint getPaint() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);// 抗(不显示)锯齿，让绘出来的物体更清晰
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setStrokeWidth(10.0f);
		mTextPaint.setColor(mResources.getColor(R.color.red));// 设置画笔的颜色
		return mTextPaint;
	}

	/**
	 * 清除写字板上的文字
	 */
	public void clearPanel() {
		if (mPaintCanvas != null) {
			mTextPath.reset();
			mPaintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			invalidate();
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
					+ File.separator + System.currentTimeMillis() + ".png";
			file = new File(savePath);
			fos = new FileOutputStream(file);
			baos = new ByteArrayOutputStream();
			mBitmapPanel.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			if (b != null) {
				fos.write(b);
			}
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
			float x = event.getX();
			float y = event.getY();
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mTextPath.moveTo(x, y);
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				mTextPath.quadTo(x, y, x, y); // 画线
				mPaintCanvas.drawPath(mTextPath, mTextPaint);
				invalidate();
				mStrBuilder.append("x = " + x + ", y = " + y).append('|');
				LogUtil.i(TAG, mStrBuilder.toString());
			}
			return true;
		}
	};

}