package jie.example.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jie.example.boutique.DynamicAddViewActivity;
import jie.example.boutique.R;
import jie.example.constant.Constant;
import jie.example.entity.DynamicAddViewEntity;
import jie.example.utils.DateUtil;
import jie.example.utils.StringUtil;
import jie.example.utils.ToastUtil;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DynamicAddView extends LinearLayout {

	private DynamicAddViewActivity mActivity;
	private LayoutInflater mInflater;
	private LinearLayout mDynamicContainer;
	private View mBtnAddView;
	private TextView mTextTimeLen;
	private LinearLayout mLayoutCalcTimeWait;
	private int mItemViewIndex = 0;
	private int mItemViewHeight = 0;// ItemView的高度
	private int mDynamicContainerHeight = 0;// mDynamicContainer的高度
	private int mRedColorRes;
	private int mGrayColorRes;
	private float mTimeIntervalHours;
	private static final String TAG = "DynamicAddView";
	private static final String SHORT_LINE = "-";

	public DynamicAddView(Context context) {
		super(context);
		initView(context);
	}

	public DynamicAddView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public DynamicAddView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mActivity = (DynamicAddViewActivity) context;
		mRedColorRes = mActivity.getResources().getColor(R.color.red);
		mGrayColorRes = mActivity.getResources().getColor(R.color.darker_gray);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.dynamic_add_view_v_main, this);
		mDynamicContainer = (LinearLayout) findViewById(R.id.dynamic_addView_layout_container);
		mBtnAddView = findViewById(R.id.dynamic_addView_btn_add);
		mBtnAddView.setOnClickListener(mClickListener);
		addItemView();
	}

	private void addItemView() {
		/*
		 * 在addItemView()方法中调用日志输出方法：Log.i(String, String,
		 * Throwable)，可以在让程序在日志控制台中以栈的形式列出程序是怎么执行到该方法
		 */
		Log.i(TAG, "How Go To Here", new Exception("Stack Way"));

		View itemView = mInflater.inflate(R.layout.dynamic_add_view_v_item,
				null);
		itemView.setId(mItemViewIndex);

		TextView textTitleIndex = (TextView) itemView
				.findViewById(R.id.dynamic_addView_title_index);
		textTitleIndex.setText(mActivity.getString(R.string.table_item_index,
				(mItemViewIndex + 1)));

		TextView btnDeleteView = (TextView) itemView
				.findViewById(R.id.dynamic_addView_delete_view);
		btnDeleteView.setTag(itemView);
		btnDeleteView.setOnClickListener(mClickListener);
		if (mItemViewIndex == 0) {// 第一个条目不显示删除按钮
			btnDeleteView.setVisibility(View.GONE);
		}

		LinearLayout beginTimeLayout = (LinearLayout) itemView
				.findViewById(R.id.begin_time_layout);
		beginTimeLayout.setOnClickListener(mClickListener);
		LinearLayout endTimeLayout = (LinearLayout) itemView
				.findViewById(R.id.end_time_layout);
		endTimeLayout.setOnClickListener(mClickListener);
		// 开始时间和结束时间相互设置Tag用于当初始化其中任何一个时，另一个跟着变化
		beginTimeLayout.setTag(endTimeLayout);
		endTimeLayout.setTag(beginTimeLayout);

		mDynamicContainer.addView(itemView, mItemViewIndex++, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// 第一次添加ItemView后，若马上在此处获取ItemView的高度，则mItemViewHeight为零
		// mItemViewHeight = mDynamicContainer.getMeasuredHeight();
		// Log.i(TAG, "mItemViewHeight = " + mItemViewHeight);
	}

	private void deleteItemView(View itemView) {
		mDynamicContainer.removeView(itemView);
		mItemViewIndex--;
		setItemViewTitleIndex(mItemViewIndex);
	}

	private void setItemViewTitleIndex(int itemViewTotal) {
		for (int index = 0; index < itemViewTotal; index++) {
			View itemView = mDynamicContainer.getChildAt(index);
			TextView textTitleIndex = (TextView) itemView
					.findViewById(R.id.dynamic_addView_title_index);
			textTitleIndex.setText(mActivity.getString(
					R.string.table_item_index, (index + 1)));
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.dynamic_addView_btn_add:
				if (mItemViewIndex == 1) {// 在此处获取ItemView的高度
					mItemViewHeight = mDynamicContainer.getMeasuredHeight();
					Log.i(TAG, "mItemViewHeight = " + mItemViewHeight);
				}
				addItemView();
				mDynamicContainerHeight = mDynamicContainer.getMeasuredHeight();
				Log.i(TAG, "mDynamicContainerHeight = "
						+ mDynamicContainerHeight);
				mActivity.setScrollViewScroll(mDynamicContainerHeight);
				break;

			case R.id.dynamic_addView_delete_view:
				view = (View) view.getTag();
				Animation animation = AnimationUtils.loadAnimation(mActivity,
						R.anim.dynamic_delete_item_view);
				view.startAnimation(animation);
				Message msg = mHandler.obtainMessage();
				msg.what = Constant.HANDLER_DYNAMIC_DELETE_ITEM_VIEW;
				msg.obj = view;
				mHandler.sendMessageDelayed(msg, 600);
				break;

			case R.id.begin_time_layout:
			case R.id.end_time_layout:
				showDateTimeDialog(view);
				break;

			default:
				break;
			}
		}
	};

	private void showDateTimeDialog(final View timeViewLayout) {
		final View itemView = (View) timeViewLayout.getParent();

		String dialogText = "";
		TextView timeText = null;
		if (timeViewLayout.getId() == R.id.begin_time_layout) {// 如果点击的开始时间
			timeText = (TextView) itemView.findViewById(R.id.begin_time_text);
		} else if (timeViewLayout.getId() == R.id.end_time_layout) {
			timeText = (TextView) itemView.findViewById(R.id.end_time_text);
		}
		dialogText = timeText.getText().toString();

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if (StringUtil.isNotEmpty(dialogText)) {
			String[] timeArray = dialogText.split(SHORT_LINE);
			if (timeArray.length == 3) {
				year = Integer.parseInt(timeArray[0]);
				monthOfYear = Integer.parseInt(timeArray[1]) - 1;
				dayOfMonth = Integer.parseInt(timeArray[2]);
			}
		}

		Dialog dialog = new DatePickerDialog(mActivity,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						try {
							String newMonth = StringUtil.addZeroBefortText(
									String.valueOf(monthOfYear + 1), 2);
							String newDayOfMonth = StringUtil
									.addZeroBefortText(
											String.valueOf(dayOfMonth), 2);
							String newDateTime = year + SHORT_LINE + newMonth
									+ SHORT_LINE + newDayOfMonth;

							View tagTimeViewLayout = (View) timeViewLayout
									.getTag();
							TextView changeTextTime = null;
							TextView followChangeTextTime = null;

							if (timeViewLayout.getId() == R.id.begin_time_layout) {
								changeTextTime = (TextView) timeViewLayout
										.findViewById(R.id.begin_time_text);
								followChangeTextTime = (TextView) tagTimeViewLayout
										.findViewById(R.id.end_time_text);
							} else if (timeViewLayout.getId() == R.id.end_time_layout) {
								changeTextTime = (TextView) timeViewLayout
										.findViewById(R.id.end_time_text);
								followChangeTextTime = (TextView) tagTimeViewLayout
										.findViewById(R.id.begin_time_text);
							}

							if (changeTextTime != null) {
								changeTextTime.setText(newDateTime);
								changeTextTime.setTextColor(mGrayColorRes);
							}

							if (followChangeTextTime != null) {
								followChangeTextTime
										.setTextColor(mGrayColorRes);
								// 在需要跟着改变时间为空值的情况下，才为它赋值
								if ("".equals(followChangeTextTime.getText()
										.toString().trim())) {
									// 如果跟着改变的是开始时间，则开始时间要往前减一天
									if (followChangeTextTime.getId() == R.id.begin_time_text) {
										followChangeTextTime.setText(DateUtil
												.addDate(newDateTime, -1));
									} else if (followChangeTextTime.getId() == R.id.end_time_text) {// 如果跟着改变的是开始时间，则开始时间要往后加一天
										followChangeTextTime.setText(DateUtil
												.addDate(newDateTime, 1));
									}
								}
							}

							TextView beginText = (TextView) itemView
									.findViewById(R.id.begin_time_text);
							TextView endText = (TextView) itemView
									.findViewById(R.id.end_time_text);
							String oneDateString = beginText.getText()
									.toString();
							String anotherDateString = endText.getText()
									.toString();
							mTextTimeLen = (TextView) itemView
									.findViewById(R.id.time_len__text_value);
							mLayoutCalcTimeWait = (LinearLayout) itemView
									.findViewById(R.id.time_len_cacl_wait);
							if (!DateUtil.dateTimeCompare(oneDateString,
									anotherDateString)) {// 判断开始时间是否小于结束时间
								beginText.setTextColor(mRedColorRes);
								endText.setTextColor(mRedColorRes);
								mTextTimeLen.setText(R.string.out_time_length);
								mTextTimeLen.setVisibility(View.VISIBLE);
								mLayoutCalcTimeWait.setVisibility(View.GONE);
								ToastUtil
										.showToast(R.string.dynamic_add_view_error_time);
							} else {// 如果时间合法合理，则计算时间差
								mTextTimeLen.setVisibility(View.GONE);
								mLayoutCalcTimeWait.setVisibility(View.VISIBLE);
								mHandler.sendEmptyMessageDelayed(
										Constant.HANDLER_CALC_TIEM_INTERVAL,
										1500);
								mTimeIntervalHours = DateUtil
										.calculateTimeIntervalHours(
												oneDateString,
												anotherDateString);
							}

						} catch (Exception e) {
							Log.e(TAG,
									"Select DateTime Exception-->"
											+ e.toString());
						}

					}
				}, year, monthOfYear, dayOfMonth);
		dialog.show();
	}

	@SuppressLint("ResourceAsColor")
	private DynamicAddViewEntity getItemViewInfo(int itemViewIndex) {
		boolean isNull = false;
		DynamicAddViewEntity itemViewInfo = null;
		try {
			View itemView = mDynamicContainer.getChildAt(itemViewIndex);

			TextView begingText = (TextView) itemView
					.findViewById(R.id.begin_time_text);
			TextView endText = (TextView) itemView
					.findViewById(R.id.end_time_text);
			String begingTextString = begingText.getText().toString();
			String endTextString = endText.getText().toString();

			if (!StringUtil.isNotEmpty(begingTextString)) {
				begingText.setHintTextColor(mRedColorRes);
				isNull = true;
			}

			if (!StringUtil.isNotEmpty(endTextString)) {
				endText.setHintTextColor(mRedColorRes);
				isNull = true;
			}

			// 在必要字段不为空的情况下，进行下一步判断
			if (!isNull) {
				if (DateUtil.dateTimeCompare(begingTextString, endTextString)) {
					itemViewInfo = new DynamicAddViewEntity();
					itemViewInfo.setBeginTime(begingTextString);
					itemViewInfo.setEndTime(endTextString);

					RadioGroup radioGroup = (RadioGroup) itemView
							.findViewById(R.id.type_group);
					String type = radioGroup.getCheckedRadioButtonId() == R.id.type_btn_one ? "1"
							: "2";
					itemViewInfo.setType(type);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "getItemViewInfo Exception-->" + e.toString());
		}

		return itemViewInfo;
	}

	public List<DynamicAddViewEntity> getItemViewInfos() {
		boolean itemViewIsNull = false;
		List<DynamicAddViewEntity> itemViewInfos = new ArrayList<DynamicAddViewEntity>();
		for (int itemViewIndex = 0; itemViewIndex < mItemViewIndex; itemViewIndex++) {
			DynamicAddViewEntity itemViewInfo = getItemViewInfo(itemViewIndex);
			if (itemViewInfo == null) {
				itemViewIsNull = true;
			}
			itemViewInfos.add(itemViewInfo);
		}
		return itemViewIsNull ? null : itemViewInfos;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.HANDLER_CALC_TIEM_INTERVAL:
				mTextTimeLen.setText(mTimeIntervalHours
						+ mActivity.getString(R.string.hours));
				mTextTimeLen.setVisibility(View.VISIBLE);
				mLayoutCalcTimeWait.setVisibility(View.GONE);
				break;

			case Constant.HANDLER_DYNAMIC_DELETE_ITEM_VIEW:
				deleteItemView((View) msg.obj);
				break;

			default:
				break;
			}
		}
	};

}
