package jie.example.boutique;

import jie.example.utils.ImageUtil;
import jie.example.utils.LogUtil;
import jie.example.utils.ScreenUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScreenColorActivity extends BasicActivity {
	private static final String TAG = ScreenColorActivity.class.getSimpleName();
	private static final int MSG_CHANGE_COLOR = 101;
	private static final int REQUEST_SACN_RESULT = 10101;
	private static final int REQUEST_TAKE_PHOTO = 10102;
	private int[] backgrounds = { R.drawable.extract_screen_bg_a,
			R.drawable.extract_screen_bg_b, R.drawable.extract_screen_bg_c,
			R.drawable.extract_screen_bg_d, R.drawable.extract_screen_bg_e };
	private int mBitmapWidth, mBitmapHeight, mIndexBg = 0;
	private RelativeLayout mParentLayout;
	private TextView mTextTopBar;
	private Button mBtnChangeBg;
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.extract_screen_c);
		setContentView(R.layout.extract_screen_aty);

		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mParentLayout = (RelativeLayout) findViewById(R.id.main_window);
		mTextTopBar = (TextView) findViewById(R.id.main_text_show_rgb);
		mBtnChangeBg = (Button) findViewById(R.id.main_btn_change_bg);
	}

	@Override
	public void loadingData() {
		mBtnChangeBg.setText(getString(R.string.change_bg_btn));
		mHandler.sendEmptyMessageDelayed(MSG_CHANGE_COLOR, 700);// 一开始进入Activity，先延迟0.7秒发消息截屏
	}

	@SuppressWarnings("deprecation")
	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.main_btn_change_bg:// 点击换背景
			mIndexBg = (mIndexBg == backgrounds.length) ? 0 : mIndexBg;
			mIndexBg++;
			mBtnChangeBg.setText(getString(R.string.change_bg_image_btn,
					mIndexBg, backgrounds.length));
			mParentLayout.setBackgroundDrawable(null);
			mParentLayout.setBackgroundDrawable(getResources().getDrawable(
					backgrounds[mIndexBg - 1]));

			recycleBitmap(mBitmap);// 先回收当前屏幕图像
			shotScreenImage();// 再更换背景后的屏幕图像
			break;
		case R.id.main_btn_scan:
			// Intent intent = new Intent(this, MipcaActivityCapture.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivityForResult(intent, REQUEST_SACN_RESULT);
			break;
		case R.id.main_btn_tp:
			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
					REQUEST_TAKE_PHOTO);
			break;
		default:
			break;
		}
	}

	private void shotScreenImage() {
		mBitmap = ScreenUtil.getScreenShot(this);// 截取屏幕图像
		// 截取出的屏幕图像的宽和高就是屏幕的宽和高，这就解决了屏幕坐标和Bitmap坐标不对应的问题
		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
		LogUtil.i(TAG, "mBitmapWidth = " + mBitmapWidth + ", mBitmapHeight = "
				+ mBitmapHeight);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_SACN_RESULT:// 扫码换背景
			if (resultCode == RESULT_OK) {
				mParentLayout.setBackgroundDrawable(null);
				Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");
				mParentLayout.setBackgroundDrawable(ImageUtil.bitmapToDrawable(
						this, bitmap));
				shotScreenImage();
			}
			break;
		case REQUEST_TAKE_PHOTO:// 拍照换背景
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				Bitmap photo = (Bitmap) bundle.get("data");
				mParentLayout.setBackgroundDrawable(null);
				mParentLayout.setBackgroundDrawable(ImageUtil.bitmapToDrawable(
						this, photo));
				shotScreenImage();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler = null;
		recycleBitmap(mBitmap);
	}

	private void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float downX = event.getRawX();
			float downY = event.getRawY();
			try {
				if (downX >= 0 && downX <= mBitmapWidth && downY >= 0
						&& downY <= mBitmapHeight) {
					int color = mBitmap.getPixel((int) downX, (int) downY);// 根据坐标获取该坐标点的颜色值
					// 取出该颜色值中的红、绿、蓝色调
					int red = Color.red(color);
					int green = Color.green(color);
					int blue = Color.blue(color);

					mTextTopBar.setBackgroundColor(color);
					mTextTopBar.setText("R:" + red + "  " + "G:" + green + "  "
							+ "B:" + blue + "(" + (int) downX + ", "
							+ (int) downY + ")");
				} else {
					LogUtil.w(TAG, "it is place image's outsize");
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "onTouch(View, MotionEvent)::" + e.toString());
			}
			return true;
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_CHANGE_COLOR:
				try {
					mTextTopBar.setText(R.string.show_color_and_rgb);
					shotScreenImage();
					mParentLayout.setOnTouchListener(mTouchListener);
				} catch (Exception e) {
					LogUtil.e(TAG, "get screen shot exception:" + e.toString());
				}
				break;
			default:
				break;
			}
		}
	};

}
