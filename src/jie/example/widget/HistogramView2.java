package jie.example.widget;

import jie.example.boutique.R;
import jie.example.constant.Constant;
import jie.example.entity.HistogramEntity;
import jie.example.utils.LogUtil;
import jie.example.utils.StringUtil;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 绘制柱状图
 */
public class HistogramView2 extends View {

	private static final String TAG = "HistogramView2";
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的顶边距
	 */
	private static final int HV_MARGIN_TOP = 100;
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的底边距
	 */
	private static final int HV_MARGIN_BOTTOM = HV_MARGIN_TOP;
	/**
	 * 柱状图(不包括左边标题)的左边距
	 */
	private static final int HV_MARGIN_LEFT = 96;
	/**
	 * 刻度分隔线的长度
	 */
	private static final int SCALE_DIVIDE_LINE_LENGTH = 15;
	/**
	 * 字体大小
	 */
	private static final int GLOBAL_PAINT_SIZE = 22;
	/**
	 * Y轴的最大刻度
	 */
	private int mYPivotMaxScale;
	/**
	 * Y轴的刻度数量
	 */
	private int mYPivotScaleNum;
	/**
	 * Y轴坐标刻度刻度之间的间隔
	 */
	private float mYPivotScalesMargin;
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
	private LinearLayout mLayoutParent;
	private Canvas mTopCanvas;
	private Bitmap mBitmapTopTitle;
	/**
	 * 占位：负责显示标题
	 */
	private ImageView mIVShowTopTitle;

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

	private RelativeLayout mLayoutChild;
	/**
	 * 占位：负责显示柱状图网格横向线和背景
	 */
	private ImageView mIVShowGridHori;
	
