package jie.example.widget;

import jie.example.utils.ToastUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class EmailDialog extends DialogPreference {
	
	public EmailDialog(Context context) {
		this(context, null);
	}

	public EmailDialog(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EmailDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if (DialogInterface.BUTTON_POSITIVE == which) {
			ToastUtil.showToast("EmailDialog");
		}
	}

}
