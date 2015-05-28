package jie.example.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import jie.example.boutique.R;

import android.app.Dialog;
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
import android.util.AttributeSet;
import android.util.Log;
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
 * 绘制圆柱
 */
public class Chartview extends LinearLayout {

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
	private boolean zoomimg = false, zoomimg2 = false;
	/**
	 * 绘制中间圆柱接口
	 */
	private ICanvasBitmap icanvasbitmap;
	private Handler handler = null;
	boolean iserro = true;
	Vector<ChartImplements> cd = null;
	Dialog dialog = null;
	ArrayList<ImageData> listid = null;
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
	private int ts = 20;
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
	private int sizetop = 19;
	/**
	 * 设置标题红色部分大小,默认20
	 */
	private int sizetopred = 18;
	/**
	 * 左边显示标题
	 */
	private String strlefttitle = "全网百分比";
	/**
	 * 右边显示标题
	 */
	private String strrighttitle = "";
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

	public int getYscaleSizeRight() {
		return YscaleSizeRight;
	}

	/**
	 * 右边刻度
	 */
	private int YscaleSizeRight;
	/**
	 * 显示更多指引图片
	 */
	private Bitmap allbmap2 = null;
	private Context mContext;
	/**
	 * 圆柱大小
	 */
	private int size = 30;
	public static Bitmap bmp = Bitmap.createBitmap(1, 1, Config.RGB_565);
	private int addy = 0;
	ArrayList<ChooseCylinder> ccs = null;
	private float avg = 0;

	static {
		Canvas ca = new Canvas();
		ca.setBitmap(bmp);
		ca.drawColor(Color.parseColor("#D1E9E9"));
	}

	/**
	 * 设置平均线大小
	 */
	public void setAvg(float f) {
		avg = f;
	}

	public float getavg() {
		return avg;
	}

	public void setaddy(int ay) {
		addy = ay;
	}

