package jie.example.widget;

import jie.example.boutique.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * 签名板
 */
public class SignNameView extends View implements View.OnClickListener {
	private WindowManager mWindiwMananger;
	private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
	private WriterView mWriterView;
	private View mWheelView;
	int x = 400, y = 500;

	public SignNameView(Context context, int x, int y) {
		super(context);
		this.x = x;
		this.y = y;
		initWindow(context);
	}

	public SignNameView(Context context) {
		super(context);
		initWindow(context);
	}

	private void initWindow(Context context) {
		mWindiwMananger = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mWheelView = LayoutInflater.from(context).inflate(
				R.layout.view_sign_name, null);
		mWindowParams.type = 2002;// type：2002表示系统级窗口
		mWindowParams.flags = 40;// 设置桌面可控
		mWindowParams.width = 450;
		mWindowParams.height = 450;
		mWindowParams.format = -3; // 透明
		mWindowParams.x = this.x;
		mWindowParams.y = this.y;
		mWindiwMananger.addView(mWheelView, mWindowParams);// 将mWheelView注册到WindowManager并显示

		initViews(mWheelView);
	}

	private void initViews(View view) {
		mWriterView = (WriterView) view.findViewById(R.id.sp_wv);
		Button btnConfirm = (Button) view.findViewById(R.id.sp_lb_btn_confirm);
		Button btnClear = (Button) view.findViewById(R.id.sp_lb_btn_clear);
		btnConfirm.setOnClickListener(this);
		btnClear.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sp_lb_btn_confirm:
			mWriterView.savePanelText();
			break;
		case R.id.sp_lb_btn_clear:
			mWriterView.clearPanel();
			break;
		default:
			break;
		}
	}

}
