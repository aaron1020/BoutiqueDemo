package jie.example.boutique;

import jie.example.net.SingleThreadDownload;
import jie.example.utils.AppUtil;
import jie.example.utils.LogUtil;
import android.test.AndroidTestCase;

public class TestMethod extends AndroidTestCase {
	private static final String TAG = TestMethod.class.getSimpleName();

	public void testGetAppName() {
		LogUtil.i(TAG, "AppUtil.getAppName() = " + AppUtil.getAppName());
	}

	public void testSingleThreadDownload() {
//		new Thread(new SingleThreadDownload(
//				"http://192.168.63.66:8080/NetForAndroid/AdobeReader.exe"))
//				.start();
	}

}
