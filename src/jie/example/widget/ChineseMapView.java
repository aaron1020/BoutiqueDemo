package jie.example.widget;

import java.util.List;

import jie.example.boutique.R;
import jie.example.constant.Constant;
import jie.example.entity.ChineseMapViewEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ChineseMapView extends RelativeLayout {

	private Context mContext;
	private String[] mProvinceArray;
	private List<ChineseMapViewEntity> mProvinceInfoList;
	/**
	 * 地图缩小比例，与值的大小成反比
	 */
	private float mScaleShow = 1.5f;
	/**
	 * 每张省份图片的宽度(可直接根据图片的尺寸得出)
	 */
	private int[] mPictruesWidth = { 31, 20, 24, 78, 99, 63, 107, 106, 153,
			206, 404, 109, 122, 89, 69, 81, 137, 99, 144, 141, 88, 199, 47,
			109, 160, 356, 93, 258, 231, 52, 368, 31 };
	/**
	 * 每张省份图片的高度(可直接根据图片的尺寸得出)
	 */
	private int[] mPictruesHeight = { 31, 16, 31, 83, 140, 133, 102, 103, 102,
			184, 344, 81, 75, 104, 78, 97, 84, 113, 112, 107, 116, 174, 39, 93,
			167, 216, 163, 217, 163, 85, 279, 73 };
	/**
	 * 每张省份图片距离屏幕左侧的左边距
	 */
	private int[] mPictruesLeftMargin = { 696, 815, 718, 538, 668, 617, 622,
			747, 772, 738, 407, 722, 698, 701, 769, 733, 590, 601, 620, 520,
			689, 391, 598, 500, 383, 60, 538, 330, 258, 513, 20, 820 };
	/**
	 * 每张省份图片距离屏幕顶端的顶边距
	 */
	private int[] mPictruesTopMargin = { 270, 459, 285, 470, 236, 282, 372,
			207, 147, 0, 10, 395, 332, 408, 476, 538, 447, 511, 606, 590, 506,
			425, 723, 532, 535, 356, 314, 242, 310, 318, 90, 580 };

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			drawChineseMapView();
		}
	};

	public ChineseMapView(Context context) {
		super(context);
		initView(context);
	}

	public ChineseMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		mProvinceArray = context.getResources().getStringArray(
				R.array.provinces_list);
		if (Constant.screenWidth > 1000 || mScaleShow == 0.0f) {
			mScaleShow = 1.0f;
		}
	}

	private void drawChineseMapView() {
		if (mProvinceInfoList != null && mProvinceInfoList.size() > 0) {
			for (int i = 0; i < mProvinceArray.length; i++) {
				final ImageView provinceImageView = new ImageView(mContext);

				// 设置图片的布局属性
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						(int) (mPictruesWidth[i] / mScaleShow),
						(int) (mPictruesHeight[i] / mScaleShow));
				layoutParams.leftMargin = (int) ((mPictruesLeftMargin[i] - 6) / mScaleShow);
				layoutParams.topMargin = (int) (mPictruesTopMargin[i] / mScaleShow);
				provinceImageView.setLayoutParams(layoutParams);

				// 设置显示的省份图片
				int provincePicStartId = 0;

				switch (mProvinceInfoList.get(i).getId() % 7) {
				case 0:
					provincePicStartId = R.drawable.province_blue_01;
					break;

				case 1:
					provincePicStartId = R.drawable.province_red_01;
					break;

				case 2:
					provincePicStartId = R.drawable.province_gray_01;
					break;

				case 3:
					provincePicStartId = R.drawable.province_green_01;
					break;

				case 4:
					provincePicStartId = R.drawable.province_yellow_01;
					break;

				case 5:
					provincePicStartId = R.drawable.province_orange_01;
					break;

				case 6:
					provincePicStartId = R.drawable.province_default_01;
					break;

				default:
					provincePicStartId = R.drawable.province_default_01;
					break;
				}
				provinceImageView.setImageResource(provincePicStartId + i);

				// 添加图片到父容器中
				mHandler.post(new Runnable() {
					@Override
					public void run() {

						if (provinceImageView != null) {
							addView(provinceImageView);
						}
					}
				});

			}
		}
	}

	public void setProvinceInfoList(List<ChineseMapViewEntity> provinceInfoList) {
		this.mProvinceInfoList = provinceInfoList;
	}

	public void refreshChineseMapView() {
		new Thread(mRunnable).start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mHandler = null;
	}

}
