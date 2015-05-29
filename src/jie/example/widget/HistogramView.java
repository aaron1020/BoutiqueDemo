package jie.example.widget;

import java.util.ArrayList;

import jie.example.boutique.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

/**
 * 绘制柱状图
 */
public class HistogramView extends LinearLayout implements ChartImplements {

	/**
	 * 设置圆柱颜色
	 */
	private String cylidercolor;
	private boolean iscylidercolor = false;
	/**
	 * 是否显示平均线
	 */
	private boolean isShowAverageLine = true;
	/**
	 * 平均线高度
	 */
	private int mAverageLineHeight = 2;
	/**
	 * 平均线颜色
	 */
	private String mAverageLineColor = null;

	private int yrightnumber = 6, yMinR = 120;
	/**
	 * 宽度和高度
	 */
	private int widthimg = 0, heightimg = 0;
	/**
	 * X轴
	 */
	private Bitmap xbit, counthorizntalbitmap, countTopbitmap, yrightbitmap,
			yleftbitmap;
	private Canvas ycanvasleft;
	/**
	 * 是否设置左边标题
	 */
	private boolean islefttitle = true;
	/**
	 * 字体间距
	 */
	int scaleLeng = 15;
	/**
	 * 是否点击事件
	 */
	private boolean zoomimg = false;
	private ArrayList<HistogramEntity> listid = null;
	/**
	 * 圆柱属性
	 */
	private ArrayList<PillarVO> pillars = new ArrayList<PillarVO>();

	/**
	 * 中間控件layout
	 */
	private LinearLayout countlayout = null;
	/**
	 * 中間控件頭部
	 */
	private ImageView countTopimg = null;
	/**
	 * 中間控件relative
	 */
	private RelativeLayout countrelative = null;
	/**
	 * 中間控件橫向線
	 */
	private ImageView counthorizntalimg = null;
	/**
	 * 中間控件竪向線
	 */
	private ImageView countvertical = null;
	private Paint paint = null;
	/**
	 * 右邊顯示圖片
	 */
	private ImageView yright = null;
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
	private int ts = 22;
	/**
	 * X轴最大显示刻度,默认120
	 */
	private int YMin = 100;
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
	 * 设置左边文字大小 默认30
	 */
	private int leftsize = 20;
	/**
	 * 设置头部标题文字大小
	 */
	private int sizetop = 24;
	/**
	 * 设置标题红色部分大小,默认20
	 */
	private int sizetopred = 22;
	/**
	 * 左边显示标题
	 */
	private String strlefttitle = "全网百分比";
	/**
	 * 上面显示标题
	 */
	private String strtoptitle = "全网监控系统";
	/**
	 * 上面显示标题,红色部分
	 */
	private String strtopredtitle = "[一级指标]";
	/**
	 * 左邊顯示的圖片
	 */
	private ImageView yleftimg;
	/**
	 * 左边显示刻度数量
	 */
	private int yleftnumber = 5;
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
	/**
	 * 显示更多指引图片
	 */
	private Bitmap allbmap2 = null;
	/**
	 * 平均值大小
	 */
	private float mAverageValue = 0;

	/**
	 * 圆柱大小
	 */
	private int size = 50;
	public static Bitmap bmp = Bitmap.createBitmap(1, 1, Config.RGB_565);
	private int addy = 0;
	ArrayList<ChooseCylinder> ccs = null;

	static {
		Canvas ca = new Canvas();
		ca.setBitmap(bmp);
		ca.drawColor(Color.parseColor("#D1E9E9"));
	}

	public void setIcanvasbitmap(ICanvasBitmap icanvasbitmap) {
		this.icanvasbitmap = icanvasbitmap;
	}

