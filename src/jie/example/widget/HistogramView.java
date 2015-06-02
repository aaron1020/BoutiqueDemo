package jie.example.widget;

import java.util.ArrayList;

import jie.example.boutique.R;
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
	/**
	 * y轴的最大刻度
	 */
	private static final int Y_PIVOT_MAX_SCALE = 100;
	/**
	 * y轴的刻度数量
	 */
	private static final int Y_PIVOT_SCALE_NUM = 5;
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
	private float mAverageValue = 0.0f;

	private int yrightnumber = 6, yMinR = 120;
	/**
	 * 宽度和高度
	 */
	private int widthimg = 0, heightimg = 0;
	/**
	 * 字体间距
	 */
	private int scaleLeng = 15;
	/**
	 * 是否点击事件
	 */
	private boolean zoomimg = true;
	private ArrayList<HistogramEntity> mHistogramEntityList;
	private Resources mResources;
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
	private Canvas mTopCanvas, mYPivotCancas, xcountcanvasright;
	/**
	 * 滑动控件
	 */
	private HorizontalScrollView countScroll;
	/**
	 * 是否加载右边刻度,默认不加载
	 */
	private boolean loadright = false;
	/**
	 * 字体大小
	 */
	private int mHistogramNameSize = 22;
	/**
	 * 图片到边缘间隙，左边默认100
	 */
	private int LBMarginleft = 100;
	/**
	 * 图片到边缘间隙，上面默认是100
	 */
	private int LBMarginTop = 100;
	/**
	 * 图片到边缘间隙，下面默认是100；
	 */
	private int LBMarginButton = 100;
	/**
	 * 图片到边缘间隙，右边 默认60
	 */
	private int LBMarginright = 60;
	/**
	 * 左边显示的图片
	 */
	private ImageView yleftimg;
	/**
	 * 柱体大小
	 */
	private int mHistogramSize = 50;
	/**
	 * Y轴坐标刻度大小
	 */
	private float YscaleSize;
	/**
	 * x轴每一格宽度
	 */
	private int xKedu = 100;

	/**
	 * 右边刻度
	 */
	private int YscaleSizeRight;
	public Bitmap bmp = Bitmap.createBitmap(1, 1, Config.RGB_565);

	private int addy = 0;

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
	 * @param isShowAverageLine
	 *            是否显示柱状图平均线：如果需要显示，则需另外setAverageValue(float)方法设置平均值的大小。
	 */
	public HistogramView(Context context, String topMainTitle,
			ArrayList<HistogramEntity> histogramEntityList,
			boolean isShowTopSubTitle, boolean isShowLeftTitle,
			boolean isShowAverageLine) {
		super(context);
		mResources = context.getResources();
		mTopMainTitle = topMainTitle;
		mHistogramEntityList = histogramEntityList;
		this.isShowTopSubTitle = isShowTopSubTitle;
		this.isShowLeftTitle = isShowLeftTitle;
		this.isShowAverageLine = isShowAverageLine;

		if (3 < histogramEntityList.size() && histogramEntityList.size() < 10) {
			this.xKedu = 200;
			this.mHistogramSize = 100;
		} else if (histogramEntityList.size() < 4) {
			this.xKedu = 350;
			this.mHistogramSize = 120;
		}

		initView(context);

		if (widthimg != 0 && mHandler != null) {
			drawimg();
		}
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {

		setBackgroundColor(Color.parseColor("#D1E9E9"));
		setOrientation(HORIZONTAL);

		yleftimg = new ImageView(context);
		yleftimg.setLayoutParams(new LayoutParams(LBMarginleft,
				LayoutParams.MATCH_PARENT));

		addView(yleftimg);
		countlayout = new LinearLayout(context);

		countlayout.setOrientation(VERTICAL);
		countTopimg = new ImageView(context);
		countTopimg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LBMarginTop));
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
		yright.setLayoutParams(new LayoutParams(LBMarginright,
				LayoutParams.MATCH_PARENT));
		addView(yright);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (widthimg == 0) {
			widthimg = this.getWidth();
			heightimg = this.getHeight();
			InitCoordinate();
		}
	}

	private void InitCoordinate() {
		if (countlayout != null) {
			try {
				countlayout.setLayoutParams(new LayoutParams(widthimg
						- LBMarginleft - LBMarginright,
						LayoutParams.MATCH_PARENT));
				yleftbitmap = Bitmap.createBitmap(LBMarginleft, heightimg,
						Bitmap.Config.ARGB_8888);
				yrightbitmap = Bitmap.createBitmap(LBMarginright, heightimg,
						Bitmap.Config.ARGB_8888);

				countTopbitmap = Bitmap.createBitmap(widthimg - LBMarginleft
						- LBMarginright, LBMarginTop, Bitmap.Config.ARGB_8888);
				counthorizntalbitmap = Bitmap.createBitmap(widthimg
						- LBMarginleft - LBMarginright, heightimg - LBMarginTop
						- LBMarginButton + addy, Bitmap.Config.ARGB_8888);

				mYPivotCancas = new Canvas();
				mYPivotCancas.setBitmap(yleftbitmap);
				yleftimg.setImageBitmap(yleftbitmap);

				mTopCanvas = new Canvas();
				mTopCanvas.setBitmap(countTopbitmap);
				countTopimg.setImageBitmap(countTopbitmap);

				xcountcanvasright = new Canvas();
				xcountcanvasright.setBitmap(yrightbitmap);
				yright.setImageBitmap(yrightbitmap);

				counthorizntalimg.setImageBitmap(counthorizntalbitmap);

				if (loadright) {
					// 加载右边刻度
					int ky = yMinR / yrightnumber;
					YscaleSizeRight = (heightimg - LBMarginButton - LBMarginTop + addy)
							/ yMinR;
					for (int j = 0; j <= yMinR; j += ky) {
						int y = heightimg - LBMarginButton
								- (int) (YscaleSizeRight * j) + addy;

						xcountcanvasright.drawLine(0, y, scaleLeng, y,
								getPaint(R.color.black, mHistogramNameSize));
						xcountcanvasright.drawText(String.valueOf(j) + "%",
								scaleLeng + 8, y + 8,
								getPaint(R.color.black, mHistogramNameSize));
					}
				}

				// 设置头部标题
				if (StringUtil.isNotEmpty(mTopMainTitle)) {
					Paint topMainTitlePaint;
					if (isShowTopSubTitle
							&& StringUtil.isNotEmpty(mTopSubTitle)) {
						topMainTitlePaint = getPaint(R.color.black, 26);
					} else {
						topMainTitlePaint = getPaint(R.color.black, 36);
					}
					float topMainTitleSize = topMainTitlePaint
							.measureText(mTopMainTitle) / 2;
					int num = (int) ((widthimg - LBMarginleft - LBMarginright) / 2 - topMainTitleSize);
					mTopCanvas.drawText(mTopMainTitle, num, LBMarginTop / 2,
							topMainTitlePaint);
				}

				// 设置头部副标题
				if (isShowTopSubTitle) {
					if (StringUtil.isNotEmpty(mTopSubTitle)) {
						Paint topSubTitlePaint = getPaint(
								R.color.histogram_view_sub_title, 24);
						float topSubTitleSize = topSubTitlePaint
								.measureText(mTopSubTitle) / 2;
						int num2 = (int) ((widthimg - LBMarginleft - LBMarginright) / 2 - topSubTitleSize);
						mTopCanvas.drawText(mTopSubTitle, num2,
								LBMarginTop / 2 + 25, topSubTitlePaint);
					}
				}

				// 绘制左边标题
				if (isShowLeftTitle) {
					if (StringUtil.isNotEmpty(mLeftTitleValue)) {
						Paint leftTitlePaint = getPaint(R.color.black, 22);
						float leftTitleSize = leftTitlePaint
								.measureText(mLeftTitleValue);
						Rect vt_TextRect = new Rect(0, LBMarginleft, scaleLeng,
								LBMarginleft);
						leftTitlePaint.getTextBounds(mLeftTitleValue, 0,
								mLeftTitleValue.length(), vt_TextRect);
						Path vt_TextPath = new Path();
						int offsetHorizontal = vt_TextRect.height();
						int offsetVertical = vt_TextRect.width();
						offsetHorizontal = Math.max(offsetHorizontal, 20);
						offsetVertical = Math.max(offsetVertical, 20);
						int vt_ViewWidth = offsetHorizontal
								+ vt_TextRect.height();
						int startX = vt_ViewWidth / 2 - 10;
						vt_TextPath.moveTo(startX, heightimg / 2
								+ leftTitleSize / 2);
						vt_TextPath.cubicTo(startX, heightimg / 2
								+ leftTitleSize / 2, startX, heightimg / 2
								+ leftTitleSize / 2, startX, 0);
						mYPivotCancas.drawTextOnPath(mLeftTitleValue,
								vt_TextPath, 0, vt_TextRect.height(),
								leftTitlePaint);
					}
				}

				// 绘制Y轴坐标
				Canvas horizontalGridLineCanvas = new Canvas();
				horizontalGridLineCanvas.setBitmap(counthorizntalbitmap);
				int yScaleValue = Y_PIVOT_MAX_SCALE / Y_PIVOT_SCALE_NUM;// 算出刻度值
				YscaleSize = (heightimg - LBMarginTop - LBMarginButton)
						/ Y_PIVOT_MAX_SCALE;
				for (int scale = 0; scale <= Y_PIVOT_MAX_SCALE; scale += yScaleValue) {
					int valueY = (int) (heightimg - LBMarginButton - (YscaleSize * scale))
							+ addy;

					// 绘制Y轴刻度分隔线，即分隔每一个刻度值的红色短线
					mYPivotCancas.drawLine(LBMarginleft - scaleLeng, valueY,
							LBMarginleft, valueY,
							getPaint(R.color.red, mHistogramNameSize));

					// 绘制Y轴刻度值
					mYPivotCancas.drawText(String.valueOf(scale),
							LBMarginleft - 55, valueY + 10,
							getPaint(R.color.eagle_two, mHistogramNameSize));

					// 绘制中间的横向网格线
					horizontalGridLineCanvas.drawLine(0, valueY
							- LBMarginButton, widthimg - LBMarginleft
							- LBMarginright, valueY - LBMarginButton,
							getGridPaint());
				}
				// 绘制最上面一根的横向网格线
				horizontalGridLineCanvas.drawLine(0, 0, widthimg - LBMarginleft
						- LBMarginright, 0, getGridPaint());

				// 绘制Y轴刻度线
				mYPivotCancas.drawLine(LBMarginleft, LBMarginButton,
						LBMarginleft, heightimg - LBMarginTop + addy,
						getPaint(R.color.black, mHistogramNameSize));

				if (widthimg != 0 && mHandler != null) {
					drawimg();
				}

			} catch (OutOfMemoryError e) {
				closeBitmap();
				e.printStackTrace();
			}
		}
	}

	Runnable myDraw1 = new Runnable() {

		@Override
		public void run() {
			drawimg();
		}
	};

	private void drawimg() {
		try {
			countvertical.setImageBitmap(null);
			if (icanvasbitmap != null) {
				icanvasbitmap.setXbit();
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (xbit != null) {
						countvertical.setImageBitmap(xbit);
						if (zoomimg) {
							countvertical
									.setOnTouchListener(new HistogramViewTouchListener());
						}
					}
				}
			});
		} catch (OutOfMemoryError e) {
			closeBitmap();
			e.printStackTrace();
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

	/*
	 * 透明背景
	 */
	private Paint getrectPaint() {
		Paint paint = new Paint();// 创建一个画笔
		paint.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		paint.setColor(mResources.getColor(R.color.completely_transparent));
		return paint;
	}

	private final class HistogramViewTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			}
			return true;
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

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	/**
	 * 绘制中间圆柱接口
	 */
	private ICanvasBitmap icanvasbitmap = new HistogramView.ICanvasBitmap() {
		@Override
		public void setXbit() {
			try {
				if (mHistogramEntityList != null
						&& mHistogramEntityList.size() > 0) {

					int w = getWidth();
					int h = getHeight();

					int xkedu = xKedu;
					int allxwhidth = mHistogramEntityList.size() * xkedu > w ? mHistogramEntityList
							.size() * xkedu + xkedu
							: w;
					int ch = h - LBMarginButton - LBMarginTop + addy;
					xbit = Bitmap.createBitmap(allxwhidth, ch + LBMarginButton,
							Bitmap.Config.ARGB_8888);
					Canvas xcanvas = new Canvas();
					xcanvas.drawRect(0, 0, allxwhidth, ch + LBMarginButton,
							getrectPaint());
					xcanvas.setBitmap(xbit);
					for (int i = 0; i < mHistogramEntityList.size(); i++) {
						int himg = (int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
								* mHistogramEntityList.get(i)
										.getHistogramValue());
						xcanvas.drawLine(
								i * xkedu + xkedu / 2,
								h - LBMarginButton - LBMarginButton + addy,
								i * xkedu + xkedu / 2,
								himg,
								getPaint(mHistogramEntityList.get(i)
										.getHistogramColor(), 20,
										mHistogramSize));

						// 绘制纵向网格线，不包括最左边的Y轴刻度线
						xcanvas.drawLine(i * xkedu + xkedu, h - LBMarginButton
								- LBMarginButton + addy, i * xkedu + xkedu, 0,
								getGridPaint());
						String yt = String.valueOf(mHistogramEntityList.get(i)
								.getHistogramValue());
						yt = yt.length() > 5 ? yt.substring(0, 5) : yt;
						xcanvas.drawText(yt, i * xkedu + xkedu / 2, himg,
								getPaint(R.color.black, 22));
						String key = mHistogramEntityList.get(i)
								.getHistogramName();
						Paint ttpaint = getPaint(R.color.black,
								mHistogramNameSize);
						float f = ttpaint.measureText(key);
						String key1 = null;
						String key2 = null;
						int numsiz = 7;
						if (mHistogramNameSize < 12) {
							numsiz = 5;
						}
						if (f > (xkedu - 20) && key.length() > numsiz) {
							key1 = key.substring(0, numsiz);
							key2 = key.substring(numsiz, key.length());
							float f1 = ttpaint.measureText(key1);
							float f2 = ttpaint.measureText(key2);
							float tw1 = xkedu / 2 - f1 / 2;
							float tw2 = xkedu / 2 - f2 / 2;
							xcanvas.drawText(key1, (xkedu * i) + tw1, ch + 15,
									ttpaint);
							float ff2 = ttpaint.measureText(key2);
							if (ff2 > (xkedu - 20) && key2.length() > numsiz) {
								String key11 = key2.substring(0, numsiz);
								String key21 = key2.substring(numsiz,
										key2.length());
								float f11 = ttpaint.measureText(key11);
								float f22 = ttpaint.measureText(key21);
								float tw11 = xkedu / 2 - f11 / 2;
								float tw22 = xkedu / 2 - f22 / 2;
								xcanvas.drawText(key11, (xkedu * i) + tw11,
										ch + 30, ttpaint);
								xcanvas.drawText(key21, (xkedu * i) + tw22,
										ch + 45, ttpaint);

							} else {
								xcanvas.drawText(key2, (xkedu * i) + tw2,
										ch + 30, ttpaint);
							}
						} else {
							float tw = xkedu / 2 - f / 2;
							xcanvas.drawText(key, (xkedu * i) + tw, ch + 20,
									ttpaint);
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
								allxwhidth - xkedu - 42,
								ch - ch / Y_PIVOT_MAX_SCALE * mAverageValue - 5,
								getPaint(R.color.eagle_four, mHistogramNameSize));
						// 绘制平均线
						xcanvas.drawLine(0,
								(int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
										* mAverageValue), allxwhidth + xkedu,
								(int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
										* mAverageValue),
								getPaint(R.color.eagle_four, 20));
					}

					// 绘制平均线上的点
					if (isShowAverageLine) {
						int avgcir = (int) ((ch) - ((ch) / Y_PIVOT_MAX_SCALE)
								* mAverageValue);

						for (int j = 0; j < mHistogramEntityList.size(); j++) {
							xcanvas.drawCircle(xkedu * j + xkedu / 2, avgcir,
									mAverageLineHeight + 3,
									getPaint(R.color.eagle_four, 20));
						}
					}
					HistogramView.this.xbit = xbit;
				}
			} catch (Exception e) {
				e.printStackTrace();
				closeBitmap();
			}
		}
	};

	/**
	 * 设置平均值大小
	 */
	public void setAverageValue(float averageValue) {
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

	public interface ICanvasBitmap {
		public void setXbit();
	}

}
