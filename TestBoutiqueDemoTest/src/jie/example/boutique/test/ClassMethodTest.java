package jie.example.boutique.test;

import jie.example.utils.LogUtil;
import android.content.Context;
import android.test.AndroidTestCase;

public class ClassMethodTest extends AndroidTestCase {

	private static final String TAG = "ClassMethodTest";
	private Context mContext;

	// setUp()方法会在所有的测试用例执行之前调用，可以在这里进行一些初始化工作。
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LogUtil.i(TAG, "AndroidTestCase-->setUp()");
		mContext = getContext();
		LogUtil.i(TAG, mContext + "");
	}

	public void testTest() {
		LogUtil.i(TAG, "AndroidTestCase-->testTest()");
		LogUtil.i(TAG, mContext + "");
		assertEquals(2, (3 - 1));// 断言。2：预计返回的值；3：实际返回的值
	}
	
	// tearDown()方法会在所有的测试用例执行之后调用，可以在这里进行一些资源的释放操作。
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		LogUtil.i(TAG, "AndroidTestCase-->tearDown()");
		mContext = null;
		LogUtil.i(TAG, mContext + "");
	}

}
