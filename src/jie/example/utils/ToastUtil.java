package jie.example.utils;

import jie.example.manager.BoutiqueApp;
import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast;

	@SuppressLint("ShowToast")
	public static void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(BoutiqueApp.getContext(), text,
					Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public static void showToast(int textId) {
		String text = BoutiqueApp.getContext().getResources().getString(textId);
		showToast(text);
	}

}
