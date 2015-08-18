package jie.example.boutique;

import jie.example.utils.AppUtil;
import jie.example.utils.LogUtil;
import android.test.AndroidTestCase;

public class TestMethod extends AndroidTestCase {
	private static final String TAG = TestMethod.class.getSimpleName();

	public void testGetAppName() {
		LogUtil.i(TAG, "AppUtil.getAppName() = " + AppUtil.getAppName());
	}

}
