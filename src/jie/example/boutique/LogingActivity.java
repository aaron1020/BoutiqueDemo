package jie.example.boutique;

import jie.example.utils.StringUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LogingActivity extends BasicActivity {
	private static final int MSG_PASSWORD_TEXT_STATUS = 101;
	private EditText mEditUserName, mEditPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setActionBarTitle(R.string.user_login);
		setContentView(R.layout.login_aty);
		initData();
		loadingData();
	}

	@Override
	public void initData() {
		TextView textName = (TextView) findViewById(R.id.login_text_password);
		// 在代码设置任意多的空格让“密码”变成“密(空格)(空格)码”，以保证和“用户名”前后对齐
		textName.setText(getString(R.string.user_password, "    "));
		mEditUserName = (EditText) findViewById(R.id.login_edit_name);
		mEditPassword = (EditText) findViewById(R.id.login_edit_password);
	}

	@Override
	public void loadingData() {
		mEditUserName.setText("Admin");
		mEditPassword.setText("123456");
	}

	public void setOnClick(View view) {
		switch (view.getId()) {
		case R.id.login_btn:
			if ("Admin".equals(mEditUserName.getText().toString())
					&& "123456".equals(mEditPassword.getText().toString())) {
				startActivity(new Intent(this, OfflineActivity.class));
			}
			break;
		case R.id.login_iv_password_show:
			if (!mEditPassword.hasFocus()) {
				mEditPassword.requestFocus();
			}
			int start = mEditPassword.getSelectionStart();// 获取光标的位置
			if (start == 0) {
				String password = mEditPassword.getText().toString();
				if (StringUtil.isNotEmpty(password)) {
					start = password.length();
				}
			}
			mEditPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());// 设置密码文本为可见状态
			mEditPassword.setSelection(start);// 修改状态后，再设置光标的位置，以免光标回到文本的第一个位置
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_PASSWORD_TEXT_STATUS;
			msg.arg1 = start;
			mHandler.sendMessageDelayed(msg, 1000);// 可见状态待续一秒后，再还原密码文本为隐藏状态
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_PASSWORD_TEXT_STATUS:
				mEditPassword
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());// 设置密码文本为隐藏状态
				if (mEditPassword.hasFocus()) {// 修改状态后，还需要重新设置光标的位置，以免光标回到文本的第一个位置
					mEditPassword.setSelection(msg.arg1);
				}
				break;
			default:
				break;
			}
		}

	};

}
