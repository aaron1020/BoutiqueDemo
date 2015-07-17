package jie.example.widget;

import java.util.ArrayList;
import java.util.List;

import jie.example.boutique.R;
import jie.example.entity.HistogramEntity;
import jie.example.utils.LogUtil;
import jie.example.utils.StringUtil;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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
@SuppressLint("HandlerLeak")
public class HistogramView extends LinearLayout {
	private static final String TAG = HistogramView.class.getSimpleName();
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
	private static final int PAINT_TEXT_SIZE = 22;
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
	 * Y轴的高度
	 */
	private int mYPivotHeight;
	/**
	 * Y轴所在布局的高度
	 */
	private int mHorizontalLayoutHeight;
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
	 * 平均值大小
	 */
	private String mAverageValue = "0.00";
	/**
	 * 是否显示平均线
	 */
	private boolean isShowAverageLine;
	/**
	 * 是否显示折线图
	 */
	private boolean isShowBrokenLine;
	/**
	 * mHistogramViewWidth：HistogramView的宽度；mHistogramViewHeight：
	 * HistogramView的高度
	 */
	private int mHistogramViewWidth, mHistogramViewHeight;
	/**
	 * 柱状图的右边距
	 */
	private int mHvMarginRight = 30;
	/**
	 * 是否显示右侧刻度线，默认不显示
	 */
	private boolean isShowRightSacle;
	/**
	 * 右侧刻度的数量
	 */
	private int mRightScaleNum;
	/**
	 * 右侧刻度的最大刻度值
	 */
	private int mRightScaleMaX;
	/**
	 * 右侧刻度线的刻度之前的间隔
	 */
	private int mRightSacleMargin;
	/**
	 * x轴每一格的宽度，也就是网格的宽度，默认是100
	 */
	private int mGridWidth = 100;
	/**
	 * 柱体的宽度，默认是50
	 */
	private int mHistogramWidth = 50;
	/**
	 * 保存点击选中的柱子的Id
	 */
	private int mClickedHistogramId = -1;
	private float mDownX = 0.0f, mDownY = 0.0f;
	private Resources mResources;
	private Canvas mTopCanvas, mLeftCancas, mRightScaleCanvas;
	private Bitmap mBitmapTopTitle, mBitmapLeftPart, mBitmapRightPart,
			mBitmapGridHori, mBitmapHistogram;
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
	private LinearLayout mHorizontalLayout;
	private RelativeLayout histogramScrollContainer;
	private HorizontalScrollView mHoriScroll;
	private ArrayList<HistogramEntity> mHistogramEntityList;
	private HistogramViewClick mHistogramViewClick;
	/**
	 * 保存柱子的坐标范围
	 */
	private List<Point> mPointList = new ArrayList<Point>();
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (mHistogramViewClick != null) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mDownX = event.getX();
					mDownY = event.getY();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 手指按下和松开是同一个地方，即是点击事件
					if ((mDownX == event.getX()) && (mDownY == event.getY())) {

						// 判断坐标的范围落下哪个柱子上
						for (int i = 0; i < mPointList.size(); i++) {
							Point point = mPointList.get(i);
							if (mDownX > point.startX && mDownX < point.stopX
									&& mDownY < point.startY
									&& mDownY > point.stopY) {
								mClickedHistogramId = i;
								LogUtil.i(TAG, "clickId = "
										+ mClickedHistogramId);
								mHistogramViewClick.setHistogramViewListener(
										mClickedHistogramId,
										mHistogramEntityList
												.get(mClickedHistogramId));
								return true;
							}
						}

					}
				}
			}
			return true;// 若不返回true，只会响应第一个动作(MotionEvent.ACTION_DOWN)事件
		}
	};

	/**
	 * @param context
	 *            上下文对象
	 * @param topMainTitle
	 *            柱状图顶部标题值
	 * @param histogramEntityList
	 *            柱体属性实体的集合
	 * @param yPivotMaxScale
	 *            Y轴的最大刻度
	 * @param yPivotScaleNum
	 *            Y轴的刻度数量
	 * @param isShowTopSubTitle
	 *            是否显示柱状图顶部副标题：如果需要显示，则需另外setTopSubTitleValue(String)方法设置副标题的值。
	 * @param isShowLeftTitle
	 *            是否显示柱状图左边标题：如果需要显示，则需另外setLeftTitleValue(String)方法设置标题的值。
	 * @param isShowRightSacle
	 *            是否显示右侧刻度：如果需要显示，则需另外setRightScaleMaX(int)和setRightScaleNum(int
	 *            )方法，分别设置右边刻度的最大值和数量。
	 * @param isShowBrokenLine
	 *            是否显示拆线图
	 */
	public HistogramView(Context context, String topMainTitle,
			ArrayList<HistogramEntity> histogramEntityList, int yPivotMaxScale,
			int yPivotScaleNum, boolean isShowTopSubTitle,
			boolean isShowLeftTitle, boolean isShowAverageLine,
			boolean isShowRightSacle, boolean isShowBrokenLine) {
		super(context);
		mTopMainTitle = topMainTitle;
		mHistogramEntityList = histogramEntityList;
		mYPivotMaxScale = yPivotMaxScale;
		mYPivotScaleNum = yPivotScaleNum;
		this.isShowTopSubTitle = isShowTopSubTitle;
		this.isShowLeftTitle = isShowLeftTitle;
		this.isShowAverageLine = isShowAverageLine;
		this.isShowRightSacle = isShowRightSacle;
		this.isShowBrokenLine = isShowBrokenLine;
		initView(context);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HistogramView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// read values from attribute
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.HistogramView);
		try {
			mYPivotMaxScale = typedArray.getInteger(
					R.styleable.HistogramView_yPivotMaxScale, 0);
			mYPivotScaleNum = typedArray.getInteger(
					R.styleable.HistogramView_yPivotScaleNum, 0);
			mTopMainTitle = typedArray
					.getString(R.styleable.HistogramView_topMainTitle);
			mTopSubTitle = typedArray
					.getString(R.styleable.HistogramView_topSubTitle);
			isShowTopSubTitle = typedArray.getBoolean(
					R.styleable.HistogramView_showTopSubTitle, false);
			mLeftTitleValue = typedArray
					.getString(R.styleable.HistogramView_leftTitle);
			isShowLeftTitle = typedArray.getBoolean(
					R.styleable.HistogramView_showLeftTitle, false);
			isShowAverageLine = typedArray.getBoolean(
					R.styleable.HistogramView_showAverageLine, false);
			isShowRightSacle = typedArray.getBoolean(
					R.styleable.HistogramView_showRightScale, false);
			isShowBrokenLine = typedArray.getBoolean(
					R.styleable.HistogramView_showBrokenLine, false);
			mRightScaleMaX = typedArray.getInteger(
					R.styleable.HistogramView_rightMaxScale, 0);
			mRightScaleNum = typedArray.getInteger(
					R.styleable.HistogramView_rightScaleNum, 0);

		} catch (Exception e) {
			LogUtil.e(TAG,
					"Read Values From Attribute Exception-->" + e.toString());
		} finally {
			if (typedArray != null) {
				typedArray.recycle();
			}
		}

		initView(context);
	}

	private void initView(Context context) {
		try {
			mResources = context.getResources();
			setOrientation(VERTICAL);
			setBackgroundColor(mResources.getColor(R.color.hv_container_bg));

			if (isShowRightSacle) {
				mHvMarginRight = 70;
			}

			mIVShowTopTitle = new ImageView(context);
			mIVShowTopTitle.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, HV_MARGIN_TOP));
			addView(mIVShowTopTitle);

			mHorizontalLayout = new LinearLayout(context);
			mHorizontalLayout.setOrientation(HORIZONTAL);

			mIVShowLeftPart = new ImageView(context);
			mIVShowLeftPart.setLayoutParams(new LayoutParams(HV_MARGIN_LEFT,
					LayoutParams.MATCH_PARENT));
			mHorizontalLayout.addView(mIVShowLeftPart);

			histogramScrollContainer = new RelativeLayout(context);

			mIVShowGridHori = new ImageView(context);
			mIVShowGridHori.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mIVShowGridHori.setBackgroundColor(Color.WHITE);// 设置网格的背景为白色
			histogramScrollContainer.addView(mIVShowGridHori);

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT);

			mIVShowGridPoti = new ImageView(context);
			mIVShowGridPoti.setLayoutParams(params);
			mIVShowGridPoti.setScaleType(ScaleType.MATRIX);

			// 定义一个水平滚动控件用于包裹各个柱子
			mHoriScroll = new HorizontalScrollView(context);
			mHoriScroll.setHorizontalScrollBarEnabled(true);
			mHoriScroll.setLayoutParams(params);
			mHoriScroll.addView(mIVShowGridPoti);// 把mIVShowGridPoti加入水平滚动控件中

			histogramScrollContainer.addView(mHoriScroll);// 把水平滚动控件加入histogramScrollContainer中
			mHorizontalLayout.addView(histogramScrollContainer);

			mIVShowRightPart = new ImageView(context);
			mIVShowRightPart.setLayoutParams(new LayoutParams(mHvMarginRight,
					LayoutParams.MATCH_PARENT));
			mHorizontalLayout.addView(mIVShowRightPart);

			addView(mHorizontalLayout);
		} catch (Exception e) {
			LogUtil.e(TAG, "Init HistogramView Exception-->" + e.toString());
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		LogUtil.i(TAG, "HistogramView-->dispatchDraw");
		if (mHistogramViewWidth == 0) {
			mHistogramViewWidth = this.getWidth();
			mHistogramViewHeight = this.getHeight();
			LogUtil.i(TAG, "mHistogramViewWidth = " + mHistogramViewWidth
					+ ", mHistogramViewHeight = " + mHistogramViewHeight);
			mHorizontalLayoutHeight = mHistogramViewHeight - HV_MARGIN_TOP;
			mYPivotHeight = mHorizontalLayoutHeight - HV_MARGIN_BOTTOM;
			mHorizontalLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, mHorizontalLayoutHeight));
			histogramScrollContainer.setLayoutParams(new LayoutParams(
					mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight,
					mHorizontalLayoutHeight));
			drawHistogramView();
		}
	}

	private void drawHistogramView() {
		try {
			// 绘制头部标题
			if (StringUtil.isNotEmpty(mTopMainTitle)) {
				mBitmapTopTitle = Bitmap.createBitmap(mHistogramViewWidth,
						HV_MARGIN_TOP, Bitmap.Config.ARGB_8888);
				mIVShowTopTitle.setImageBitmap(mBitmapTopTitle);
				mTopCanvas = new Canvas();
				mTopCanvas.setBitmap(mBitmapTopTitle);
				Paint topTitlePaint = getPaint();

				// 主标题
				topTitlePaint.setTextSize(26.0f);
				if (!(isShowTopSubTitle && StringUtil.isNotEmpty(mTopSubTitle))) {
					topTitlePaint.setTextSize(36.0f);
				}
				float textWidth = topTitlePaint.measureText(mTopMainTitle);// 测量出标题的宽度
				float x = (mHistogramViewWidth - textWidth) / 2;
				mTopCanvas.drawText(mTopMainTitle, x, HV_MARGIN_TOP / 2,
						topTitlePaint);

				// 副标题
				if (isShowTopSubTitle) {
					if (StringUtil.isNotEmpty(mTopSubTitle)) {
						topTitlePaint
								.setColor(mResources.getColor(R.color.red));
						topTitlePaint.setTextSize(24.0f);
						textWidth = topTitlePaint.measureText(mTopSubTitle);
						x = (mHistogramViewWidth - textWidth) / 2;
						mTopCanvas.drawText(mTopSubTitle, x,
								HV_MARGIN_TOP / 2 + 26, topTitlePaint);
					}
				}
			}

			// 绘制左边标题
			mBitmapLeftPart = Bitmap.createBitmap(HV_MARGIN_LEFT,
					mHorizontalLayoutHeight, Bitmap.Config.ARGB_8888);
			mIVShowLeftPart.setImageBitmap(mBitmapLeftPart);
			mLeftCancas = new Canvas();
			mLeftCancas.setBitmap(mBitmapLeftPart);
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
					float y = (mYPivotHeight + leftTitleSize) / 2;
					path.moveTo(startX, y);
					path.cubicTo(startX, y, startX, y, startX, 0);

					mLeftCancas.drawTextOnPath(mLeftTitleValue, path, 0,
							rect.height(), leftTitlePaint);
				}
			}

			// 绘制白色背景
			mBitmapGridHori = Bitmap.createBitmap(mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight, mHorizontalLayoutHeight
					- HV_MARGIN_BOTTOM, Bitmap.Config.ARGB_8888);
			mIVShowGridHori.setImageBitmap(mBitmapGridHori);

			// 绘制Y轴刻度
			Canvas horizontalGridLineCanvas = new Canvas();
			horizontalGridLineCanvas.setBitmap(mBitmapGridHori);
			int yScaleValue = mYPivotMaxScale / mYPivotScaleNum;// 算出刻度值
			mYPivotScalesMargin = mYPivotHeight / mYPivotMaxScale;
			Paint paint = getPaint(R.color.eagle_two, PAINT_TEXT_SIZE);
			for (int scale = 0; scale <= mYPivotMaxScale; scale += yScaleValue) {

				// 减一是为了让最下面一根的横向网格线显示并与零刻度对齐(①Ⅰ)
				float startY = mYPivotHeight - scale * mYPivotScalesMargin - 1;
				float stopY = startY;

				// 绘制Y轴刻度分隔线，即分隔每一个刻度值的红色短线
				mLeftCancas.drawLine(HV_MARGIN_LEFT - SCALE_DIVIDE_LINE_LENGTH,
						startY, HV_MARGIN_LEFT, stopY,
						getPaint(R.color.red, PAINT_TEXT_SIZE));

				// 绘制Y轴刻度值
				String scaleValue = String.valueOf(scale);
				float textWidth = paint.measureText(scaleValue) + 2;
				mLeftCancas.drawText(scaleValue, HV_MARGIN_LEFT
						- SCALE_DIVIDE_LINE_LENGTH - textWidth, startY + 8,
						paint);

				// 绘制中间的横向网格线
				horizontalGridLineCanvas.drawLine(0, startY,
						mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight,
						stopY, getGridPaint());
			}
			// 绘制最上面一根的横向网格线
			horizontalGridLineCanvas.drawLine(0, 0, mHistogramViewWidth
					- HV_MARGIN_LEFT - mHvMarginRight, 0, getGridPaint());
			// 绘制最下面一根的横向网格线
			horizontalGridLineCanvas.drawLine(0, mYPivotHeight,
					mHistogramViewWidth - HV_MARGIN_LEFT - mHvMarginRight,
					mYPivotHeight, getGridPaint());

			// 绘制Y轴刻度线
			mLeftCancas.drawLine(HV_MARGIN_LEFT, 0, HV_MARGIN_LEFT,
					mYPivotHeight, getPaint());

			drawHistogram();

			// 绘制右边刻度线
			mBitmapRightPart = Bitmap.createBitmap(mHvMarginRight,
					mHorizontalLayoutHeight, Bitmap.Config.ARGB_8888);
			mIVShowRightPart.setImageBitmap(mBitmapRightPart);
			mRightScaleCanvas = new Canvas();
			mRightScaleCanvas.setBitmap(mBitmapRightPart);
			mRightScaleCanvas.drawLine(0, 0, 0, mYPivotHeight, getPaint());

			// 绘制右边刻度
			if (isShowRightSacle) {
				int scaleValue = mRightScaleMaX / mRightScaleNum;
				mRightSacleMargin = mYPivotHeight / mRightScaleMaX;
				for (int j = 0; j <= mRightScaleMaX; j += scaleValue) {
					// 减一是为让该侧的零刻度同最下面一根的横向网格线对齐
					float startY = mYPivotHeight - mRightSacleMargin * j - 1;

					mRightScaleCanvas.drawLine(0, startY,
							SCALE_DIVIDE_LINE_LENGTH, startY,
							getPaint(R.color.black, PAINT_TEXT_SIZE));

					mRightScaleCanvas.drawText(String.valueOf(j) + "%",
							SCALE_DIVIDE_LINE_LENGTH, startY + 8,
							getPaint(R.color.black, PAINT_TEXT_SIZE));
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "Draw HistogramView Exception -->" + e.toString());
		}
	}

	/**
	 * 绘制各个柱子
	 */
	private void drawHistogram() {
		try {
			if (mHistogramEntityList != null && mHistogramEntityList.size() > 0) {

				int listSize = mHistogramEntityList.size();
				if (listSize > 3 && mHistogramEntityList.size() < 10) {
					this.mGridWidth = 200;
					this.mHistogramWidth = 100;
				} else if (listSize < 4) {
					this.mGridWidth = 350;
					this.mHistogramWidth = 120;
				}

				int xPivotLen = listSize * mGridWidth > mHistogramViewWidth ? listSize
						* mGridWidth + mGridWidth
						: mHistogramViewWidth;
				mBitmapHistogram = Bitmap.createBitmap(xPivotLen,
						mHorizontalLayoutHeight, Bitmap.Config.ARGB_8888);
				Canvas histogramCanvas = new Canvas();
				histogramCanvas.setBitmap(mBitmapHistogram);
				mPointList.clear();

				Path path = new Path();
				boolean moveTo = true;
				for (int i = 0; i < listSize; i++) {
					// 绘制柱体;(mYPivotHeight / mYPivotMaxScale)为单位刻度值占有多少的高度
					float stopY = (mYPivotHeight - mYPivotHeight
							/ mYPivotMaxScale
							* mHistogramEntityList.get(i).getHistogramValue()) - 1;// 减1是因为(①Ⅰ)处减了1
					float xValue = i * mGridWidth + mGridWidth / 2;
					Paint paint = getPaint(mHistogramEntityList.get(i)
							.getHistogramColor(), 20, mHistogramWidth);
					histogramCanvas.drawLine(xValue, mYPivotHeight, xValue,
							stopY, paint);

					// 保存柱体所在的坐标范围，为了方便点击，我们扩大了柱体所在的X坐标范围至网格所在的X坐标范围。
					float startX = i * mGridWidth;
					float stopX = startX + mGridWidth;// 再加一个宽度
					mPointList.add(new Point(startX, stopX, mYPivotHeight,
							stopY));

					if (isShowBrokenLine) {
						// 绘制折线上的转折点
						histogramCanvas.drawCircle(xValue, stopY, 3,
								getPaint(R.color.hv_broken_line, 20));
						// 设置折线路径走向
						if (moveTo) {
							path.moveTo(xValue, stopY);
							moveTo = false;
						} else {
							path.lineTo(xValue, stopY);
						}
					}

					// 绘制纵向网格线，不包括最左边的Y轴刻度线
					histogramCanvas.drawLine(stopX, mYPivotHeight, stopX, 0,
							getGridPaint());

					// 绘制柱体上的数值文字
					String histogramValue = String.format("%.2f",
							mHistogramEntityList.get(i).getHistogramValue());// 四舍五入，保留两位小数
					paint = getPaint(R.color.black, 22);
					float textWidth = paint.measureText(histogramValue);
					histogramCanvas.drawText(histogramValue, startX
							+ (mGridWidth - textWidth) / 2, stopY - 3, paint);

					// 绘制柱体名称
					String histogramName = mHistogramEntityList.get(i)
							.getHistogramName();
					if (StringUtil.isNotEmpty(histogramName)) {
						paint = getPaint(R.color.black, PAINT_TEXT_SIZE);
						textWidth = paint.measureText(histogramName);
						histogramCanvas.drawText(histogramName, startX
								+ (mGridWidth - textWidth) / 2,
								mYPivotHeight + 20, paint);
					}

					// 绘制平均线上的点
					if (isShowAverageLine) {
						float cy = mYPivotHeight - mYPivotHeight
								/ mYPivotMaxScale
								* Float.parseFloat(mAverageValue);
						histogramCanvas.drawCircle(xValue, cy - 1, 3,
								getPaint(R.color.eagle_four, 20));// 减1是因为(①Ⅰ)处减了1
					}
				}

				if (isShowBrokenLine) {
					// 绘制折线图
					Paint paint = getPaint();
					paint.setStyle(Paint.Style.STROKE);
					paint.setColor(mResources.getColor(R.color.hv_broken_line));
					paint.setStrokeWidth(2);
					histogramCanvas.drawPath(path, paint);
				}

				if (isShowAverageLine) {
					// 绘制平均线
					float stopY = mYPivotHeight - mYPivotHeight
							/ mYPivotMaxScale * Float.parseFloat(mAverageValue);
					histogramCanvas.drawLine(0, stopY - 1, xPivotLen
							+ mGridWidth, stopY - 1,
							getPaint(R.color.eagle_four, 20));// 减1是因为(①Ⅰ)处减了1

					// 绘制平均值
					Paint paint = getPaint(R.color.eagle_four, PAINT_TEXT_SIZE);
					String averageValue = mResources
							.getString(R.string.hv_average_value)
							+ mAverageValue;
					float textWidth = paint.measureText(averageValue) + 10;
					histogramCanvas.drawText(averageValue, xPivotLen
							- textWidth, stopY - 5, paint);
				}

				// 在Handler中刷新界面
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mIVShowGridPoti.setImageBitmap(mBitmapHistogram);
						mIVShowGridPoti.setOnTouchListener(mTouchListener);
					}
				});

			}
		} catch (Exception e) {
			LogUtil.e(TAG, "Draw Histograms Exception -->" + e.toString());
		}
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
	 * 获取柱状图默认的画笔对象：默认颜色为黑色
	 * 
	 * @return Paint
	 */
	private Paint getPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 抗(不显示)锯齿，让绘出来的物体更清晰
		paint.setColor(mResources.getColor(R.color.black));// 设置画笔的颜色
		return paint;
	}

	private Paint getPaint(int paintColor, int textSize) {
		Paint paint = getPaint();
		paint.setColor(mResources.getColor(paintColor));// 设置画笔的颜色
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

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogUtil.i(TAG, "HistogramView-->onDetachedFromWindow");
		releaseResource(mBitmapTopTitle, mTopCanvas);
		releaseResource(mBitmapLeftPart, mLeftCancas);
		releaseResource(mBitmapRightPart, mRightScaleCanvas);
		releaseResource(mBitmapGridHori, null);
		releaseResource(mBitmapHistogram, null);
	}

	private void releaseResource(Bitmap bitmap, Canvas canvas) {
		if (bitmap != null && !bitmap.isRecycled()) {
			LogUtil.i(TAG, "HistogramView-->bitmap is recycled");
			canvas = null;
			bitmap.recycle();
		}
	}

	public void setHistogramEntityList(
			ArrayList<HistogramEntity> histogramEntityList) {
		this.mHistogramEntityList = histogramEntityList;
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

	/**
	 * 设置平均值大小
	 */
	public void setAverageValue(String averageValue) {
		this.mAverageValue = averageValue;
	}

	/**
	 * 设置右边刻度的最大值
	 */
	public void setRightScaleMaX(int mRightScaleMaX) {
		this.mRightScaleMaX = mRightScaleMaX;
	}

	/**
	 * 设置右边刻度的数量
	 */
	public void setRightScaleNum(int mRightScaleNum) {
		this.mRightScaleNum = mRightScaleNum;
	}

	public void setHistogramViewClick(HistogramViewClick histogramViewClick) {
		mHistogramViewClick = histogramViewClick;
	}

	/**
	 * 当数据改变时，调用此方法刷新柱状图
	 */
	public void refreshHistogramView() {
		drawHistogram();
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