	public HistogramView(Context context,
			ArrayList<HistogramEntity> histogramEntityList, float averageValue) {
		super(context);
		listid = histogramEntityList;
		mAverageValue = averageValue;

		if (3 < histogramEntityList.size() && histogramEntityList.size() < 10) {
			this.xKedu = 200;
			this.size = 100;
		} else if (histogramEntityList.size() < 4) {
			this.xKedu = 350;
			this.size = 120;
		}

		init(context);

		if (widthimg != 0 && mHandler != null) {
			drawimg();
		}
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {

		setBackgroundColor(Color.parseColor("#D1E9E9"));
		setOrientation(HORIZONTAL);

		yleftimg = new ImageView(context);
		yleftimg.setLayoutParams(new LayoutParams(LBMarginleft,
				LayoutParams.MATCH_PARENT));

		// yleftimg.setBackgroundColor(Color.GREEN);
		addView(yleftimg);
		countlayout = new LinearLayout(context);

		countlayout.setOrientation(VERTICAL);
		// countlayout.setBackgroundColor(Color.BLUE);
		countTopimg = new ImageView(context);
		countTopimg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LBMarginTop));
		// countTopimg.setBackgroundColor(Color.YELLOW);
		countlayout.addView(countTopimg);
		countrelative = new RelativeLayout(context);
		countrelative.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// countrelative.setBackgroundColor(Color.WHITE);
		counthorizntalimg = new ImageView(context);
		counthorizntalimg.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		counthorizntalimg.setBackgroundColor(Color.WHITE);
		countrelative.addView(counthorizntalimg);
		countScroll = new HorizontalScrollView(context);
		countScroll.setHorizontalScrollBarEnabled(true);
		// countScroll.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled)
		// countScroll.setfadeScrollbars(false);
		// android:focusable="true"
		// countScroll.setfooterDividersEnabled(false);
		// countScroll.setscrollbarFadeDuration(0);
		// countScroll.setscrollbar
		// countScroll.setBackgroundColor(Color.RED);
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
		if (widthimg == 0) {
			widthimg = this.getWidth();
			heightimg = this.getHeight();
			InitCoordinate();
		}
		super.dispatchDraw(canvas);
	}

	public interface ICanvasBitmap {
		void setXbit();
	}

	Canvas xcountcanvastop = null;
	Canvas xcountcanvasright = null;

	void InitCoordinate() {

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

				ycanvasleft = new Canvas();
				ycanvasleft.setBitmap(yleftbitmap);
				yleftimg.setImageBitmap(yleftbitmap);

				xcountcanvastop = new Canvas();
				xcountcanvastop.setBitmap(countTopbitmap);
				countTopimg.setImageBitmap(countTopbitmap);

				xcountcanvasright = new Canvas();
				xcountcanvasright.setBitmap(yrightbitmap);
				yright.setImageBitmap(yrightbitmap);

				Canvas xcountcanvashorizntal = new Canvas();
				xcountcanvashorizntal.setBitmap(counthorizntalbitmap);
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
								getCoordinatePaint(paint, ts));
						xcountcanvasright.drawText(String.valueOf(j) + "%",
								scaleLeng + 8, y + 8,
								getCoordinatePaint(paint, ts));
					}
				}

				int k = YMin / yleftnumber;
				YscaleSize = (heightimg - LBMarginTop - LBMarginButton) / YMin;
				for (int i = 0; i <= YMin; i += k) {
					int y = (int) (heightimg - LBMarginButton - (YscaleSize * i))
							+ addy;
					ycanvasleft.drawLine(LBMarginleft - scaleLeng, y,
							LBMarginleft, y,
							HistogramView.getCoordinatePaint(paint, ts));

					ycanvasleft
							.drawText(String.valueOf(i), LBMarginleft - 55,
									y + 10,
									HistogramView.getCoordinatePaint(paint, ts));

					xcountcanvashorizntal.drawLine(0, y - LBMarginButton,
							widthimg - LBMarginleft - LBMarginright, y
									- LBMarginButton,
							HistogramView.getBackGroundPaint(paint));

				}

				// 设置头部标题
				Paint toppaint = gettoptextPaint(paint, sizetop);
				float topsiz = toppaint.measureText(strtoptitle) / 2;
				int num = (int) ((widthimg - LBMarginleft - LBMarginright) / 2 - topsiz);
				xcountcanvastop.drawText(strtoptitle, num, LBMarginTop / 2,
						toppaint);
				// 红色 部分
				Paint toppaint2 = gettoptextREDPaint(paint, "#F20231",
						sizetopred);
				float topsiz2 = toppaint2.measureText(strtopredtitle) / 2;
				int num2 = (int) ((widthimg - LBMarginleft - LBMarginright) / 2 - topsiz2);
				xcountcanvastop.drawText(strtopredtitle, num2,
						LBMarginTop / 2 + 25, toppaint2);

				ycanvasleft.drawLine(LBMarginleft, LBMarginButton,
						LBMarginleft, heightimg - LBMarginTop + addy,
						getCoordinatePaint(paint, ts));

				xcountcanvashorizntal.drawLine(0, 0, widthimg - LBMarginleft
						- LBMarginright, 0, getBackGroundPaint(paint));

				// 绘制左边标题
				if (islefttitle) {
					Paint leftpainttitle = getTextPaint(paint, leftsize);
					float sizelef = leftpainttitle.measureText(strlefttitle);
					Rect vt_TextRect = new Rect(0, LBMarginleft, scaleLeng,
							LBMarginleft);
					leftpainttitle.getTextBounds(strlefttitle, 0,
							strlefttitle.length(), vt_TextRect);
					Path vt_TextPath = new Path();
					int offsetHorizontal = vt_TextRect.height();
					int offsetVertical = vt_TextRect.width();
					offsetHorizontal = Math.max(offsetHorizontal, 20);
					offsetVertical = Math.max(offsetVertical, 20);
					int vt_ViewWidth = offsetHorizontal + vt_TextRect.height();
					int startX = vt_ViewWidth / 2 - 10;
					vt_TextPath.moveTo(startX, heightimg / 2 + sizelef / 2);
					vt_TextPath.cubicTo(startX, heightimg / 2 + sizelef / 2,
							startX, heightimg / 2 + sizelef / 2, startX, 0);
					ycanvasleft.drawTextOnPath(strlefttitle, vt_TextPath, 0,
							vt_TextRect.height(), leftpainttitle);
				}
				if (widthimg != 0 && mHandler != null) {
					drawimg();
				}
			} catch (OutOfMemoryError e) {
				closeBitmap();
				e.printStackTrace();

			}
		}
	}

	ArrayList<String> namelist = null;
	String[] namestr = null;
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
						allbmap2 = BitmapFactory.decodeResource(getResources(),
								R.drawable.right_jt);
						xcountcanvasright.drawBitmap(allbmap2, 0,
								(heightimg) / 2 - 50, null);

						if (zoomimg) {
							countvertical
									.setOnTouchListener(new TouchListener());
						}
					}

				}
			});
		} catch (OutOfMemoryError e) {
			closeBitmap();
			e.printStackTrace();
		}

	}

	public static Paint getCoordinatePaint(Paint paint, int ts) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(2);// 笔宽像素
		paint.setTextSize(ts);
		paint.setColor(Color.argb(255, 0, 0, 0));
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	public static Paint getCoordinatePaint2(Paint paint, int ts) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(2);// 笔宽像素
		paint.setTextSize(ts);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	/**
	 * 头部标题
	 */
	public static Paint gettoptextPaint(Paint paint, int sizet) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(10);// 笔宽像素
		paint.setTextSize(sizet);
		paint.setColor(Color.argb(255, 0, 0, 0));
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	/**
	 * 头部标题 红色部分
	 */
	public static Paint gettoptextREDPaint(Paint paint, String co, int sizet) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(6);// 笔宽像素
		paint.setTextSize(sizet);
		paint.setColor(Color.parseColor(co));
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	/**
	 * 左边文字
	 */
	public static Paint getTextPaint(Paint paint, int sizet) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setTextSize(sizet);
		paint.setColor(Color.argb(255, 0, 0, 0));
		return paint;
	}

	/**
	 * x轴背景画线
	 */
	public static Paint getBackGroundPaint(Paint paint) {
		paint = new Paint();// 创建一个画笔
		paint.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		paint.setColor(Color.argb(74, 74, 74, 74));
		return paint;
	}

	/**
	 * 圆柱
	 */
	public static Paint getDrawPaint(Paint paint, int size, String color) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(size);// 笔宽像素
		paint.setTextSize(20);
		if (color == null || color.equals("") || color.equals("null")) {
			paint.setColor(Color.RED);
		} else {
			paint.setColor(Color.parseColor(color));
		}
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	/*
	 * 透明背景
	 */
	public static Paint getrectPaint(Paint paint) {
		paint = new Paint();// 创建一个画笔
		paint.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		paint.setColor(Color.argb(0, 0, 0, 0));
		return paint;
	}

	/*
	 * 平均线
	 */
	public static Paint getaveragePaint(Paint paint, String col, int avgsize) {
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setStrokeWidth(avgsize);// 笔宽像素
		paint.setTextSize(20);
		paint.setColor(Color.parseColor(col));
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setDither(true);
		return paint;
	}

	PillarVO pvo = null;
	TextView tv;
	View vwweb = null;
	ArrayList<HistogramEntity> avs = null;

	private final class TouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();
				for (int i = 0; i < pillars.size(); i++) {
					pvo = pillars.get(i);
					float thisx = pvo.getXpillar();
					float thisy = pvo.getAzurepillar();
					if (thisx >= x - size / 2
							&& thisx <= x + size / 2
							&& y < heightimg - LBMarginButton - LBMarginTop
									+ addy && y > thisy) {
						break;
					}
				}
				if (ccs != null && ccs.size() > 0)
					for (int k = 0; k < ccs.size(); k++) {
						float xonclick = ccs.get(k).getX();
						float yonclick = ccs.get(k).getY();
						if ((x >= xonclick && x < (xonclick + ccs.get(k)
								.getXnum()))
								&& y >= yonclick - ccs.get(k).getYnum()
								&& y < yonclick + ccs.get(k).getYnum()) {
							if (icanvasbitmap != null) {
								countvertical.setImageBitmap(null);
								// icanvasbitmap.setXbit();
								mHandler.postDelayed(myDraw1, 500);
								// drawimg();
							}
							break;
						}

					}

			}
			return true;
		}
	}

	public void closeBitmap() {

		if (yright != null) {
			yright.setImageBitmap(HistogramView.bmp);
			yright.setImageBitmap(null);
			yright = null;
		}

		if (yleftimg != null) {
			yleftimg.setImageBitmap(HistogramView.bmp);
			yleftimg.setImageBitmap(null);
			yleftimg = null;
		}

		if (countTopimg != null) {
			countTopimg.setImageBitmap(HistogramView.bmp);
			countTopimg.setImageBitmap(null);
			countTopimg = null;
		}

		if (counthorizntalimg != null) {
			counthorizntalimg.setImageBitmap(HistogramView.bmp);
			counthorizntalimg.setImageBitmap(null);
			counthorizntalimg = null;
		}

		if (countvertical != null) {
			countvertical.setImageBitmap(HistogramView.bmp);
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

		if (allbmap2 != null && !allbmap2.isRecycled()) {
			allbmap2.recycle();
			allbmap2 = null;
		} else {
			allbmap2 = null;
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
		if (paint != null) {
			paint.reset();
			paint = null;
		}
		closecloss();
		System.gc();
	}

	private void closecloss() {
		pvo = null;
		tv = null;
		vwweb = null;
		avs = null;
	}

	// 设置表格文字样式
	public void setStyle(TextView textview) {
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		lp.gravity = Gravity.CENTER;
		lp.setMargins(0, 0, 1, 1);
		textview.setLayoutParams(lp);
		textview.setWidth(100);
		textview.setTextSize(20);
		textview.setTextColor(Color.BLACK);
		textview.setGravity(Gravity.CENTER);
		textview.setBackgroundColor(getResources().getColor(R.color.none));
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
				if (listid != null && listid.size() > 0) {

					int w = getWidth();
					int h = getHeight();

					int xkedu = xKedu;
					int allxwhidth = listid.size() * xkedu > w ? listid.size()
							* xkedu + xkedu : w;
					int ch = h - LBMarginButton - LBMarginTop + addy;
					xbit = Bitmap.createBitmap(allxwhidth, ch + LBMarginButton,
							Bitmap.Config.ARGB_8888);
					Canvas xcanvas = new Canvas();
					// xcanvas.setDensity(xcanvas.getDensity()/4);
					xcanvas.drawRect(0, 0, allxwhidth, ch + LBMarginButton,
							HistogramView.getrectPaint(paint));
					pillars.clear();
					PillarVO pv = null;
					xcanvas.setBitmap(xbit);
					for (int i = 0; i < listid.size(); i++) {
						int himg = (int) ((ch) - ((ch) / YMin)
								* listid.get(i).getY());
						if (iscylidercolor) {
							cylidercolor = listid.get(i).getColor();
							if (cylidercolor == null || cylidercolor.equals("")
									|| cylidercolor.equals("null")) {
								cylidercolor = "#08e08c";
							}
						} else {
							cylidercolor = "#FF0000";
						}
						pv = new PillarVO();
						pv.setName(listid.get(i).getName());

						xcanvas.drawLine(i * xkedu + xkedu / 2, h
								- LBMarginButton - LBMarginButton + addy, i
								* xkedu + xkedu / 2, himg, HistogramView
								.getDrawPaint(paint, size, cylidercolor));
						pv.setAzurepillar(himg);
						pv.setXpillar(i * xkedu + xkedu / 2);
						pillars.add(pv);
						xcanvas.drawLine(i * xkedu + xkedu, h - LBMarginButton
								- LBMarginButton + addy, i * xkedu + xkedu, 0,
								HistogramView.getBackGroundPaint(paint));
						String yt = String.valueOf(listid.get(i).getY());
						yt = yt.length() > 5 ? yt.substring(0, 5) : yt;
						xcanvas.drawText(yt, i * xkedu + xkedu / 2, himg,
								HistogramView.getTextPaint(paint, sizetopred));
						String key = listid.get(i).getName();
						Paint ttpaint = HistogramView.getCoordinatePaint(paint,
								ts);
						float f = ttpaint.measureText(key);
						String key1 = null;
						String key2 = null;
						int numsiz = 7;
						if (ts < 12) {
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
					if (isShowAverageLine && mAverageLineColor == null) {
						mAverageLineColor = "#08e08c";
					}

					// 绘制平均线
					if (isShowAverageLine) {
						xcanvas.drawText("AVG:" + mAverageValue, allxwhidth
								- xkedu, (int) ((ch) - ((ch) / YMin)
								* mAverageValue) - 5, HistogramView
								.gettoptextREDPaint(paint, mAverageLineColor,
										ts));
						xcanvas.drawLine(0, (int) ((ch) - ((ch) / YMin)
								* mAverageValue), allxwhidth + xkedu,
								(int) ((ch) - ((ch) / YMin) * mAverageValue),
								HistogramView.getaveragePaint(paint,
										mAverageLineColor, mAverageLineHeight));
					}

					// 绘制平均线上的点
					if (isShowAverageLine) {
						int avgcir = (int) ((ch) - ((ch) / YMin)
								* mAverageValue);

						for (int j = 0; j < listid.size(); j++) {
							xcanvas.drawCircle(xkedu * j + xkedu / 2, avgcir,
									mAverageLineHeight + 3, HistogramView
											.getaveragePaint(paint,
													mAverageLineColor,
													mAverageLineHeight));
						}
					}
					HistogramView.this.xbit = xbit;
				}
			} catch (Exception e) {
				e.printStackTrace();
				closeBimt();
			}
		}
	};

	@Override
	public void closeBimt() {
		closeBitmap();
		if (xbit != null && !xbit.isRecycled()) {
			xbit.recycle();
			xbit = null;
		}
	}

}
