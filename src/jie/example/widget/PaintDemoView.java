package jie.example.widget;

import jie.example.boutique.R;
import jie.example.constant.Constant;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class PaintDemoView extends View {

	private static final int Y_BASIC_COORDINATE = 50;
	private Resources mResources;

	public PaintDemoView(Context context) {
		super(context);
		initView(context);
	}

	public PaintDemoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mResources = context.getResources();
		setMinimumHeight(Constant.screenHeight);
		setMinimumWidth(Constant.screenWidth);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 画字-->float x:X轴坐标；float y:Y轴坐标
		Paint paint = getPaint(44.5f);
		String paintText = mResources.getString(R.string.paint_text);
		float paintTextWidth = paint.measureText(paintText);// 根据画笔，测出文字的宽度
		// (Constant.screenWidth - paintTextWidth) / 2：水平居中
		canvas.drawText(paintText, (Constant.screenWidth - paintTextWidth) / 2,
				Y_BASIC_COORDINATE, paint);

		paint = getPaint();
		paint.setStyle(Paint.Style.STROKE);// 空心，默认是实心。

		// 画圆-->float cx:圆心的X轴坐标；float cy:圆心的Y轴坐标；float radius:半径
		canvas.drawCircle(Constant.screenWidth / 2, Y_BASIC_COORDINATE * 2, 36,
				paint);

		/*
		 * 画线-->float startX:线条起点的X轴坐标；float startY:线条起点的Y轴坐标；float
		 * stopX:线条终点的X轴坐标；float stopY::线条终点的Y轴坐标
		 */
		// 横线-->Y轴坐标不变
		float startY = Y_BASIC_COORDINATE * 1.0f * 3;
		float stopY = startY;
		canvas.drawLine(Constant.screenWidth / 4, startY,
				Constant.screenWidth / 4 * 3, stopY, paint);
		// 竖线-->X轴坐标不变
		float startX = Constant.screenWidth / 2;
		float stopX = startX;
		canvas.drawLine(startX, startY, stopX, Y_BASIC_COORDINATE * 10, paint);
		// 左斜线
		canvas.drawLine(startX, startY, stopX - 200, Y_BASIC_COORDINATE * 10,
				paint);
		// 右斜线
		canvas.drawLine(startX, startY, stopX + 200, Y_BASIC_COORDINATE * 10,
				paint);
		// 粗线条
		canvas.drawLine(startX + 250, startY, startX + 250,
				Y_BASIC_COORDINATE * 10, getStoreWidthPaint(40));

		// 画弧-->start
		/*
		 * float left:矩形左边所在的X坐标；float top:矩形顶边所在的Y坐标；float
		 * right:矩形右边所在的X坐标float bottom:矩形底边所在的Y坐标。
		 */
		RectF ovalFalse = new RectF(100, 20, 300, 220);
		/*
		 * RectF oval:定义弧形的形状和位置，如果上面的(right -
		 * left)=(bottom-top)，那么是圆形形状的弧形，如果不等，则是椭圆形状的弧形；float
		 * startAngle:弧形开始的角度；float sweepAngle:弧形扫过多少度；boolean
		 * useCenter:注意这个取值的不同而导致弧形形状的不同
		 */
		canvas.drawArc(ovalFalse, 0, 270, false, getPaint());// 绘画弧形规则：从三点钟方向开始顺时针方向绘制
		RectF ovalTrue = new RectF(100, 230, 300, 430);
		canvas.drawArc(ovalTrue, 0, 270, true, getPaint());
		// 画多个具有相同的圆心但颜色不同的弧形
		ovalTrue.set(310, 230, 510, 430);
		canvas.drawArc(ovalTrue, 0, 45, true, getPaint());
		canvas.drawArc(ovalTrue, 45, 45, true, getPaint(R.color.eagle_four));
		// 画弧-->end

		// 画矩形-->若(right-left)=(bottom-top)，则是正方形，若不等是矩形
		canvas.drawRect(100, 440, 250, 540, paint);// 空心矩形
		canvas.drawRect(260, 440, 410, 540, getPaint());// 实心矩形
		canvas.drawRect(420, 440, 520, 540, getPaint());// 正方形

		// 定义一个颜色渐变的着色器
		Shader shader = new LinearGradient(0, 0, 100, 100,
				new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
						Color.LTGRAY }, null, Shader.TileMode.REPEAT);
		paint.setShader(shader);
		paint.setStrokeWidth(2.0f);

		/*
		 * 画椭圆-->若(right-left)=(bottom-top)，则是圆形，若不等才是椭圆
		 */
		ovalTrue.set(100, 550, 250, 660);
		canvas.drawOval(ovalTrue, paint);
		ovalTrue.set(260, 550, 410, 700);
		canvas.drawOval(ovalTrue, paint);

		// 画任意多边形
		Path path = new Path();
		// 等边三角形
		path.moveTo(300, 300);// 第一点的坐标
		path.lineTo(500, 646.41f);// 第二点的坐标
		path.lineTo(700, 300);// 第三点的坐标
		path.close();// 设置图形封闭
		canvas.drawPath(path, paint);
		// 等边三角形
		path = new Path();
		path.moveTo(500, 710);
		path.lineTo(600, 536.8f);
		path.lineTo(700, 710);
		// 未设置封闭//path.close();
		canvas.drawPath(path, paint);
		// 折线图
		float[] y = { 660, 730, 680, 550, 660 };
		path = new Path();
		path.moveTo(710, 710);
		for (int i = 0; i < 5; i++) {
			path.lineTo(740 + 30 * i, y[i]);
		}
		canvas.drawPath(path, paint);

		// 画圆角矩形
		ovalTrue.set(100, 720, 200, 880);
		canvas.drawRoundRect(ovalTrue, 20, 20, paint);// 第二个参数是x半径，第三个参数是y半径

		// 画点
		canvas.drawPoint(220, 800, getStoreWidthPaint(10));// 画一个点
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(10);
		canvas.drawPoint(240, 800, paint);// 画一个点
		canvas.drawPoints(new float[] { 260, 800, 280, 800, 300, 800 }, paint);// 画多个点

		// 画图片(贴图)
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.province_blue_25);
		canvas.drawBitmap(bitmap, 330, 720, getPaint());

		// 画贝塞尔曲线
		paint.setStyle(Paint.Style.STROKE);
		path = new Path();
		path.moveTo(500, 850);// 设置Path的起点
		path.quadTo(500, 600, 880, 800); // 设置贝塞尔曲线的控制点坐标和终点坐标
		canvas.drawPath(path, paint);

		invalidate();
	}

	/**
	 * 获取默认颜色的画笔
	 */
	private Paint getPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 抗(不显示)锯齿，让绘出来的物体更清晰
		paint.setColor(mResources.getColor(R.color.red));// 设置画笔的颜色
		return paint;
	}

	/**
	 * 获取特定颜色的画笔
	 */
	private Paint getPaint(int colorRes) {
		Paint paint = getPaint();
		paint.setColor(mResources.getColor(colorRes));// 设置画笔的颜色
		return paint;
	}

	/**
	 * 用于绘制文字的画笔
	 * 
	 * @param textSixe
	 *            文字的大小
	 */
	private Paint getPaint(float textSize) {
		Paint paint = getPaint();
		if (textSize > 0) {
			paint.setTextSize(textSize);// 设置文字的大小
		}
		return paint;
	}

	/**
	 * 获取具有一定宽度的画笔
	 * 
	 * @param width
	 *            画笔的宽度
	 */
	private Paint getStoreWidthPaint(int width) {
		Paint paint = getPaint();
		paint.setStrokeWidth(width);// 设置画笔的宽度
		return paint;
	}

}
