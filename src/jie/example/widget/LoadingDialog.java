package jie.example.widget;

import jie.example.boutique.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义LoadingDialog
 */
public class LoadingDialog extends Dialog {

	/**
	 * LoadingDialog提示语
	 */
	private TextView mTextDialogNote;
	/**
	 * LoadingDialog提示语后面的点
	 */
	private TextView mTextLoadingPoint;

	/**
	 * 用于连接“ .”或删除“ .”
	 */
	private StringBuilder mPointBuilder = new StringBuilder();;
	/**
	 * 用于记录点的个数
	 */
	private int mTextPointCount = 0;
	/**
	 * 点个数的增减状态
	 */
	private boolean mIsAdd = true;
	/**
	 * 空格加点
	 */
	private static final String TEXT_POINT = " .";
	/**
	 * 点的最大个数
	 */
	private static final int MAX_TEXT_POINT_NUM = 3;
	private static final int HANDLER_CHANGE_POINT_NUM = 101;
	/**
	 * 用于在主界面更新点的个数
	 */
	private Handler mHandler;

	public LoadingDialog(Context context) {
		super(context, R.style.LoadingDialogStyle);
		initDialog();
	}

	private void initDialog() {
		View dialogView = View.inflate(getContext(),
				R.layout.loading_dialog_layout, null);
		setContentView(dialogView);
		mTextLoadingPoint = (TextView) findViewById(R.id.dialog_text_point);
		mTextDialogNote = (TextView) findViewById(R.id.dialog_content_note);
	}

	@Override
	public void show() {
		super.show();
		if (mHandler == null) {
			mHandler = new TextPointHandler();
		}
		mHandler.sendEmptyMessage(HANDLER_CHANGE_POINT_NUM);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mHandler = null;// 把Handler设为空，以免Handler在后台发送消息。
	}

	/**
	 * 设置LoadingDialog的提示语，但要注意提示语的字数尽量不要过多
	 */
	public void setDialogNoteContent(String noteContent) {
		if (mTextDialogNote == null) {
			mTextDialogNote = (TextView) findViewById(R.id.dialog_content_note);
		}
		mTextDialogNote.setText(noteContent.trim());
	}

	public void setNoteText(int resId) {
		String noteContent = getContext().getString(resId);
		setDialogNoteContent(noteContent);
	}

	@SuppressLint("HandlerLeak")
	private class TextPointHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_CHANGE_POINT_NUM:
				if (mIsAdd) {// 如果是添加状态，则继续添加“ .”
					mTextPointCount++;
					mPointBuilder.append(TEXT_POINT);
					if (mTextPointCount == MAX_TEXT_POINT_NUM) {
						mIsAdd = false;// “ .”的个数到达最大值后，改变成减少状态
					}
				} else {
					mTextPointCount--;
					mPointBuilder.delete(2 * mTextPointCount - 1,
							2 * mTextPointCount + 1);
					if (mTextPointCount == 1) {
						mIsAdd = true;
					}
				}
				mTextLoadingPoint.setText(mPointBuilder.toString().trim());
				if (mHandler != null) {
					// 每300毫秒发送一次消息更新界面
					mHandler.sendEmptyMessageDelayed(HANDLER_CHANGE_POINT_NUM,
							300);
				}
				break;
			default:
				break;
			}
		}
	}

}
