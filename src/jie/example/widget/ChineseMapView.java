package jie.example.widget;

import java.util.List;

import jie.example.boutique.R;
import jie.example.entity.ChineseMapViewEntity;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ChineseMapView {

	private Context mContext;
	private Handler mHandler;
	private RelativeLayout mMapViewContainer;
	private List<ChineseMapViewEntity> mProvinceInfoList;
	public static String[] mProvinceArray;
	static float mScaleShow = 1.5f;// 地图缩小比例，与值的大小成反比
	public static int[] ws = { 31, 20, 24, 78, 99, 63, 107, 106, 153, 206, 404,
			109, 122, 89, 69, 81, 137, 99, 144, 141, 88, 199, 47, 109, 160,
			356, 93, 258, 231, 52, 368, 31 };

	public static int[] hs = { 31, 16, 31, 83, 140, 133, 102, 103, 102, 184,
			344, 81, 75, 104, 78, 97, 84, 113, 112, 107, 116, 174, 39, 93, 167,
			216, 163, 217, 163, 85, 279, 73 };

	public static int[] lm = { 696, 815, 718, 538, 668, 617, 622, 747, 772,
			738, 407, 722, 698, 701, 769, 733, 590, 601, 620, 520, 689, 391,
			598, 500, 383, 60, 538, 330, 258, 513, 20, 820 };

	public static int[] tm = { 270, 459, 285, 470, 236, 282, 372, 207, 147, 0,
			10, 395, 332, 408, 476, 538, 447, 511, 606, 590, 506, 425, 723,
			532, 535, 356, 314, 242, 310, 318, 90, 580 };

	/**
	 * @param context
	 * @param handler
	 * @param mapViewContainer
	 *            地图所在的布局
	 * @param provinceInfoList
	 *            省份信息集合
	 * @param screenWidth
	 *            设备屏幕宽度
	 */
	public ChineseMapView(Context context, Handler handler,
			RelativeLayout mapViewContainer,
			List<ChineseMapViewEntity> provinceInfoList, int screenWidth) {
		mContext = context;
		mHandler = handler;
		mMapViewContainer = mapViewContainer;
		mProvinceInfoList = provinceInfoList;
		mProvinceArray = context.getResources().getStringArray(
				R.array.provinces_list);
		if (screenWidth > 1000 || mScaleShow == 0.0f) {
			mScaleShow = 1.0f;
		} else {
			mScaleShow = 1.5f;
		}
		new Thread(runnable).start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			drawChineseMapView();
		}
	};

	private void drawChineseMapView() {
		for (int i = 0; i < mProvinceArray.length; i++) {
			final ImageView provinceImageView = new ImageView(mContext);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					(int) (ws[i] / mScaleShow), (int) (hs[i] / mScaleShow));
			layoutParams.leftMargin = (int) ((lm[i] - 6) / mScaleShow);
			layoutParams.topMargin = (int) (tm[i] / mScaleShow);
			provinceImageView.setLayoutParams(layoutParams);

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

			mHandler.post(new Runnable() {
				@Override
				public void run() {

					if (provinceImageView != null) {
						mMapViewContainer.addView(provinceImageView);
					}
				}
			});

		}
	}

	public void refreshChineseMapView() {
		new Thread(runnable).start();
	}

}
