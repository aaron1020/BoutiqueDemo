package jie.example.widget;

import java.lang.ref.WeakReference;

import jie.example.boutique.R;
import jie.example.constant.Constant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * 具有水波效果的View：半径逐渐变大，透明度逐渐变小
 */
public class WaterWaveView extends View {

	private static final int WHAT_REFRESH = 1;
	// 100ms per mine has 60 fragment, so
	private static final long DEFAULT_SLEEP_TIME = 100L;
	private boolean isSecondRunning = false;
	private DrawThread drawThread;
	// 空心圆
	private Paint mPaint;
	// 中间不做渐变的圆
	private Paint mCenterPaint;

	// 绘制时间 paint
	private Paint mTimePaint;
	private Circle firstCircle;
	private Circle secondCircle;
	// center point
	private Point centerPoint;
	// margin top.
	private int yOffset = 100;
	// alpha
	private static final int DEFAULT_ALPHA = 120;
	// radius
	private static int startRadius = 120;

	public WaterWaveView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaterWaveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WaterWaveView(Context context) {
		this(context, null, 0);
	}

	public WaterWaveView(Context context, int yOffset) {
		super(context);
		this.yOffset = yOffset;
		init(context);
	}

	private void init(Context context) {
		// (int) (options.outHeight * scaleDensity);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int height = displayMetrics.heightPixels * 25 / 100;
		int centerX = displayMetrics.widthPixels / 2;
		int centerY = height / 2 + yOffset;// 这里+100表示是距离顶部的高度
		centerPoint = new Point(centerX, centerY);
		startRadius = height / 2;// init start
		initPaint();
		initCircle();
	}

	// 开始线程
	public void startDrawThread() {
		if (null == drawThread) {
			drawThread = new DrawThread("PunchCard", this);
			drawThread.startDraw();
		}
	}

	// 暂停线程
	public void pauseDrawThread() {
		if (null != drawThread && drawThread.isThreadRuning) {
			drawThread.isThreadRuning = false;
			resetDraw(); // 需要重置绘制
		}
	}

	// 恢复线程
	public void resumeDrawThread() {
		if (null != drawThread && drawThread.isThreadStart
				&& !drawThread.isThreadRuning) {
			drawThread.isThreadRuning = true;
		}
	}

	// 停止线程。
	public void stopDrawThread() {
		if (null != drawThread) {
			drawThread.stopDraw();
			drawThread = null;
		}
	}

	private void initPaint() {
		int circleColorId = getResources().getColor(R.color.eg);
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(0);

		mCenterPaint = new Paint();
		mCenterPaint.setColor(circleColorId);
		mCenterPaint.setAntiAlias(true);
		mCenterPaint.setStyle(Paint.Style.STROKE);
		mCenterPaint.setStrokeWidth(20F);

		// time paint
		mTimePaint = new Paint();
		mTimePaint.setColor(circleColorId);
		mTimePaint.setAntiAlias(true);
		mTimePaint.setStyle(Paint.Style.FILL);
		mTimePaint.setTextSize(60);
	}

	// 初始化圆
	private void initCircle() {
		firstCircle = new Circle(startRadius, centerPoint.x, centerPoint.y,
				DEFAULT_ALPHA);
		secondCircle = new Circle(startRadius, centerPoint.x, centerPoint.y,
				DEFAULT_ALPHA);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBg(canvas);
	}

	/**
	 * 两个渐变消失的圆
	 */
	private void drawBg(Canvas canvas) {
		try {
			// 第1个圆
			mPaint.setAlpha(firstCircle.alpha);
			mPaint.setStrokeWidth((firstCircle.radius - startRadius) * 2);
			canvas.drawCircle(firstCircle.point.x, firstCircle.point.y,
					firstCircle.radius, mPaint);
			// 第2个圆
			mPaint.setAlpha(secondCircle.alpha);
			mPaint.setStrokeWidth((secondCircle.radius - startRadius) * 2);
			canvas.drawCircle(secondCircle.point.x, secondCircle.point.y,
					secondCircle.radius, mPaint);
		} catch (Exception e) {
			Log.e(Constant.TAG_GLOBAL,
					"Paint Circle Exception-->" + e.toString());
		}
	}

