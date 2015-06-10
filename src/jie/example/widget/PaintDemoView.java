package jie.example.widget;

import jie.example.boutique.R;
import jie.example.constant.Constant;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
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
	}

	private void initView(Context context) {
		mResources = context.getResources();
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
		// 画圆-->float cx:圆心的X轴坐标；float cy:圆心的Y轴坐标；float radius:半径
		canvas.drawCircle(Constant.screenWidth / 2, Y_BASIC_COORDINATE * 2, 36,
				paint);

		// 横线
		float startY = Y_BASIC_COORDINATE * 1.0f * 3;
		float stopY = startY;
		canvas.drawLine(Constant.screenWidth / 4, startY,
				Constant.screenWidth / 4 * 3, stopY, paint);
		// 竖线
		float startX = Constant.screenWidth / 2;
		float stopX = startX;
		canvas.drawLine(startX, startY, stopX, Y_BASIC_COORDINATE * 10, paint);
		// 左斜线
		canvas.drawLine(startX, startY, stopX - 200, Y_BASIC_COORDINATE * 10,
				paint);
		// 右斜线
		canvas.drawLine(startX, startY, stopX + 200, Y_BASIC_COORDINATE * 10,
				paint);
	}

	private Paint getPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 抗(不显示)锯齿，请绘出来的物体更清晰
		paint.setColor(mResources.getColor(R.color.red));// 设置画笔的颜色
		return paint;
	}

	private Paint getPaint(float textSixe) {
		Paint paint = getPaint();
		paint.setTextSize(textSixe);// 设置文字的大小
		return paint;
	}

}
