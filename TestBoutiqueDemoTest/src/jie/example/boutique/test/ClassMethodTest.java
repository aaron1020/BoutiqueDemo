package jie.example.boutique.test;

import jie.example.utils.LogUtil;
import android.content.Context;
import android.test.AndroidTestCase;

public class ClassMethodTest extends AndroidTestCase {

	private static final String TAG = "ClassMethodTest";
	private Context mContext;

	// setUp()�����������еĲ�������ִ��֮ǰ���ã��������������һЩ��ʼ��������
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
		assertEquals(2, (3 - 1));// ���ԡ�2��Ԥ�Ʒ��ص�ֵ��3��ʵ�ʷ��ص�ֵ
	}
	
	// tearDown()�����������еĲ�������ִ��֮����ã��������������һЩ��Դ���ͷŲ�����
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		LogUtil.i(TAG, "AndroidTestCase-->tearDown()");
		mContext = null;
		LogUtil.i(TAG, mContext + "");
	}

}
