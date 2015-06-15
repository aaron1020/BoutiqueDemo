package jie.example.widget;

import jie.example.boutique.R;
import jie.example.entity.HistogramEntity;
import jie.example.utils.LogUtil;
import jie.example.utils.StringUtil;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 绘制柱状图
 */
public class HistogramView2 extends LinearLayout {

	private static final String TAG = "HistogramView2";
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的顶边距
	 */
	private static final int HV_MARGIN_TOP = 100;
	/**
	 * 柱状图(不包括左边标题)的左边距
	 */
	private static final int HV_MARGIN_LEFT = 96;
	/**
	 * Y轴的最大刻度
	 */
	private int mYPivotMaxScale;
	/**
	 * Y轴的刻度数量
	 */
	private int mYPivotScaleNum;
	/**
	 * 顶部标题
	 */
	private String mTopMainTitle;
	/**
	 * 顶部副标题
	 */
	private String mTopSubTitle;
	/**
	 * 是否显示顶部副标题
	 */
	private boolean isShowTopSubTitle;
	/**
	 * 是否显示左边标题
	 */
	private boolean isShowLeftTitle;
	/**
	 * 左边标题
	 */
	private String mLeftTitleValue;
	/**
	 * 是否显示右侧刻度线，默认不显示
	 */
	private boolean isShowRightSacle;
	/**
	 * mHistogramViewWidth：HistogramView的宽度；mHistogramViewHeight：
	 * HistogramView的高度
	 */
	private int mHistogramViewWidth, mHistogramViewHeight;
	/**
	 * 柱状图的右边距
	 */
	private int mHvMarginRight = 30;
	private Resources mResources;

	public HistogramView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HistogramView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// read values from attribute-->start
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.HistogramView2);
		try {
			mYPivotMaxScale = typedArray.getInteger(
					R.styleable.HistogramView2_yPivotMaxScale, 0);
			mYPivotScaleNum = typedArray.getInteger(
					R.styleable.HistogramView2_yPivotScaleNum, 0);
			mTopMainTitle = typedArray
					.getString(R.styleable.HistogramView2_topMainTitle);
			mTopSubTitle = typedArray
					.getString(R.styleable.HistogramView2_topSubTitle);
			isShowTopSubTitle = typedArray.getBoolean(
					R.styleable.HistogramView2_showTopSubTitle, false);
			mLeftTitleValue = typedArray
					.getString(R.styleable.HistogramView2_leftTitle);
			isShowLeftTitle = typedArray.getBoolean(
					R.styleable.HistogramView2_showLeftTitle, false);
			isShowRightSacle = typedArray.getBoolean(
					R.styleable.HistogramView2_showRightScaleLine, false);
			LogUtil.i(TAG, mYPivotMaxScale + ", " + mYPivotScaleNum + ", "
					+ mTopMainTitle + ", " + mTopSubTitle + ", "
					+ mLeftTitleValue + ", " + isShowTopSubTitle + ", "
					+ isShowLeftTitle + ", " + isShowRightSacle);
		} catch (Exception e) {
			LogUtil.e(TAG,
					"Read Values From Attribute Exception-->" + e.toString());
		} finally {
			if (typedArray != null) {
				typedArray.recycle();
			}
		}
		// read values from attribute-->end

		initView(context);
	}

	private void initView(Context context) {
		mResources = context.getResources();

		if (isShowRightSacle) {
			mHvMarginRight = 70;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		LogUtil.i(TAG, "HistogramView-->dispatchDraw");
		if (mHistogramViewWidth == 0) {
			// 在此处获取布局的宽和高：若在initView方法就获取，则宽和高的值均为0。
			mHistogramViewWidth = this.getWidth();
			mHistogramViewHeight = this.getHeight();
			LogUtil.i(TAG, "mHistogramViewWidth = " + mHistogramViewWidth
					+ ", mHistogramViewHeight = " + mHistogramViewHeight);
			drawHistogramView(canvas);
		}
	}

	private void drawHistogramView(Canvas canvas) {
		// 设置头部标题
		if (StringUtil.isNotEmpty(mTopMainTitle)) {
			Paint topMainTitlePaint = getPaint();
			topMainTitlePaint.setTextSize(26.0f);
			// if (!(isShowTopSubTitle && StringUtil.isNotEmpty(mTopSubTitle)))
			// {
			// topMainTitlePaint.setTextSize(36.0f);
			// }
			float x = (mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight) / 2;
			float topMainTitleSize = topMainTitlePaint
					.measureText(mTopMainTitle);// // 测量出标题的宽度
			canvas.drawText(mTopMainTitle, x, HV_MARGIN_TOP / 2,
					topMainTitlePaint);
		}
	}

	/**
	 * 获取柱状图默认的画笔对象
	 * 
	 * @param paintColor
	 *            画笔的颜色
	 * @param textSize
	 *            需要设置的文字的大小
	 * @return Paint
	 */
	private Paint getPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 抗(不显示)锯齿，让绘出来的物体更清晰
		paint.setColor(mResources.getColor(R.color.black));// 设置画笔的颜色
		return paint;
	}

	private Paint getPaint(int paintColor) {
		Paint paint = getPaint();
		paint.setColor(mResources.getColor(paintColor));// 设置画笔的颜色
		return paint;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, "HistogramView-->onDetachedFromWindow");
	}

	/**
	 * 坐标内部类，用于保存柱体所在的坐标范围
	 */
	private final class Point {

		float startX;
		float stopX;
		float startY;
		float stopY;

		public Point(float startX, float stopX, float startY, float stopY) {
			this.startX = startX;
			this.stopX = stopX;
			this.startY = startY;
			this.stopY = stopY;
		}
	}

	public interface HistogramViewClick {
		/**
		 * @param histogramId
		 *            柱子的id
		 * @param histogramEntity
		 *            柱子的信息
		 */
		void setHistogramViewListener(int histogramId,
				HistogramEntity histogramEntity);
	}

}