	public int getaddy() {
		return addy;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setxy(ArrayList<ChooseCylinder> ccs) {
		this.ccs = ccs;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public boolean isLoadright() {
		return loadright;
	}

	public void setLoadright(boolean loadright) {
		this.loadright = loadright;
	}

	public int getTs() {
		return ts;
	}

	public void setTs(int ts) {
		this.ts = ts;
	}

	public int getYMin() {
		return YMin;
	}

	public void setYMin(int yMin) {
		YMin = yMin;
	}

	public int getLBMarginleft() {
		return LBMarginleft;
	}

	public void setLBMarginleft(int lBMarginleft) {
		LBMarginleft = lBMarginleft;
	}

	public int getLBMarginTop() {
		return LBMarginTop;
	}

	public void setLBMarginTop(int lBMarginTop) {
		LBMarginTop = lBMarginTop;
	}

	public int getLBMarginButton() {
		return LBMarginButton;
	}

	public void setLBMarginButton(int lBMarginButton) {
		LBMarginButton = lBMarginButton;
	}

	public int getLBMarginright() {
		return LBMarginright;
	}

	public void setLBMarginright(int lBMarginright) {
		LBMarginright = lBMarginright;
	}

	public int getLeftsize() {
		return leftsize;
	}

	public void setLeftsize(int leftsize) {
		this.leftsize = leftsize;
	}

	public int getSizetop() {
		return sizetop;
	}

	public void setSizetop(int sizetop) {
		this.sizetop = sizetop;
	}

	public int getSizetopred() {
		return sizetopred;
	}

	public void setSizetopred(int sizetopred) {
		this.sizetopred = sizetopred;
	}

	public String getStrlefttitle() {
		return strlefttitle;
	}

	public void setStrlefttitle(String strlefttitle) {
		this.strlefttitle = strlefttitle;
	}

	public String getStrrighttitle() {
		return strrighttitle;
	}

	public void setStrrighttitle(String strrighttitle) {
		this.strrighttitle = strrighttitle;
	}

	public String getStrtoptitle() {
		return strtoptitle;
	}

	public void setStrtoptitle(String strtoptitle) {
		this.strtoptitle = strtoptitle;
	}

	public String getStrtopredtitle() {
		return strtopredtitle;
	}

	public void setStrtopredtitle(String strtopredtitle) {
		this.strtopredtitle = strtopredtitle;
	}

	public ImageView getYleftimg() {
		return yleftimg;
	}

	public void setYleftimg(ImageView yleftimg) {
		this.yleftimg = yleftimg;
	}

	public int getYleftnumber() {
		return yleftnumber;
	}

	public void setYleftnumber(int yleftnumber) {
		this.yleftnumber = yleftnumber;
	}

	public float getYscaleSize() {
		return YscaleSize;
	}

	public void setYscaleSize(float yscaleSize) {
		YscaleSize = yscaleSize;
	}

	public LinearLayout getCountlayout() {
		return countlayout;
	}

	public void setCountlayout(LinearLayout countlayout) {
		this.countlayout = countlayout;
	}

	public ImageView getCountTopimg() {
		return countTopimg;
	}

	public void setCountTopimg(ImageView countTopimg) {
		this.countTopimg = countTopimg;
	}

	public RelativeLayout getCountrelative() {
		return countrelative;
	}

	public void setCountrelative(RelativeLayout countrelative) {
		this.countrelative = countrelative;
	}

	public ImageView getCounthorizntalimg() {
		return counthorizntalimg;
	}

	public void setCounthorizntalimg(ImageView counthorizntalimg) {
		this.counthorizntalimg = counthorizntalimg;
	}

	public ImageView getCountvertical() {
		return countvertical;
	}

	public void setCountvertical(ImageView countvertical) {
		this.countvertical = countvertical;
	}

	public ImageView getYright() {
		return yright;
	}

	public void setYright(ImageView yright) {
		this.yright = yright;
	}

	public HorizontalScrollView getCountScroll() {
		return countScroll;
	}

	public void setCountScroll(HorizontalScrollView countScroll) {
		this.countScroll = countScroll;
	}

	public Bitmap getXbit() {
		return xbit;
	}

	public void setXbit(Bitmap xbit) {
		this.xbit = xbit;
	}

	public Bitmap getCounthorizntalbitmap() {
		return counthorizntalbitmap;
	}

	public void setCounthorizntalbitmap(Bitmap counthorizntalbitmap) {
		this.counthorizntalbitmap = counthorizntalbitmap;
	}

	public Bitmap getCountTopbitmap() {
		return countTopbitmap;
	}

	public void setCountTopbitmap(Bitmap countTopbitmap) {
		this.countTopbitmap = countTopbitmap;
	}

	public Bitmap getYrightbitmap() {
		return yrightbitmap;
	}

	public void setYrightbitmap(Bitmap yrightbitmap) {
		this.yrightbitmap = yrightbitmap;
	}

	public Bitmap getYleftbitmap() {
		return yleftbitmap;
	}

	public void setYleftbitmap(Bitmap yleftbitmap) {
		this.yleftbitmap = yleftbitmap;
	}

	public Canvas getYcanvasleft() {
		return ycanvasleft;
	}

	public void setYcanvasleft(Canvas ycanvasleft) {
		this.ycanvasleft = ycanvasleft;
	}

	public boolean isIslefttitle() {
		return islefttitle;
	}

	public void setIslefttitle(boolean islefttitle) {
		this.islefttitle = islefttitle;
	}

	public int getScaleLeng() {
		return scaleLeng;
	}

	public void setScaleLeng(int scaleLeng) {
		this.scaleLeng = scaleLeng;
	}

	public boolean isZoomimg() {
		return zoomimg;
	}

	public void setZoomimg(boolean zoomimg) {
		this.zoomimg = zoomimg;
	}

	public void setZoomimg2(boolean zoomimg2) {
		this.zoomimg2 = zoomimg2;
	}

	public boolean getZoomimg2() {
		return zoomimg2;
	}

	public ICanvasBitmap getIcanvasbitmap() {
		return icanvasbitmap;
	}

	public void setIcanvasbitmap(ICanvasBitmap icanvasbitmap) {
		this.icanvasbitmap = icanvasbitmap;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public boolean isIserro() {
		return iserro;
	}

	public void setIserro(boolean iserro) {
		this.iserro = iserro;
	}

	public ArrayList<PillarVO> getPillars() {
		return pillars;
	}

	public void setPillars(ArrayList<PillarVO> pillars) {
		this.pillars = pillars;
	}

	public int getxKedu() {
		return xKedu;
	}

	public void setxKedu(int xKedu) {
		this.xKedu = xKedu;
	}

	public int getWidthImg() {
		return heightimg;
	}

	public int getHeightsImg() {
		return heightimg;
	}

	public void setT3onclickStr(String s, String w) {
	}

	public void setList(ArrayList<ImageData> list) {
		listid = list;
	}

	public ArrayList<ImageData> getList() {
		return listid;
	}

	private String names = null;

	public void setnames(String names) {
		this.names = names;
	}

	public void setData(Handler h, Vector<ChartImplements> cd, Dialog dialog) {

		handler = h;
		this.cd = cd;
		this.dialog = dialog;
		if (widthimg != 0 && handler != null) {
			// new Thread(myDraw).start();
			// handler.postDelayed(myDraw, 100);
			drawimg();
		}

	}

	public Chartview(Context context) {
		super(context, null);
		mContext = context;
		init(context);
	}

	public Chartview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);
	}

	private String t1 = null, t2 = null, t3 = null;

	public void setT1(String t1, String t2, String t3) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
	}

