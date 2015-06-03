package jie.example.widget;

import java.util.ArrayList;

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
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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
	 * y轴的最大刻度
	 */
	private static final int Y_PIVOT_MAX_SCALE = 100;
	/**
	 * y轴的刻度数量
	 */
	private static final int Y_PIVOT_SCALE_NUM = 5;
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
	 * 柱状图的右边距
	 */
	private static final int HV_MARGIN_RIGHT = 70;
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的顶边距
	 */
	private static final int HV_MARGIN_TOP = 100;
	/**
	 * 柱状图(不包括Y轴刻度线和柱体名称)的底边距
	 */
	private static final int HV_MARGIN_BOTTOM = 101;
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
	 * 柱体的宽度
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
	 * 是否显示右侧刻度，默认不显示
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
	 * x轴每一格的宽度，也就是网格的宽度
	 */
	private int mGridWidth = 100;
	private Resources mResources;
	private Canvas mTopCanvas, mYPivotCancas, mRightScaleCanvas;
	private ArrayList<HistogramEntity> mHistogramEntityList;
	/**
	 * 字体间距
	 */
	private int scaleLeng = 15;
	/**
	 * 是否点击事件
	 */
	private boolean zoomimg = true;
	/**
	 * 中间控件layout
	 */
	private LinearLayout countlayout = null;
	/**
	 * 中间控件头部
	 */
	private ImageView countTopimg = null;
	/**
	 * 中间控件relative
	 */
	private RelativeLayout countrelative = null;
	/**
	 * 中间控件横向线
	 */
	private ImageView counthorizntalimg = null;
	/**
	 * 中间控件竖向线
	 */
	private ImageView countvertical = null;
	/**
	 * 右边显示的图片
	 */
	private ImageView yright = null;
	private Bitmap xbit, counthorizntalbitmap, countTopbitmap, yrightbitmap,
			yleftbitmap;
	/**
	 * 滑动控件
	 */
	private HorizontalScrollView countScroll;
	/**
	 * 左边显示的图片
	 */
	private ImageView yleftimg;
	/**
	 * Y轴坐标刻度大小
	 */
	private float YscaleSize;
	/**
	 * 右边刻度
	 */
	private int YscaleSizeRight;
	private int addy = 0;
	public Bitmap bmp = Bitmap.createBitmap(1, 1, Config.RGB_565);
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
	 *            是否显示右侧刻度，一般默认不显示(传入false)
	 */
	public HistogramView(Context context, String topMainTitle,
			ArrayList<HistogramEntity> histogramEntityList,
			boolean isShowTopSubTitle, boolean isShowLeftTitle,
			boolean isShowAverageLine, boolean isShowRightSacle) {
		super(context);
		mResources = context.getResources();
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

		initData(context);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	private void initData(Context context) {
		setBackgroundColor(Color.parseColor("#D1E9E9"));
		setOrientation(HORIZONTAL);

		yleftimg = new ImageView(context);
		yleftimg.setLayoutParams(new LayoutParams(HV_MARGIN_LEFT,
				LayoutParams.MATCH_PARENT));

		addView(yleftimg);
		countlayout = new LinearLayout(context);

		countlayout.setOrientation(VERTICAL);
		countTopimg = new ImageView(context);
		countTopimg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				HV_MARGIN_TOP));
		countlayout.addView(countTopimg);
		countrelative = new RelativeLayout(context);
		countrelative.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		counthorizntalimg = new ImageView(context);
		counthorizntalimg.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		counthorizntalimg.setBackgroundColor(Color.WHITE);
		countrelative.addView(counthorizntalimg);
		countScroll = new HorizontalScrollView(context);
		countScroll.setHorizontalScrollBarEnabled(true);
		countvertical = new ImageView(context);

		LayoutParams lps = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);

		lps.setMargins(0, 0, 0, 0);
		countvertical.setLayoutParams(lps);
		countScroll.setLayoutParams(lps);
		countvertical.setScaleType(ScaleType.MATRIX);

		countScroll.addView(countvertical);
		countrelative.addView(countScroll);
		countlayout.addView(countrelative);
		addView(countlayout);
		yright = new ImageView(context);
		yright.setLayoutParams(new LayoutParams(HV_MARGIN_RIGHT,
				LayoutParams.MATCH_PARENT));
		addView(yright);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHistogramViewWidth == 0) {
			mHistogramViewWidth = this.getWidth();
			mHistogramViewHeight = this.getHeight();
			drawHistogramView();
		}
	}

	private void drawHistogramView() {
		try {
			countlayout.setLayoutParams(new LayoutParams(mHistogramViewWidth
					- HV_MARGIN_LEFT - HV_MARGIN_RIGHT,
					LayoutParams.MATCH_PARENT));
			yleftbitmap = Bitmap.createBitmap(HV_MARGIN_LEFT,
					mHistogramViewHeight, Bitmap.Config.ARGB_8888);
			yrightbitmap = Bitmap.createBitmap(HV_MARGIN_RIGHT,
					mHistogramViewHeight, Bitmap.Config.ARGB_8888);

			countTopbitmap = Bitmap.createBitmap(mHistogramViewWidth
					- HV_MARGIN_LEFT - HV_MARGIN_RIGHT, HV_MARGIN_TOP,
					Bitmap.Config.ARGB_8888);
			counthorizntalbitmap = Bitmap.createBitmap(mHistogramViewWidth
					- HV_MARGIN_LEFT - HV_MARGIN_RIGHT, mHistogramViewHeight
					- HV_MARGIN_TOP - HV_MARGIN_BOTTOM + addy,
					Bitmap.Config.ARGB_8888);

			mYPivotCancas = new Canvas();
			mYPivotCancas.setBitmap(yleftbitmap);
			yleftimg.setImageBitmap(yleftbitmap);

			mTopCanvas = new Canvas();
			mTopCanvas.setBitmap(countTopbitmap);
			countTopimg.setImageBitmap(countTopbitmap);

			mRightScaleCanvas = new Canvas();
			mRightScaleCanvas.setBitmap(yrightbitmap);
			yright.setImageBitmap(yrightbitmap);

			counthorizntalimg.setImageBitmap(counthorizntalbitmap);

			if (isShowRightSacle) {
				// 加载右边刻度
				int ky = RIGHT_SCALE_MAX / RIGHT_SCALE_NUM;
				YscaleSizeRight = (mHistogramViewHeight - HV_MARGIN_BOTTOM
						- HV_MARGIN_TOP + addy)
						/ RIGHT_SCALE_MAX;
				for (int j = 0; j <= RIGHT_SCALE_MAX; j += ky) {
					int y = mHistogramViewHeight - HV_MARGIN_BOTTOM
							- (int) (YscaleSizeRight * j) + addy;

					mRightScaleCanvas.drawLine(0, y, scaleLeng, y,
							getPaint(R.color.black, GLOBAL_TEXT_SIZE));
					mRightScaleCanvas.drawText(String.valueOf(j) + "%",
							scaleLeng, y + 8,
							getPaint(R.color.black, GLOBAL_TEXT_SIZE));
				}
			}

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
				int num = (int) ((mHistogramViewWidth - HV_MARGIN_LEFT - HV_MARGIN_RIGHT) / 2 - topMainTitleSize);
				mTopCanvas.drawText(mTopMainTitle, num, HV_MARGIN_TOP / 2,
						topMainTitlePaint);
			}

			// 设置头部副标题
			if (isShowTopSubTitle) {
				if (StringUtil.isNotEmpty(mTopSubTitle)) {
					Paint topSubTitlePaint = getPaint(
							R.color.histogram_view_sub_title, 24);
					float topSubTitleSize = topSubTitlePaint
							.measureText(mTopSubTitle) / 2;
					int num2 = (int) ((mHistogramViewWidth - HV_MARGIN_LEFT - HV_MARGIN_RIGHT) / 2 - topSubTitleSize);
					mTopCanvas.drawText(mTopSubTitle, num2,
							HV_MARGIN_TOP / 2 + 25, topSubTitlePaint);
				}
			}

			// 绘制左边标题
			if (isShowLeftTitle) {
				if (StringUtil.isNotEmpty(mLeftTitleValue)) {
					Paint leftTitlePaint = getPaint(R.color.black, 22);
					float leftTitleSize = leftTitlePaint
							.measureText(mLeftTitleValue);
					Rect vt_TextRect = new Rect(0, HV_MARGIN_LEFT, scaleLeng,
							HV_MARGIN_LEFT);
					leftTitlePaint.getTextBounds(mLeftTitleValue, 0,
							mLeftTitleValue.length(), vt_TextRect);
					Path vt_TextPath = new Path();
					int offsetHorizontal = vt_TextRect.height();
					int offsetVertical = vt_TextRect.width();
					offsetHorizontal = Math.max(offsetHorizontal, 20);
					offsetVertical = Math.max(offsetVertical, 20);
					int vt_ViewWidth = offsetHorizontal + vt_TextRect.height();
					int startX = vt_ViewWidth / 2 - 10;
					vt_TextPath.moveTo(startX, mHistogramViewHeight / 2
							+ leftTitleSize / 2);
					vt_TextPath.cubicTo(startX, mHistogramViewHeight / 2
							+ leftTitleSize / 2, startX, mHistogramViewHeight
							/ 2 + leftTitleSize / 2, startX, 0);
					mYPivotCancas.drawTextOnPath(mLeftTitleValue, vt_TextPath,
							0, vt_TextRect.height(), leftTitlePaint);
				}
			}

			// 绘制Y轴坐标
			Canvas horizontalGridLineCanvas = new Canvas();
			horizontalGridLineCanvas.setBitmap(counthorizntalbitmap);
			int yScaleValue = Y_PIVOT_MAX_SCALE / Y_PIVOT_SCALE_NUM;// 算出刻度值
			YscaleSize = (mHistogramViewHeight - HV_MARGIN_TOP - HV_MARGIN_BOTTOM)
					/ Y_PIVOT_MAX_SCALE;
			for (int scale = 0; scale <= Y_PIVOT_MAX_SCALE; scale += yScaleValue) {
				int valueY = (int) (mHistogramViewHeight - HV_MARGIN_BOTTOM - (YscaleSize * scale))
						+ addy;

				// 绘制Y轴刻度分隔线，即分隔每一个刻度值的红色短线
				mYPivotCancas.drawLine(HV_MARGIN_LEFT - scaleLeng, valueY,
						HV_MARGIN_LEFT, valueY,
						getPaint(R.color.red, GLOBAL_TEXT_SIZE));

				// 绘制Y轴刻度值
				mYPivotCancas.drawText(String.valueOf(scale),
						HV_MARGIN_LEFT - 55, valueY + 10,
						getPaint(R.color.eagle_two, GLOBAL_TEXT_SIZE));

				// 绘制中间的横向网格线
				horizontalGridLineCanvas.drawLine(0, valueY - HV_MARGIN_BOTTOM,
						mHistogramViewWidth - HV_MARGIN_LEFT - HV_MARGIN_RIGHT,
						valueY - HV_MARGIN_BOTTOM, getGridPaint());
			}
			// 绘制最上面一根的横向网格线
			horizontalGridLineCanvas.drawLine(0, 0, mHistogramViewWidth
					- HV_MARGIN_LEFT - HV_MARGIN_RIGHT, 0, getGridPaint());

			// 绘制Y轴刻度线
			mYPivotCancas.drawLine(HV_MARGIN_LEFT, HV_MARGIN_BOTTOM,
					HV_MARGIN_LEFT,
					mHistogramViewHeight - HV_MARGIN_TOP + addy,
					getPaint(R.color.black, GLOBAL_TEXT_SIZE));

			if (mHistogramViewWidth != 0 && mHandler != null) {
				countvertical.setImageBitmap(null);
				if (mHistogramEntityList != null
						&& mHistogramEntityList.size() > 0) {
					int w = getWidth();
					int h = getHeight();
					int allxwhidth = mHistogramEntityList.size() * mGridWidth > w ? mHistogramEntityList
							.size() * mGridWidth + mGridWidth
							: w;
					int ch = h - HV_MARGIN_BOTTOM - HV_MARGIN_TOP + addy;
					xbit = Bitmap.createBitmap(allxwhidth, ch
							+ HV_MARGIN_BOTTOM, Bitmap.Config.ARGB_8888);
					Canvas xcanvas = new Canvas();
					xcanvas.drawRect(0, 0, allxwhidth, ch + HV_MARGIN_BOTTOM,
							getrectPaint());
					xcanvas.setBitmap(xbit);
					for (int i = 0; i < mHistogramEntityList.size(); i++) {
						int himg = (int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
								* mHistogramEntityList.get(i)
										.getHistogramValue());
						xcanvas.drawLine(
								i * mGridWidth + mGridWidth / 2,
								h - HV_MARGIN_BOTTOM - HV_MARGIN_BOTTOM + addy,
								i * mGridWidth + mGridWidth / 2,
								himg,
								getPaint(mHistogramEntityList.get(i)
										.getHistogramColor(), 20,
										mHistogramWidth));

						// 绘制纵向网格线，不包括最左边的Y轴刻度线
						xcanvas.drawLine(i * mGridWidth + mGridWidth, h
								- HV_MARGIN_BOTTOM - HV_MARGIN_BOTTOM + addy, i
								* mGridWidth + mGridWidth, 0, getGridPaint());

						// 绘制柱体上的数值文字
						String histogramValue = String
								.format("%.2f", mHistogramEntityList.get(i)
										.getHistogramValue());// 四舍五入，保留两位小数
						xcanvas.drawText(histogramValue, i * mGridWidth
								+ mGridWidth / 2 - 27, himg - 2,
								getPaint(R.color.black, 22));

						String histogramName = mHistogramEntityList.get(i)
								.getHistogramName();
						Paint paint = getPaint(R.color.black, GLOBAL_TEXT_SIZE);
						float f = paint.measureText(histogramName);
						String key1 = null;
						String key2 = null;
						int numsiz = 7;
						if (f > (mGridWidth - 20)
								&& histogramName.length() > numsiz) {
							key1 = histogramName.substring(0, numsiz);
							key2 = histogramName.substring(numsiz,
									histogramName.length());
							float f1 = paint.measureText(key1);
							float f2 = paint.measureText(key2);
							float tw1 = mGridWidth / 2 - f1 / 2;
							float tw2 = mGridWidth / 2 - f2 / 2;
							xcanvas.drawText(key1, (mGridWidth * i) + tw1,
									ch + 15, paint);
							float ff2 = paint.measureText(key2);
							if (ff2 > (mGridWidth - 20)
									&& key2.length() > numsiz) {
								String key11 = key2.substring(0, numsiz);
								String key21 = key2.substring(numsiz,
										key2.length());
								float f11 = paint.measureText(key11);
								float f22 = paint.measureText(key21);
								float tw11 = mGridWidth / 2 - f11 / 2;
								float tw22 = mGridWidth / 2 - f22 / 2;
								xcanvas.drawText(key11,
										(mGridWidth * i) + tw11, ch + 30, paint);
								xcanvas.drawText(key21,
										(mGridWidth * i) + tw22, ch + 45, paint);

							} else {
								xcanvas.drawText(key2, (mGridWidth * i) + tw2,
										ch + 30, paint);
							}
						} else {
							float tw = mGridWidth / 2 - f / 2;
							xcanvas.drawText(histogramName, (mGridWidth * i)
									+ tw, ch + 20, paint);
						}
					}

					if (isShowAverageLine) {
						/*
						 * 绘制平均值。"AVG:" + mAverageValue：需要绘制的文字；allxwhidth -
						 * xkedu - X：x轴上的偏移量，即向左或向右偏移；ch - ch /
						 * Y_PIVOT_MAX_SCALE * mAverageValue -
						 * X：y轴上的偏移量，即向上或向下偏移。
						 */
						xcanvas.drawText(
								mResources.getString(R.string.hv_average_value)
										+ mAverageValue,
								allxwhidth - mGridWidth - 10,
								ch - ch / Y_PIVOT_MAX_SCALE
										* Float.parseFloat(mAverageValue) - 5,
								getPaint(R.color.eagle_four, GLOBAL_TEXT_SIZE));
						// 绘制平均线
						xcanvas.drawLine(
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
							xcanvas.drawCircle(mGridWidth * j + mGridWidth / 2,
									avgcir, mAverageLineHeight + 3,
									getPaint(R.color.eagle_four, 20));
						}
					}
					HistogramView.this.xbit = xbit;
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (xbit != null) {
							countvertical.setImageBitmap(xbit);
							if (zoomimg) {
							}
						}
					}
				});

			}

		} catch (Exception e) {
			closeBitmap();
			LogUtil.e(TAG, "Draw Histogram View Exception-->" + e.toString());
		}
	}

	private void closeBitmap() {
		if (yright != null) {
			yright.setImageBitmap(bmp);
			yright.setImageBitmap(null);
			yright = null;
		}

		if (yleftimg != null) {
			yleftimg.setImageBitmap(bmp);
			yleftimg.setImageBitmap(null);
			yleftimg = null;
		}

		if (countTopimg != null) {
			countTopimg.setImageBitmap(bmp);
			countTopimg.setImageBitmap(null);
			countTopimg = null;
		}

		if (counthorizntalimg != null) {
			counthorizntalimg.setImageBitmap(bmp);
			counthorizntalimg.setImageBitmap(null);
			counthorizntalimg = null;
		}

		if (countvertical != null) {
			countvertical.setImageBitmap(bmp);
			countvertical.setImageBitmap(null);
			countvertical = null;
		}

		invalidate();

		if (xbit != null && !xbit.isRecycled()) {
			xbit.recycle();
			xbit = null;
		} else {
			xbit = null;
		}

		if (counthorizntalbitmap != null && !counthorizntalbitmap.isRecycled()) {
			counthorizntalbitmap.recycle();
			counthorizntalbitmap = null;
		} else {
			counthorizntalbitmap = null;
		}

		if (countTopbitmap != null && !countTopbitmap.isRecycled()) {
			countTopbitmap.recycle();
			countTopbitmap = null;
		} else {
			countTopbitmap = null;
		}

		if (yrightbitmap != null && !yrightbitmap.isRecycled()) {
			yrightbitmap.recycle();
			yrightbitmap = null;
		} else {
			yrightbitmap = null;
		}

		if (yleftbitmap != null && !yleftbitmap.isRecycled()) {
			yleftbitmap.recycle();
			yleftbitmap = null;
		} else {
			yleftbitmap = null;
		}

		if (xbit != null && !xbit.isRecycled()) {
			xbit.recycle();
			xbit = null;
		}
		System.gc();
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

	/*
	 * 透明背景
	 */
	private Paint getrectPaint() {
		Paint paint = new Paint();// 创建一个画笔
		paint.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		paint.setColor(mResources.getColor(R.color.completely_transparent));
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

}
