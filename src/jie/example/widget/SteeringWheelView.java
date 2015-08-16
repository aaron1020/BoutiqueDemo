package jie.example.widget;

import jie.example.boutique.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class SteeringWheelView extends View {

	private Context mContent;
	private WindowManager mWindiwMananger;
	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private View mWheelView;
	int x = 400, y = 500;

	public SteeringWheelView(Context context, int x, int y) {
		super(context);
		mContent = context;
		this.x = x;
		this.y = y;
		initWindow();
	}

	public SteeringWheelView(Context context) {
		super(context);
		mContent = context;
		initWindow();
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	private void initWindow() {
		mWindiwMananger = (WindowManager) mContent
				.getSystemService(Context.WINDOW_SERVICE);
		mWheelView = LayoutInflater.from(mContent).inflate(
				R.layout.steering_wheel_view, null);
		mWindowParams.type = 2002;// type：2002表示系统级窗口
		mWindowParams.flags = 40;// 设置桌面可控
		mWindowParams.width = 400;
		mWindowParams.height = 250;
		mWindowParams.format = -3; // 透明
		mWindowParams.x = this.x;
		mWindowParams.y = this.y;
		mWindiwMananger.addView(mWheelView, mWindowParams);// 将mWheelView注册到WindowManager并显示
	}

}