	private void init(Context context) {

		Log.i("WBJ", "Chartview init");
		mContext = context;
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
		/* countScroll.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled) */
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
							Chartview.getCoordinatePaint(paint, ts));

					ycanvasleft.drawText(String.valueOf(i), LBMarginleft - 55,
							y + 10, Chartview.getCoordinatePaint(paint, ts));

					xcountcanvashorizntal.drawLine(0, y - LBMarginButton,
							widthimg - LBMarginleft - LBMarginright, y
									- LBMarginButton,
							Chartview.getBackGroundPaint(paint));

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
				if (widthimg != 0 && handler != null) {
					// new Thread(myDraw).start();
					// handler.postDelayed(myDraw, 100);
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
	ArrayList<ChooseCylinder> ccsd = new ArrayList<ChooseCylinder>();
	HashMap<String, String> colors = new HashMap<String, String>();
	String thisname = "";
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
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (xbit != null) {
						countvertical.setImageBitmap(xbit);

						if (t1 != null && t2 != null && t3 != null) {
							if (t1.equals(getResources().getString(
									R.string.boss_menu_t1))
									&& t2.equals(getResources().getString(
											R.string.boss_menu_t1_2))
									&& t3.equals(getResources().getString(
											R.string.boss_menu_t1_2_2))) {
								Bitmap bmap = BitmapFactory.decodeResource(
										getResources(), R.drawable.img_button);
								xcountcanvasright.drawBitmap(bmap, 0, 20, null);

								xcountcanvasright.drawText("更多", 15, 38,
										getCoordinatePaint(paint, ts));

								yright.setImageBitmap(yrightbitmap);
								yright.setOnTouchListener(new OnTouchListener3());
							}
							if (!t3.equals(getResources().getString(
									R.string.boss_menu_t1_3))) {
								allbmap2 = BitmapFactory.decodeResource(
										getResources(), R.drawable.right_jt);
								xcountcanvasright.drawBitmap(allbmap2, 0,
										(heightimg) / 2 - 50, null);

							}
						} else {

							allbmap2 = BitmapFactory.decodeResource(
									getResources(), R.drawable.right_jt);
							xcountcanvasright.drawBitmap(allbmap2, 0,
									(heightimg) / 2 - 50, null);

						}

						if (zoomimg2 && names != null && !names.equals("")
								&& !names.equals("null")) {
							if (names != null && !names.equals("")) {
								namestr = names.split(",");
								if (colors == null || colors.size() < 1) {
									for (int i = 0; i < listid.size(); i++) {
										int si = colors.size();
										if (si < 1) {
											colors.put(namestr[si],
													listid.get(i).getColor());
										} else {
											for (int j = 0; j < si; j++) {
												String color = colors
														.get(namestr[j]);
												if (color.equals(listid.get(i)
														.getColor())) {
													break;
												} else {
													if (j == (si - 1)) {
														colors.put(
																namestr[si],
																listid.get(i)
																		.getColor());
														break;
													}
												}
											}
										}
									}
								}
							}
							if (namelist == null) {
								namelist = new ArrayList<String>();
								for (int i = 0; i < namestr.length; i++) {
									namelist.add(namestr[i]);
								}
							}
							float xonclick = 0;
							float yonclick = 90;
							ChooseCylinder cc1 = null;
							for (int j = 0; j < namelist.size(); j++) {
								if (j == 0) {
									cc1 = new ChooseCylinder();
									cc1.setX(xonclick);
									cc1.setY(yonclick);
									cc1.setXnum(70);
									cc1.setYnum(20);
									cc1.setName(namestr[j]);
									cc1.setId(2);
									if (thisname.indexOf(namestr[j]) == -1) {
										xcountcanvastop
												.drawLine(
														xonclick,
														yonclick,
														xonclick + 70,
														yonclick,
														getDrawPaint(
																paint,
																20,
																colors.get(namestr[j])));
									} else {
										xcountcanvastop.drawLine(
												xonclick,
												yonclick,
												xonclick + 70,
												yonclick,
												getDrawPaint(paint, 20,
														"#8B8F86"));
									}
									xcountcanvastop.drawText(namestr[j],
											xonclick + 12, yonclick + 5,
											getCoordinatePaint2(paint, 15));
									ccsd.add(cc1);
								} else {
									xonclick = ccsd.get(j - 1).getX()
											+ ccsd.get(j - 1).getXnum() + 20;
									cc1 = new ChooseCylinder();
									cc1.setX(xonclick);
									cc1.setY(yonclick);
									cc1.setXnum(70);
									cc1.setYnum(20);
									cc1.setId(2);
									cc1.setName(namestr[j]);
									if (thisname.indexOf(namestr[j]) == -1) {
										xcountcanvastop
												.drawLine(
														xonclick,
														yonclick,
														xonclick + 70,
														yonclick,
														getDrawPaint(
																paint,
																20,
																colors.get(namestr[j])));
									} else {
										xcountcanvastop.drawLine(
												xonclick,
												yonclick,
												xonclick + 70,
												yonclick,
												getDrawPaint(paint, 20,
														"#8B8F86"));
									}
									xcountcanvastop.drawText(namestr[j],
											xonclick + 12, yonclick + 5,
											getCoordinatePaint2(paint, 15));
									ccsd.add(cc1);

								}
							}

							countTopimg.setImageBitmap(countTopbitmap);
							countTopimg
									.setOnTouchListener(new TouchListener2());
						}
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
	ArrayList<ImageData> avs = null;
	ChooseCylinder ccr = null;

	private final class TouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();
				if (t1.equals(getResources().getString(R.string.boss_menu_t2))
						&& (t2.equals(getResources().getString(
								R.string.boss_menu_t2_1)) && t3
								.equals(getResources().getString(
										R.string.boss_menu_t2_1_1)))
						|| (t2.equals(getResources().getString(
								R.string.boss_menu_t2_2)) && t3
								.equals(getResources().getString(
										R.string.boss_menu_t2_2_1)))) {
				} else {
					for (int i = 0; i < pillars.size(); i++) {
						pvo = pillars.get(i);
						float thisx = pvo.getXpillar();
						float thisy = pvo.getAzurepillar();
						if (thisx >= x - getSize() / 2
								&& thisx <= x + getSize() / 2
								&& y < heightimg - LBMarginButton - LBMarginTop
										+ getaddy() && y > thisy) {
							if (t1.equals(mContext.getResources().getString(
									R.string.boss_menu_t7))
									&& t2.equals(mContext.getResources()
											.getString(R.string.boss_menu_t7_1))
									&& t3.equals(mContext.getResources()
											.getString(R.string.boss_menu_t7_1))) {
								new Thread(new Runnable() {
									@Override
									public void run() {
									}
								}).start();
								break;
							} else if (t1.equals(mContext.getResources()
									.getString(R.string.boss_menu_t8))
									&& t2.equals(mContext.getResources()
											.getString(R.string.boss_menu_t8_2))
									&& t3.equals(mContext.getResources()
											.getString(R.string.boss_menu_t8_2))) {
								new Thread(new Runnable() {

									@Override
									public void run() {
									}
								}).start();
								break;
							} else {
								new Thread(new Runnable() {
									@Override
									public void run() {
									}
								}).start();
							}
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
								for (int i = 0; i < listid.size(); i++) {

									switch (ccs.get(k).getId()) {
									case 1:
										if (listid.get(i).getShowid2() == 0) {
											listid.get(i).setShowid2(2);

										} else {
											listid.get(i).setShowid2(0);

										}
										break;
									case 2:
										if (listid.get(i).getShowid1() == 0) {
											listid.get(i).setShowid1(1);
										} else {
											listid.get(i).setShowid1(0);
										}
										break;

									case 3:
										if (listid.get(i).getShowid3() == 0) {
											listid.get(i).setShowid3(3);
										} else {
											listid.get(i).setShowid3(0);
										}
										break;
									default:
										break;
									}
								}
								if (icanvasbitmap != null) {
									countvertical.setImageBitmap(null);
									// icanvasbitmap.setXbit();
									handler.postDelayed(myDraw1, 500);
									// drawimg();
								}
								break;
							}

						}

				}
			}
			return true;
		}
	}

	private class OnTouchListener3 implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				float y = event.getY();
				if (y < 80) {
					new Thread(new Runnable() {
						@Override
						public void run() {
						}
					}).start();
				}

				break;
			}
			return true;
		}
	}

	private final class TouchListener2 implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();

				for (int i = 0; i < ccsd.size(); i++) {
					ccr = ccsd.get(i);
					if (x > ccr.getX() && x < (ccr.getX() + ccr.getXnum())
							&& y > (ccr.getY() - ccr.getYnum())
							&& y < (ccr.getY() + ccr.getYnum())) {
						if (thisname.indexOf(ccr.getName()) == -1) {
							thisname += ccr.getName() + ",";
						} else {
							thisname = thisname.replaceAll(ccr.getName() + ",",
									"");
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
							}
						}).start();

						break;
					}
				}
			}
			return true;
		}
	}

	public void closeBitmap() {

		if (yright != null) {
			yright.setImageBitmap(Chartview.bmp);
			yright.setImageBitmap(null);
			yright = null;
		}

		if (yleftimg != null) {
			yleftimg.setImageBitmap(Chartview.bmp);
			yleftimg.setImageBitmap(null);
			yleftimg = null;
		}

		if (countTopimg != null) {
			countTopimg.setImageBitmap(Chartview.bmp);
			countTopimg.setImageBitmap(null);
			countTopimg = null;
		}

		if (counthorizntalimg != null) {
			counthorizntalimg.setImageBitmap(Chartview.bmp);
			counthorizntalimg.setImageBitmap(null);
			counthorizntalimg = null;
		}

		if (countvertical != null) {
			countvertical.setImageBitmap(Chartview.bmp);
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

	public void setYrightnumber(int yrightnumber) {
		this.yrightnumber = yrightnumber;
	}

	public void setYMinR(int yMinR) {
		this.yMinR = yMinR;
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

	public void setYscaleSize2(int y2s) {
	}
}