	private long logic() {
		long startTime = System.currentTimeMillis();
		doWaitPunshCard();
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	/**
	 * 等待打卡的绘制
	 */
	private void doWaitPunshCard() {
		// 半径变大，透明度变小
		offsetCircle(firstCircle);

		// 当第1个圆渐变到透明度只有50的时候 开始绘制第2个圆
		if (firstCircle.alpha < 50) {
			isSecondRunning = true;
		}

		// 当第1个消失后 重置透明度，等待
		if (firstCircle.alpha < 0) {
			resetCircle(firstCircle);
		}

		// 重置第2个圆，
		if (isSecondRunning) {
			offsetCircle(secondCircle);
			if (secondCircle.alpha < 0) {
				resetCircle(secondCircle);
			}
		}
	}

	/**
	 * 重置Circle.消失后，重新开始.
	 * 
	 * @param targetCircle
	 */
	private void resetCircle(Circle targetCircle) {
		targetCircle.alpha = DEFAULT_ALPHA;
		targetCircle.radius = startRadius;
	}

	/**
	 * 半径变大，透明度变小
	 * 
	 * @param targetCircle
	 */
	private void offsetCircle(Circle targetCircle) {
		targetCircle.alpha -= 5;
		targetCircle.radius += 2;
	}

	private void resetDraw() {
		resetCircle(firstCircle);
		resetCircle(secondCircle);
		isSecondRunning = false;
		mHandler.sendEmptyMessage(WHAT_REFRESH);
	}

	// 内部线程类
	private static class DrawThread extends Thread {

		private WeakReference<WaterWaveView> weak;
		private boolean isThreadStart = false; // 线程是否开始运行
		private boolean isThreadRuning = false;// 线程是否需要运行。

		public DrawThread(String name, WaterWaveView target) {
			super(name);
			weak = new WeakReference<WaterWaveView>(target);
		}

		public synchronized void startDraw() {
			if (!this.isThreadStart) {
				isThreadStart = true;
				start();
			}
		}

		@Override
		public synchronized void start() {
			super.start();
			isThreadRuning = true;
		}

		public synchronized void stopDraw() {
			isThreadRuning = false;
			isThreadStart = false;
		}

		@Override
		public void run() {
			while (isThreadStart) {
				if (isThreadRuning) {
					final WaterWaveView target = weak.get();
					if (null != target) {
						long host = target.logic();
						try {
							if (host < DEFAULT_SLEEP_TIME) {
								Thread.sleep((DEFAULT_SLEEP_TIME - host));
							}
						} catch (InterruptedException e) {
							Log.e(Constant.TAG_GLOBAL,
									"Paint Circle Thread Exception-->"
											+ e.toString());
						}
						target.mHandler.sendEmptyMessage(WHAT_REFRESH);
					}
				} else {
					// 如果isThreadRunning 为false 这个是否会抢占系统资源。
				}
			}
		}
	}

	private UiHandler mHandler = new UiHandler(this);

	private static class UiHandler extends Handler {

		private WeakReference<WaterWaveView> weak;

		public UiHandler(WaterWaveView target) {
			this.weak = new WeakReference<WaterWaveView>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final WaterWaveView target = weak.get();
			if (null != target) {
				switch (msg.what) {
				case WHAT_REFRESH:
					target.invalidate();
					break;
				default:
					break;
				}
			}
		}
	}

	// 圆圈封装类。
	class Circle {

		private Point point;
		private float radius;
		private int alpha;

		public Circle(float radius, int x, int y, int alpha) {
			this.radius = radius;
			this.alpha = alpha;
			this.point = new Point(x, y);
		}

	}

}
