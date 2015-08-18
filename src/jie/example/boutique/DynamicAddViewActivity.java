package jie.example.boutique;

import java.util.List;
import java.util.Locale;

import jie.example.constant.Constant;
import jie.example.entity.DynamicAddViewEntity;
import jie.example.entity.PersonParcel;
import jie.example.entity.PersonSerialize;
import jie.example.utils.LogUtil;
import jie.example.utils.ToastUtil;
import jie.example.widget.DynamicAddView;
import jie.example.widget.LoadingDialog;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.ScrollView;

/**
 * 动态添加或删除View
 */
public class DynamicAddViewActivity extends BasicActivity {
	private static final String TAG = "DynamicAddViewActivity";
	private ScrollView mScrollView;
	private DynamicAddView mDynamicAddView;
	private LoadingDialog mLoadingDialog;
	private TextToSpeech mTextSpeech;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.aty_add_view);
		setContentView(R.layout.dynamic_add_view_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		mLoadingDialog = new LoadingDialog(this);
		mScrollView = (ScrollView) findViewById(R.id.dynamic_add_view_scrollView);
		mDynamicAddView = (DynamicAddView) findViewById(R.id.dynamic_add_view);
	}

	@Override
	public void loadingData() {
		Intent intent = getIntent();
		PersonParcel personParcel = intent.getParcelableExtra("PersonParcel");
		LogUtil.i(TAG, personParcel.toString());
		PersonSerialize personSerialize = (PersonSerialize) intent
				.getSerializableExtra("PersonSerialize");
		LogUtil.i(TAG, personSerialize.toString());

		mTextSpeech = new TextToSpeech(this, new OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int result = mTextSpeech.setLanguage(Locale.ENGLISH);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						LogUtil.e(TAG, "tts no working!!");
					} else {
						mTextSpeech.speak("UniStrong",
								TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
		});
	}

	public void setScrollViewScroll(int offsetYScroll) {
		// 需要在mHandler中设置mScrollView滚动到某个位置，在mHandler外设置没有效果
		Message msg = mHandler.obtainMessage();
		msg.what = Constant.HANDLER_DYNAMIC_ADD_VIEW;
		msg.arg1 = offsetYScroll;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.HANDLER_DYNAMIC_ADD_VIEW:
				mScrollView.smoothScrollTo(0, msg.arg1);
				mTextSpeech.speak("提交", TextToSpeech.QUEUE_FLUSH, null);
				break;

			case Constant.HANDLER_SUBMIT_INFO_SUCCESS:
				if (mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				ToastUtil.showToast(R.string.submit_infos_success);
				mTextSpeech.speak("Nan Tian", TextToSpeech.QUEUE_FLUSH, null);
				break;

			default:
				break;
			}

		}
	};

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.bottom_btn_commit:
			submitItemViewInfos();
			break;

		default:
			break;
		}
	}

	private void submitItemViewInfos() {
		List<DynamicAddViewEntity> itemViewInfos = mDynamicAddView
				.getItemViewInfos();
		if (itemViewInfos == null) {
			ToastUtil.showToast(R.string.please_fillin_infos);
		} else {
			mLoadingDialog.setNoteText(R.string.submit_infos_note);
			mLoadingDialog.show();
			mHandler.sendEmptyMessageDelayed(
					Constant.HANDLER_SUBMIT_INFO_SUCCESS, 3000);
			for (DynamicAddViewEntity itemViewInfo : itemViewInfos) {
				LogUtil.i(TAG, itemViewInfo.toString());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTextSpeech != null) {
			mTextSpeech.stop();
			mTextSpeech.shutdown();
		}
	}

}