	private void initView(Context context) {
		mResources = context.getResources();

		if (isShowRightSacle) {
			mHvMarginRight = 70;
		}

		mLayoutChild = new RelativeLayout(context);
		mLayoutChild.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		mIVShowGridHori = new ImageView(context);
		mIVShowGridHori.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mIVShowGridHori.setBackgroundColor(Color.WHITE);// 设置网格的背景为白色
		mLayoutChild.addView(mIVShowGridHori);
		this.addView(mLayoutChild);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		mHistogramViewWidth = this.getWidth();
		mHistogramViewHeight = this.getHeight();
		LogUtil.i(TAG, "mHistogramViewWidth = " + mHistogramViewWidth
				+ ", mHistogramViewHeight = " + mHistogramViewHeight);
		drawHistogramView(canvas);
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	// super.onDraw(canvas);
	// // if (mHistogramViewWidth == 0) {
	// // 在此处获取布局的宽和高：若在initView方法就获取，则宽和高的值均为0。
	// mHistogramViewWidth = this.getWidth();
	// mHistogramViewHeight = this.getHeight();
	// LogUtil.i(TAG, "mHistogramViewWidth = " + mHistogramViewWidth
	// + ", mHistogramViewHeight = " + mHistogramViewHeight);
	// drawHistogramView(canvas);
	//
	// // }
	// }

	private void drawHistogramView(Canvas canvas) {

		// 设置头部标题
		if (StringUtil.isNotEmpty(mTopMainTitle)) {
			LogUtil.e(TAG, "fffff");
			Paint topMainTitlePaint = getPaint();
			topMainTitlePaint.setTextSize(26.0f);
			if (!(isShowTopSubTitle && StringUtil.isNotEmpty(mTopSubTitle))) {
				topMainTitlePaint.setTextSize(36.0f);
			}
			float topMainTitleSize = topMainTitlePaint
					.measureText(mTopMainTitle);// // 测量出标题的宽度
			float x = (mHistogramViewWidth - topMainTitleSize) / 2;
			canvas.drawText(mTopMainTitle, x, HV_MARGIN_TOP / 2,
					topMainTitlePaint);
		}

		// 设置头部副标题
		if (isShowTopSubTitle) {
			if (StringUtil.isNotEmpty(mTopSubTitle)) {
				Paint topSubTitlePaint = getPaint(R.color.red);
				topSubTitlePaint.setTextSize(24.0f);
				float topSubTitleSize = topSubTitlePaint
						.measureText(mTopSubTitle);
				float x = (mHistogramViewWidth - topSubTitleSize) / 2;
				canvas.drawText(mTopSubTitle, x, HV_MARGIN_TOP / 2 + 26,
						topSubTitlePaint);
			}
		}

		// 绘制左边标题
		if (isShowLeftTitle) {
			if (StringUtil.isNotEmpty(mLeftTitleValue)) {
				Paint leftTitlePaint = getPaint();
				leftTitlePaint.setTextSize(22.0f);
				float leftTitleSize = leftTitlePaint
						.measureText(mLeftTitleValue);

				Rect rect = new Rect(0, HV_MARGIN_LEFT,
						SCALE_DIVIDE_LINE_LENGTH, HV_MARGIN_LEFT);
				leftTitlePaint.getTextBounds(mLeftTitleValue, 0,
						mLeftTitleValue.length(), rect);
				int offsetHorizontal = rect.height();
				int offsetVertical = rect.width();
				offsetHorizontal = Math.max(offsetHorizontal, 20);
				offsetVertical = Math.max(offsetVertical, 20);
				int width = offsetHorizontal + rect.height();
				int startX = width / 2 - 10;

				Path path = new Path();
				path.moveTo(startX, mHistogramViewHeight / 2 + leftTitleSize
						/ 2);
				path.cubicTo(startX, mHistogramViewHeight / 2 + leftTitleSize
						/ 2, startX, mHistogramViewHeight / 2 + leftTitleSize
						/ 2, startX, 0);

				canvas.drawTextOnPath(mLeftTitleValue, path, 0, rect.height(),
						leftTitlePaint);
			}
		}

		// 绘制Y轴坐标
		Paint yPivotScaleShortLines = getPaint(R.color.red);
		Paint yPivotScaleValues = getPaint(R.color.eagle_two);
		yPivotScaleValues.setTextSize(GLOBAL_PAINT_SIZE);
		Paint gridLinePaint = getPaint(R.color.histogram_view_grid_line);
		gridLinePaint.setStyle(Style.FILL_AND_STROKE);
		int yScaleValue = mYPivotMaxScale / mYPivotScaleNum;// 算出刻度值
		mYPivotScalesMargin = (mHistogramViewHeight - HV_MARGIN_TOP - HV_MARGIN_BOTTOM)
				/ mYPivotMaxScale;
		for (int scale = 0; scale <= mYPivotMaxScale; scale += yScaleValue) {

			// 绘制Y轴刻度分隔线，即分隔每一个刻度值的红色短线
			float startY = mHistogramViewHeight - HV_MARGIN_BOTTOM
					- (scale * mYPivotScalesMargin);
			float stopY = startY;
			canvas.drawLine(HV_MARGIN_LEFT - SCALE_DIVIDE_LINE_LENGTH, startY,
					HV_MARGIN_LEFT, stopY, yPivotScaleShortLines);

			// 绘制Y轴刻度值
			canvas.drawText(String.valueOf(scale), HV_MARGIN_LEFT
					- SCALE_DIVIDE_LINE_LENGTH - 40, startY + 8,
					yPivotScaleValues);

			// 绘制中间的横向网格线
			canvas.drawLine(HV_MARGIN_LEFT, startY, mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight, stopY, gridLinePaint);
		}

		// 绘制最上面一根的横向网格线
		canvas.drawLine(HV_MARGIN_LEFT, HV_MARGIN_TOP, mHistogramViewWidth
				- HV_MARGIN_LEFT - mHvMarginRight, HV_MARGIN_TOP, gridLinePaint);

		// 绘制Y轴刻度线
		canvas.drawLine(HV_MARGIN_LEFT, HV_MARGIN_BOTTOM, HV_MARGIN_LEFT,
				mHistogramViewHeight - HV_MARGIN_TOP, getPaint());

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
