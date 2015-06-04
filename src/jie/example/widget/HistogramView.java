package jie.example.widget;

import java.util.ArrayList;
import java.util.List;

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
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

/**
 * 绘制柱状图
 */
public class HistogramView extends LinearLayout {
	private static final String TAG = "HistogramView";
	/**
	 * Y轴的最大刻度
	 */
	private static final int Y_PIVOT_MAX_SCALE = 100;
	/**
	 * Y轴的刻度数量
	 */
	private static final int Y_PIVOT_SCALE_NUM = 5;
	/**
	 * 刻度分隔线的长度
	 */
	private static final int SCALE_DIVIDE_LINE_LENGTH = 15;
	/**
	 * 右侧刻度的数量
	 */
	private static final int RIGHT_SCALE_NUM = 6;
	/**
	 * 右侧刻度的最大刻度值
	 */
	private static final int RIGHT_SCALE_MAX = 120;
	/**
	 * 柱状图(不包括左边标题)的左边距
	 */
	private static final int HV_MARGIN_LEFT = 96;
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的顶边距
	 */
	private static final int HV_MARGIN_TOP = 100;
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的底边距
	 */
	private static final int HV_MARGIN_BOTTOM = HV_MARGIN_TOP;
	/**
	 * 字体大小
	 */
	private static final int GLOBAL_TEXT_SIZE = 22;
	/**
	 * mHistogramViewWidth：HistogramView所在布局的宽度；mHistogramViewHeight：
	 * HistogramView所在布局的高度
	 */
	private int mHistogramViewWidth = 0, mHistogramViewHeight = 0;
	/**
	 * 柱状图的右边距
	 */
	private int mHvMarginRight = 30;
	/**
	 * 柱体的宽度，默认是50
	 */
	private int mHistogramWidth = 50;
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
	 * 左边显示的标题
	 */
	private String mLeftTitleValue;
	/**
	 * 是否显示右侧刻度线，默认不显示
	 */
	private boolean isShowRightSacle;
	/**
	 * 是否显示平均线
	 */
	private boolean isShowAverageLine;
	/**
	 * 平均线高度
	 */
	private int mAverageLineHeight = 2;
	/**
	 * 平均值大小
	 */
	private String mAverageValue = "0.00";
	/**
	 * Y轴坐标刻度刻度之间的间隔
	 */
	private float yPivotScaleMargin;
	/**
	 * 右侧刻度线的刻度之前的间隔
	 */
	private int mRightSacleMargin;
	/**
	 * x轴每一格的宽度，也就是网格的宽度
	 */
	private int mGridWidth = 100;
	private Resources mResources;
	private LinearLayout mLayoutParent;
	private RelativeLayout mLayoutChild;
	private HorizontalScrollView mHoriScroll;
	private Canvas mTopCanvas, mYPivotCancas, mRightScaleCanvas;
	private ArrayList<HistogramEntity> mHistogramEntityList;
	/**
	 * 占位：负责显示标题
	 */
	private ImageView mIVShowTopTitle;
	/**
	 * 占位：负责显示左边标题和刻度
	 */
	private ImageView mIVShowLeftPart;
	/**
	 * 占位：负责显示柱状图网格横向线和背景
	 */
	private ImageView mIVShowGridHori;
	/**
	 * 占位：负责显示柱状图网格纵向线和柱体
	 */
	private ImageView mIVShowGridPoti;
	/**
	 * 占位：负责显示右边刻度
	 */
	private ImageView mIVShowRightPart;
	private Bitmap mBitmapTopTitle, mBitmapLeftPart, mBitmapGridHori,
			mBitmapRightPart, mBitmapHistogram;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	/**
	 * @param context
	 *            上下文对象
	 * @param topMainTitle
	 *            柱状图顶部标题值
	 * @param histogramEntityList
	 *            柱体属性实体的集合
	 * @param isShowTopSubTitle
	 *            是否显示柱状图顶部副标题：如果需要显示，则需另外setTopSubTitleValue(String)方法设置副标题的值。
	 * @param isShowLeftTitle
	 *            是否显示柱状图左边标题：如果需要显示，则需另外setLeftTitleValue(String)方法设置标题的值。
	 * @param isShowRightSacle
	 *            是否显示右侧刻度线，一般不显示(传入false)
	 */
	public HistogramView(Context context, String topMainTitle,
			ArrayList<HistogramEntity> histogramEntityList,
			boolean isShowTopSubTitle, boolean isShowLeftTitle,
			boolean isShowAverageLine, boolean isShowRightSacle) {
		super(context);
		mTopMainTitle = topMainTitle;
		mHistogramEntityList = histogramEntityList;
		this.isShowTopSubTitle = isShowTopSubTitle;
		this.isShowLeftTitle = isShowLeftTitle;
		this.isShowAverageLine = isShowAverageLine;
		this.isShowRightSacle = isShowRightSacle;

		if (histogramEntityList != null) {
			if (histogramEntityList.size() > 3
					&& histogramEntityList.size() < 10) {
				this.mGridWidth = 200;
				this.mHistogramWidth = 100;
			} else if (histogramEntityList.size() < 4) {
				this.mGridWidth = 350;
				this.mHistogramWidth = 120;
			}
		}
		initView(context);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mResources = context.getResources();

		setBackgroundColor(mResources.getColor(R.color.hv_container_bg));
		setOrientation(HORIZONTAL);
		if (isShowRightSacle) {
			mHvMarginRight = 70;
		}

		mIVShowLeftPart = new ImageView(context);
		mIVShowLeftPart.setLayoutParams(new LayoutParams(HV_MARGIN_LEFT,
				LayoutParams.MATCH_PARENT));
		addView(mIVShowLeftPart);

		mLayoutParent = new LinearLayout(context);
		mLayoutParent.setOrientation(VERTICAL);

		mIVShowTopTitle = new ImageView(context);
		mIVShowTopTitle.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, HV_MARGIN_TOP));
		mLayoutParent.addView(mIVShowTopTitle);

		mLayoutChild = new RelativeLayout(context);
		mLayoutChild.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mIVShowGridHori = new ImageView(context);
		mIVShowGridHori.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mIVShowGridHori.setBackgroundColor(Color.WHITE);// 设置网格的背景为白色
		mLayoutChild.addView(mIVShowGridHori);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);

		mIVShowGridPoti = new ImageView(context);
		mIVShowGridPoti.setLayoutParams(params);
		mIVShowGridPoti.setScaleType(ScaleType.MATRIX);

		mHoriScroll = new HorizontalScrollView(context);
		mHoriScroll.setHorizontalScrollBarEnabled(true);
		mHoriScroll.setLayoutParams(params);
		mHoriScroll.addView(mIVShowGridPoti);

		mLayoutChild.addView(mHoriScroll);// 把mHoriScroll做为mLayoutChild的子View
		mLayoutParent.addView(mLayoutChild);
		addView(mLayoutParent);

		mIVShowRightPart = new ImageView(context);
		mIVShowRightPart.setLayoutParams(new LayoutParams(mHvMarginRight,
				LayoutParams.MATCH_PARENT));
		addView(mIVShowRightPart);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		LogUtil.i(TAG, "dispatchDraw");
		if (mHistogramViewWidth == 0) {
			// 在此处获取布局的宽和高：若在initView方法就获取，则宽和高的值均为0。
			mHistogramViewWidth = this.getWidth();
			mHistogramViewHeight = this.getHeight();
			LogUtil.i(TAG, "mHistogramViewWidth = " + mHistogramViewWidth
					+ ", mHistogramViewHeight = " + mHistogramViewHeight);
			mLayoutParent.setLayoutParams(new LayoutParams(mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight,
					LayoutParams.MATCH_PARENT));
			drawHistogramView();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, "onDetachedFromWindow");
		mHandler = null;
		setBitmapNull(mBitmapTopTitle);
		setBitmapNull(mBitmapLeftPart);
		setBitmapNull(mBitmapGridHori);
		setBitmapNull(mBitmapRightPart);
		setBitmapNull(mBitmapHistogram);
	}

	private void setBitmapNull(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 绘制柱状图
	 */
	private void drawHistogramView() {
		try {
			mBitmapLeftPart = Bitmap.createBitmap(HV_MARGIN_LEFT,
					mHistogramViewHeight, Bitmap.Config.ARGB_8888);
			mBitmapRightPart = Bitmap.createBitmap(mHvMarginRight,
					mHistogramViewHeight, Bitmap.Config.ARGB_8888);

			mBitmapTopTitle = Bitmap.createBitmap(mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight, HV_MARGIN_TOP,
					Bitmap.Config.ARGB_8888);
			mBitmapGridHori = Bitmap
					.createBitmap(mHistogramViewWidth - HV_MARGIN_LEFT
							- mHvMarginRight, mHistogramViewHeight
							- HV_MARGIN_TOP - HV_MARGIN_BOTTOM,
							Bitmap.Config.ARGB_8888);

			mYPivotCancas = new Canvas();
			mYPivotCancas.setBitmap(mBitmapLeftPart);
			mIVShowLeftPart.setImageBitmap(mBitmapLeftPart);

			mTopCanvas = new Canvas();
			mTopCanvas.setBitmap(mBitmapTopTitle);
			mIVShowTopTitle.setImageBitmap(mBitmapTopTitle);

			mRightScaleCanvas = new Canvas();
			mRightScaleCanvas.setBitmap(mBitmapRightPart);
			mIVShowRightPart.setImageBitmap(mBitmapRightPart);

			mIVShowGridHori.setImageBitmap(mBitmapGridHori);

			// 设置头部标题
			if (StringUtil.isNotEmpty(mTopMainTitle)) {
				Paint topMainTitlePaint;
				if (isShowTopSubTitle && StringUtil.isNotEmpty(mTopSubTitle)) {
					topMainTitlePaint = getPaint(R.color.black, 26);
				} else {
					topMainTitlePaint = getPaint(R.color.black, 36);
				}
				float topMainTitleSize = topMainTitlePaint
						.measureText(mTopMainTitle) / 2;
				float x = (mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight)
						/ 2 - topMainTitleSize;
				mTopCanvas.drawText(mTopMainTitle, x, HV_MARGIN_TOP / 2,
						topMainTitlePaint);
			}

			// 设置头部副标题
			if (isShowTopSubTitle) {
				if (StringUtil.isNotEmpty(mTopSubTitle)) {
					Paint topSubTitlePaint = getPaint(
							R.color.histogram_view_sub_title, 24);
					float topSubTitleSize = topSubTitlePaint
							.measureText(mTopSubTitle) / 2;
					float x = (mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight)
							/ 2 - topSubTitleSize;
					mTopCanvas.drawText(mTopSubTitle, x,
							HV_MARGIN_TOP / 2 + 25, topSubTitlePaint);
				}
			}

			// 绘制左边标题
			if (isShowLeftTitle) {
				if (StringUtil.isNotEmpty(mLeftTitleValue)) {
					Paint leftTitlePaint = getPaint(R.color.black, 22);
					float leftTitleSize = leftTitlePaint
							.measureText(mLeftTitleValue);
					Rect rect = new Rect(0, HV_MARGIN_LEFT,
							SCALE_DIVIDE_LINE_LENGTH, HV_MARGIN_LEFT);
					leftTitlePaint.getTextBounds(mLeftTitleValue, 0,
							mLeftTitleValue.length(), rect);
					Path path = new Path();
					int offsetHorizontal = rect.height();
					int offsetVertical = rect.width();
					offsetHorizontal = Math.max(offsetHorizontal, 20);
					offsetVertical = Math.max(offsetVertical, 20);
					int width = offsetHorizontal + rect.height();
					int startX = width / 2 - 10;
					path.moveTo(startX, mHistogramViewHeight / 2
							+ leftTitleSize / 2);
					path.cubicTo(startX, mHistogramViewHeight / 2
							+ leftTitleSize / 2, startX, mHistogramViewHeight
							/ 2 + leftTitleSize / 2, startX, 0);
					mYPivotCancas.drawTextOnPath(mLeftTitleValue, path, 0,
							rect.height(), leftTitlePaint);
				}
			}

			// 绘制右边刻度
			if (isShowRightSacle) {
				int ky = RIGHT_SCALE_MAX / RIGHT_SCALE_NUM;
				mRightSacleMargin = (mHistogramViewHeight - HV_MARGIN_BOTTOM - HV_MARGIN_TOP)
						/ RIGHT_SCALE_MAX;
				for (int j = 0; j <= RIGHT_SCALE_MAX; j += ky) {
					int y = mHistogramViewHeight - HV_MARGIN_BOTTOM
							- (int) (mRightSacleMargin * j);

					mRightScaleCanvas.drawLine(0, y, SCALE_DIVIDE_LINE_LENGTH,
							y, getPaint(R.color.black, GLOBAL_TEXT_SIZE));

					mRightScaleCanvas.drawText(String.valueOf(j) + "%",
							SCALE_DIVIDE_LINE_LENGTH, y + 8,
							getPaint(R.color.black, GLOBAL_TEXT_SIZE));
				}
			}

			// 绘制Y轴坐标
			Canvas horizontalGridLineCanvas = new Canvas();
			horizontalGridLineCanvas.setBitmap(mBitmapGridHori);
			int yScaleValue = Y_PIVOT_MAX_SCALE / Y_PIVOT_SCALE_NUM;// 算出刻度值
			yPivotScaleMargin = (mHistogramViewHeight - HV_MARGIN_TOP - HV_MARGIN_BOTTOM)
					/ Y_PIVOT_MAX_SCALE;
			for (int scale = 0; scale <= Y_PIVOT_MAX_SCALE; scale += yScaleValue) {
				int valueY = (int) (mHistogramViewHeight - HV_MARGIN_BOTTOM - (yPivotScaleMargin * scale));

				// 绘制Y轴刻度分隔线，即分隔每一个刻度值的红色短线
				mYPivotCancas.drawLine(HV_MARGIN_LEFT
						- SCALE_DIVIDE_LINE_LENGTH, valueY, HV_MARGIN_LEFT,
						valueY, getPaint(R.color.red, GLOBAL_TEXT_SIZE));

				// 绘制Y轴刻度值
				mYPivotCancas.drawText(String.valueOf(scale),
						HV_MARGIN_LEFT - 55, valueY + 10,
						getPaint(R.color.eagle_two, GLOBAL_TEXT_SIZE));

				// 绘制中间的横向网格线
				horizontalGridLineCanvas.drawLine(0, valueY - HV_MARGIN_BOTTOM,
						mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight,
						valueY - HV_MARGIN_BOTTOM, getGridPaint());
			}
			// 绘制最上面一根的横向网格线
			horizontalGridLineCanvas.drawLine(0, 0, mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight, 0, getGridPaint());

			// 绘制Y轴刻度线
			mYPivotCancas.drawLine(HV_MARGIN_LEFT, HV_MARGIN_BOTTOM,
					HV_MARGIN_LEFT, mHistogramViewHeight - HV_MARGIN_TOP,
					getPaint(R.color.black, GLOBAL_TEXT_SIZE));

			if (mHistogramEntityList != null && mHistogramEntityList.size() > 0) {
				int allxwhidth = mHistogramEntityList.size() * mGridWidth > mHistogramViewWidth ? mHistogramEntityList
						.size() * mGridWidth + mGridWidth
						: mHistogramViewWidth;
				int ch = mHistogramViewHeight - HV_MARGIN_BOTTOM
						- HV_MARGIN_TOP;

				mBitmapHistogram = Bitmap.createBitmap(allxwhidth, ch
						+ HV_MARGIN_BOTTOM, Bitmap.Config.ARGB_8888);
				Canvas histogramCanvas = new Canvas();
				histogramCanvas.setBitmap(mBitmapHistogram);

				for (int i = 0; i < mHistogramEntityList.size(); i++) {
					int stopX = (int) (ch - ch / Y_PIVOT_MAX_SCALE
							* mHistogramEntityList.get(i).getHistogramValue());

					// 绘制柱体
					float startX = i * mGridWidth + mGridWidth / 2;
					float startY = mHistogramViewHeight - HV_MARGIN_BOTTOM
							- HV_MARGIN_BOTTOM;
					float stopXValue = startX + mHistogramWidth;
					float stopY = stopX;

					mPointList
							.add(new Point(startX, stopXValue, startY, stopY));

					if (i < 3) {
						Log.i(TAG, "startX = " + startX + ", startY = "
								+ startY + ", stopXValue = " + stopXValue
								+ ", stopY = " + stopY);
					}

					histogramCanvas.drawLine(
							i * mGridWidth + mGridWidth / 2,
							mHistogramViewHeight - HV_MARGIN_BOTTOM
									- HV_MARGIN_BOTTOM,
							i * mGridWidth + mGridWidth / 2,
							stopX,
							getPaint(mHistogramEntityList.get(i)
									.getHistogramColor(), 20, mHistogramWidth));

					// 绘制纵向网格线，不包括最左边的Y轴刻度线
					histogramCanvas.drawLine(i * mGridWidth + mGridWidth,
							mHistogramViewHeight - HV_MARGIN_BOTTOM
									- HV_MARGIN_BOTTOM, i * mGridWidth
									+ mGridWidth, 0, getGridPaint());

					// 绘制柱体上的数值文字
					String histogramValue = String.format("%.2f",
							mHistogramEntityList.get(i).getHistogramValue());// 四舍五入，保留两位小数
					histogramCanvas.drawText(histogramValue, i * mGridWidth
							+ mGridWidth / 2 - 27, stopX - 2,
							getPaint(R.color.black, 22));

					// 绘制柱体名称
					String histogramName = mHistogramEntityList.get(i)
							.getHistogramName();
					if (StringUtil.isNotEmpty(histogramName)) {
						Paint paint = getPaint(R.color.black, GLOBAL_TEXT_SIZE);
						float textWidth = paint.measureText(histogramName);
						histogramCanvas.drawText(histogramName,
								(mGridWidth * i) + mGridWidth / 2 - textWidth
										/ 2, ch + 20, paint);
					}
				}

				if (isShowAverageLine) {
					/*
					 * 绘制平均值。"AVG:" + mAverageValue：需要绘制的文字；allxwhidth - xkedu -
					 * X：x轴上的偏移量，即向左或向右偏移；ch - ch / Y_PIVOT_MAX_SCALE *
					 * mAverageValue - X：y轴上的偏移量，即向上或向下偏移。
					 */
					histogramCanvas.drawText(
							mResources.getString(R.string.hv_average_value)
									+ mAverageValue,
							allxwhidth - mGridWidth - 10,
							ch - ch / Y_PIVOT_MAX_SCALE
									* Float.parseFloat(mAverageValue) - 5,
							getPaint(R.color.eagle_four, GLOBAL_TEXT_SIZE));
					// 绘制平均线
					histogramCanvas.drawLine(
							0,
							ch - ch / Y_PIVOT_MAX_SCALE
									* Float.parseFloat(mAverageValue),
							allxwhidth + mGridWidth,
							ch - ch / Y_PIVOT_MAX_SCALE
									* Float.parseFloat(mAverageValue),
							getPaint(R.color.eagle_four, 20));
				}

				// 绘制平均线上的点
				if (isShowAverageLine) {
					int avgcir = (int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
							* Float.parseFloat(mAverageValue));

					for (int j = 0; j < mHistogramEntityList.size(); j++) {
						histogramCanvas.drawCircle(mGridWidth * j + mGridWidth
								/ 2, avgcir, mAverageLineHeight + 3,
								getPaint(R.color.eagle_four, 20));
					}
				}
			}

			// 在Handler中刷新界面
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mIVShowGridPoti.setImageBitmap(mBitmapHistogram);
					if (mClickListener != null) {
						mIVShowGridPoti.setOnTouchListener(mTouchListener);
						mClickListener.setOnClickListener(mClickedHistogramId);
					}
				}
			});

		} catch (Exception e) {
			LogUtil.e(TAG, "Draw Histogram View Exception-->" + e.toString());
		}
	}

	/**
	 * 获取柱状图的部分画笔
	 * 
	 * @param paintColor
	 *            画笔的颜色
	 * @param textSize
	 *            需要设置的文字的大小
	 * @return Paint
	 */
	private Paint getPaint(int paintColor, int textSize) {
		Paint paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		paint.setColor(mResources.getColor(paintColor));
		paint.setTextSize(textSize);
		return paint;
	}

	/**
	 * 绘制柱体的画笔
	 * 
	 * @return Paint
	 */
	private Paint getPaint(int paintColor, int textSize, int strokeWidth) {
		Paint paint = getPaint(paintColor, textSize);
		paint.setStrokeWidth(strokeWidth);// 笔宽像素
		return paint;
	}

	/**
	 * 绘制柱状图网格的画笔
	 */
	private Paint getGridPaint() {
		Paint paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(mResources.getColor(R.color.histogram_view_grid_line));
		return paint;
	}

	/**
	 * 设置平均值大小
	 */
	public void setAverageValue(String averageValue) {
		this.mAverageValue = averageValue;
	}

	/**
	 * 设置柱状图顶部副标题的内容
	 */
	public void setTopSubTitleValue(String topSubTitleValue) {
		this.mTopSubTitle = topSubTitleValue;
	}

	/**
	 * 设置柱状图左边的标题内容
	 */
	public void setLeftTitleValue(String leftTitleValue) {
		this.mLeftTitleValue = leftTitleValue;
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float downX = event.getX();
			float downY = event.getY();
			Log.i(TAG,
					"event.getX() = " + downX + " ,event.getY() = "
							+ event.getY());
			int mClickedHistogramId = 0;
			for (int i = 0; i < mPointList.size(); i++) {
				Point point = mPointList.get(i);
				if (downX > point.startX && downX < point.stopX
						&& downY < point.startY && downY > point.stopY) {
					mClickedHistogramId = i;
					Log.i(TAG, "clickId = " + mClickedHistogramId);
					break;
				}
			}
			return false;
		}
	};

	private int mClickedHistogramId = -1;
	private List<Point> mPointList = new ArrayList<Point>();

	class Point {

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

	private OnClickListener mClickListener;

	public void setOnClickListener(OnClickListener onClickListener) {
		this.mClickListener = onClickListener;
	}

	public interface OnClickListener {
		public void setOnClickListener(int position);
	}

}
