package jie.example.widget;

import jie.example.utils.LogUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 垂直排布文字
 */
public class VerticalLayoutText extends LinearLayout {
	private static final String TAG = VerticalLayoutText.class.getSimpleName();
	private String mSourceText = "刘鹏:你是傻逼".trim();
	private String[] mLayoutText;

	public VerticalLayoutText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalLayoutText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		int len = mSourceText.length();
		mLayoutText = new String[len];
		TextView textView;
		for (int index = 0; index < len; index++) {
			String singleText = String.valueOf(mSourceText.charAt(index));
			mLayoutText[index] = singleText;
			LogUtil.i(TAG, "mLayoutText[" + index + "] = " + singleText);
			textView = new TextView(context);
			textView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			textView.setText(singleText);
			addView(textView);
		}

	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

}